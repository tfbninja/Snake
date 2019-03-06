package snake;

//<editor-fold defaultstate="collapsed" desc="imports">
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidObjectException;
import javafx.util.Pair;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
//</editor-fold>

/**
 *
 * @author Tim Barber
 */
public class Snake extends Application {

    //<editor-fold defaultstate="collapsed" desc="instance vars">
// primary game window
    private final int canvasMargin = 10;
    private final int canvasW = 430;
    private final int canvasH = 430;
    private final int WIDTH = 430 + canvasMargin * 2;
    private final int HEIGHT = 430 + canvasMargin * 2;

    // secondary sandbox tool window
    private final int TOOLWIDTH = 200;
    private final int TOOLHEIGHT = 450;
    private TestPanel testPanel;
    private JFrame toolboxFrame;

    private int frame = 0;

    private final boolean AI = false;

    private static Board board;

    private final Sound menuMusic = new Sound("resources/sounds/menuMusic.wav");
    private final Sound DAWON = new Sound("resources/sounds/DAWON.mp3");
    private boolean won = false;
    private static ArrayList<Integer> scores = new ArrayList<>();
    private ImageView HS_IV; // High Score screen stored in an 'ImageView' class

    private boolean scoresOverwritten = false;

    private File settings;
    private final String settingsLocation = "resources/settings.snk";
    private static File sandbox;
    private static final String SANDBOXLOCATION = "resources/unsaved.sandbox";
    private static final int[][] SANDBOXPLAYAREA = new int[25][25];
    private static Pair<Integer, Integer> sandboxHeadPos;

    private boolean sfxOn = true;
    private boolean musicOn = true;
    private boolean nightMode = false;
    private boolean sandboxReset = false;
    private String tempSandboxFile = "";
    int[][] appleMap;

    private static final ArrayList<String> MENUNAMES = new ArrayList<String>() {
        {
            add("Main");
            add("High Scores");
            add("Help");
            add("Death");
            add("Game");
        }
    };
    private final MenuManager MM = new MenuManager(MENUNAMES);
    private final MainMenu MENU = new MainMenu();
    private static final GameState GS = new GameState(1);

    private static boolean pause = false;
//</editor-fold>

    @Override
    public void start(Stage primaryStage) {

        //<editor-fold defaultstate="collapsed" desc="initialization">
        // TODO: Assert that resources folder exists
        // Create Board of block objects
        board = new Board(canvasW, canvasH, MM, MENU, GS, primaryStage);
        board.setOutsideMargin(canvasMargin);

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
         * Initialize the 'testPanel,' a separate AWT container containing
         * controls for manipulating the Grid object with a GUI in sandbox mode
         */
        testPanel = new TestPanel(board.getColorScheme(), board.getGrid(), MM, board, GS, (int) primaryStage.getX() - 290, (int) primaryStage.getY());

        // The toolboxFrame is the actual window housing the AWT panel
        toolboxFrame = new JFrame("Toolbox");
        toolboxFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        toolboxFrame.getContentPane().add(testPanel);
        toolboxFrame.pack();
        toolboxFrame.setVisible(false);

        /*
         * The board needs control of these two objects as it also manages
         * keystrokes which start the different difficulty levels
         */
        board.addToolFrame(toolboxFrame);
        board.addTestPanel(testPanel);

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
            menuMusic.mute();
            MENU.turnOffMusic();
        }

        /*
         * Even if the music is set to off we want it on in the background so
         * that it doesn't have to restart every single time the end user
         * toggles the mute button
         */
        menuMusic.loop();

        // Helper method that retrieves the high scores from the resources folder
        getScores();

        /*
         * If the local files are unreadable (most likely they just haven't been
         * created yet), set them to 0. We DON'T do this for the world scores as
         * world high scores being unreadable/nonexistent just means the user
         * has messed with the files or something funky is going on.
         */
        for (int i = 0; i < scores.size(); i += 2) { // loop through local scores
            if (scores.get(i) == -1) { // if bad encode/nonexistent
                scores.set(i, 0); // set score to 0
                writeEncodedScore("resources\\scores\\local\\localHighScore" + (i / 2 + 1) + ".local", 0); // write 0 to file
            }
        }

        // Initializes ImageView object for viewing the high scores (viewed by pressing 'h' on the menu screen)
        HS_IV = createHighScoreScreen();

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

        // This is the class that actually displays a 'physical' window on the screen
        primaryStage.setTitle("JSnake");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            // Safely exit the program when closed
            System.exit(0);
        });
        primaryStage.setOnHidden(event -> {
            pause = true;
            // When the main game window is hidden we don't want the toolbox shown
            toolboxFrame.setVisible(false);
        });
        primaryStage.setOnShowing(event -> {
            pause = false;
            // If the user minimized the main window and maximized it again, bring up the toolbox
            toolboxFrame.setVisible(true);
        });

        primaryStage.show();
        toolboxFrame.setLocation((int) primaryStage.getX() - testPanel.getWidth() - 20, (int) primaryStage.getY());
        toolboxFrame.setVisible(false);
//</editor-fold>

        // Main game loop - this is called every 1/30th of a second or so
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (testPanel.isVisible()) { // only if we are using the testPanel
                    // Set all the grid settings accessed by the testPanel to their corresponding values, and repaint the testPanel
                    testPanel.update();
                }

                if (!pause) {
                    /*
                     * frame holds the number of updates to the screen that have
                     * ocurred since the game started, useful for timing certain
                     * things
                     */
                    frame++;

                    if (frame % 30 == 0) {

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
                            System.out.println(x.getLocalizedMessage() + " oof.");
                        }
                    }

                    // Make sure the Board object is consistent with the MainMenu object
                    board.setSFX(MENU.getSFX());

                    // Make sure the background music is consistent with the MainMenu object
                    if (MENU.getMusic()) {
                        menuMusic.unmute();
                    } else {
                        menuMusic.mute();
                    }

                    /*
                     * Since the board displays most of the graphics, it
                     * naturally follows that it should manage night mode, hence
                     * we grab it from the Board object here
                     */
                    nightMode = board.getNightTheme();

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
                            root.setTop(MENU.getMenu());
                            won = false;
                            break;
                        case 1:
                            // show high scores
                            root.setTop(HS_IV);
                            break;
                        case 2:
                            // show help
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

                                board.resetKeepGrid(); // reverts apples to initial
                                int[] headPos2 = board.getGrid().getStartPos();
                                board.getGrid().removeAll(1);
                                board.getGrid().removeAll(2);
                                board.getGrid().setPos(headPos2[0], headPos2[1]);
                                board.getGrid().setGrowBy(testPanel.getGrowBy());
                                board.getGrid().setEdgeKills(testPanel.getEdgeKills());
                                //System.out.println("Setting to sandbox play area from snake");
                                board.setToSandboxPlayArea();
                                //System.out.println("Set to sandbox play area from snake");
                                //board.getGrid().unFreezeApples();
                                board.getGrid().setApples(appleMap);
                                board.drawBlocks();
                                MM.setCurrent(4);
                                GS.setToPreGame();
                            } else if (!scoresOverwritten && board.getGrid().getDiffLevel() != 0) {
                                //<editor-fold defaultstate="collapsed" desc="save high scores">
                                int thisDifficulty = board.getGrid().getDiffLevel();
                                int thisScore = board.getGrid().getApplesEaten();
                                boolean highScore = thisScore > scores.get((thisDifficulty - 1) * 2) || thisScore > scores.get((thisDifficulty - 1) * 2 + 1);
                                int[] oldScores = toList(scores);

                                if (highScore) {
                                    //  (if score is higher than local or world)

                                    // write scores to files
                                    writeEncodedScore("resources\\scores\\local\\localHighScore" + thisDifficulty + ".local", thisScore);

                                    if (thisScore > scores.get((thisDifficulty - 1) * 2 + 1)) {
                                        if (checkFileExists("resources\\scores\\world\\worldHighScore" + thisDifficulty + ".world")) {
                                            writeEncodedScore("resources\\scores\\world\\worldHighScore" + thisDifficulty + ".world", thisScore);
                                        } else {
                                            // if there's no world file, it ain't legit
                                            //System.out.println("maybe keep the world high score file around buddy...");
                                        }
                                    }
                                }
                                // re-grab scores
                                getScores();
                                // copy the master image
                                overlayImage("resources\\art\\loseScreenMaster.png", "resources\\art\\loseScreen.png", String.valueOf(thisScore), 248, 194, new Font("Impact", 26), 177, 96, 15);
                                int y = 320;
                                int x;
                                for (int i = 0; i < scores.size(); i++) {
                                    if (i % 2 == 0) {
                                        if (i > 1) {
                                            y += 27;
                                        }
                                        x = 264;
                                    } else {
                                        x = 153;
                                    }
                                    if (i / 2 + 1 == thisDifficulty && highScore && thisScore > oldScores[i]) {
                                        overlayImage("resources\\art\\loseScreen.png", "resources\\art\\loseScreen.png", String.valueOf(scores.get(i)), x, y, new Font("Impact", 22), 255, 0, 0);
                                    } else {
                                        overlayImage("resources\\art\\loseScreen.png", "resources\\art\\loseScreen.png", String.valueOf(scores.get(i)), x, y, new Font("Impact", 22), 177, 96, 15);
                                    }
                                }

                                if (highScore) {
                                    overlayImage("resources\\art\\loseScreen.png", "resources\\art\\loseScreen.png", "NEW HIGHSCORE", 105, 34, new Font("Impact", 34), 255, 0, 0);
                                }
                                scoresOverwritten = true;
                                ImageView LOSE_IV = getImageView("resources\\art\\loseScreen.png");
                                root.setTop(LOSE_IV);
                                HS_IV = createHighScoreScreen(); // re-cache high score screen
                            }
                            //</editor-fold>
                            break;
                        case 4:
                            // show the actual game
                            sandboxReset = false;
                            if (root.getTop() != board.getCanvas() && !GS.isPostGame()) {
                                root.setTop(board.getCanvas());
                            }
                            if (GS.isPreGame()) {
                                int tempSize = board.getGrid().getPlayArea().length;
                                appleMap = new int[tempSize][tempSize];
                                for (int r = 0; r < tempSize; r++) {
                                    for (int c = 0; c < tempSize; c++) {
                                        appleMap[r][c] = board.getGrid().getPlayArea()[r][c];
                                    }
                                }
                            }
                            if (!GS.isPostGame()) {
                                if (AI) {
                                    AI();
                                }
                                board.drawBlocks();
                                scoresOverwritten = false;
                                if (frame % board.getGrid().getFrameSpeed() == 0) {
                                    for (int i = 0; i < board.getGrid().getGensPerFrame(); i++) {
                                        board.getGrid().nextGen();
                                    }
                                }
                                if (board.getGrid().countVal(0) == 0 && !won && GS.isGame()) {
                                    won = true;
                                    if (MENU.getSFX()) {
                                        DAWON.play();
                                        board.getGrid().won();
                                    }
                                }
                            } else {
                                MM.setCurrent(3);
                                if (board.getGrid().getDiffLevel() == 0) {
                                    GS.setToPreGame();
                                }
                            }
                    }
                }
            }
        }.start();

        // Input handling
        scene.setOnMousePressed(
                (MouseEvent event) -> {
                    board.mouseClicked(event);
                    /*
                     * if (board.getGrid().getDiffLevel() == 0) {
                     * toolboxFrame.setVisible(true);
                     * toolboxFrame.toFront();
                     * primaryStage.requestFocus();
                     * }
                     */
                }
        );

        scene.setOnMouseDragged(
                (MouseEvent event) -> {
                    board.mouseDragged(event);
                    /*
                     * if (board.getGrid().getDiffLevel() == 0) {
                     * toolboxFrame.setVisible(true);
                     * toolboxFrame.toFront();
                     * primaryStage.requestFocus();
                     * }
                     */
                }
        );

        scene.setOnKeyPressed(
                (KeyEvent eventa) -> {
                    board.keyPressed(eventa);
                    /*
                     * if (board.getGrid().getDiffLevel() == 0) {
                     * toolboxFrame.setVisible(true);
                     * toolboxFrame.toFront();
                     * primaryStage.requestFocus();
                     * }
                     */
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
        try {
            try {
                Grid tempGrid = new Grid(25, 25, 0, 0);
                tempGrid.setDiffLevel(0);
                tempGrid.addGameState(GS);

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
                //System.out.println("first char: \"" + negative + "\"");
                long seed = 0;
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
                for (int y = 0; y < 25; y++) {
                    for (int x = 0; x < 25; x++) {
                        try {
                            num = s.nextInt();
                        } catch (java.util.NoSuchElementException e) {
                            System.out.println("Failed to read integer from sandbox");
                        }
                        if (num == 1) {
                            tempGrid.setPos(x, y);
                            //System.out.println("head at " + x + ", " + y);
                        }
                        SANDBOXPLAYAREA[y][x] = num;
                    }
                    s.nextLine();
                }
                tempGrid.setPlayArea(SANDBOXPLAYAREA.clone());
                return tempGrid;
            } catch (NumberFormatException e) {
                System.out.println("Trouble reading in default sandbox file with content: \"" + content + "\"");
                Grid errorGrid = new Grid(25, 25, 0, 0);
                //<editor-fold defaultstate="collapsed" desc="Set up">
                errorGrid.addGameState(GS);
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
                return errorGrid;
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Trouble reading in default sandbox file with content: \"" + content + "\"");
            Grid errorGrid = new Grid(25, 25, 0, 0);
            //<editor-fold defaultstate="collapsed" desc="Set up">
            errorGrid.addGameState(GS);
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
            return errorGrid;
        }

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
     *
     */
    public static void initSandboxFile() {
        try {
            //System.out.println("Initializing sandbox");
            sandbox = new File(SANDBOXLOCATION);
            Scanner reader = new Scanner(sandbox);
            reader.useDelimiter("2049jg0324u0j2m0352035");
            board.getGrid().overwrite(loadSandboxFile(reader.next()));
        } catch (FileNotFoundException x) {
            System.out.println("Cannot find sandbox file in " + SANDBOXLOCATION + ", try setting the working dir to src/snake.");
        }
    }

    private static ImageView createHighScoreScreen() {
        // re-grab scores
        getScores();
        // copy the master image
        overlayImage("resources\\art\\HighScoreScreen.png", "resources\\art\\HighScoreScreen.png", "resources\\art\\HighScoreScreenMaster.png", 0, 0);
        int y = 236;
        int x;
        for (int i = 0; i < scores.size(); i++) {
            if (i % 2 == 0) {
                if (i > 1) {
                    y += 35;
                }
                x = 320;
            } else {
                x = 193;
            }
            overlayImage("resources\\art\\HighScoreScreen.png", "resources\\art\\HighScoreScreen.png", String.valueOf(scores.get(i)), x, y, new Font("Impact", 28), 177, 96, 15);
        }
        // set up lose screen
        ImageView iv = getImageView("resources\\art\\HighScoreScreen.png");
        return iv;
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
        scores.add(readDecodedFile("resources/scores/local/localHighScore1.local"));
        scores.add(readDecodedFile("resources/scores/world/worldHighScore1.world"));
        scores.add(readDecodedFile("resources/scores/local/localHighScore2.local"));
        scores.add(readDecodedFile("resources/scores/world/worldHighScore2.world"));
        scores.add(readDecodedFile("resources/scores/local/localHighScore3.local"));
        scores.add(readDecodedFile("resources/scores/world/worldHighScore3.world"));
        scores.add(readDecodedFile("resources/scores/local/localHighScore4.local"));
        scores.add(readDecodedFile("resources/scores/world/worldhighScore4.world"));
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static int readDecodedFile(String fileName) {
        int decodedScore = -1;
        try {
            File highScore = new File(fileName);
            Scanner reader = new Scanner(highScore);
            reader.useDelimiter("YeetCommunismChungusMan");
            String temp = reader.next().trim();
            try {
                decodedScore = Enigma.decode(temp);
            } catch (InvalidObjectException ioe) {
                //System.out.println("bad encode for file " + fileName);
                return decodedScore;
            }
        } catch (FileNotFoundException x) {
            //System.out.println("bad file: " + fileName);
            return decodedScore;
        }
        return decodedScore;
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
     *
     */
    public static void AI() {
        if (GS.isGame()) {
            Grid grid = board.getGrid();
            int direction = board.getGrid().getDirection();
            int randomizer = 0;
            int x = board.getGrid().getHeadX(), y = board.getGrid().getHeadY();
            int left = board.getGrid().getLeft(), right = board.getGrid().getRight(), front = board.getGrid().getFront();
            System.out.println("Left: " + left + ", right: " + right + ", front: " + front + ", dir: " + direction);
            if ((grid.willKill(left)) && !grid.willKill(front) && y != 0) {
                return;
            } else if (grid.willKill(left) && grid.willKill(front)) {
                System.out.println("turning right");
                grid.turnRight();
                return;
            } else if (grid.willKill(front) && grid.willKill(right)) {
                System.out.println("turning left");
                grid.turnLeft();
                return;
            } else if (!grid.willKill(left)) {
                return;
            }
            grid.turnRight();
        } else {
            // game is not in session
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
     * @param filename
     * @param score
     */
    public static void writeEncodedScore(String filename, int score) {
        try {
            FileWriter creator;
            try (PrintWriter printer = new PrintWriter(filename, "UTF-8")) {
                creator = new FileWriter(new File(filename));
                printer.print(Enigma.encode(score));
            }
            creator.close();
        } catch (IOException x) {
            System.out.println(x.getLocalizedMessage() + " oof.");
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
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
