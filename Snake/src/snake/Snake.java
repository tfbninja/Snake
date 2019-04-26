package snake;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * Runner class that initiates all high level tasks pertaining to the game
 *
 * @author Tim Barber
 */
public class Snake extends Application implements Loggable {

    //<editor-fold defaultstate="collapsed" desc="instance vars">
    private final int canvasMargin = 10;
    private final int canvasW = 430;
    private final int canvasH = 430;
    private final int WIDTH = 430 + canvasMargin * 2;
    private final int HEIGHT = 430 + canvasMargin * 2;

    private static final Random random = new Random(System.currentTimeMillis());

    // secondary sandbox tool window
    private static ToolPanel toolPanel;
    private JFrame toolboxFrame;

    private int frame = 0;

    private static boolean AI = false;

    private static Board board;

    private ArrayList<Sound> bgMusic = new ArrayList<>();
    private final Sound DAWON = new Sound("resources/sounds/DAWON.mp3");
    private boolean won = false;
    private static ArrayList<Integer> scores = new ArrayList<>();
    private int[] oldScores;
    private static ArrayList<String> names = new ArrayList<>();
    private static String[] funnyDefaultNames = {"ERR", "OOF", "RIP"};

    private boolean scoresOverwritten = false;

    public static String tempName = "";

    private File settings;
    private final String settingsLocation = "resources/settings.snk";
    private static File sandbox;
    private static final String SANDBOXLOCATION = "resources/unsaved.sandbox";

    private boolean sfxOn = true;
    private boolean musicOn = true;
    private boolean nightMode = false;
    private boolean sandboxReset = false;
    private String tempSandboxFile = "";
    private static int[][] appleMap;

    private static final ArrayList<String> MENUNAMES = new ArrayList<String>() {
        {
            add("Main");
            add("High Scores");
            add("Help");
            add("Death");
            add("Game");
        }
    };
    private static final MenuManager MM = new MenuManager(MENUNAMES);
    private static final MainMenu MENU = new MainMenu();
    private static final GameState GS = new GameState(1);

    private static boolean pause = false;

    private Logger log = new Logger(this);
    private static String events = "";

    private boolean fullscreen = false;
//</editor-fold>

    @Override
    public void start(Stage primaryStage) {
        //<editor-fold defaultstate="collapsed" desc="initialization">
        setupBGMusic();
        //Assert resources folder
        // Create Board of block objects
        board = new Board(canvasW, canvasH, MM, MENU, GS, primaryStage);
        board.setOutsideMargin(canvasMargin);
        log.add(board);
        log.add(board.getGrid());

        // if log files are more than 30 days old, delete
        File logFolder = new File("resources/logs");
        File[] directoryListing = logFolder.listFiles();

        if (directoryListing != null) {
            for (File child : directoryListing) {
                //if (child.getPath())
            }
        } else {
            events += "Can not find resources/logs folder | ";
        }


        /*
         * Initialize settings to last used using a settings.snk file that
         * contains variables for the background music, the sound fx, and night
         * mode. This file is updated every 30 frames.
         */
        try {
            settings = new File(settingsLocation);
            Scanner reader = new Scanner(settings);
            reader.useDelimiter(" ");
            String temp = reader.next().trim();
            sfxOn = temp.equals("1");
            reader.nextLine();
            temp = reader.next().trim();
            nightMode = temp.equals("1");
            reader.nextLine();
            temp = reader.next().trim();
            musicOn = temp.equals("1");
        } catch (Exception x) {
            /*
             * Yes, I know this is *terrible* practice, but realize that several
             * possible errors can happen here including FileNotFound and
             * multiple Scanner exceptions, all of which are solved by the
             * following code. The end user will benefit from a more robust
             * program. Additionally, for the programmer, any error is printed
             * to console, it's not like errors are being caught behind the
             * scenes and causing seemingly unrelated problems down the road
             * that are near impossible to fix
             */
            System.out.println("bad file: " + settingsLocation);
            System.out.println(x.getLocalizedMessage());
            sfxOn = true;
            musicOn = true;
            MENU.turnOnMusic();
            MENU.turnOnSFX();
            nightMode = false;
        }

        /*
         * Initialize the 'toolPanel,' a separate AWT container containing
         * controls for manipulating the Grid object with a GUI in sandbox mode
         */
        toolPanel = new ToolPanel(board.getColorScheme(), board.getGrid(), MM, board, GS, (int) primaryStage.getX() - 290, (int) primaryStage.getY());

        // The toolboxFrame is the actual window housing the AWT panel
        toolboxFrame = new JFrame("Toolbox");
        toolboxFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        toolboxFrame.getContentPane().add(toolPanel);
        toolboxFrame.pack();
        toolboxFrame.setVisible(false);

        /*
         * The board needs control of these two objects as it also manages
         * keystrokes which start the different difficulty levels
         */
        board.addToolFrame(toolboxFrame);
        board.addToolPanel(toolPanel);

        if (nightMode) {
            board.setDarkMode();
        }
        board.setSFX(sfxOn);
        if (sfxOn) {
            MENU.turnOnSFX();
        } else {
            MENU.turnOffSFX();
        }
        if (musicOn) {
            MENU.turnOnMusic();
        } else {
            muteBG();
            MENU.turnOffMusic();
        }

        /*
         * Even if the music is set to off we want it on in the background so
         * that it doesn't have to restart every single time the end user
         * toggles the mute button
         */
        playBG();

        // Helper method that retrieves the high scores from the resources folder
        getScores();
        oldScores = toList(scores);

        /*
         * If the local files are unreadable (most likely they just haven't been
         * created yet), set them to 0. We DON'T do this for the world scores as
         * world high scores being unreadable/nonexistent just means the user
         * has messed with the files or something funky is going on.
         */
        for (int i = 0; i < scores.size(); i += 2) { // loop through local scores
            if (scores.get(i) == -1) { // if bad encode/nonexistent
                scores.set(i, 0); // set score to 0
                writeEncodedScore("resources\\scores\\local\\localHighScore" + (i / 2 + 1) + ".local", 0, randomStr(3)); // write 0 to file
            }
        }

        // Initialize help screen (Accessed from menu)
        ImageView HELP_IV = getImageView("resources\\art\\help.jpg");

        // Arrange objects in window with a BorderPane
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
        root.setStyle("-fx-background-color: black");

        // More information on the MainMenu class in MainMenu.java
        root.setTop(MENU.getMenu()); // display titlescreen

        // A Scene object tells a Stage object what to display
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // Get the Canvas used by Board ready to display when the user selects a difficulty level
        board.drawBlocks();
        board.getGrid().addMainMenu(MENU);

        // This is the class that actually displays a 'physical' window on the screen
        primaryStage.setTitle("JSnake");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            log.saveLogFile("resources/logs/log" + log.formatDateTime().replaceAll("[.:/ ]", "") + ".snklog");
            // Safely exit the program when closed
            System.exit(0);
        });

        primaryStage.iconifiedProperty().addListener(e -> {
            if (primaryStage.isIconified()) {
                pause = true;
                // When the main game window is hidden we don't want the toolbox shown
                System.out.println("hiding toolframe");
                toolboxFrame.setState(JFrame.ICONIFIED);
            } else {

                pause = false;
                // If the user minimized the main window and maximized it again, bring up the toolbox
                System.out.println("restoring toolframe");
                toolboxFrame.setState(JFrame.NORMAL);
                toolboxFrame.toFront();
            }
        });

        primaryStage.show();
        toolboxFrame.setLocation((int) primaryStage.getX() - toolPanel.getWidth() - 20, (int) primaryStage.getY());
        toolboxFrame.setVisible(false);
//</editor-fold>
        events += "Initialized. | ";
        log.logState();

        // Main game loop - this is called every 1/30th of a second or so
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (toolPanel.isVisible()) { // only if we are using the toolPanel
                    // Set all the grid settings accessed by the toolPanel to their corresponding values, and repaint the toolPanel
                    toolPanel.update();
                }

                if (!pause) {
                    /*
                     * frame holds the number of updates to the screen that have
                     * ocurred since the game started, useful for timing certain
                     * things
                     */
                    frame++;
                    if (frame % 30 == 0) {
                        loopBG();
                        if (frame % 900 == 0) {
                            // approx once every 30 seconds
                            log.logState();
                        }
                        // We really don't need to set this variable every single time the game is updated, so we only do it every 30 times
                        if (GS.isGame()) {
                            sandboxReset = false;
                        }

                        // If we're currently in sandbox mode, and the board is not completely empty, and we are viewing the grid itself...
                        if (board.getGrid().getDiffLevel() == 0 && !board.getGrid().isClear() && MM.getCurrent() == 4) {
                            // ... then save the current grid vales to unsaved.sandbox
                            try {
                                // tempSandboxFile is a String holding all the important grid values in a custom format
                                tempSandboxFile = compileToSandboxFile(board.getGrid().getEdgeKills(), board.getGrid().getFrameSpeed(), board.getGrid().getInitialLength(), board.getGrid().getGrowBy(), board.getGrid().getPlayArea(), board.getGrid().getExtremeWarp(), board.getGrid().getUseSameSeed(), board.getGrid().getSeed());

                                try (BufferedWriter buffer = new BufferedWriter(new FileWriter("resources/unsaved.sandbox"))) {
                                    /*
                                     * Since for some reason BufferedWriter can
                                     * only write one line at a time, we just
                                     * loop over the string separated by
                                     * newlines
                                     */
                                    for (String s : tempSandboxFile.split("\n")) {
                                        buffer.write(s);
                                        buffer.newLine();
                                    }
                                }
                            } catch (IOException x) {
                                events += "Could not save temp sandbox file. | ";
                                System.out.println("Could not save temp sandbox file.");
                            }
                        }
                        try {
                            /*
                             * Here we save the booleans for background music,
                             * sound fx, and night mode in the settings.snk file
                             * every 30th frame
                             */
                            FileWriter creator;
                            try (PrintWriter printer = new PrintWriter(settingsLocation, "UTF-8")) {
                                creator = new FileWriter(new File(settingsLocation));
                                int tempSFX = MENU.getSFX() ? 1 : 0, tempNightMode = nightMode ? 1 : 0, tempMusic = MENU.getMusic() ? 1 : 0;
                                printer.print("" + tempSFX + " - SFX toggle (0 for off, 1 for on)");
                                printer.println();
                                printer.print("" + tempNightMode + " - appearance (0 for normal, 1 for night mode)");
                                printer.println();
                                printer.print("" + tempMusic + " - background music toggle (0 for normal, 1 for night mode)");
                                printer.println();
                            }
                            creator.close();
                        } catch (IOException x) {
                            events += "Could not save settings | ";
                            System.out.println("Could not save settings - " + x.getLocalizedMessage());
                        }
                    }

                    // Make sure the Board object is consistent with the MainMenu object
                    board.setSFX(MENU.getSFX());

                    // Make sure the background music is consistent with the MainMenu object
                    if (MENU.getMusic()) {
                        unmuteBG();
                    } else {
                        muteBG();
                    }

                    /*
                     * Since the board displays most of the graphics, it
                     * naturally follows that it should manage night mode, hence
                     * we grab it from the Board object here
                     */
                    nightMode = board.getNightTheme();
                    updateScreen(primaryStage, root, HELP_IV);
                }
            }
        }.
                start();

        // Input handling
        scene.setOnMousePressed(
                (MouseEvent event) -> {
                    events += "Mouse clk at (" + event.getX() + ", " + event.getY() + ") | ";
                    board.mouseClicked(event);
                }
        );

        scene.setOnMouseDragged(
                (MouseEvent event) -> {
                    board.mouseDragged(event);
                }
        );

        scene.setOnMouseReleased((MouseEvent event) -> {
            events += "Mouse released | ";
            board.mouseReleased(event);
        }
        );

        scene.setOnKeyPressed(
                (KeyEvent eventa) -> {
                    if (eventa.getCode() == KeyCode.F11) {
                        fullscreen = !fullscreen;
                        primaryStage.setFullScreen(fullscreen);
                        double w = primaryStage.getWidth();
                        double h = primaryStage.getHeight();
                        if (fullscreen) {
                            board.setFullscreen(w, h);
                            int yspace = (int) (Math.max(h - w, 0) / 2);
                            int xspace = (int) (Math.max(w - h, 0) / 2);
                            root.setPadding(new Insets(yspace, xspace, yspace, xspace));
                        } else {
                            board.turnOffFullscreen(canvasW, canvasH);
                            root.setPadding(new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
                        }
                        updateScreen(primaryStage, root, HELP_IV);
                    }
                    if (eventa.getCode() == KeyCode.ESCAPE && fullscreen) {
                        fullscreen = !fullscreen;
                        primaryStage.setFullScreen(false);
                        double w = primaryStage.getWidth();
                        double h = primaryStage.getHeight();
                        board.turnOffFullscreen(canvasW, canvasH);
                        root.setPadding(new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
                        updateScreen(primaryStage, root, HELP_IV);
                    } else {
                        board.keyPressed(eventa);
                    }
                }
        );
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Sets what the user views
     *
     * @param primaryStage
     * @param root
     * @param HELP_IV
     */
    public void updateScreen(Stage primaryStage, BorderPane root, ImageView HELP_IV) {
        /*
         * This switch statement is the main controller of what is
         * going on at any given point in time, dictated by the
         * MenuManager class. More info on MenuManager in
         * MenuManager.java Essentially, the game can be showing one
         * of any four different screens at any point. They are: 0 -
         * the main menu,
         * 1 - the high score screen accessed from * the main menu,
         * 2 - the help screen,
         * 3 - the 'lose' or game over screen (which also displays
         * high scores but is not related to the high scores screen
         * at all), and
         * 4 - the game itself with the squares and such
         *
         * Whatever number the MenuManager currently has set
         * internally as the current screen, the switch statement
         * sets the appropriate object to the root variable (class
         * BorderPane)
         */
        switch (MM.getCurrent()) {
            case 0:
                // show main menu
                if (fullscreen) {
                    double w = primaryStage.getWidth();
                    double h = primaryStage.getHeight();
                    int yspace = (int) (Math.max(h - w, 0) / 2);
                    int xspace = (int) (Math.max(w - h, 0) / 2);
                    root.setPadding(new Insets(yspace, xspace, yspace, xspace));
                    root.setTop(board.getFullScreenMenu(Math.min(w, h)));
                } else {
                    root.setPadding(new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
                    //root.setTop(MENU.getMenu());
                    root.setTop(board.getFullScreenMenu(430));
                }

                won = false;
                break;
            case 1:
                // show high scores
                if (fullscreen) {
                    double w = primaryStage.getWidth();
                    double h = primaryStage.getHeight();
                    int yspace = (int) (Math.max(h - w, 0) / 2);
                    int xspace = (int) (Math.max(w - h, 0) / 2);
                    root.setPadding(new Insets(yspace, xspace, yspace, xspace));
                } else {
                    root.setPadding(new Insets(0, 0, 0, 0));
                }
                root.setTop(drawHighScoreScreen(Math.min(primaryStage.getWidth(), primaryStage.getHeight())));
                break;
            case 2:
                // show help
                if (fullscreen) {
                    double w = primaryStage.getWidth();
                    double h = primaryStage.getHeight();
                    int yspace = (int) (Math.max(h - w, 0) / 2);
                    int xspace = (int) (Math.max(w - h, 0) / 2);
                    root.setPadding(new Insets(yspace, xspace, yspace, xspace));
                } else {
                    root.setPadding(new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
                }
                root.setTop(HELP_IV);
                break;
            case 3:
                // game over - show lose screen and deal with high scores
                GS.setToPostGame();
                board.drawBlocks();
                won = false;
                // reset sandbox
                if (board.getGrid().getDiffLevel() == 0 && !sandboxReset) {
                    sandboxReset = true;
                    resetSandbox();

                } else if (!scoresOverwritten && board.getGrid().getDiffLevel() != 0) {
                    //<editor-fold defaultstate="collapsed" desc="save high scores">
                    int thisDifficulty = board.getGrid().getDiffLevel();
                    int thisScore = board.getGrid().getApplesEaten();
                    boolean highScore = thisScore > scores.get((thisDifficulty - 1) * 2) || thisScore > scores.get((thisDifficulty - 1) * 2 + 1);

                    //<editor-fold defaultstate="collapsed" desc="if highscore">
                    if (highScore) {
                        //  (if score is higher than local or world)
                        JFrame window = new JFrame();
                        HighScore tempWindow = new HighScore();

                        window.setLocation((int) primaryStage.getX() + 50, (int) primaryStage.getY() + 50);
                        window.setTitle("HIGHSCORE");
                        window.add(tempWindow);
                        window.setSize(new Dimension(tempWindow.getPreferredSize().width + 5, tempWindow.getPreferredSize().height + 25));
                        window.setResizable(false);
                        window.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
                        window.setType(java.awt.Window.Type.UTILITY);
                        window.setAutoRequestFocus(true);
                        window.setVisible(true);
                        window.setAlwaysOnTop(true);
                        window.requestFocus();
                        window.requestFocusInWindow();
                        tempWindow.setFocusOnField();
                        Robot bot;
                        //int oldX = ; // get old position so it can be moved back
                        //int oldY = ;
                        try {
                            bot = new Robot();
                            int mask = InputEvent.BUTTON1_DOWN_MASK;
                            bot.mouseMove(window.getX() + tempWindow.getFieldX() + 5, window.getY() + tempWindow.getFieldY() + 5);
                            bot.mousePress(mask);
                            bot.mouseRelease(mask);
                        } catch (AWTException e) {

                        }

                        for (int i = 120; i >= 0; i--) {
                            if (tempName.isEmpty()) {
                                try {
                                    if (i % 4 == 0) {
                                        tempWindow.setCounter(i / 4);
                                    }
                                    Thread.sleep(250);
                                } catch (InterruptedException ex) {
                                    //System.out.println("interuppted");
                                }
                            } else {
                                break;
                            }
                        }

                        window.setVisible(false);
                        window.dispose();
                        String name = tempName;
                        tempName = "";

                        // write scores to files
                        writeEncodedScore("resources\\scores\\local\\localHighScore" + thisDifficulty + ".local", thisScore, name);

                        if (thisScore > scores.get((thisDifficulty - 1) * 2 + 1)) {
                            if (checkFileExists("resources\\scores\\world\\worldHighScore" + thisDifficulty + ".world")) {
                                writeEncodedScore("resources\\scores\\world\\worldHighScore" + thisDifficulty + ".world", thisScore, name);
                            } else {
                                // if there's no world file, it ain't legit                                        }
                            }
                        }
                        getScores();
                    }
//</editor-fold>

                    //<editor-fold defaultstate="collapsed" desc="if fullscreen">
                    if (fullscreen) {
                        ArrayList<Boolean> highs = new ArrayList<>();
                        int index = 0;
                        for (int i : oldScores) {
                            if (i < scores.get(index)) {
                                highs.add(true);
                            } else {
                                highs.add(false);
                            }
                            index++;
                        }
                        double w = primaryStage.getWidth();
                        double h = primaryStage.getHeight();
                        int yspace = (int) (Math.max(h - w, 0) / 2);
                        int xspace = (int) (Math.max(w - h, 0) / 2);
                        root.setPadding(new Insets(yspace, xspace, yspace, xspace));
                        root.setTop(board.getFullScreenBigOof(Math.min(primaryStage.getWidth(), primaryStage.getHeight()), scores, highs, names));
                        scoresOverwritten = true;
                    } else {
                        ArrayList<Boolean> highs = new ArrayList<>();
                        int index = 0;
                        for (int i : oldScores) {
                            if (i < scores.get(index)) {
                                highs.add(true);
                            } else {
                                highs.add(false);
                            }
                            index++;
                        }
                        root.setPadding(new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
                        root.setTop(board.getFullScreenBigOof(430, scores, highs, names));
                        scoresOverwritten = true;
                    }
//</editor-fold>

                } else {
                    ArrayList<Boolean> highs = new ArrayList<>();
                    int index = 0;
                    for (int i : oldScores) {
                        if (i < scores.get(index)) {
                            highs.add(true);
                        } else {
                            highs.add(false);
                        }
                        index++;
                    }
                    if (fullscreen) {
                        double w = primaryStage.getWidth();
                        double h = primaryStage.getHeight();
                        int yspace = (int) (Math.max(h - w, 0) / 2);
                        int xspace = (int) (Math.max(w - h, 0) / 2);
                        root.setPadding(new Insets(yspace, xspace, yspace, xspace));
                        root.setTop(board.getFullScreenBigOof(Math.min(primaryStage.getWidth(), primaryStage.getHeight()), scores, highs, names));
                        scoresOverwritten = true;
                    } else {
                        root.setPadding(new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
                        root.setTop(board.getFullScreenBigOof(430, scores, highs, names));
                        scoresOverwritten = true;
                    }
                }
                //</editor-fold>
                break;

            case 4:
                root.setPadding(new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));

                // show the actual game
                sandboxReset = false;
                if (root.getTop() != board.getCanvas() && !GS.isPostGame()) {
                    root.setTop(board.getCanvas());
                }
                if (GS.isPreGame()) {
                    oldScores = toList(scores);
                    int tempSize = board.getGrid().getLength();
                    appleMap = new int[tempSize][tempSize];
                    for (int r = 0; r < tempSize; r++) {
                        for (int c = 0; c < tempSize; c++) {
                            appleMap[r][c] = board.getGrid().getPlayArea()[r][c];
                        }
                    }
                }
                if (!GS.isPostGame()) {
                    board.drawBlocks();
                    scoresOverwritten = false;
                    if (frame % board.getGrid().getFrameSpeed() == 0) {
                        for (int i = 0; i < board.getGrid().getGensPerFrame(); i++) {
                            if (AI) {
                                AI();
                            }
                            board.getGrid().update();
                        }
                    }
                    if (board.getGrid().countVal(0) == 0 && !won && GS.isGame()) {
                        won = true;
                        if (MENU.getSFX()) {
                            DAWON.play();
                            events += "Won. | ";
                            board.getGrid().won();
                        }
                    }
                } else {
                    MM.setCurrent(3);
                    if (board.getGrid().getDiffLevel() == 0) {
                        GS.setToPreGame();
                    }
                }
                break;
        }
    }

    public static void resetSandbox() {
        board.resetKeepGrid(); // reverts apples to initial
        int[] headPos2 = board.getGrid().getStartPos();
        board.getGrid().removeAll(1);
        board.getGrid().removeAll(2);
        board.getGrid().setPos(headPos2[0], headPos2[1]);
        board.getGrid().setGrowBy(toolPanel.getGrowBy());
        board.getGrid().setEdgeKills(toolPanel.getEdgeKills());
        board.setToSandboxPlayArea();
        board.getGrid().setApples(appleMap);
        board.drawBlocks();
        MM.setCurrent(4);
        GS.setToPreGame();
    }

    public static void toggleAI() {
        AI = !AI;
    }

    /**
     * Adds all files in resources/sounds/death to the list of sounds to play
     * when game is lost
     */
    public void setupBGMusic() {
        File bgMusicFolder = new File("resources/sounds/bg");
        File[] directoryListing = bgMusicFolder.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                this.bgMusic.add(new Sound(Grid.formatFilePath(child.getPath())));
            }
        } else {
            events += "Can not find resources/sounds/bg folder | ";
            System.out.println("Cannot find the resources/sounds/bg folder... try setting the working directory to the folder that Snake.java or Snake.jar is contained in.");
        }
    }

    /**
     * Returns the major events that happened while this class was initialized
     *
     * @return String of events
     */
    @Override
    public String getEvents() {
        return events + "end]";
    }

    /**
     * Mutes the background music
     */
    public void muteBG() {
        for (Sound s : bgMusic) {
            s.mute();
        }
    }

    /**
     * Unmutes the background music
     */
    public void unmuteBG() {
        for (Sound s : bgMusic) {
            s.unmute();
        }
    }

    /**
     * Plays the background music
     */
    public void playBG() {
        bgMusic.get(0).play();
        bgMusic.add(bgMusic.get(0));
        bgMusic.remove(0);
    }

    /**
     * Checks all of the songs and if none are playing it plays the next one
     */
    public void loopBG() {
        for (Sound s : bgMusic) {
            if (s.isPlaying()) {
                return;
            }
        }
        playBG();
    }

    /**
     * Returns the state of the important variables in this class
     *
     * @return String of all variables
     */
    @Override
    public String getState() {
        return "[Frame: " + frame + ", "
                + "AI: " + AI + ", "
                + "Board is not null: " + (board != null) + ", "
                + "Grid is not null: " + (board.getGrid() != null) + ", "
                + "Won: " + won + ", "
                + "Scores overwritten: " + scoresOverwritten + ", "
                + "sfxOn: " + sfxOn + ", "
                + "musicOn: " + musicOn + ", "
                + "night mode: " + nightMode + ", "
                + "sandboxReset: " + sandboxReset + ", "
                + "Apple map is not null: " + (appleMap != null) + ", "
                + "Menu Manager: " + MM + ", "
                + "Main Menu: " + MENU + ", "
                + "Game State: " + GS + ", "
                + "pause: " + pause + ", "
                + "]";
    }

    /**
     *
     * @param len Length of the random string
     * @return random len-character string
     */
    public static String randomStr(int len) {
        String out = "";
        for (int i = 0; i < len; i++) {
            out += (char) (65 + (random.nextInt(26)));
        }
        if (random.nextInt(7) == random.nextInt(7)) {
            out = funnyDefaultNames[random.nextInt(funnyDefaultNames.length - 1)];
        }
        return out;
    }

    /**
     *
     * @param obj The Integer ArrayList to be copied into an int[]
     * @return int[] containing the same values as the obj
     */
    public static int[] toList(ArrayList<Integer> obj) {
        int[] list = new int[obj.size()];
        int index = 0;
        for (Integer i : obj) {
            list[index] = i;
            index++;
        }
        return list;
    }

    /**
     * Used for communication between HighScore and this class for what name the
     * high-scorer is using
     *
     * @param s username
     */
    public static void setUserName(String s) {
        tempName = s;
    }

    /**
     *
     * @param filename
     * @return
     */
    public static ImageView getImageView(String filename) {
        try {
            FileInputStream tempStream = new FileInputStream(filename);
            Image tempImg = new Image(tempStream);
            ImageView iv = new ImageView();
            iv.setImage(tempImg);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setCache(true);
            return iv;
        } catch (FileNotFoundException f) {
            System.out.println(f.getLocalizedMessage());
            return null;
        }
    }

    /**
     *
     * @param edgeKills
     * @param frmSpd
     * @param initialLength
     * @param growBy
     * @param playArea
     * @param extremeWarp
     * @param useSameSeed
     * @param seed
     * @return
     */
    public static String compileToSandboxFile(boolean edgeKills, int frmSpd, int initialLength, int growBy, int[][] playArea, boolean extremeWarp, boolean useSameSeed, long seed) {
        String s = "" + frmSpd + " - number of frames to wait before waiting (min 1)\n";
        s += initialLength + " - initial snake length\n";
        s += growBy + " - snake grow amt\n";
        s += edgeKills ? 1 : 0;
        s += " - edge kills (0 for false, 1 for true)\n";
        s += extremeWarp ? 1 : 0;
        s += " - extreme warp (0 for false, 1 for true)\n";
        s += useSameSeed ? 1 : 0;
        s += " - use the same random seed for generating apples every time (0 for false, 1 for true)\n";
        s += seed + " - seed to use\n\n";
        s += " *\n * Square types:\n * 0 - blank\n * 1 - head (only one of these)\n * 2 - body\n * 3 - Apple\n * 4 - Rock\n * 5 - Invisible\n * 10 and higher - portals (no more than and no less than 2 of each type of portal)\n *\n\n";
        for (int[] y : playArea) {
            for (int x : y) {
                if (x == 2) {
                    x = 0;
                }
                s += x + " ";
            }
            s += "\n";
        }
        return s;
    }

    /**
     *
     * @param content
     * @return
     */
    public static Grid loadSandboxFile(String content) {
        pause = true;
        try {
            try {
                Grid tempGrid = new Grid(25, 25, 0, 0);
                tempGrid.setDiffLevel(0);
                tempGrid.addGameState(GS);
                tempGrid.addMainMenu(MENU);
                tempGrid.addToolPanel(toolPanel);

                Scanner s = new Scanner(content);

                s.useDelimiter(" ");
                int frmSpd = s.nextInt();
                tempGrid.setFrameSpeed(frmSpd);
                s.nextLine();
                int initLen = s.nextInt();
                tempGrid.setInitialSize(initLen);
                s.nextLine();
                int growBy = s.nextInt();
                tempGrid.setGrowBy(growBy);
                s.nextLine();
                boolean edge = s.nextInt() == 1;
                tempGrid.setEdgeKills(edge);
                s.nextLine();
                boolean extrm = s.nextInt() == 1;
                tempGrid.setExtremeStyleWarp(extrm);
                s.nextLine();
                boolean useSame = s.nextInt() == 1;
                s.nextLine();
                s.useDelimiter("");
                String negative = s.next();
                s.useDelimiter(" ");
                long seed;
                if (s.hasNextLong()) {
                    seed = s.nextLong();
                } else {
                    seed = Long.valueOf(negative);
                    seed = -seed;
                    negative = "-";
                }
                if (negative.equals("-")) {
                    seed = -seed;
                } else {
                    seed = Long.valueOf(negative + seed);
                }
                tempGrid.setSeed(seed);
                tempGrid.setUseSameSeed(useSame);
                s.useDelimiter(" ");
                s.nextLine();
                s.nextLine();
                String temp = s.nextLine();
                while (temp.contains("*")) {
                    s.nextLine();
                    temp = s.nextLine();
                }

                // begin reading in grid
                int num = 0;
                outer:
                for (int y = 0; y < 25; y++) {
                    for (int x = 0; x < 25; x++) {
                        try {
                            num = s.nextInt();
                        } catch (java.util.NoSuchElementException e) {
                            System.out.println("Failed to read integer from sandbox");
                            events += "Bad sandbox file | ";
                            throw new java.util.InputMismatchException("Bad sandbox file");
                        }
                        if (num == 1) {
                            tempGrid.setPos(x, y);
                            //System.out.println("head at " + x + ", " + y);
                        }
                        tempGrid.setCell(x, y, num);
                    }
                    s.nextLine();
                }
                pause = false;
                tempGrid.addToolPanel(toolPanel);
                return tempGrid;
            } catch (NumberFormatException e) {
            }
        } catch (java.util.InputMismatchException e) {
        }
        System.out.println("Trouble reading in default sandbox file with content: \"" + content.replaceAll("\n", " (newline) ") + "\"");
        events += "Trouble reading in default sandbox file with content: \"" + content.replaceAll("\n", " (newline) ") + "\"";
        Grid errorGrid = new Grid(25, 25, 0, 0);
        //<editor-fold defaultstate="collapsed" desc="Set up">
        errorGrid.addGameState(GS);
        errorGrid.addMainMenu(MENU);
        errorGrid.addToolPanel(toolPanel);
        errorGrid.setFrameSpeed(3);
        errorGrid.setEdgeKills(true);
        errorGrid.setExtremeStyleWarp(false);
        errorGrid.setGrowBy(1);
        errorGrid.setInitialSize(5);
        errorGrid.setPos(0, 0);
        errorGrid.setDiffLevel(0);
        // Auto generated by pressing SHFT + =
        errorGrid.setCell(0, 9, 3);
        errorGrid.setCell(1, 9, 3);
        errorGrid.setCell(2, 9, 3);
        errorGrid.setCell(3, 9, 3);
        errorGrid.setCell(5, 9, 3);
        errorGrid.setCell(6, 9, 3);
        errorGrid.setCell(7, 9, 3);
        errorGrid.setCell(10, 9, 3);
        errorGrid.setCell(11, 9, 3);
        errorGrid.setCell(12, 9, 3);
        errorGrid.setCell(16, 9, 3);
        errorGrid.setCell(17, 9, 3);
        errorGrid.setCell(20, 9, 3);
        errorGrid.setCell(21, 9, 3);
        errorGrid.setCell(22, 9, 3);
        errorGrid.setCell(0, 10, 3);
        errorGrid.setCell(5, 10, 3);
        errorGrid.setCell(8, 10, 3);
        errorGrid.setCell(10, 10, 3);
        errorGrid.setCell(13, 10, 3);
        errorGrid.setCell(15, 10, 3);
        errorGrid.setCell(18, 10, 3);
        errorGrid.setCell(20, 10, 3);
        errorGrid.setCell(23, 10, 3);
        errorGrid.setCell(0, 11, 3);
        errorGrid.setCell(1, 11, 3);
        errorGrid.setCell(2, 11, 3);
        errorGrid.setCell(5, 11, 3);
        errorGrid.setCell(6, 11, 3);
        errorGrid.setCell(7, 11, 3);
        errorGrid.setCell(10, 11, 3);
        errorGrid.setCell(11, 11, 3);
        errorGrid.setCell(12, 11, 3);
        errorGrid.setCell(15, 11, 3);
        errorGrid.setCell(18, 11, 3);
        errorGrid.setCell(20, 11, 3);
        errorGrid.setCell(21, 11, 3);
        errorGrid.setCell(22, 11, 3);
        errorGrid.setCell(0, 12, 3);
        errorGrid.setCell(5, 12, 3);
        errorGrid.setCell(7, 12, 3);
        errorGrid.setCell(10, 12, 3);
        errorGrid.setCell(12, 12, 3);
        errorGrid.setCell(15, 12, 3);
        errorGrid.setCell(18, 12, 3);
        errorGrid.setCell(20, 12, 3);
        errorGrid.setCell(22, 12, 3);
        errorGrid.setCell(0, 13, 3);
        errorGrid.setCell(1, 13, 3);
        errorGrid.setCell(2, 13, 3);
        errorGrid.setCell(3, 13, 3);
        errorGrid.setCell(5, 13, 3);
        errorGrid.setCell(8, 13, 3);
        errorGrid.setCell(10, 13, 3);
        errorGrid.setCell(13, 13, 3);
        errorGrid.setCell(16, 13, 3);
        errorGrid.setCell(17, 13, 3);
        errorGrid.setCell(20, 13, 3);
        errorGrid.setCell(23, 13, 3);
        errorGrid.setCell(3, 17, 3);
        errorGrid.setCell(4, 17, 3);
        errorGrid.setCell(5, 17, 3);
        errorGrid.setCell(6, 17, 3);
        errorGrid.setCell(10, 17, 3);
        errorGrid.setCell(11, 17, 3);
        errorGrid.setCell(12, 17, 3);
        errorGrid.setCell(13, 17, 3);
        errorGrid.setCell(14, 17, 3);
        errorGrid.setCell(17, 17, 3);
        errorGrid.setCell(18, 17, 3);
        errorGrid.setCell(19, 17, 3);
        errorGrid.setCell(20, 17, 3);
        errorGrid.setCell(3, 18, 3);
        errorGrid.setCell(7, 18, 3);
        errorGrid.setCell(12, 18, 3);
        errorGrid.setCell(17, 18, 3);
        errorGrid.setCell(21, 18, 3);
        errorGrid.setCell(3, 19, 3);
        errorGrid.setCell(7, 19, 3);
        errorGrid.setCell(12, 19, 3);
        errorGrid.setCell(17, 19, 3);
        errorGrid.setCell(21, 19, 3);
        errorGrid.setCell(3, 20, 3);
        errorGrid.setCell(4, 20, 3);
        errorGrid.setCell(5, 20, 3);
        errorGrid.setCell(6, 20, 3);
        errorGrid.setCell(12, 20, 3);
        errorGrid.setCell(17, 20, 3);
        errorGrid.setCell(18, 20, 3);
        errorGrid.setCell(19, 20, 3);
        errorGrid.setCell(20, 20, 3);
        errorGrid.setCell(3, 21, 3);
        errorGrid.setCell(6, 21, 3);
        errorGrid.setCell(12, 21, 3);
        errorGrid.setCell(17, 21, 3);
        errorGrid.setCell(3, 22, 3);
        errorGrid.setCell(7, 22, 3);
        errorGrid.setCell(12, 22, 3);
        errorGrid.setCell(17, 22, 3);
        errorGrid.setCell(3, 23, 3);
        errorGrid.setCell(7, 23, 3);
        errorGrid.setCell(10, 23, 3);
        errorGrid.setCell(11, 23, 3);
        errorGrid.setCell(12, 23, 3);
        errorGrid.setCell(13, 23, 3);
        errorGrid.setCell(14, 23, 3);
        errorGrid.setCell(17, 23, 3);
//</editor-fold>
        pause = false;
        return errorGrid;
    }

    /**
     *
     * @param sandboxFile
     * @return
     */
    public static Grid loadSandboxFile(File sandboxFile) {
        try {
            Scanner s = new Scanner(sandboxFile);
            s.useDelimiter("2049jg0324u0j2m0352035");
            return loadSandboxFile(s.next());
        } catch (FileNotFoundException x) {
            System.out.println("Invalid file: \"" + sandboxFile.getAbsolutePath() + "\".");
            System.out.println(sandboxFile);
            System.out.println(x.getMessage());
            return null;
        }
    }

    /**
     * Initialize the sandbox file from the default location
     */
    public static void initSandboxFile() {
        events += "Initializing sandbox file... ";
        try {
            sandbox = new File(SANDBOXLOCATION);
            Scanner reader = new Scanner(sandbox);
            reader.useDelimiter("2049jg0324u0j2m0352035");
            board.getGrid().overwrite(loadSandboxFile(reader.next()));
            events += "finished and grid overwritten";
        } catch (FileNotFoundException x) {
            System.out.println("Cannot find sandbox file in " + SANDBOXLOCATION + ", try setting the working dir to src/snake.");
            events += "ERROR could not load sandbox file, file not found at \"" + SANDBOXLOCATION + "\", try setting the working dir to src/snake. | ";
        }
    }

    /**
     *
     * @param size side length of the imaginary square bounding the high score
     * screen
     * @return Canvas with high scores drawn on
     */
    public static Canvas drawHighScoreScreen(double size) {
        Canvas c = new Canvas(size, size);
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.setFill(Color.web("b1600f"));

        gc.setFont(new Font("Impact", 78 / 430.0 * size));
        gc.fillText("HIGHSCORES", 15 / 430.0 * size, 113 / 430.0 * size);

        gc.setFont(new Font("Impact", 28 / 430.0 * size));
        gc.fillText("EASY", 28 / 430.0 * size, 236 / 430.0 * size);
        gc.fillText("MEDIUM", 11 / 430.0 * size, 271 / 430.0 * size);
        gc.fillText("HARD", 26 / 430.0 * size, 306 / 430.0 * size);
        gc.fillText("EXTREME", 6 / 430.0 * size, 341 / 430.0 * size);

        gc.setFont(new Font("Impact", 36 / 430.0 * size));
        gc.fillText("WORLD", 151 / 430.0 * size, 180 / 430.0 * size);
        gc.fillText("LOCAL", 286 / 430.0 * size, 180 / 430.0 * size);

        gc.fillRect(133 / 430.0 * size, 188 / 430.0 * size, 253 / 430.0 * size, 2 / 430.0 * size);
        gc.fillRect(267 / 430.0 * size, 190 / 430.0 * size, 2 / 430.0 * size, 151 / 430.0 * size);
        // re-grab scores
        getScores();

        double y = 236 / 430.0 * size;
        double x;
        for (int i = 0; i < scores.size(); i++) {
            if (i % 2 == 0) {
                if (i > 1) {
                    y += 35 / 430.0 * size;
                }
                x = 284 / 430.0 * size;
            } else {
                x = 156 / 430.0 * size;
            }
            gc.setFont(new Font("Impact", 28 / 430.0 * size));
            gc.fillText(String.valueOf(scores.get(i)) + " - " + names.get(i), x, y);
        }
        return c;
    }

    /**
     *
     * @param srcName
     * @param destName
     * @return
     */
    public static boolean copyFile(String srcName, String destName) {
        try {
            File src = new File(srcName);
            File dest = new File(destName);
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            try {
                File src = new File(srcName);
                File dest = new File(destName);
                Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException b) {
                return false;
            }
            return false;
        }
        return true;
    }

    private static void getScores() {
        scores = new ArrayList<>();
        names = new ArrayList<>();
        scores.add(readDecodedFile("resources/scores/local/localHighScore1.local").getKey());
        scores.add(readDecodedFile("resources/scores/world/worldHighScore1.world").getKey());
        scores.add(readDecodedFile("resources/scores/local/localHighScore2.local").getKey());
        scores.add(readDecodedFile("resources/scores/world/worldHighScore2.world").getKey());
        scores.add(readDecodedFile("resources/scores/local/localHighScore3.local").getKey());
        scores.add(readDecodedFile("resources/scores/world/worldHighScore3.world").getKey());
        scores.add(readDecodedFile("resources/scores/local/localHighScore4.local").getKey());
        scores.add(readDecodedFile("resources/scores/world/worldhighScore4.world").getKey());
        names.add(readDecodedFile("resources/scores/local/localHighScore1.local").getValue());
        names.add(readDecodedFile("resources/scores/world/worldHighScore1.world").getValue());
        names.add(readDecodedFile("resources/scores/local/localHighScore2.local").getValue());
        names.add(readDecodedFile("resources/scores/world/worldHighScore2.world").getValue());
        names.add(readDecodedFile("resources/scores/local/localHighScore3.local").getValue());
        names.add(readDecodedFile("resources/scores/world/worldHighScore3.world").getValue());
        names.add(readDecodedFile("resources/scores/local/localHighScore4.local").getValue());
        names.add(readDecodedFile("resources/scores/world/worldhighScore4.world").getValue());
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static Pair<Integer, String> readDecodedFile(String fileName) {
        int decodedScore = -1;
        String name = "AAA";
        try {
            File highScore = new File(fileName);
            Scanner reader = new Scanner(highScore);
            String temp = reader.nextLine().trim();
            try {
                name = reader.nextLine().trim();
            } catch (java.util.NoSuchElementException x) {
                name = randomStr(3);
                events += "no name for score \"" + fileName + "\" | ";
            }
            try {
                decodedScore = Enigma.decode(temp);
            } catch (InvalidObjectException ioe) {
                events += "bad encode for highscore file " + fileName + " | ";
                Pair<Integer, String> out = new Pair<>(decodedScore, name);
                return out;
            }
        } catch (FileNotFoundException x) {
            events += "bad file: " + fileName + " | ";
            Pair<Integer, String> out = new Pair<>(decodedScore, name);
            return out;
        }
        Pair<Integer, String> out = new Pair<>(decodedScore, name);
        return out;
    }

    /**
     *
     * @param targetXPos
     * @param targetYPos
     * @param selfX
     * @param selfY
     * @return
     */
    public int distanceToPoint(int targetXPos, int targetYPos, int selfX, int selfY) {
        // Manhattan/Taxicab distance
        return Math.abs(selfX - targetXPos) + Math.abs(selfY - targetYPos);
    }

    /**
     * Called every grid.update() if the boolean AI is true
     */
    public static void AI() {
        if (GS.isGame()) {
            Grid grid = board.getGrid();
            double multiplier = 1.5;

            int left = board.getGrid().getLeft(), right = board.getGrid().getRight(), front = board.getGrid().getFront();
            int frontArea = grid.getFrontArea();
            int rightArea = grid.getRightArea();
            int leftArea = grid.getLeftArea();

            //System.out.println("Left: " + left + ", right: " + right + ", front: " + front + ", dir: " + direction);
            boolean killLeft = grid.willKill(left), killFront = grid.willKill(front), killRight = grid.willKill(right);
            if (killLeft && killFront && killRight) {
                // screwed
            } else if (killLeft && killFront && !killRight) {
                grid.turnRight();
            } else if (killLeft && !killFront && killRight) {
                // continue on
            } else if (killLeft && !killFront && !killRight) {
                if (rightArea > frontArea * multiplier) {
                    grid.turnRight();
                } else if (frontArea > rightArea * multiplier) {
                    // go forward
                } else {
                    goToApple(true, false, false);
                }
            } else if (!killLeft && killFront && killRight) {
                grid.turnLeft();
            } else if (!killLeft && killFront && !killRight) {
                // need to turn (fix spiral of death)
                if (rightArea > leftArea * multiplier) {
                    grid.turnRight();
                } else if (leftArea > rightArea * multiplier) {
                    grid.turnLeft();
                } else {
                    goToApple(false, true, false);
                }
            } else if (!killLeft && !killFront && killRight) {
                if (frontArea > leftArea * multiplier) {
                    // go forward
                } else if (leftArea > frontArea * multiplier) {
                    grid.turnLeft();
                } else {
                    goToApple(false, false, true);
                }
            } else if (!killLeft && !killFront && !killRight) {

                if (frontArea > leftArea + rightArea) {
                    // go forward
                } else if (leftArea > frontArea + rightArea) {
                    grid.turnLeft();
                } else if (rightArea > leftArea + frontArea) {
                    grid.turnRight();
                } else if (leftArea > rightArea * multiplier && leftArea > frontArea * multiplier) {
                    grid.turnLeft();
                } else if (rightArea > leftArea * multiplier && rightArea > frontArea * multiplier) {
                    grid.turnRight();
                } else if (frontArea > leftArea * multiplier && frontArea > rightArea * multiplier) {
                    // go forward
                } else if (frontArea * multiplier < leftArea || frontArea * multiplier < rightArea) {
                    goToApple(false, true, false);
                } else if (leftArea * multiplier < frontArea || leftArea * multiplier < rightArea) {
                    goToApple(true, false, false);
                } else if (rightArea * multiplier < leftArea || rightArea * multiplier < frontArea) {
                    goToApple(false, false, true);
                } else {
                    goToApple(false, false, false);
                }
            }
        } else {
            // game is not in session
        }
    }

    public static void goToApple(boolean excludeLeft, boolean excludeFront, boolean excludeRight) {
        Grid grid = board.getGrid();
        int direction = board.getGrid().getDirection();
        double randomizer = Math.random() * 1.3;
        int x = board.getGrid().getHeadX(), y = board.getGrid().getHeadY();
        int appleX;
        int appleY;
        try {
            appleX = board.getGrid().find(3).get(0).getKey();
            appleY = board.getGrid().find(3).get(0).getValue();
        } catch (java.lang.IndexOutOfBoundsException e) {
            // no apple
            appleX = 0;
            appleY = 0;
        }

        switch (direction) {
            case 1:
                if (x - appleX > 0) {
                    if (!excludeLeft) {
                        grid.turnLeft();
                    }
                } else if (x - appleX < 0) {
                    if (!excludeRight) {
                        grid.turnRight();
                    }
                } else if (y - appleY > 0) {
                    if (excludeFront) {
                        if (randomizer < 0.5) {
                            if (!excludeLeft) {
                                grid.turnLeft();
                            } else {
                                grid.turnRight();
                            }
                        } else {
                            if (!excludeRight) {
                                grid.turnRight();
                            } else {
                                grid.turnLeft();
                            }
                        }
                    }
                } else if (y - appleY < 0) {
                    if (randomizer > 0.5) {
                        if (!excludeRight) {
                            grid.turnRight();
                        }
                    } else {
                        if (!excludeLeft) {
                            grid.turnLeft();
                        }
                    }
                }
                break;
            case 2:
                if (x - appleX > 0) {
                    if (randomizer > 0.5) {
                        if (!excludeRight) {
                            grid.turnRight();
                        }
                    } else {
                        if (!excludeLeft) {
                            grid.turnLeft();
                        }
                    }
                } else if (x - appleX < 0) {
                    if (excludeFront) {
                        if (randomizer < 0.5) {
                            if (!excludeLeft) {
                                grid.turnLeft();
                            } else {
                                grid.turnRight();
                            }
                        } else {
                            if (!excludeRight) {
                                grid.turnRight();
                            } else {
                                grid.turnLeft();
                            }
                        }
                    }
                } else if (y - appleY > 0) {
                    if (!excludeLeft) {
                        grid.turnLeft();
                    }
                } else if (y - appleY < 0) {
                    if (!excludeRight) {
                        grid.turnRight();
                    }
                }
                break;
            case 3:
                if (x - appleX > 0) {
                    if (!excludeRight) {
                        grid.turnRight();
                    }
                } else if (x - appleX < 0) {
                    if (!excludeLeft) {
                        grid.turnLeft();
                    }
                } else if (y - appleY > 0) {
                    if (randomizer > 0.5) {
                        if (!excludeRight) {
                            grid.turnRight();
                        }
                    } else {
                        if (!excludeLeft) {
                            grid.turnLeft();
                        }
                    }
                } else if (y - appleY < 0) {
                    if (excludeFront) {
                        if (randomizer < 0.5) {
                            if (!excludeLeft) {
                                grid.turnLeft();
                            } else {
                                grid.turnRight();
                            }
                        } else {
                            if (!excludeRight) {
                                grid.turnRight();
                            } else {
                                grid.turnLeft();
                            }
                        }
                    }
                }
                break;

            case 4:
                if (x - appleX > 0) {
                    if (randomizer > 0.5) {
                        if (!excludeRight) {
                            grid.turnRight();
                        }
                    } else {
                        if (!excludeLeft) {
                            grid.turnLeft();
                        }
                    }
                } else if (x - appleX < 0) {
                    if (excludeFront) {
                        if (randomizer < 0.5) {
                            if (!excludeLeft) {
                                grid.turnLeft();
                            } else {
                                grid.turnRight();
                            }
                        } else {
                            if (!excludeRight) {
                                grid.turnRight();
                            } else {
                                grid.turnLeft();
                            }
                        }
                    }
                } else if (y - appleY > 0) {
                    if (!excludeLeft) {
                        grid.turnLeft();
                    }
                } else if (y - appleY < 0) {
                    if (!excludeRight) {
                        grid.turnRight();
                    }
                }
                break;
        }
    }

    /**
     *
     * @param filename
     * @return
     */
    public static boolean checkFileExists(String filename) {
        try {
            Scanner temp = new Scanner(new File(filename));
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    /**
     *
     * @param filename destination file path
     * @param score raw score
     * @param username name of scorer
     */
    public static void writeEncodedScore(String filename, int score, String username) {
        try {
            FileWriter creator;
            try (PrintWriter printer = new PrintWriter(filename, "UTF-8")) {
                creator = new FileWriter(new File(filename));
                printer.print(Enigma.encode(score));
                printer.println();
                printer.print(username);
            }
            creator.close();
        } catch (IOException x) {
            events += "could not save score \"" + score + "\" to \"" + filename + "\" - " + x.getLocalizedMessage() + " | ";
            System.out.println("Trouble saving score \"" + score + "\" to \"" + filename + "\" - " + x.getLocalizedMessage());
        }
    }

    /**
     *
     * @param filename
     * @param newFilename
     * @param text
     * @param xPos
     * @param yPos
     * @param font
     * @param red
     * @param green
     * @param blue
     * @return
     */
    public static boolean overlayImage(String filename, String newFilename, String text, int xPos, int yPos, Font font, int red, int green, int blue) {
        try {
            final BufferedImage image = ImageIO.read(new File(filename));

            Graphics g = image.getGraphics();
            g.setFont(new java.awt.Font(font.getName(), 0, (int) font.getSize()));
            java.awt.Color c = new java.awt.Color(red, green, blue);
            g.setColor(c);
            g.drawString(text, xPos, yPos);
            g.dispose();

            ImageIO.write(image, "png", new File(newFilename));
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    /**
     *
     * @param filename
     * @param newFilename
     * @param addFilename
     * @param xPos
     * @param yPos
     * @return
     */
    public static boolean overlayImage(String filename, String newFilename, String addFilename, int xPos, int yPos) {
        try {
            BufferedImage oldImage = ImageIO.read(new File(filename));
            BufferedImage addImage = ImageIO.read(new File(addFilename));

            Graphics g = oldImage.getGraphics();
            g.drawImage(addImage, xPos, yPos, null);
            g.dispose();
            ImageIO.write(oldImage, "png", new File(newFilename));
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            return false;
        }
        return true;
    }
}
/*
 * The MIT License
 *
 * Copyright (c) 2018 Tim Barber.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
