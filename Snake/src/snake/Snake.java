package snake;

import java.applet.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.*;
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
import javafx.stage.Stage;

/**
 *
 * @author Tim Barber
 */
public class Snake extends Application {

    private int canvasMargin = 10;
    private int sizeMultiplier = 1;
    private int WIDTH = 430 + canvasMargin * 2;
    private int HEIGHT = 430 + canvasMargin * 2;
    private int canvasW = 430;
    private int canvasH = 430;

    private int frame = 0;

    private static Board board;

    //button constants
    private int bW = 50; // button Width
    private int bH = 25; // button Height

    private Sound menuMusic = new Sound("menuMusic.wav");

    private FileInputStream menuStream;
    private FileInputStream loseScreenStream;

    @Override
    public void start(Stage primaryStage) {

        // Create Board of block objects
        board = new Board(canvasW, canvasH);

        // Difficulty Level
        board.getGrid().setDiffLevel(2);

        // background music
        menuMusic.setVolume(0.15);
        menuMusic.playMP3();

        // set up menu screen
        try {
            menuStream = new FileInputStream("menu.jpg");
        } catch (FileNotFoundException f) {
            System.out.println("oof");
        }
        Image image = new Image(menuStream);
        ImageView iv1 = new ImageView();
        iv1.setImage(image);
        iv1.setPreserveRatio(true);
        iv1.setSmooth(true);
        iv1.setCache(true);

        // set up lose screen
        try {
            loseScreenStream = new FileInputStream("art\\loseScreen.png");
        } catch (FileNotFoundException f) {
            System.out.println("big oof");
        }
        Image loseScreen = new Image(loseScreenStream);
        ImageView iv2 = new ImageView();
        iv2.setImage(loseScreen);
        iv2.setPreserveRatio(true);
        iv2.setSmooth(true);
        iv2.setCache(true);

        // arrange objects in window
        BorderPane root = new BorderPane(); // better arrangement style
        root.setPadding(new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
        root.setStyle("-fx-background-color: black");
        root.setTop(iv1); // display titlescreen

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

                if (board.getShowMenu() && !board.getGrid().getGameOver()) {
                    // If we're supposed to be showing the menu and we're not already, show it
                    if (root.getTop() != iv1) {
                        root.setTop(iv1);
                    }
                } else {
                    // If we're supposed to be showing the game graphics and we're not already, show it
                    if (root.getTop() != board.getCanvas() && !board.getGrid().getGameOver()) {
                        root.setTop(board.getCanvas());
                    }

                    if (!board.getGrid().getGameOver()) {
                        //AI();
                        board.drawBlocks();
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
                        root.setTop(iv2);
                    }
                }
            }
        }.start();

        // Input handling
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                board.mouseClicked(event);
                //playSound("warp.wav");
            }
        }
        );

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
