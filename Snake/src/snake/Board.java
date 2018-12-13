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

    //menu variables
    private boolean showMenu = true;
    private int easyX = 25;
    private int easyY = 350;
    private int easyW = 150;
    private int easyH = 75;

    private int medX = 25;
    private int medY = 25;
    private int medW = 10;
    private int medH = 10;

    private int hardX = 50;
    private int hardY = 50;
    private int hardW = 10;
    private int hardH = 10;

    private int impX = 75;
    private int impY = 75;
    private int impW = 10;
    private int impH = 10;

    public Board() {
        width = 600;
        height = 600;
        canvas = new Canvas(width, height);
        //grid = new Grid(gridSize, gridSize, 13, 20);
    }

    public Board(int sizeMultiplier) {
        this.width = getPixelDimensions()[0];
        this.height = getPixelDimensions()[1];
        canvas = new Canvas(width, height);
        grid = new Grid(gridSize * sizeMultiplier, gridSize * sizeMultiplier, 21, 20);
    }
    
    public boolean getShowMenu(){
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
        int[] dimensions = {XMARGIN * 2 + gridSize * size + (gridSize - 1) * margin, YMARGIN * 2 + gridSize * size + (gridSize - 1) * margin};
        return dimensions;
    }

    public void drawBlocks() {
        // don't bother drawing the game if the menu is up, it'll just get drawn over
        if (!this.showMenu) {
            GraphicsContext gc = this.canvas.getGraphicsContext2D();

            //clear background
            gc.setFill(Color.web(this.bg));
            gc.fillRect(0, 0, this.width, this.height);

            // draw black border
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(borderSize);
            gc.strokeRoundRect(borderSize / 2, borderSize / 2, width - borderSize, height - borderSize, 2, 2);

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
            gc.fillText(String.valueOf(frame / 30.0), XMARGIN + getPixelDimensions()[0] / 2, YMARGIN + getPixelDimensions()[1] + 20);

            // we've drawn all the blocks, now if we've lost we need to act on it
            if (this.lost == true) {
                // paint the background over, but add an alpha value so you can still see the mines
                Block transparentCover = new Block(0, 0, this.width, this.height, Color.web(this.bg + "D8"));
                transparentCover.draw(canvas);
                // add code for a text obj...
            }
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

            // menu catching
            if (mX >= easyX && mY >= easyY && mX <= easyX + easyW && easyY <= easyY + easyH) {
                // easy mode chosen
                this.grid.setDiffLevel(1);
                this.showMenu = false;
            } else if (mX >= medX && mY >= medY && mX <= medX + medW && medY <= medY + medH) {
                // medium mode chosen
                this.grid.setDiffLevel(2);
                this.showMenu = false;
            } else if (mX >= hardX && mY >= hardY && mX <= hardX + hardW && hardY <= hardY + hardH) {
                // hard mode chosen
                this.grid.setDiffLevel(3);
                this.showMenu = false;
            } else if (mX >= impX && mY >= impY && mX <= impX + impW && impY <= impY + impH) {
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
