package snake;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
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
import java.util.Arrays;
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

/**
 *
 * @author Tim Barber
 */
public class Snake extends Application {

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
    private static final String SANDBOXLOCATION = "resources/sandbox.sandbox";
    private static int[][] sandboxPlayArea = new int[25][25];
    private static Pair<Integer, Integer> sandboxHeadPos;

    private boolean sfxOn = true;
    private boolean musicOn = true;
    private boolean nightMode = false;
    private boolean sandboxReset = false;

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

    @Override
    public void start(Stage primaryStage) {

        /*
         * Assert that resources folder exists
         */
        // Create Board of block objects
        board = new Board(canvasW, canvasH, MM, MENU, GS, primaryStage);
        board.setOutsideMargin(canvasMargin);

        // initialize settings to last used
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
        } catch (FileNotFoundException x) {
            System.out.println("bad file: " + settingsLocation);
            sfxOn = true;
            nightMode = false;
            musicOn = true;
            MENU.turnOnMusic();
            MENU.turnOnSFX();
        }
        testPanel = new TestPanel(board.getColorScheme(), board.getGrid(), MM, board, GS);
        //System.out.println(Arrays.deepToString(board.getColorScheme()));
        toolboxFrame = new JFrame("Toolbox");
        toolboxFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        toolboxFrame.getContentPane().add(testPanel);
        toolboxFrame.pack();
        toolboxFrame.setVisible(false);
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
        menuMusic.loop();

        // init sandbox file
        initSandboxFile();

        getScores();
        // if local files unreadable, set to 0
        for (int i = 0; i < scores.size(); i += 2) { // loop through local scores
            if (scores.get(i) == -1) { // if bad encode
                scores.set(i, 0); // set score to 0
                //System.out.println("Re-writing local high score " + (i / 2 + 1) + " to 0.");
                writeEncodedScore("resources\\scores\\local\\localHighScore" + (i / 2 + 1) + ".local", 0); // write 0 to file
            }
        }

        // set up high score screen
        HS_IV = createHighScoreScreen();

        // set up help screen
        ImageView HELP_IV = getImageView("resources\\art\\help.jpg");

        // arrange objects in window
        BorderPane root = new BorderPane(); // better arrangement style
        root.setPadding(new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
        root.setStyle("-fx-background-color: black");
        root.setTop(MENU.getMenu()); // display titlescreen        

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        board.drawBlocks();
        primaryStage.setTitle("JSnake");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        primaryStage.setOnHidden(event -> {
            pause = true;
            testPanel.setVisible(false);
        });
        primaryStage.setOnShowing(event -> {
            pause = false;
            testPanel.setVisible(true);
        });

        primaryStage.show();

        // Main loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (testPanel.isVisible()) {
                    testPanel.update();
                }
                if (!pause) {
                    frame++;
                    if (frame % 30 == 0) {
                        if (GS.isGame()) {
                            sandboxReset = false;
                        }
                        try {
                            PrintWriter printer = new PrintWriter(settingsLocation, "UTF-8");
                            FileWriter creator = new FileWriter(new File(settingsLocation));
                            int tempSFX = MENU.getSFX() ? 1 : 0, tempNightMode = nightMode ? 1 : 0, tempMusic = MENU.getMusic() ? 1 : 0;
                            printer.print("" + tempSFX + " - SFX toggle (0 for off, 1 for on)");
                            printer.println();
                            printer.print("" + tempNightMode + " - appearance (0 for normal, 1 for night mode)");
                            printer.println();
                            printer.print("" + tempMusic + " - background music toggle (0 for normal, 1 for night mode)");
                            printer.println();
                            printer.close();
                            creator.close();
                        } catch (IOException x) {
                            System.out.println(x.getLocalizedMessage() + " oof.");
                        }
                    }
                    board.setSFX(MENU.getSFX());
                    if (MENU.getMusic()) {
                        menuMusic.unmute();
                    } else {
                        menuMusic.mute();
                    }

                    nightMode = board.getNightTheme();
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
                            // game over - show lose screen and add high scores
                            board.drawBlocks();
                            won = false;
                            if (board.getGrid().getDiffLevel() == 0 && !sandboxReset) {
                                sandboxReset = true;
                                board.resetKeepGrid();
                                //ArrayList<Pair<Integer, Integer>> headPos = board.getGrid().find(1);
                                int[] headPos2 = board.getGrid().getStartPos();
                                board.getGrid().removeAll(1);
                                board.getGrid().removeAll(2);
                                //System.out.println("Number of head and bodies: " + (board.getGrid().countVal(1) + board.getGrid().countVal(2)));
                                //initSandboxFile();

                                //board.getGrid().setDiffLevel(0);
                                //board.getGrid().setPos(headPos.get(0).getKey(), headPos.get(0).getValue());
                                board.getGrid().setPos(headPos2[0], headPos2[1]);
                                board.getGrid().setGrowBy(testPanel.getGrowBy());
                                board.getGrid().setEdgeKills(testPanel.getEdgeKills());
                                board.setToSandboxPlayArea();
                                board.drawBlocks();
                                MM.setCurrent(4);
                            } else if (!scoresOverwritten && board.getGrid().getDiffLevel() != 0) {

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
                            break;

                        case 4:
                            // show the game
                            sandboxReset = false;
                            if (root.getTop() != board.getCanvas() && !GS.isPostGame()) {
                                root.setTop(board.getCanvas());
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
                                    DAWON.play();
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
                }
        );

        scene.setOnMouseDragged(
                (MouseEvent event) -> {
                    board.mouseDragged(event);
                }
        );

        scene.setOnKeyPressed(
                (KeyEvent eventa) -> {
                    if (eventa.getCode() == KeyCode.DIGIT0 && eventa.isShiftDown() && MM.getCurrent() == 0) {
                        initSandboxFile();
                    }
                    board.keyPressed(eventa);
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
     * @param obj
     * @return
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
     * @return
     */
    public static String compileToSandboxFile(boolean edgeKills, int frmSpd, int initialLength, int growBy, int[][] playArea) {
        String s = "" + frmSpd + " - number of frames to wait before waiting (min 1)\n";
        s += initialLength + " - initial snake length\n";
        s += growBy + " - snake grow amt\n";
        s += edgeKills ? 1 : 0;
        s += " - edge kills (0 for false, 1 for true)\n";
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
        Grid tempGrid = new Grid(25, 25, 0, 0);
        tempGrid.setDiffLevel(0);
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
        String temp = s.nextLine();
        while (temp.contains("*")) {
            s.nextLine();
            temp = s.nextLine();
        }

        // begin reading in grid
        int num;
        for (int y = 0; y < 25; y++) {
            for (int x = 0; x < 25; x++) {
                num = s.nextInt();
                if (num == 1) {
                    tempGrid.setPos(x, y);
                }
                sandboxPlayArea[y][x] = num;
            }
            s.nextLine();
        }
        tempGrid.setPlayArea(sandboxPlayArea.clone());
        return tempGrid;
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

    private static void initSandboxFile() {
        try {
            sandbox = new File(SANDBOXLOCATION);
            Scanner reader = new Scanner(sandbox);
            reader.useDelimiter(" ");
            int frmSpd = reader.nextInt();
            board.getGrid().setSandboxFrameSpeed(frmSpd);
            reader.nextLine();
            int initLen = reader.nextInt();
            board.getGrid().setSandboxLen(initLen);
            reader.nextLine();
            int growBy = reader.nextInt();
            board.getGrid().setSandboxGrowBy(growBy);
            reader.nextLine();
            boolean edge = reader.nextInt() == 1;
            board.getGrid().setSandboxEdgeKills(edge);
            reader.nextLine();
            String temp = reader.nextLine();
            while (temp.contains("*")) {
                reader.nextLine();
                temp = reader.nextLine();
            }

            // begin reading in grid
            int num;
            for (int y = 0; y < 25; y++) {
                for (int x = 0; x < 25; x++) {
                    num = reader.nextInt();
                    if (num == 1) {
                        board.getGrid().setSandboxHeadPos(x, y);
                    }
                    sandboxPlayArea[y][x] = num;
                }
                reader.nextLine();
            }
            board.setSandbox(sandboxPlayArea.clone());
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
        int x = 0;
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
                return -1;
            }
        } catch (FileNotFoundException x) {
            //System.out.println("bad file: " + fileName);
            return -1;
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
            int direction = board.getGrid().getDirection();
            int randomizer = 0;
            int x = board.getGrid().getHeadX(), y = board.getGrid().getHeadY();
            int[] nextPos = board.getGrid().nextPos();
            int[] applePos = board.getGrid().getApplePos();
            if (Math.abs(applePos[0] - x) > 0 && randomizer < 5) {
                // apple is not in same column
                if (applePos[0] < x) {
                    board.getGrid().attemptSetDirection(4);
                } else {
                    board.getGrid().attemptSetDirection(2);
                }
                randomizer++;
            } else if (Math.abs(applePos[1] - y) > 0 && randomizer < 10) {
                // apple is in same column but not same row
                if (applePos[1] < y) {
                    board.getGrid().attemptSetDirection(1);
                } else {
                    board.getGrid().attemptSetDirection(3);
                }
            } else {
                randomizer = 0;
                board.getGrid().attemptSetDirection(board.getGrid().getDirection());
                // there is no apple -- it's probably being re-positioned
            }
            if ((x < 1 && direction == 4 || y < 1 && direction == 1) && board.getGrid().getEdgeKills()) {
                board.getGrid().turnRight();
            } else if ((x > board.getGrid().getWidth() - 2 && direction == 2 || y > board.getGrid().getLength() - 2 && direction == 3) && board.getGrid().getEdgeKills()) {
                board.getGrid().turnLeft();
            }

            // update nextPos with new direction
            nextPos = board.getGrid().nextPos();
            // value of the square in front of the snake
            int nextSquare = board.getGrid().safeCheck(nextPos[0], nextPos[1]);
            // if the square in front is not empty or an apple...
            if (nextSquare != 3 && nextSquare > 0) {
                int left = board.getGrid().getEast();
                int right = board.getGrid().getWest();
                int top = board.getGrid().getNorth();
                int bottom = board.getGrid().getSouth();
                // choose the first free direction
                if (top == 0 || top == 3) {
                    board.getGrid().attemptSetDirection(1);
                } else if (right == 0 || right == 3) {
                    board.getGrid().attemptSetDirection(2);
                } else if (left == 0 || left == 3) {
                    board.getGrid().attemptSetDirection(4);
                } else if (bottom > 0 && bottom != 3) {
                    board.getGrid().attemptSetDirection(3);
                } else {
                    // completely surrounded, nothing can be done
                }
            }
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
            PrintWriter printer = new PrintWriter(filename, "UTF-8");
            FileWriter creator = new FileWriter(new File(filename));
            printer.print(Enigma.encode(score));
            printer.close();
            creator.close();
        } catch (Exception x) {
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
