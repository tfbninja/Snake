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
import java.util.ArrayList;
import java.util.Scanner;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author Tim Barber
 */
public class Snake extends Application {

    private final int canvasMargin = 10;
    private final int canvasW = 430;
    private final int canvasH = 430;
    private final int WIDTH = 430 + canvasMargin * 2;
    private final int HEIGHT = 430 + canvasMargin * 2;

    private int frame = 0;

    private final boolean AI = false;

    private static Board board;

    private final Sound menuMusic = new Sound("resources/sounds/menuMusic.wav");
    private static ArrayList<Integer> scores = new ArrayList<>();
    private ImageView HS_IV; // High Score screen stored in an 'ImageView' class

    private boolean scoresOverwritten = false;

    private File settings;
    private final String settingsLocation = "resources/settings.snk";
    private File sandbox;
    private final String sandboxLocation = "resources/sandbox.sandbox";
    private int[][] sandboxPlayArea = new int[25][25];
    private boolean sandboxEdge;
    private Pair<Integer, Integer> sandboxHeadPos;

    private boolean sfxOn = true;
    private boolean musicOn = true;
    private boolean nightMode = false;

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

    @Override
    public void start(Stage primaryStage) {
        // Create Board of block objects
        board = new Board(canvasW, canvasH, MM, MENU);
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
        System.out.println("SFX: " + sfxOn + "\nNight mode: " + nightMode + "\nMusic: " + musicOn);
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
            menuMusic.loop();
            MENU.turnOnMusic();
        } else {
            MENU.turnOffMusic();
        }

        // init sandbox file
        try {
            sandbox = new File(sandboxLocation);
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
                        System.out.println("worked");
                        this.sandboxHeadPos = new Pair<Integer, Integer>(x, y);
                    }
                    sandboxPlayArea[y][x] = num;
                }
                reader.nextLine();
            }
            board.setSandbox(sandboxPlayArea.clone());
            board.getGrid().setSandboxHeadPos(sandboxHeadPos.getKey(), sandboxHeadPos.getValue());
        } catch (FileNotFoundException x) {
            System.out.println("Cannot find sandbox file in " + sandboxLocation + ", try setting the working dir to src/snake.");
        }
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
        primaryStage.show();
        board.getGrid().addPortal(16, 16, 4, 9);

        // Main loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                frame++;
                if (frame % 30 == 0) {
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
                    } catch (Exception x) {
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
                if (MM.getCurrent() == 0 && !board.getGrid().getGameOver()) {
                    // If we're supposed to be showing the menu and we're not already, show it
                    root.setTop(MENU.getMenu());
                } else if (MM.getCurrent() == 1) {
                    root.setTop(HS_IV);
                } else if (MM.getCurrent() == 2) {
                    root.setTop(HELP_IV);
                } else {
                    // If we're supposed to be showing the game graphics and we're not already, show it
                    if (root.getTop() != board.getCanvas() && !board.getGrid().getGameOver()) {
                        root.setTop(board.getCanvas());
                    }

                    if (!board.getGrid().getGameOver()) {
                        if (AI) {
                            AI();
                        }
                        board.drawBlocks();
                        scoresOverwritten = false;
                        if (frame % board.getGrid().getFrameSpeed() == 0) {
                            for (int i = 0; i < board.getGrid().getGensPerFrame(); i++) {
                                if (board.getPlaying()) {
                                    board.getGrid().nextGen();
                                }
                            }
                        }
                    } else {
                        // game over
                        board.drawBlocks();
                        if (board.getGrid().getDiffLevel() == 0) {
                            board.reset();
                            board.getGrid().setDiffLevel(0);
                            board.getGrid().setPlayArea(sandboxPlayArea.clone());
                            board.getGrid().setSandboxEdgeKills(sandboxEdge);
                            board.getGrid().setSandboxHeadPos(sandboxHeadPos.getKey(), sandboxHeadPos.getValue());
                            MM.setCurrent(4);
                        } else if (!scoresOverwritten) {
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
                    }
                }
            }
        }.start();

        // Input handling
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                board.mouseClicked(event);
            }
        });

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent eventa) {
                board.keyPressed(eventa);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static int[] toList(ArrayList<Integer> obj) {
        int[] list = new int[obj.size()];
        int index = 0;
        for (Integer i : obj) {
            list[index] = i;
            index++;
        }
        return list;
    }

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

    public int distanceToPoint(int targetXPos, int targetYPos, int selfX, int selfY) {
        // Manhattan/Taxicab distance
        return Math.abs(selfX - targetXPos) + Math.abs(selfY - targetYPos);
    }

    public static void AI() {
        if (board.getPlaying()) {
            int x = board.getGrid().getHeadX(), y = board.getGrid().getHeadY();
            int[] nextPos = board.getGrid().nextPos();
            int[] applePos = board.getGrid().getApplePos();
            if (Math.abs(applePos[0] - x) > 0) {
                // apple is not in same column
                if (applePos[0] < x) {
                    board.getGrid().attemptSetDirection(4);
                } else {
                    board.getGrid().attemptSetDirection(2);
                }
            } else if (Math.abs(applePos[1] - y) > 0) {
                // apple is in same column but not same row
                if (applePos[1] < y) {
                    board.getGrid().attemptSetDirection(1);
                } else {
                    board.getGrid().attemptSetDirection(3);
                }
            } else {
                // there is no apple -- it's probably being re-positioned

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

    public static boolean checkFileExists(String filename) {
        try {
            Scanner temp = new Scanner(new File(filename));
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

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
