package snake;

//<editor-fold defaultstate="collapsed" desc="imports">
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.swing.JFrame;
import javafx.util.Pair;
import javafx.scene.text.Font;
//</editor-fold>

/**
 *
 * @author Timothy
 */
public class Board {

    //<editor-fold defaultstate="collapsed" desc="instance vars">
    private final int width;
    private final int height;
    private Grid grid;
    private final Canvas canvas;
    private int outsideMargin = 10;
    private final int margin = 1;
    private final int XMARGIN = 15;
    private final int YMARGIN = 5;
    private final int size = 15;
    private final int borderSize = 2;
    private final int edgeSize = 2;
    private final int GRIDSIZE = 25;

    private int mouseClicks = 0;

    // colors (day theme)
    private String blank = "74bfb0";
    private String apple = "cc1212";
    private String body = "249b0f";
    private String head = "b76309";
    private String bg = "ceceb5";
    private String rock = "53585e";
    private String applesEaten = "750BE0";
    private final String[] portalColors = {"90094E", "550C74", "dfb708", "ef5658", "bb3dff"};

    private boolean lost = false;

    private int keyPresses = 0;

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

    private final MenuManager MM;
    private final MainMenu MENU;
    private final GameState GS;

    private boolean sandboxExists = false;
    private int[][] sandbox;

    private ToolPanel toolPanel;
    private final Stage primaryStage;
    private JFrame toolFrame;
//</editor-fold>

    /**
     *
     * @param w
     * @param h
     * @param mm
     * @param menu
     * @param gs
     * @param primary
     */
    public Board(int w, int h, MenuManager mm, MainMenu menu, GameState gs, Stage primary) {
        this.width = w;
        this.height = h;
        this.MM = mm;
        this.MENU = menu;
        this.GS = gs;
        canvas = new Canvas(width, height);
        createGrid();
        grid.addGameState(GS);
        grid.clearApples();
        primaryStage = primary;
    }

    /**
     *
     */
    public void setDarkMode() {
        blank = "444444";
        apple = "E51B39";
        body = "2377DD";
        head = "AF6C00";
        bg = "212121";
        rock = "1e1e1e";
        applesEaten = "EDDDD4";
        toolPanel.updateButtonColors(getColorScheme());
    }

    /**
     *
     * @return
     */
    public String[] getColorScheme() {
        String[] colorScheme = {blank, head, apple, rock, portalColors[0], bg};
        return colorScheme;
    }

    /**
     *
     */
    public void setLightMode() {
        blank = "74bfb0";
        apple = "cc1212";
        body = "249b0f";
        head = "b76309";
        bg = "ceceb5";
        rock = "53585e";
        applesEaten = "750BE0";
        toolPanel.updateButtonColors(getColorScheme());
    }

    /**
     *
     * @param amt
     */
    public void setOutsideMargin(int amt) {
        this.outsideMargin = amt;
    }

    /**
     *
     */
    public void createGrid() {
        grid = new Grid(GRIDSIZE, GRIDSIZE, 21, 20);
        grid.addGameState(GS);
    }

    /**
     *
     * @return
     */
    public Grid getGrid() {
        return this.grid;
    }

    /**
     *
     * @param newGrid
     */
    public void setGrid(Grid newGrid) {
        this.grid = newGrid;
    }

    private int[] getPixelDimensions() {
        int[] dimensions = {margin * (GRIDSIZE - 1) + size * GRIDSIZE, margin * (GRIDSIZE - 1) + size * GRIDSIZE};
        return dimensions;
    }

    /**
     *
     * @return
     */
    public boolean getNightTheme() {
        return this.nightTheme;
    }

    /**
     *
     * @param val
     */
    public void setNightTheme(boolean val) {
        this.nightTheme = val;
        if (val) {
            setDarkMode();
        } else {
            setLightMode();
        }
    }

    /**
     * draws the blox
     */
    public void drawBlocks() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        // don't bother drawing the game if the menu is up, it'll just get drawn over
        if (MM.getCurrent() != 0) {

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
            int pixelSize = GRIDSIZE * size + GRIDSIZE * margin;
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
                        temp.setColor(Color.web(this.rock)); // gray
                    } else if (this.grid.isPortal(x, y) && grid.find(grid.safeCheck(x, y)).size() == 2) {
                        temp.setColor(Color.web(portalColors[(grid.safeCheck(x, y) - 10) % this.portalColors.length]));
                    } else { // unmatched portal
                        temp.setColor(Color.BLACK);
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

            if (!this.lost && GS.isPostGame()) {
                this.lost = true;
            }

            // we've drawn all the blocks, now if we've lost we need to act on it
            if (this.lost == true) {
                GS.setToPostGame();
            }
        } else {

        }
    }

    /**
     *
     */
    public void reset() {
        keyPresses = 0;
        this.lost = false;
        MM.setCurrent(0);
        GS.setToPreGame();
        this.grid.setSoundOn(this.soundOn);
        //createGrid();
        grid.reset();
        grid.clear();
        grid.safeSetCell(21, 20, 1);
        grid.setPos(21, 20);
    }

    /**
     *
     */
    public void setToSandboxPlayArea() {
        grid.setPlayArea(this.sandbox);
    }

    /**
     *
     */
    public void resetKeepGrid() {
        grid.reset();
        keyPresses = 0;
        this.lost = false;
        MM.setCurrent(4);
        GS.setToPreGame();
        this.grid.setSoundOn(this.soundOn);
    }

    /**
     *
     * @return
     */
    public boolean getSFXOn() {
        return this.soundOn;
    }

    /**
     *
     * @param val
     */
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

    /**
     *
     * @param playArea
     */
    /*
     * public void setSandbox(int[][] playArea) {
     * sandboxExists = true;
     * sandbox = playArea;
     * grid.setSandbox(playArea);
     * }
     *
     */
    /**
     *
     * @param e
     */
    public void keyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.R && MM.getCurrent() == 3) {
            reset();
        }
        if (e.getCode() == KeyCode.N) {
            this.nightTheme = !this.nightTheme;
            this.setNightTheme(nightTheme);
        }
        if (e.getCode() == KeyCode.ESCAPE) {
            reset();
            MM.setCurrent(0);
            toolFrame.setVisible(false);
        }

        if (e.getCode() == KeyCode.EQUALS && e.isShiftDown()) {
            System.out.println(grid.exportCode());
        }

        if (MM.getCurrent() == 4 && grid.getDiffLevel() == 0) {
            if (null != e.getCode()) {
                switch (e.getCode()) {
                    case DIGIT0:
                        toolPanel.setCurrentTool(0);
                        break;
                    case DIGIT1:
                        toolPanel.setCurrentTool(1);
                        break;
                    case DIGIT2:
                        toolPanel.setCurrentTool(2);
                        break;
                    case DIGIT3:
                        toolPanel.setCurrentTool(3);
                        break;
                    case DIGIT4:
                        toolPanel.setCurrentTool(4);
                        break;
                    default:
                        break;
                }
            }
        }

        if (MM.getCurrent() == 0) {
            if (e.getCode() == KeyCode.DIGIT1) {
                // easy mode chosen
                grid.setDiffLevel(1);
                MM.setCurrent(4);
                GS.setToPreGame();
            } else if (e.getCode() == KeyCode.DIGIT2) {
                // medium mode chosen
                this.grid.setDiffLevel(2);
                MM.setCurrent(4);
                GS.setToPreGame();
            } else if (e.getCode() == KeyCode.DIGIT3) {
                // hard mode chosen
                this.grid.setDiffLevel(3);
                GS.setToPreGame();
                MM.setCurrent(4);
            } else if (e.getCode() == KeyCode.DIGIT4) {
                // impossible mode chosen
                this.grid.setDiffLevel(4);
                GS.setToPreGame();
                MM.setCurrent(4);
            } else if (e.getCode() == KeyCode.DIGIT0 && e.isShiftDown()) {
                Snake.initSandboxFile();
                toolFrame.setVisible(true);
                toolFrame.requestFocus(); // bring this to front
                toolPanel.setCurrentTool(0);
                toolPanel.setGrid(grid);
                toolPanel.updateControls();
                primaryStage.requestFocus(); // but we want this one in focus still
                MM.setCurrent(4);
                GS.setToPreGame();
                drawBlocks();
            }
        }
        if (isDirectional(e) && MM.getCurrent() == 4) {
            keyPresses++;
        }
        if (GS.isPreGame() && MM.getCurrent() == 4 && isDirectional(e)) {
            grid.setApplesEaten(0);
            grid.resetSize();
            if (grid.containsUnmatchedPortal() > -1) {
                // can't play with unmatched portals
                Toolkit.getDefaultToolkit().beep();
            } else {
                if (grid.getDiffLevel() == 0) {
                    sandbox = grid.getPlayArea();
                }
                grid.setApples();
                GS.setToGame();
            }
        }
        if (e.getCode() == KeyCode.H) {
            if (MM.getCurrent() == 0) {
                MM.setCurrent(1);
            } else if (MM.getCurrent() == 1) {
                MM.setCurrent(0);
            }
        }
        if (e.getCode() == KeyCode.M) {
            toggleMusic();
        }
        if (e.getCode() == KeyCode.X) {
            toggleSFX();
        }
        if (this.lost && (MM.getCurrent() == 3 || MM.getCurrent() == 4) && (e.getCode() == KeyCode.R || e.getCode() == KeyCode.SPACE)) {
            reset();
        }
        if (GS.isGame() && keyPresses > 1) {
            if (null != e.getCode()) {
                switch (e.getCode()) {
                    case UP:
                    case W:
                        // user pressed up key
                        this.grid.attemptSetDirection(1);
                        break;
                    case DOWN:
                    case S:
                        // user pressed down key
                        this.grid.attemptSetDirection(3);
                        break;
                    case LEFT:
                    case A:
                        // user pressed left key
                        this.grid.attemptSetDirection(4);
                        break;
                    case RIGHT:
                    case D:
                        // user pressed right key
                        this.grid.attemptSetDirection(2);
                        break;
                    default:
                        break;
                }
            }
        } else if (GS.isGame()) {
            if (null != e.getCode()) {
                switch (e.getCode()) {
                    case UP:
                    case W:
                        // user pressed up key
                        this.grid.setDirection(1);
                        break;
                    case DOWN:
                    case S:
                        // user pressed down key
                        this.grid.setDirection(3);
                        break;
                    case LEFT:
                    case A:
                        // user pressed left key
                        this.grid.setDirection(4);
                        break;
                    case RIGHT:
                    case D:
                        // user pressed right key
                        this.grid.setDirection(2);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     *
     */
    public void toggleMusic() {
        if (MENU.getMusic()) {
            MENU.turnOffMusic();
        } else {
            MENU.turnOnMusic();
        }
        this.soundOn = MENU.getMusic();
    }

    /**
     *
     */
    public void toggleSFX() {
        if (MENU.getSFX()) {
            MENU.turnOffSFX();
        } else {
            MENU.turnOnSFX();
        }
        this.grid.setSoundOn(MENU.getSFX());
    }

    /**
     *
     * @return
     */
    public int findUnusedPortalNum() {
        int num = 10;
        while (grid.find(num).size() > 1) {
            num++;
        }
        return num;
    }

    /**
     *
     * @param AWTTool
     * @return
     */
    public int AWTToolToRealTool(int AWTTool) {
        switch (AWTTool) {
            case 0:
                return 0;
            case 1:
                // apple
                return 3;
            case 2:
                // head
                return 1;
            case 3:
                return 4;
            case 4:
                return 5;
            default:
                return AWTTool;
        }
    }

    /**
     *
     * @param panel
     */
    public void addToolPanel(ToolPanel panel) {
        toolPanel = panel;
    }

    /**
     *
     * @param frame
     */
    public void addToolFrame(JFrame frame) {
        toolFrame = frame;
    }

    /**
     *
     * @param e
     */
    public void mouseDragged(MouseEvent e) {
        double mouseX = e.getX();
        double mouseY = e.getY();
        // account for border outside of canvas
        mouseY -= outsideMargin;
        mouseX -= outsideMargin;
        int mX = (int) mouseX;
        int mY = (int) mouseY;
        // top right:
        // margin * x + xPos + (size * (x-1)) : += size
        //solve:
        //margin * (x+1)) + (size * (x-1)) = z, z = margin * x + xPos + margin + size * x - size, z = x(margin + size) + xPos + margin - size, (z + size - margin)/(margin + size) = x
        int xVal = (mX + size - XMARGIN) / (margin + size) - 1;
        int yVal = (mY + size - YMARGIN) / (margin + size) - 1;
        //xVal %= this.gridSize;
        //yVal %= this.gridSize;

        boolean leftClick = e.isPrimaryButtonDown();
        boolean rightClick = e.isSecondaryButtonDown();
        if (rightClick) {
            try {
                grid.setCell(xVal, yVal, 0);
            } catch (ArrayIndexOutOfBoundsException x) {
                return;
            }
            return;
        }

        if (leftClick) {
            // left click

            // sandbox mode editing
            if (MM.getCurrent() == 4 && grid.getDiffLevel() == 0 && xVal >= 0 && xVal < grid.getWidth() && yVal >= 0 && yVal < grid.getLength()) {

                int tool = toolPanel.getCurrentTool();

                switch (tool) {
                    case 4:
                    case 3:
                    case 0:
                        grid.setCell(xVal, yVal, tool);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     *
     * @param e
     */
    public void mouseClicked(MouseEvent e) {
        this.mouseClicks++;

        double mouseX = e.getX();
        double mouseY = e.getY();
        // account for border outside of canvas
        mouseY -= outsideMargin;
        mouseX -= outsideMargin;
        int mX = (int) mouseX;
        int mY = (int) mouseY;
        // top right:
        // margin * x + xPos + (size * (x-1)) : += size
        //solve:
        //margin * (x+1)) + (size * (x-1)) = z, z = margin * x + xPos + margin + size * x - size, z = x(margin + size) + xPos + margin - size, (z + size - margin)/(margin + size) = x
        int xVal = (mX + size - XMARGIN) / (margin + size) - 1;
        int yVal = (mY + size - YMARGIN) / (margin + size) - 1;
        //xVal %= this.gridSize;
        //yVal %= this.gridSize;

        boolean leftClick = e.isPrimaryButtonDown();
        if (leftClick) {
            // left click

            // sandbox mode editing
            if (MM.getCurrent() == 4 && grid.getDiffLevel() == 0 && xVal >= 0 && xVal < grid.getWidth() && yVal >= 0 && yVal < grid.getLength()) {
                int tool = toolPanel.getCurrentTool();
                toolPanel.hideSaved();
                switch (tool) {
                    case 1:
                        // tell the grid where the head is
                        grid.setSandboxHeadPos(xVal, yVal);
                        grid.setPos(xVal, yVal);
                        grid.removeAll(1);
                    case 3:
                        // apple
                        grid.setCell(xVal, yVal, tool);
                        break;
                    case 5:
                        if (grid.containsUnmatchedPortal() == -1) {
                            //System.out.println("no open portals");
                            tool = findUnusedPortalNum();
                            grid.setCell(xVal, yVal, tool);
                        } else {
                            // get an unused number for a new portal
                            int newPortalNum = grid.safeCheck(grid.findUnmatchedPortal());
                            // get the location of an unmatched portal
                            Pair<Integer, Integer> tempPos = grid.findUnmatchedPortal();
                            // set that old unmatched portal to... it's current value...hmmm
                            //grid.setCell(tempPos, newPortalNum);
                            //System.out.println("open portal at " + tempPos);
                            // set the clicked square to the value of the unmatched portal
                            grid.setCell(xVal, yVal, newPortalNum);
                        }
                        break;
                    default:
                        // possibly not the best programming technique.... lmao
                        grid.setCell(xVal, yVal, tool);
                        break;
                }
            }

            // menu catching
            if (MM.getCurrent() == 0) {
                if (mX >= easyButton[0] && mY >= easyButton[1] && mX <= easyButton[0] + easyButton[2] && mY <= easyButton[1] + easyButton[3]) {
                    // easy mode chosen
                    this.grid.setDiffLevel(1);
                    MM.setCurrent(4);
                    GS.setToPreGame();
                } else if (mX >= medButton[0] && mY >= medButton[1] && mX <= medButton[0] + medButton[2] && mY <= medButton[1] + medButton[3]) {
                    // medium mode chosen
                    this.grid.setDiffLevel(2);
                    MM.setCurrent(4);
                    GS.setToPreGame();
                } else if (mX >= hardButton[0] && mY >= hardButton[1] && mX <= hardButton[0] + hardButton[2] && mY <= hardButton[1] + hardButton[3]) {
                    // hard mode chosen
                    this.grid.setDiffLevel(3);
                    MM.setCurrent(4);
                    GS.setToPreGame();
                } else if (mX >= impButton[0] && mY >= impButton[1] && mX <= impButton[0] + impButton[2] && mY <= impButton[1] + impButton[3]) {
                    // impossible mode chosen
                    this.grid.setDiffLevel(4);
                    MM.setCurrent(4);
                    GS.setToPreGame();
                } else if (mX >= musicButton[0] && mY >= musicButton[1] && mX <= musicButton[0] + musicButton[2] && mY <= musicButton[1] + musicButton[3]) {
                    // toggle music
                    if (MENU.getMusic()) {
                        MENU.turnOffMusic();
                    } else {
                        MENU.turnOnMusic();
                    }
                } else if (mX >= SFXButton[0] && mY >= SFXButton[1] && mX <= SFXButton[0] + SFXButton[2] && mY <= SFXButton[1] + SFXButton[3]) {
                    // toggle sfx
                    if (MENU.getSFX()) {
                        MENU.turnOffSFX();
                    } else {
                        MENU.turnOnSFX();
                    }
                } else if (mX >= helpButton[0] && mY >= helpButton[1] && mX <= helpButton[0] + helpButton[2] && mY <= helpButton[1] + helpButton[3]) {
                    // help screen
                    MM.setCurrent(2);
                    StringSelection tmpSel = new StringSelection("github.com/tfbninja/snake");
                    Clipboard tmpClp = Toolkit.getDefaultToolkit().getSystemClipboard();
                    tmpClp.setContents(tmpSel, null);
                }
            } else if (MM.getCurrent() == 2) {
                MM.setCurrent(0);
            }
        } else if (e.isSecondaryButtonDown()) {
            // right click
            if (MM.getCurrent() == 4 && grid.getDiffLevel() == 0) {
                grid.safeSetCell(xVal, yVal, 0);
            }
        } else {
            // middle button
        }
    }

    /**
     *
     * @return
     */
    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public String toString() {
        return "";
    }
}
