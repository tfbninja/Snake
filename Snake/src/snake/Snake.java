package snake;

import java.applet.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.PrintWriter;
import java.net.*;
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

    private int canvasMargin = 10;
    private int sizeMultiplier = 1;
    private int canvasW = 430;
    private int canvasH = 430;
    private int WIDTH = 430 + canvasMargin * 2;
    private int HEIGHT = 430 + canvasMargin * 2;

    private int frame = 0;

    private static Board board;

    //button constants
    private int bW = 50; // button Width
    private int bH = 25; // button Height

    private Sound menuMusic = new Sound("resources/sounds/menuMusic.wav");
    private static ArrayList<Integer> scores = new ArrayList<>();
    private ImageView HS_IV;

    private boolean scoresOverwritten = false;

    @Override
    public void start(Stage primaryStage) {
        getScores();
        // if local files unreadable, set to 0
        for (int i = 0; i < scores.size(); i += 2) { // loop through local scores
            if (scores.get(i) == -1) { // if bad encode
                scores.set(i, 0); // set score to 0
                //System.out.println("Re-writing local high score " + (i / 2 + 1) + " to 0.");
                writeEncodedScore("resources\\scores\\local\\localHighScore" + (i / 2 + 1) + ".dat", 0); // write 0 to file
            }
        }

        // set up high score screen
        HS_IV = createHighScoreScreen();

        // set up help screen
        ImageView HELP_IV = getImageView("resources\\art\\help.jpg");
        /*
         * for (int i : scores) {
         * System.out.println(i);
         * }
         */

        // Create Board of block objects
        board = new Board(canvasW, canvasH);
        board.setOutsideMargin(canvasMargin);

        // Difficulty Level
        board.getGrid().setDiffLevel(2);

        // background music
        menuMusic.setVolume(0.15);
        menuMusic.loop();

        // set up menu screen with sound on
        ImageView MON_IV = getImageView("resources\\art\\menuOn.jpg");

        // set up menu screen with sound off
        ImageView MOFF_IV = getImageView("resources\\art\\menuOff.jpg");

        // arrange objects in window
        BorderPane root = new BorderPane(); // better arrangement style
        root.setPadding(new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
        root.setStyle("-fx-background-color: black");
        root.setTop(MON_IV); // display titlescreen

        Scene scene = new Scene(root, WIDTH, HEIGHT);

        board.drawBlocks();
        primaryStage.setTitle("JSnake");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Main loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                frame++;
                board.setFrame(frame);
                if (board.getSoundOn()) {
                    menuMusic.unmute();
                } else {
                    menuMusic.mute();
                }
                if (board.getShowMenu() && !board.getGrid().getGameOver()) {
                    // If we're supposed to be showing the menu and we're not already, show it
                    if (board.getSoundOn()) {
                        if (root.getTop() != MON_IV) {
                            root.setTop(MON_IV);
                        }
                    } else {
                        if (root.getTop() != MOFF_IV) {
                            root.setTop(MOFF_IV);
                        }
                    }
                } else if (board.getShowHighScores()) {
                    root.setTop(HS_IV);
                } else if (board.getShowHelp()) {
                    root.setTop(HELP_IV);
                } else {
                    // If we're supposed to be showing the game graphics and we're not already, show it
                    if (root.getTop() != board.getCanvas() && !board.getGrid().getGameOver()) {
                        root.setTop(board.getCanvas());
                    }

                    if (!board.getGrid().getGameOver()) {
                        //AI();
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
                        HS_IV = createHighScoreScreen(); // re-cache high score screen
                        board.drawBlocks();
                        if (!scoresOverwritten) {
                            int thisDifficulty = board.getGrid().getDiffLevel();
                            int thisScore = board.getGrid().getApplesEaten();
                            boolean highScore = thisScore > scores.get((thisDifficulty - 1) * 2) || thisScore > scores.get((thisDifficulty - 1) * 2 + 1);
                            int[] oldScores = toList(scores);

                            if (highScore) {
                                //  (if score is higher than local or world)

                                // write scores to files
                                writeEncodedScore("resources\\scores\\local\\localHighScore" + thisDifficulty + ".dat", thisScore);

                                if (thisScore > scores.get((thisDifficulty - 1) * 2 + 1)) {
                                    if (checkFileExists("resources\\scores\\world\\worldHighScore" + thisDifficulty + ".dat")) {
                                        writeEncodedScore("resources\\scores\\world\\worldHighScore" + thisDifficulty + ".dat", thisScore);
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
            return null;
        }
    }

    public static ImageView createHighScoreScreen() {
        // re-grab scores
        getScores();
        // copy the master image
        copyFile("resources\\art\\HighScoreScreenMaster.png", "resources\\art\\HighScoreScreen.png");
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

    public static void getScores() {
        scores = new ArrayList<>();
        scores.add(readDecodedFile("resources\\scores\\local\\localHighScore1.dat"));
        scores.add(readDecodedFile("resources\\scores\\world\\worldHighScore1.dat"));
        scores.add(readDecodedFile("resources\\scores\\local\\localHighScore2.dat"));
        scores.add(readDecodedFile("resources\\scores\\world\\worldHighScore2.dat"));
        scores.add(readDecodedFile("resources\\scores\\local\\localHighScore3.dat"));
        scores.add(readDecodedFile("resources\\scores\\world\\worldHighScore3.dat"));
        scores.add(readDecodedFile("resources\\scores\\local\\localHighScore4.dat"));
        scores.add(readDecodedFile("resources\\scores\\world\\worldhighScore4.dat"));
    }

    public static int readDecodedFile(String fileName) {
        int decodedScore = -1;
        try {
            File highScore = new File(fileName);
            Scanner reader = new Scanner(highScore);
            reader.useDelimiter("Y33T");
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

    public static void AI() {
        if (board.getPlaying()) {
            int[] nextPos = board.getGrid().nextPos();
            if (board.getGrid().isApple(nextPos[0], nextPos[1])) {
                // about to eat an apple
            }
            while (board.getGrid().isBody(nextPos[0], nextPos[1]) || board.getGrid().isRock(nextPos[0], nextPos[1])) {
                board.getGrid().attemptSetDirection((board.getGrid().getDirection() - 1) % 4 + 1);
            }
            while (board.getGrid().getEdgeKills() && (nextPos[0] >= board.getGrid().getWidth() || nextPos[0] < 0 || nextPos[1] >= board.getGrid().getLength() || nextPos[1] < 0)) {
                board.getGrid().attemptSetDirection((board.getGrid().getDirection() - 1) % 4 + 1);
            }
        } else {
            board.getGrid().setDirection(1);
        }
    }

    public static void playSound(String name) {
        // Taken from https://www.cs.cmu.edu/~illah/CLASSDOCS/javasound.pdf
        try {
            AudioClip clip = Applet.newAudioClip(new URL("file:" + name));
            clip.play();
        } catch (MalformedURLException malURL) {
            System.out.println(malURL);
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
