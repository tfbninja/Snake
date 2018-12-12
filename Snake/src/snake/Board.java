package snake;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Board {

    private int width;
    private int height;
    private Grid grid;
    private Canvas canvas;

    private int margin = 1;
    private int xPos = 10; // starting values for grid position on screen
    private int size = 15;
    private int yPos = 10;
    private int mouseClicks = 0;

    private int gridSize = 25;

    private String blank = "74bfb0";
    private String apple = "cc1212";
    private String body = "249b0f";
    private String head = "b76309";
    private String bg = "ceceb5";
    private String rock = "53585e";

    private boolean lost = false;

    public Board() {
        width = 600;
        height = 600;
        canvas = new Canvas(width, height);
        //grid = new Grid(gridSize, gridSize, 13, 20);
    }

    public Board(int width, int height, int sizeMultiplier) {
        this.width = width;
        this.height = height;
        canvas = new Canvas(width, height);
        grid = new Grid(gridSize * sizeMultiplier, gridSize * sizeMultiplier, 21, 20);
    }

    public Grid getGrid() {
        return this.grid;
    }

    public void setGrid(Grid newGrid) {
        this.grid = newGrid;
    }

    public int[] getPixelDimensions() {
        int[] dimensions = {gridSize * size + (gridSize - 1) * margin, gridSize * size + (gridSize - 1) * margin};
        return dimensions;
    }

    public void drawBlocks() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        //clear background
        gc.setFill(Color.web(this.bg));
        gc.fillRect(0, 0, this.width, this.height);
        if (this.grid.getEdgeKills()) {
            gc.setStroke(Color.CRIMSON);
            gc.setLineWidth(5);
            gc.strokeRect(xPos - 5, yPos - 5, xPos + getPixelDimensions()[0], yPos + getPixelDimensions()[1]);
        }

        //draw squares
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
                } else if (this.grid.isRock(x, y)) {
                    temp.setColor(Color.web(this.rock));
                } else { // there's a problem
                    //System.out.println(this.grid.getCell(x, y));
                    temp.setColor(Color.BLUEVIOLET);
                }
                temp.setX(xPixel);
                temp.setY(yPixel);
                temp.setWidth(size);
                temp.setHeight(size);
                temp.draw(canvas);
                yPixel += margin + size;
            }
            xPixel += margin + size;
        }

        // we've drawn all the blocks, now if we've lost we need to act on it
        if (this.lost == true) {
            // paint the background over, but add an alpha value so you can still see the mines
            Block transparentCover = new Block(0, 0, this.width, this.height, Color.web(this.bg + "D8"));
            transparentCover.draw(canvas);
            // add code for a text obj...
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) {
            // user pressed up key
            this.grid.attemptSetDirection(1);
        } else if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) {
            // user pressed down key
            this.grid.attemptSetDirection(3);
        } else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
            // user pressed left key
            this.grid.attemptSetDirection(4);
        } else if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
            // user pressed right key
            this.grid.attemptSetDirection(2);
        }
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
