package snake;

import java.util.Scanner;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class Board {

    private int width;
    private int height;
    private Grid grid;
    private Canvas canvas;

    private int margin = 2;
    private int xPos = 10; // starting values for grid position on screen
    private int size = 15;
    private int yPos = 10;
    private int mouseClicks = 0;

    private int gridSize = 25;

    private int resetButtonX;
    private int toggleX;
    private int buttonEdgeSpace = 3;
    private int buttonY;
    private int buttonW = 120;
    private int buttonH = 30;

    private String blank = "74bfb0";
    private String apple = "cc1212";
    private String body = "249b0f";
    private String head = "b76309";
    private String bg = "ceceb5";

    private Block reset;
    private Block toggle;
    private boolean lost = false;

    public Board() {
        width = 600;
        height = 600;
        canvas = new Canvas(width, height);
        grid = new Grid(gridSize, gridSize, 13, 20);
    }

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        canvas = new Canvas(width, height);
        grid = new Grid(gridSize, gridSize, 13, 20);
        this.grid.savePlayArea();
    }

    public Grid getGrid() {
        return this.grid;
    }

    public void setGrid(Grid newGrid) {
        this.grid = newGrid;
    }

    public void drawBlocks() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        gc.setFill(Color.web(this.bg));
        gc.fillRect(0, 0, this.width, this.height);
        int xPixel = this.xPos;
        for (int x = 0; x < this.grid.getWidth(); x++) {
            int yPixel = this.yPos;
            for (int y = 0; y < this.grid.getLength(); y++) {
                Block temp = new Block();
                if (this.grid.isApple(x, y)) {
                    temp.setColor(Color.web(this.apple)); // red
                } else if (this.grid.isBody(x, y)) {
                    temp.setColor(Color.web(this.body)); // green
                } else if (this.grid.isHead(x, y)) {
                    temp.setColor(Color.web(this.head)); // brown
                } else if (this.grid.isBlank(x, y)) {
                    temp.setColor(Color.web(this.blank)); // light blue
                } else { // there's a problem
                    //System.out.println(this.grid.getCell(x, y));
                    temp.setColor(Color.BLUEVIOLET);
                }
                temp.setX(xPixel);
                temp.setY(yPixel);
                temp.setWidth(size);
                temp.setHeight(size);
                temp.draw(canvas);
            }
        }

        // we've drawn all the blocks, now too check if we've lost,
        if (this.lost == true) {
            // paint the background over, but add an alpha value so you can still see the mines
            Block transparentCover = new Block(0, 0, this.width, this.height, Color.web(this.bg + "D8"));
            transparentCover.draw(canvas);
            // add code for a text obj...
        }
    }

    public void keyPressed(KeyEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
        this.mouseClicks++;

        double mouseX = e.getX();
        double mouseY = e.getY();
        int mX = (int) mouseX;
        int mY = (int) mouseY;

        boolean leftClick = e.isPrimaryButtonDown();
        if (leftClick) {
            // left click
        } else if (e.isSecondaryButtonDown()) {
            // right click
        } else {
            // middle button
        }
    }

    public void mouseMoved(MouseEvent e) {
        double mouseX = e.getX();
        double mouseY = e.getY();
        int mX = (int) mouseX;
        int mY = (int) mouseY;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public String toString() {
        return "";
    }
}
