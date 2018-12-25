package snake;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Board {

    private int width;
    private int height;
    private Grid grid;
    private Canvas canvas;

    private int margin = 1;
    private final int XMARGIN = 5; // margin inside the stackpane
    private final int YMARGIN = 5;
    private int size = 15;
    private int borderSize = 2;
    private int edgeSize = 2;

    private int mouseClicks = 0;

    private int gridSize = 25;

    private String blank = "74bfb0";
    private String apple = "cc1212";
    private String body = "249b0f";
    private String head = "b76309";
    private String bg = "ceceb5";
    private String rock = "53585e";

    private boolean lost = false;

    private int frame = 0;
    private int keyPresses = 0;

    private boolean playing = false;

    //menu variables
    private boolean showMenu = true;

    // in order, xPos, yPos, Width, Height
    private int[] easyButton = {12, 292, 194, 51};
    private int[] medButton = {219, 292, 194, 51};
    private int[] hardButton = {12, 353, 194, 51};
    private int[] impButton = {219, 353, 194, 51};
    private int edgeMargin = 0;

    // settings variables
    private boolean showSettings = false;

    public Board(int w, int h) {
        this.width = w;
        this.height = h;
        canvas = new Canvas(width, height);
        grid = new Grid(gridSize, gridSize, 21, 20);
        edgeMargin = margin;
        grid.clearApples();
    }

    public boolean getPlaying() {
        return this.playing;
    }

    public boolean getShowMenu() {
        return this.showMenu;
    }

    public Grid getGrid() {
        return this.grid;
    }

    public void setGrid(Grid newGrid) {
        this.grid = newGrid;
    }

    public void setFrame(int amt) {
        this.frame = amt;
    }

    public int[] getPixelDimensions() {
        int[] dimensions = {margin * (gridSize - 1) + size * gridSize, margin * (gridSize - 1) + size * gridSize};
        return dimensions;
    }

    public void drawBlocks() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        // don't bother drawing the game if the menu is up, it'll just get drawn over
        if (!this.showMenu) {

            //clear background
            gc.setFill(Color.web(this.bg));
            gc.fillRect(0, 0, this.width, this.height);

            // draw black border
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(borderSize);
            gc.fillRect(borderSize / 2, borderSize / 2, width - borderSize, height - borderSize);

            if (this.grid.getEdgeKills()) {
                // draw red border indicating that edge kills
                gc.setStroke(Color.CRIMSON.darker());
            } else {
                gc.setStroke(Color.DARKORANGE);
            }
            gc.setLineWidth(edgeSize);
            int pixelSize = gridSize * size + gridSize * margin;
            gc.strokeRect(XMARGIN - edgeSize / 2, YMARGIN - edgeSize / 2, pixelSize + edgeSize - 1, pixelSize + edgeSize - 1);

            //draw squares
            int xPixel = this.XMARGIN;
            for (int x = 0; x < this.grid.getWidth(); x++) {
                int yPixel = this.YMARGIN;
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

            // draw frame number / 30
            gc.setFill(Color.BLUEVIOLET);
            gc.setFont(Font.font("Verdana", 20));
            gc.fillText("Apples eaten: " + this.getGrid().getApplesEaten(), XMARGIN + width / 2 - 100, YMARGIN + getPixelDimensions()[1] + 20
            );

            if (!this.lost && this.grid.getGameOver()) {
                this.lost = true;
            }

            // we've drawn all the blocks, now if we've lost we need to act on it
            if (this.lost == true) {
            }
        } else {

        }
    }

    public void keyPressed(KeyEvent e) {
        keyPresses++;
        if (!this.playing && !this.showMenu) {
            this.playing = true;
        }
        if (keyPresses > 1) {
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
        } else {
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) {
                // user pressed up key
                this.grid.setDirection(1);
            } else if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) {
                // user pressed down key
                this.grid.setDirection(3);
            } else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
                // user pressed left key
                this.grid.setDirection(4);
            } else if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
                // user pressed right key
                this.grid.setDirection(2);
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        this.mouseClicks++;

        double mouseX = e.getX();
        double mouseY = e.getY();
        mouseY += edgeMargin;
        mouseX += edgeMargin;
        int mX = (int) mouseX;
        int mY = (int) mouseY;

        boolean leftClick = e.isPrimaryButtonDown();
        if (leftClick) {
            // left click

            // menu catching
            if (mX >= easyButton[0] && mY >= easyButton[1] && mX <= easyButton[0] + easyButton[2] && mY <= easyButton[1] + easyButton[3]) {
                // easy mode chosen
                this.grid.setDiffLevel(1);
                this.showMenu = false;
            } else if (mX >= medButton[0] && mY >= medButton[1] && mX <= medButton[0] + medButton[2] && mY <= medButton[1] + medButton[3]) {
                // medium mode chosen
                this.grid.setDiffLevel(2);
                this.showMenu = false;
            } else if (mX >= hardButton[0] && mY >= hardButton[1] && mX <= hardButton[0] + hardButton[2] && mY <= hardButton[1] + hardButton[3]) {
                // hard mode chosen
                this.grid.setDiffLevel(3);
                this.showMenu = false;
            } else if (mX >= impButton[0] && mY >= impButton[1] && mX <= impButton[0] + impButton[2] && mY <= impButton[1] + impButton[3]) {
                // impossible mode chosen
                this.grid.setDiffLevel(4);
                this.showMenu = false;
            }
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
