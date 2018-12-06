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
        grid = new Grid(gridSize, gridSize, numMines);
        this.resetButtonX = width / 2 - buttonW / 2;
        this.toggleX = this.width - this.buttonEdgeSpace - this.buttonW;
        this.buttonY = (int) (this.height * 0.8);
        this.reset = new Block(resetButtonX, buttonY, buttonW, buttonH, Color.web(off));
        this.toggle = new Block(toggleX, buttonY, buttonW, buttonH, Color.web(off));

        this.menuX = width / 2 - buttonW / 2;
        this.menuMiddleY = height / 2 - buttonH / 2;
        this.buttonYSpace = height / 4;
        this.buttonEasy = new Block(menuX, menuMiddleY - buttonYSpace, buttonW, buttonH, Color.web(this.buttonColors[0]));
        this.buttonMed = new Block(menuX, menuMiddleY, buttonW, buttonH, Color.web(this.buttonColors[1]));
        this.buttonHard = new Block(menuX, menuMiddleY + buttonYSpace, buttonW, buttonH, Color.web(this.buttonColors[2]));

    }

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        canvas = new Canvas(width, height);
        grid = new Grid(gridSize, gridSize, numMines);
        this.grid.fillMines("Filled board constructor");
        this.resetButtonX = width / 2 - buttonW / 2;
        this.toggleX = this.width - this.buttonEdgeSpace - this.buttonW;
        this.buttonY = (this.height - (this.height - (10 + (this.gridSize * this.size) + (this.margin * (this.gridSize - 1)))) / 2) - this.buttonH / 2;
        this.reset = new Block(resetButtonX, buttonY, (int) (buttonW), buttonH, Color.web(off));
        this.toggle = new Block(toggleX, buttonY, buttonW, buttonH, Color.web(off));
        this.grid.savePlayArea();

        this.menuX = width / 2 - buttonW / 2;
        this.menuMiddleY = height / 2 - buttonH / 2;
        this.buttonYSpace = height / 4;
        this.buttonEasy = new Block(menuX, menuMiddleY - buttonYSpace, buttonW, buttonH, Color.web(this.buttonColors[0]));
        this.buttonMed = new Block(menuX, menuMiddleY, buttonW, buttonH, Color.web(this.buttonColors[1]));
        this.buttonHard = new Block(menuX, menuMiddleY + buttonYSpace, buttonW, buttonH, Color.web(this.buttonColors[2]));
    }

    public Grid getGrid() {
        return this.grid;
    }

    public void setGrid(Grid newGrid) {
        this.grid = newGrid;
    }

    public void setNumMines(double mineDensity) {
        this.numMines = (int) (this.gridSize * this.gridSize * mineDensity);
    }

    public void drawBlocks() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        if (this.showMenu) {
            gc.setFill(Color.web(this.bg));
            gc.fillRect(0, 0, width, height);
            int radius = 15;
            this.buttonEasy.drawRounded(canvas, radius);
            this.buttonMed.drawRounded(canvas, radius);
            this.buttonHard.drawRounded(canvas, radius);

            gc.setFill(Color.web(this.buttonTextColor));
            gc.setFont(Font.font("Verdana", 20));
            gc.fillText("EASY", this.menuX + this.buttonTextXAdjust + easyAndHardXAdjust + 2, this.menuMiddleY - this.buttonYSpace + this.buttonTextYAdjust);
            gc.fillText("MEDIUM", this.menuX + this.buttonTextXAdjust - 1, this.menuMiddleY + this.buttonTextYAdjust);
            gc.fillText("HARD", this.menuX + this.buttonTextXAdjust + easyAndHardXAdjust - 1, this.menuMiddleY + this.buttonYSpace + this.buttonTextYAdjust);

        } else {
            gc.setFill(Color.web(this.bg));
            gc.fillRect(0, 0, this.width, this.height);
            int xPixel = this.xPos;
            for (int x = 0; x < this.grid.getWidth(); x++) {
                int yPixel = this.yPos;
                for (int y = 0; y < this.grid.getLength(); y++) {
                    Block temp = new Block();
                    if (this.grid.isFlagged(x, y)) {
                        temp.setColor(Color.web(this.flagged)); // orangish/reddish
                    } else if (this.grid.isUntouched(x, y)) {
                        temp.setColor(Color.web(this.blank)); // grey
                    } else if (this.grid.isDetonated(x, y)) {
                        temp.setColor(Color.web(this.mine)); // red
                        this.lost = true;
                        this.grid.stopTimer();
                    } else if (this.grid.
                            isClicked(x, y)) {
                        temp.setColor(Color.web(this.clicked)); // light blue
                    } else { // there's a problem
                        //System.out.println(this.grid.getCell(x, y));
                        temp.setColor(Color.BLUEVIOLET);
                    }
                    temp.setX(xPixel);
                    temp.setY(yPixel);
                    temp.setWidth(size);
                    temp.setHeight(size);
                    temp.draw(canvas);
                    if (this.grid.isClicked(x, y)) {
                        int neighbors = this.grid.getNeighbors(x, y);
                        if (neighbors > 0) {
                            if (neighbors < 6) {
                                gc.setFill(Color.web(this.neighborColor[neighbors]));
                            } else {
                                gc.setFill(Color.web(this.neighborColor[5]));
                            }
                            gc.setFont(Font.font("Courier", FontWeight.BOLD, 15));
                            gc.fillText(String.valueOf(neighbors), xPixel + 4, yPixel + 13);
                        }
                    }
                    yPixel += margin + size;
                }
                xPixel += margin + size;
            }

            // we've drawn all the blocks, now too check if we've lost,
            // as we'll have to redraw the unclicked mines
            if (this.lost == true) {
                int xMinePixel = this.xPos;
                for (int mineX = 0; mineX < this.grid.getWidth(); mineX++) {
                    int yMinePixel = this.yPos;
                    for (int mineY = 0; mineY < this.grid.getLength(); mineY++) {
                        Block tempMine = new Block();
                        if (this.grid.isInactiveMine(mineX, mineY)) {
                            tempMine.setColor(Color.web(this.mine).brighter());
                            tempMine.setPos(xMinePixel, yMinePixel);
                            tempMine.setWidth(this.size);
                            tempMine.setHeight(this.size);
                            tempMine.draw(canvas);
                        }
                        yMinePixel += margin + size;
                    }
                    xMinePixel += margin + size;
                    yMinePixel = this.yPos;
                }
                // paint the background over, but add an alpha value so you can still see the mines
                Block transparentCover = new Block(0, 0, this.width, this.height, Color.web(this.bg + "D8"));
                transparentCover.draw(canvas);
                gc.setFill(Color.RED);
                gc.setFont(Font.font("Impact", FontWeight.SEMI_BOLD, 75));
                gc.fillText("   YOU\n  LOST", this.width / 2 - 100, this.height / 2 - 50);
            }
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
        this.lastMC[0] = mX;
        this.lastMC[1] = mY;

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
