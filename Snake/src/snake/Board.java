package snake;

//<editor-fold defaultstate="collapsed" desc="imports">
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.util.Arrays;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import javax.swing.JFrame;

/**
 * Graphics runner and user input handler
 *
 * @author Tim Barber
 */
public class Board implements Loggable {

    //<editor-fold defaultstate="collapsed" desc="instance vars">
    private int width;
    private int height;
    private Grid grid;
    private Canvas canvas;
    private int outsideMargin = 10;
    private int margin = 1;
    private int XMARGIN = 15;
    private int YMARGIN = 5;
    private int size = 15;
    private int borderSize = 2;
    private int edgeSize = 2;
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

    private int[][] sandbox;

    private ToolPanel toolPanel;
    private final Stage primaryStage;
    private JFrame toolFrame;

    private String events = "";
    private boolean mouseIsBeingDragged = false;
    private int oldMouseX;
    private int oldMouseY;
//</editor-fold>

    /**
     *
     * @param w       the horizontal width
     * @param h       the vertical height
     * @param mm      the MenuManager object
     * @param menu    the Menu object
     * @param gs      the GameState object
     * @param primary the stage object holding the various graphical components
     */
    public Board(int w, int h, MenuManager mm, MainMenu menu, GameState gs, Stage primary) {
        this.width = w;
        this.height = h;
        this.MM = mm;
        this.MENU = menu;
        this.GS = gs;
        canvas = new Canvas(width, height);
        createGrid();
        grid.clearApples();
        primaryStage = primary;
        events += "Initialized | ";
    }

    /**
     * Returns the major events that happened while this class was initialized
     *
     * @return String of events
     */
    @Override
    public String getEvents() {
        return events + "end]";
    }

    /**
     * Returns the state of the important variables in this class
     *
     * @return String of variables
     */
    @Override
    public String getState() {
        return "[mouse clicks: " + mouseClicks + ", "
                + "grid is not null: " + (grid != null) + ", "
                + "Canvas is not null: " + (canvas != null) + ", "
                + "Colors: [blank: \"" + blank + "\", apple: \"" + apple + "\", "
                + "body: \"" + body + "\", head: \""
                + head + "\", bg: \"" + bg + "\", rock: \""
                + rock + "\", applesEaten: \"" + applesEaten
                + "\", portal colors: " + Arrays.deepToString(portalColors) + "], "
                + "lost: " + lost + ", "
                + "key presses: " + keyPresses + ", "
                + "sound on: " + soundOn + ", "
                + "night theme: " + this.nightTheme + ", "
                + "MM: " + MM + ", "
                + "MENU: " + MENU + ", "
                + "GS: " + GS + ", "
                + "sandbox is not null: " + (sandbox != null) + ", "
                + "tool panel is not null: " + (toolPanel != null) + ", "
                + "stage is not null: " + (primaryStage != null) + ", "
                + "tool frame is not null: " + (toolFrame != null)
                + "]";
    }

    /**
     * Sets the different colors variables to a dark colors cheme
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
     * @return A list of colors formatted like this: "rrggbb"
     */
    public String[] getColorScheme() {
        String[] colorScheme = {blank, head, apple, rock, portalColors[0], bg};
        return colorScheme;
    }

    /**
     * Sets the different color variables to a light theme (default)
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

    public void setFullscreen(double screenWidth, double screenHeight) {
        width = (int) screenWidth - outsideMargin;
        height = (int) screenHeight - outsideMargin;
        XMARGIN = Math.max((int) (screenWidth - screenHeight) / 2, 0);
        YMARGIN = Math.max((int) (screenWidth - screenHeight) / 2, 0);
        canvas = new Canvas(width, height);
        margin = 3;
        size = 20;
        borderSize = 5;
        edgeSize = 5;
        drawBlocks();
    }

    public void turnOffFullscreen(int w, int h) {
        width = w;
        height = h;
        canvas = new Canvas(w, h);
        outsideMargin = 10;
        margin = 1;
        XMARGIN = 15;
        YMARGIN = 5;
        size = 15;
        borderSize = 2;
        edgeSize = 2;
        drawBlocks();
    }

    /**
     *
     * @param amt the pixel length of the black border around the game
     */
    public void setOutsideMargin(int amt) {
        this.outsideMargin = amt;
    }

    /**
     * The less repeated code, the better. All this does is create a grid and
     * load it with the necessary values
     */
    public void createGrid() {
        grid = new Grid(GRIDSIZE, GRIDSIZE, 21, 20);
        grid.addGameState(GS);
        grid.addToolPanel(toolPanel);
    }

    /**
     *
     * @return the grid object used by Board
     */
    public Grid getGrid() {
        return this.grid;
    }

    /**
     *
     * @param newGrid the new grid to use
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
     * @return the boolean representing whether night mode is enabled
     */
    public boolean getNightTheme() {
        return this.nightTheme;
    }

    /**
     *
     * @param val the boolean representing whether night mode will be enabled
     */
    public void setNightTheme(boolean val) {
        this.nightTheme = val;
        if (val) {
            setDarkMode();
            events += "set to Dark Mode | ";
        } else {
            setLightMode();
            events += "set to Light Mode | ";
        }
    }

    /**
     * draws the blox
     */
    public void drawBlocks() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        //clear background
        gc.setFill(Color.web(this.bg));
        gc.fillRect(0, 0, this.width, this.height);

        // update black border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(borderSize);
        gc.fillRect(borderSize / 2, borderSize / 2, width - borderSize, height - borderSize);

        if (this.grid.getEdgeKills()) {
            // update red border indicating that edge kills
            gc.setStroke(Color.CRIMSON.darker());
        } else {
            // update green border indicating that warp mode is on
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
                temp.update(canvas);
                yPixel += margin + size;
            }
            xPixel += margin + size;
        }

        // update apples eaten
        gc.setFill(Color.web(applesEaten));
        gc.setFont(Font.font("Impact", 22));
        gc.fillText("Apples eaten: " + this.getGrid().getApplesEaten(), XMARGIN + width / 2 - 100, YMARGIN + getPixelDimensions()[1] + 22);

        if (!this.lost && GS.isPostGame()) {
            this.lost = true;
        }
    }

    /**
     * Resets game-by-game variables and prepares for next round - used for the
     * standard difficulty levels, 1-4
     */
    public void reset() {
        keyPresses = 0;
        this.lost = false;
        GS.setToPreGame();
        this.grid.setSoundOn(this.soundOn);
        //createGrid();
        grid.reset();
        grid.clear();
        grid.safeSetCell(21, 20, 1);
        grid.setPos(21, 20);
    }

    /**
     * Resets sandbox map (only called after you die)
     */
    public void setToSandboxPlayArea() {
        events += "Set to sandbox, sandbox is null: " + (sandbox == null) + " | ";
        grid.setPlayArea(this.sandbox);
    }

    /**
     * Resets game-by-game variables but keeps the same grid object - used in
     * sandbox mode
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
     * @return whether the SFX are on
     */
    public boolean getSFXOn() {
        return this.soundOn;
    }

    /**
     *
     * @param val the value determining whether the SFX is on
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
     * @param e KeyEvent holding the key press information
     */
    public void keyPressed(KeyEvent e) {
        if ((e.getCode() == KeyCode.R || e.getCode() == KeyCode.SPACE) && MM.getCurrent() == 3) {
            events += "reset | ";
            reset();
            grid.setDiffLevel(grid.getDiffLevel());
            MM.setCurrent(4);
        }
        if (e.getCode() == KeyCode.N) {
            this.nightTheme = !this.nightTheme;
            this.setNightTheme(nightTheme);
        }
        if (e.getCode() == KeyCode.ESCAPE) {
            events += "ESC to menu | ";
            MM.setCurrent(0);
            reset();
            toolFrame.setVisible(false);
            Snake.loadHighScoreScreen();
        }

        if (e.getCode() == KeyCode.EQUALS && e.isShiftDown()) {
            events += "Grid exported | ";
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
                events += "chose easy mode | ";
                grid.setDiffLevel(1);
                MM.setCurrent(4);
                GS.setToPreGame();
            } else if (e.getCode() == KeyCode.DIGIT2) {
                events += "chose medium mode | ";
                // medium mode chosen
                this.grid.setDiffLevel(2);
                MM.setCurrent(4);
                GS.setToPreGame();
            } else if (e.getCode() == KeyCode.DIGIT3) {
                events += "chose hard mode | ";
                // hard mode chosen
                this.grid.setDiffLevel(3);
                GS.setToPreGame();
                MM.setCurrent(4);
            } else if (e.getCode() == KeyCode.DIGIT4) {
                events += "chose extreme mode | ";
                // impossible mode chosen
                this.grid.setDiffLevel(4);
                GS.setToPreGame();
                MM.setCurrent(4);
            } else if (e.getCode() == KeyCode.DIGIT0 && e.isShiftDown()) {
                events += "chose sandbox mode | ";
                Snake.initSandboxFile();
                sandbox = new int[grid.getWidth()][grid.getLength()];
                events += "loaded sandbox file | ";
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
            events += "help screen | ";
            if (MM.getCurrent() == 0) {
                MM.setCurrent(1);
            } else if (MM.getCurrent() == 1) {
                MM.setCurrent(0);
            }
        }
        if (e.getCode() == KeyCode.M) {
            toggleMusic();
            events += "music set to " + (MENU.getMusic() ? "on" : "off") + " | ";
        }
        if (e.getCode() == KeyCode.X) {
            toggleSFX();
            events += "SFX set to " + (MENU.getSFX() ? "on" : "off") + " | ";
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
     * Toggles background music and updates MENU controller
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
     * Toggles SFX and updates MENU controller
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
     * @return the lowest int starting from ten that has no corresponding pair
     *         in the grid
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
     * @param AWTTool the integer used to denote the current tool by toolPanel
     * @return the integer used by the grid class
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
     * @param panel the toolPanel object used for sandbox mode
     */
    public void addToolPanel(ToolPanel panel) {
        toolPanel = panel;
    }

    /**
     *
     * @param frame the 'physical' window holding the toolPanel
     */
    public void addToolFrame(JFrame frame) {
        toolFrame = frame;
    }

    /**
     *
     * @param e MouseEvent holding information of the mouse drag
     */
    public void mouseDragged(MouseEvent e) {
        // interpolate drags
        boolean interpolate = false;
        if (!mouseIsBeingDragged) {
            interpolate = true;
        }
        mouseIsBeingDragged = true;
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
        int oldXVal = 0;
        int oldYVal = 0;
        if (interpolate) {
            oldXVal = (oldMouseX + size - XMARGIN) / (margin + size) - 1;
            oldYVal = (oldMouseY + size - YMARGIN) / (margin + size) - 1;
        }
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
                        if (interpolate) {
                            //int step = size / 2;
                            int xfactor = (xVal - oldXVal) / (Math.abs(xVal - oldXVal));
                            int yStep = (yVal - oldYVal) / (xVal - oldXVal);
                            int tempY = oldYVal;
                            for (int x = oldXVal; x > x; x += xfactor) {
                                grid.setCell(x, tempY, tool);
                                tempY += yStep;
                                if (xfactor > 0) {
                                    if (x >= xVal) {
                                        break;
                                    }
                                } else {
                                    if (x <= xVal) {
                                        break;
                                    }
                                }
                            }

                            int yfactor = (yVal - oldYVal) / (Math.abs(yVal - oldYVal));
                            int tempX = oldXVal;
                            int xStep = (xVal - oldXVal) / (yVal - oldYVal);
                            for (int y = oldYVal; y > y; y += yfactor) {
                                grid.setCell(tempX, y, tool);
                                tempX += xStep;
                                if (yfactor > 0) {
                                    if (y >= yVal) {
                                        break;
                                    }
                                } else {
                                    if (y <= yVal) {
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        oldMouseX = (int) mouseX;
        oldMouseY = (int) mouseY;
    }

    public void mouseReleased(MouseEvent e) {
        mouseIsBeingDragged = false;
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
    }

    /**
     *
     * @param e MouseEvent holding information of the mouse click
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
     * @return the canvas object used
     */
    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public String toString() {
        return "Board: [" + width + ", " + height + ", " + GRIDSIZE + "]";
    }
}
