package snake;

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Board {
    
    private final int width;
    private final int height;
    private Grid grid;
    private final Canvas canvas;
    private int outsideMargin = 10;
    private final int margin = 1; // margin between individual squares
    private final int XMARGIN = 15; // margin inside the stackpane
    private final int YMARGIN = 5;
    private final int size = 15;
    private final int borderSize = 2;
    private final int edgeSize = 2;
    private final int gridSize = 25;
    
    private int mouseClicks = 0;

    // colors (day theme)
    private String blank = "74bfb0";
    private String apple = "cc1212";
    private String body = "249b0f";
    private String head = "b76309";
    private String bg = "ceceb5";
    private String rock = "53585e";
    private String applesEaten = "750BE0";
    private String[] portalColors = {"e842f4", "#f44141", "f49a41", "f4e541", "c1f441"};
    
    private boolean lost = false;
    
    private int keyPresses = 0;
    
    private boolean playing = false;

    //menu variables
    private boolean soundOn = true;

    // in order, xPos, yPos, Width, Height
    private final int[] easyButton = {12, 292, 194, 51};
    private final int[] medButton = {219, 292, 194, 51};
    private final int[] hardButton = {12, 353, 194, 51};
    private final int[] impButton = {219, 353, 194, 51};
    private final int[] musicButton = {12, 18, 55, 37};
    private final int[] SFXButton = {83, 18, 28, 37};
    private final int[] helpButton = {13, 255, 47, 22};
    
    private boolean nightTheme = false;
    
    private final MenuManager mm;
    
    private final MainMenu menu;
    
    private boolean sandboxExists = false;
    private int[][] sandbox;
    
    public Board(int w, int h, MenuManager mm, MainMenu menu) {
        this.width = w;
        this.height = h;
        this.mm = mm;
        this.menu = menu;
        canvas = new Canvas(width, height);
        createGrid();
        grid.clearApples();
    }
    
    public void setDarkMode() {
        blank = "444444";
        apple = "E51B39";
        body = "2377DD";
        head = "AF6C00";
        bg = "212121";
        rock = "1e1e1e";
        applesEaten = "EDDDD4";
    }
    
    public void setLightMode() {
        blank = "74bfb0";
        apple = "cc1212";
        body = "249b0f";
        head = "b76309";
        bg = "ceceb5";
        rock = "53585e";
        applesEaten = "750BE0";
    }
    
    public void setOutsideMargin(int amt) {
        this.outsideMargin = amt;
    }
    
    public boolean getShowHelp() {
        return mm.getCurrent() == 2;
    }
    
    public void createGrid() {
        grid = new Grid(gridSize, gridSize, 21, 20);
    }
    
    public boolean getShowHighScores() {
        return mm.getCurrent() == 1;
    }
    
    public boolean getPlaying() {
        return this.playing;
    }
    
    public boolean getShowMenu() {
        return mm.getCurrent() == 0;
    }
    
    public Grid getGrid() {
        return this.grid;
    }
    
    public void setGrid(Grid newGrid) {
        this.grid = newGrid;
    }
    
    private int[] getPixelDimensions() {
        int[] dimensions = {margin * (gridSize - 1) + size * gridSize, margin * (gridSize - 1) + size * gridSize};
        return dimensions;
    }
    
    public boolean getNightTheme() {
        return this.nightTheme;
    }
    
    public void setNightTheme(boolean val) {
        this.nightTheme = val;
        if (val) {
            setDarkMode();
        } else {
            setLightMode();
        }
    }
    
    public void drawBlocks() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        // don't bother drawing the game if the menu is up, it'll just get drawn over
        if (mm.getCurrent() != 0) {

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
                // draw green border indicating that warp mode is on
                gc.setStroke(Color.CHARTREUSE);
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
                    } else if (this.grid.isPortal(x, y)) {
                        temp.setColor(Color.web(portalColors[grid.safeCheck(x, y) - 10]));
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

            // draw apples eaten
            gc.setFill(Color.web(applesEaten));
            gc.setFont(Font.font("Impact", 22));
            gc.fillText("Apples eaten: " + this.getGrid().getApplesEaten(), XMARGIN + width / 2 - 100, YMARGIN + getPixelDimensions()[1] + 22);
            
            if (!this.lost && this.grid.getGameOver()) {
                this.lost = true;
            }

            // we've drawn all the blocks, now if we've lost we need to act on it
            if (this.lost == true) {
            }
        } else {
            
        }
    }
    
    public void reset() {
        keyPresses = 0;
        this.lost = false;
        mm.setCurrent(0);
        this.playing = false;
        createGrid();
        this.grid.setSoundOn(this.soundOn);
    }
    
    public boolean getSFXOn() {
        return this.soundOn;
    }
    
    public void setSFX(boolean val) {
        this.soundOn = val;
        this.grid.setSoundOn(val);
    }
    
    private boolean isDirectional(KeyEvent i) {
        //System.out.println(i.getCode());
        return i.getCode() == KeyCode.UP || i.getCode() == KeyCode.W
                || i.getCode() == KeyCode.DOWN || i.getCode() == KeyCode.S
                || i.getCode() == KeyCode.LEFT || i.getCode() == KeyCode.A
                || i.getCode() == KeyCode.RIGHT || i.getCode() == KeyCode.D;
    }
    
    public void setSandbox(int[][] playArea) {
        sandboxExists = true;
        sandbox = playArea;
    }
    
    public void keyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.R && mm.getCurrent() == 3) {
            reset();
        }
        if (e.getCode() == KeyCode.N) {
            this.nightTheme = !this.nightTheme;
            this.setNightTheme(nightTheme);
        }
        if (e.getCode() == KeyCode.ESCAPE) {
            mm.setCurrent(0);
        }
        
        if (mm.getCurrent() == 0) {
            if (e.getCode() == KeyCode.DIGIT1) {
                // easy mode chosen
                this.grid.setDiffLevel(1);
                mm.setCurrent(4);
            } else if (e.getCode() == KeyCode.DIGIT2) {
                // medium mode chosen
                this.grid.setDiffLevel(2);
                mm.setCurrent(4);
            } else if (e.getCode() == KeyCode.DIGIT3) {
                // hard mode chosen
                this.grid.setDiffLevel(3);
                mm.setCurrent(4);
            } else if (e.getCode() == KeyCode.DIGIT4) {
                // impossible mode chosen
                this.grid.setDiffLevel(4);
                mm.setCurrent(4);
            } else if (e.getCode() == KeyCode.DIGIT0 && e.isShiftDown() && sandboxExists) {
                this.grid.setDiffLevel(0);
                this.grid.setPlayArea(sandbox);
                mm.setCurrent(4);
            }
        }
        if (isDirectional(e) && mm.getCurrent() == 4) {
            keyPresses++;
        }
        if (!this.playing && mm.getCurrent() == 4 && isDirectional(e)) {
            this.playing = true;
        }
        if (e.getCode() == KeyCode.H) {
            if (mm.getCurrent() == 0) {
                mm.setCurrent(1);
            } else if (mm.getCurrent() == 1) {
                mm.setCurrent(0);
            }
        }
        if (e.getCode() == KeyCode.M) {
            toggleMusic();
        }
        if (e.getCode() == KeyCode.X) {
            toggleSFX();
        }
        if (this.lost && (mm.getCurrent() == 3 || mm.getCurrent() == 4) && (e.getCode() == KeyCode.R || e.getCode() == KeyCode.SPACE)) {
            reset();
        }
        if (this.playing && keyPresses > 1) {
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
        } else if (this.playing) {
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
    
    public void toggleMusic() {
        if (menu.getMusic()) {
            menu.turnOffMusic();
        } else {
            menu.turnOnMusic();
        }
        this.soundOn = menu.getMusic();
    }
    
    public void toggleSFX() {
        if (menu.getSFX()) {
            menu.turnOffSFX();
        } else {
            menu.turnOnSFX();
        }
        this.grid.setSoundOn(menu.getSFX());
    }
    
    public void mouseClicked(MouseEvent e) {
        this.mouseClicks++;
        
        double mouseX = e.getX();
        double mouseY = e.getY();
        // account for border outside of canvas
        mouseY -= outsideMargin;
        mouseX -= outsideMargin;
        int mX = (int) mouseX;
        int mY = (int) mouseY;
        
        boolean leftClick = e.isPrimaryButtonDown();
        if (leftClick) {
            // left click

            // menu catching
            if (mm.getCurrent() == 0) {
                if (mX >= easyButton[0] && mY >= easyButton[1] && mX <= easyButton[0] + easyButton[2] && mY <= easyButton[1] + easyButton[3]) {
                    // easy mode chosen
                    this.grid.setDiffLevel(1);
                    mm.setCurrent(4);
                } else if (mX >= medButton[0] && mY >= medButton[1] && mX <= medButton[0] + medButton[2] && mY <= medButton[1] + medButton[3]) {
                    // medium mode chosen
                    this.grid.setDiffLevel(2);
                    mm.setCurrent(4);
                } else if (mX >= hardButton[0] && mY >= hardButton[1] && mX <= hardButton[0] + hardButton[2] && mY <= hardButton[1] + hardButton[3]) {
                    // hard mode chosen
                    this.grid.setDiffLevel(3);
                    mm.setCurrent(4);
                } else if (mX >= impButton[0] && mY >= impButton[1] && mX <= impButton[0] + impButton[2] && mY <= impButton[1] + impButton[3]) {
                    // impossible mode chosen
                    this.grid.setDiffLevel(4);
                    mm.setCurrent(4);
                } else if (mX >= musicButton[0] && mY >= musicButton[1] && mX <= musicButton[0] + musicButton[2] && mY <= musicButton[1] + musicButton[3]) {
                    // toggle music
                    if (menu.getMusic()) {
                        menu.turnOffMusic();
                    } else {
                        menu.turnOnMusic();
                    }
                } else if (mX >= SFXButton[0] && mY >= SFXButton[1] && mX <= SFXButton[0] + SFXButton[2] && mY <= SFXButton[1] + SFXButton[3]) {
                    // toggle sfx
                    if (menu.getSFX()) {
                        menu.turnOffSFX();
                    } else {
                        menu.turnOnSFX();
                    }
                } else if (mX >= helpButton[0] && mY >= helpButton[1] && mX <= helpButton[0] + helpButton[2] && mY <= helpButton[1] + helpButton[3]) {
                    // help screen
                    mm.setCurrent(2);
                    StringSelection tmpSel = new StringSelection("github.com/tfbninja/snake");
                    Clipboard tmpClp = Toolkit.getDefaultToolkit().getSystemClipboard();
                    tmpClp.setContents(tmpSel, null);
                }
            } else if (mm.getCurrent() == 2) {
                mm.setCurrent(0);
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
