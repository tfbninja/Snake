package snake;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
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
    private int sizeMulitiplier = 1;
    private int WIDTH = 500 * sizeMulitiplier;
    private int HEIGHT = 550 * sizeMulitiplier;

    private int frame = 0;

    //button constants
    private int bW = 50; // button Width
    private int bH = 25; // button Height

    private int resetX = 100; // reset button x position
    private int resetY = 100; // reset button y position

    @Override
    public void start(Stage primaryStage) {

        // Create Board of block objects
        Board board = new Board(sizeMulitiplier);

        // Difficulty Level
        board.getGrid().setDiffLevel(1);

        BorderPane root = new BorderPane(); // better arrangement style
        root.setPadding(new Insets(canvasMargin, canvasMargin, 10, 10));
        root.setTop(board.getCanvas());

        Scene scene = new Scene(root, WIDTH, HEIGHT);

        board.drawBlocks();
        primaryStage.setTitle("Snake");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Main loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                frame++;
                if (board.getGrid().getGameOver() == false) {
                    board.drawBlocks();
                    if (frame % board.getGrid().getFrameSpeed() == 0) {
                        for (int i = 0; i < board.getGrid().getGensPerFrame(); i++) {
                            board.getGrid().nextGen();
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
