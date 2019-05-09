package snake;

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.util.ArrayList;
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
    private double screenW;
    private double screenH;
    private int width;
    private int height;
    private Grid grid;
    private Canvas canvas;
    private int outsideMargin = 10;
    private int margin = 1;
    private int XMARGIN = 15;
    private int YMARGIN = 5;
    private int blockSize = 15;
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
    private String backdropKill = "a29b95";
    private String backdropSafe = "000000";
    private String rock = "53585e";
    private String applesEatenKill = "750BE0";
    private String applesEatenSafe = "750BE0";
    private final String[] portalColors = {"90094E", "550C74", "dfb708", "ef5658", "bb3dff"};
    
    private boolean lost = false;
    
    private int keyPresses = 0;

    //menu variables
    private boolean soundOn = true;
    
    private int toolButtonX = 5;
    private int toolButtonY = 30;
    private int toolButtonSpace = 10;
    private int toolButtonSize = 30;

    // in order, xPos, yPos, Width, Height
    private final Button easyButton = new Button(12, 292, 194, 51);
    private final Button medButton = new Button(219, 292, 194, 51);
    private final Button hardButton = new Button(12, 353, 194, 51);
    private final Button impButton = new Button(219, 353, 194, 51);
    private final Button musicButton = new Button(12, 18, 55, 37);
    private final Button SFXButton = new Button(83, 18, 28, 37);
    private final Button helpButton = new Button(13, 255, 47, 22);
    
    private Button easyButtonFS;
    private Button medButtonFS;
    private Button hardButtonFS;
    private Button impButtonFS;
    private Button musicButtonFS;
    private Button SFXButtonFS;
    private Button helpButtonFS;
    
    private Button[] sandboxButtonsFS = {new Button(toolButtonX, toolButtonY, toolButtonSize, toolButtonSize),
        new Button(toolButtonX, toolButtonY + toolButtonSize + toolButtonSpace, toolButtonSize, toolButtonSize),
        new Button(toolButtonX, toolButtonY + (toolButtonSize + toolButtonSpace) * 2, toolButtonSize, toolButtonSize),
        new Button(toolButtonX, toolButtonY + (toolButtonSize + toolButtonSpace) * 3, toolButtonSize, toolButtonSize),
        new Button(toolButtonX, toolButtonY + (toolButtonSize + toolButtonSpace) * 4, toolButtonSize, toolButtonSize),
        new Button(toolButtonX, toolButtonY + (toolButtonSize + toolButtonSpace) * 5, toolButtonSize, toolButtonSize)};
    private String[] sandboxButtonsFSNames = {"BLANK", "HEAD", "APPLE", "ROCK", "PORTAL", "CLEAR"};
    
    private boolean nightTheme = false;
    
    private final MenuManager MM;
    private final MainMenu MENU;
    private final GameState GS;
    
    private int[][] sandbox;
    
    private ToolPanel toolPanel;
    private final Stage primaryStage;
    private JFrame toolFrame;
    
    private String events = "";
    
    private boolean fullscreen = false;
    private int appleTextX;
    private int appleTextY;
    private Font appleFont = new Font("Impact", 22);
    private Font fullScreenAppleFont = new Font("Courier", 50);

//</editor-fold>
    /**
     *
     * @param w the horizontal width
     * @param h the vertical height
     * @param mm the MenuManager object
     * @param menu the Menu object
     * @param gs the GameState object
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
        turnOffFullscreen(w, h);
        
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
                + rock + "\", applesEatenKill: \"" + applesEatenKill
                + "\", applesEatenSafe: \"" + applesEatenSafe
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
     * Sets the different colors variables to a dark colors scheme
     */
    public void setDarkMode() {
        blank = "444444";
        apple = "E51B39";
        body = "2377DD";
        head = "AF6C00";
        bg = "212121";
        backdropKill = "5B2333";
        backdropSafe = "247BA0";
        rock = "1e1e1e";
        applesEatenKill = "0A090C";
        applesEatenSafe = "0A090C";
        toolPanel.updateButtonColors(getColorScheme());
    }

    /**
     *
     * @param size the dimension defining the side length of the imaginary
     * square around the menu screen
     * @return A Canvas object with graphics displaying the menu
     */
    public Canvas getFullScreenMenu(double size) {
        Canvas c = new Canvas(size, size);
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.setFill(Color.web("dcf9ff"));
        gc.fillRect(0, 0, size, size);

        // draw buttons
        gc.setFill(Color.web("212121"));
        double shadowOpacity = 0.1;

//<editor-fold defaultstate="collapsed" desc="Easy">
//<editor-fold defaultstate="collapsed" desc="shadow">
        gc.setFill(Color.web("212121", shadowOpacity));
        gc.fillRect(18 / 430.0 * size, 300 / 430.0 * size, 194 / 430.0 * size, 51 / 430.0 * size);
//</editor-fold>
        gc.setFill(Color.web("212121"));
        gc.fillRect(12 / 430.0 * size, 293 / 430.0 * size, 194 / 430.0 * size, 49 / 430.0 * size);
        gc.setFill(Color.web("495456"));
        gc.fillRect(12 / 430.0 * size, 292 / 430.0 * size, 194 / 430.0 * size, 1 / 430.0 * size);
        gc.fillRect(12 / 430.0 * size, 342 / 430.0 * size, 194 / 430.0 * size, 1 / 430.0 * size);
        
        gc.setFill(Color.web("e2e2e2"));
        gc.setFont(new Font("Impact", 45 / 430.0 * size));
        gc.fillText("EASY", 62 / 430.0 * size, 335 / 430.0 * size);
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Medium">
//<editor-fold defaultstate="collapsed" desc="shadow">
        gc.setFill(Color.web("212121", shadowOpacity));
        gc.fillRect(225 / 430.0 * size, 300 / 430.0 * size, 194 / 430.0 * size, 51 / 430.0 * size);
//</editor-fold>
        gc.setFill(Color.web("212121"));
        gc.fillRect(219 / 430.0 * size, 293 / 430.0 * size, 194 / 430.0 * size, 49 / 430.0 * size);
        gc.setFill(Color.web("495456"));
        gc.fillRect(219 / 430.0 * size, 292 / 430.0 * size, 194 / 430.0 * size, 1 / 430.0 * size);
        gc.fillRect(219 / 430.0 * size, 342 / 430.0 * size, 194 / 430.0 * size, 1 / 430.0 * size);
        
        gc.setFill(Color.web("e2e2e2"));
        gc.setFont(new Font("Impact", 45 / 430.0 * size));
        gc.fillText("MEDIUM", 244 / 430.0 * size, 335 / 430.0 * size);
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Hard">
//<editor-fold defaultstate="collapsed" desc="shadow">
        gc.setFill(Color.web("212121", shadowOpacity));
        gc.fillRect(18 / 430.0 * size, 361 / 430.0 * size, 194 / 430.0 * size, 51 / 430.0 * size);
//</editor-fold>
        gc.setFill(Color.web("212121"));
        gc.fillRect(12 / 430.0 * size, 354 / 430.0 * size, 194 / 430.0 * size, 49 / 430.0 * size);
        gc.setFill(Color.web("495456"));
        gc.fillRect(12 / 430.0 * size, 353 / 430.0 * size, 194 / 430.0 * size, 1 / 430.0 * size);
        gc.fillRect(12 / 430.0 * size, 403 / 430.0 * size, 194 / 430.0 * size, 1 / 430.0 * size);
        
        gc.setFill(Color.web("e2e2e2"));
        gc.setFont(new Font("Impact", 45 / 430.0 * size));
        gc.fillText("HARD", 57 / 430.0 * size, 396 / 430.0 * size);
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Extreme">
//<editor-fold defaultstate="collapsed" desc="shadow">
        gc.setFill(Color.web("212121", shadowOpacity));
        gc.fillRect(225 / 430.0 * size, 361 / 430.0 * size, 194 / 430.0 * size, 51 / 430.0 * size);
//</editor-fold>
        gc.setFill(Color.web("212121"));
        gc.fillRect(219 / 430.0 * size, 354 / 430.0 * size, 194 / 430.0 * size, 49 / 430.0 * size);
        gc.setFill(Color.web("495456"));
        gc.fillRect(219 / 430.0 * size, 353 / 430.0 * size, 194 / 430.0 * size, 1 / 430.0 * size);
        gc.fillRect(219 / 430.0 * size, 403 / 430.0 * size, 194 / 430.0 * size, 1 / 430.0 * size);
        
        gc.setFill(Color.web("e2e2e2"));
        gc.setFont(new Font("Impact", 45 / 430.0 * size));
        gc.fillText("EXTREME", 241 / 430.0 * size, 396 / 430.0 * size);
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="SNAKE">
        gc.setFill(new Color(33 / 255.0, 33 / 255.0, 33 / 255.0, shadowOpacity));
        gc.setFont(new Font("Impact", 85 / 430.0 * size));
        gc.fillText("SNAKE", 197 / 430.0 * size, 274 / 430.0 * size);
        gc.setFill(new Color(33 / 255.0, 33 / 255.0, 33 / 255.0, 1));
        gc.fillText("SNAKE", 193 / 430.0 * size, 266 / 430.0 * size);

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Help">
        gc.setFill(new Color(0, 0, 0, shadowOpacity));
        gc.setFont(new Font("Impact", 25 / 430.0 * size));
        gc.fillText("Help", 17 / 430.0 * size, 282 / 430.0 * size);
        gc.setFill(Color.BLACK);
        gc.fillText("Help", 13 / 430.0 * size, 275 / 430.0 * size);

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Snake pic">
        double body2xTrans = 54;
        double body2yTrans = -27;
        for (int i = 6; i >= 2; i--) {
            //body front
            gc.setFill(Color.web("87b201"));
            double[] body1FX = {(38 + body2xTrans * i) / 430.0 * size, (83 + body2xTrans * i) / 430.0 * size, (83 + body2xTrans * i) / 430.0 * size, (38 + body2xTrans * i) / 430.0 * size};
            double[] body1FY = {(162 + body2yTrans * (i + 2)) / 430.0 * size, (185 + body2yTrans * (i + 2)) / 430.0 * size, (231 + body2yTrans * (i + 2)) / 430.0 * size, (208 + body2yTrans * (i + 2)) / 430.0 * size};
            gc.fillPolygon(body1FX, body1FY, 4);
            gc.setStroke(Color.web("393f3f"));
            gc.setLineWidth(2 / 430.0 * size);
            gc.strokePolygon(body1FX, body1FY, 4);

            // body side
            gc.setFill(Color.web("a6d43d"));
            double[] body1SX = {(84 + body2xTrans * i) / 430.0 * size, (131 + body2xTrans * i) / 430.0 * size, (131 + body2xTrans * i) / 430.0 * size, (84 + body2xTrans * i) / 430.0 * size};
            double[] body1SY = {(185 + body2yTrans * (i + 2)) / 430.0 * size, (161.8 + body2yTrans * (i + 2)) / 430.0 * size, (209 + body2yTrans * (i + 2)) / 430.0 * size, (231 + body2yTrans * (i + 2)) / 430.0 * size};
            gc.fillPolygon(body1SX, body1SY, 4);
            gc.setStroke(Color.web("393f3f"));
            gc.setLineWidth(2 / 430.0 * size);
            double[] outlSX1 = {body1SX[1], body1SX[2], body1SX[3]}; // the name is OUTLine Side X body 1
            double[] outlSY1 = {body1SY[1], body1SY[2], body1SY[3]};
            gc.strokePolyline(outlSX1, outlSY1, 3);

            //body top
            gc.setFill(Color.web("a6d43d"));
            double[] body1TX = {(38.5 + body2xTrans * i) / 430.0 * size, (85 + body2xTrans * i) / 430.0 * size, (130 + body2xTrans * i) / 430.0 * size, (84 + body2xTrans * i) / 430.0 * size};
            double[] body1TY = {(161 + body2yTrans * (i + 2)) / 430.0 * size, (138 + body2yTrans * (i + 2)) / 430.0 * size, (161 + body2yTrans * (i + 2)) / 430.0 * size, (184.5 + body2yTrans * (i + 2)) / 430.0 * size};
            gc.fillPolygon(body1TX, body1TY, 4);
            gc.setStroke(Color.web("393f3f"));
            gc.setLineWidth(2 / 430.0 * size);
            gc.strokePolyline(body1TX, body1TY, 4);
        }
        
        double body1xTrans = 54;
        double body1yTrans = -27;
        for (int i = 3; i >= 1; i--) {
            //body front
            gc.setFill(Color.web("87b201"));
            double[] body1FX = {(38 + body1xTrans * i) / 430.0 * size, (83 + body1xTrans * i) / 430.0 * size, (83 + body1xTrans * i) / 430.0 * size, (38 + body1xTrans * i) / 430.0 * size};
            double[] body1FY = {(162 + body1yTrans * i) / 430.0 * size, (185 + body1yTrans * i) / 430.0 * size, (231 + body1yTrans * i) / 430.0 * size, (208 + body1yTrans * i) / 430.0 * size};
            gc.fillPolygon(body1FX, body1FY, 4);
            gc.setStroke(Color.web("393f3f"));
            gc.setLineWidth(2 / 430.0 * size);
            gc.strokePolygon(body1FX, body1FY, 4);

            // body side
            gc.setFill(Color.web("a6d43d"));
            double[] body1SX = {(84 + body1xTrans * i) / 430.0 * size, (131 + body1xTrans * i) / 430.0 * size, (131 + body1xTrans * i) / 430.0 * size, (84 + body1xTrans * i) / 430.0 * size};
            double[] body1SY = {(185 + body1yTrans * i) / 430.0 * size, (161.8 + body1yTrans * i) / 430.0 * size, (209 + body1yTrans * i) / 430.0 * size, (231 + body1yTrans * i) / 430.0 * size};
            gc.fillPolygon(body1SX, body1SY, 4);
            gc.setStroke(Color.web("393f3f"));
            gc.setLineWidth(2 / 430.0 * size);
            double[] outlSX1 = {body1SX[1], body1SX[2], body1SX[3]}; // the name is OUTLine Side X body 1
            double[] outlSY1 = {body1SY[1], body1SY[2], body1SY[3]};
            gc.strokePolyline(outlSX1, outlSY1, 3);

            //body top
            gc.setFill(Color.web("a6d43d"));
            double[] body1TX = {(38.5 + body1xTrans * i) / 430.0 * size, (85 + body1xTrans * i) / 430.0 * size, (130 + body1xTrans * i) / 430.0 * size, (84 + body1xTrans * i) / 430.0 * size};
            double[] body1TY = {(161 + body1yTrans * i) / 430.0 * size, (138 + body1yTrans * i) / 430.0 * size, (161 + body1yTrans * i) / 430.0 * size, (184.5 + body1yTrans * i) / 430.0 * size};
            gc.fillPolygon(body1TX, body1TY, 4);
            gc.setStroke(Color.web("393f3f"));
            gc.setLineWidth(2 / 430.0 * size);
            gc.strokePolyline(body1TX, body1TY, 4);
        }

        //head front
        gc.setFill(Color.web("ff8e02"));
        double[] headFX = {38 / 430.0 * size, 83 / 430.0 * size, 83 / 430.0 * size, 38 / 430.0 * size};
        double[] headFY = {162 / 430.0 * size, 185 / 430.0 * size, 231 / 430.0 * size, 208 / 430.0 * size};
        gc.fillPolygon(headFX, headFY, 4);
        gc.setStroke(Color.web("393f3f"));
        gc.setLineWidth(2 / 430.0 * size);
        gc.strokePolygon(headFX, headFY, 4);

        // head side
        gc.setFill(Color.web("ffb94d"));
        double[] headSX = {84 / 430.0 * size, 131 / 430.0 * size, 131 / 430.0 * size, 84 / 430.0 * size};
        double[] headSY = {185 / 430.0 * size, 161.8 / 430.0 * size, 209 / 430.0 * size, 231 / 430.0 * size};
        gc.fillPolygon(headSX, headSY, 4);
        gc.setStroke(Color.web("393f3f"));
        gc.setLineWidth(2 / 430.0 * size);
        double[] outlSX = {headSX[1], headSX[2], headSX[3]};
        double[] outlSY = {headSY[1], headSY[2], headSY[3]};
        gc.strokePolyline(outlSX, outlSY, 3);

        //head top
        gc.setFill(Color.web("ffb94d"));
        double[] headTX = {38.5 / 430.0 * size, 85 / 430.0 * size, 130 / 430.0 * size, 84 / 430.0 * size};
        double[] headTY = {161 / 430.0 * size, 138 / 430.0 * size, 161 / 430.0 * size, 184.5 / 430.0 * size};
        gc.fillPolygon(headTX, headTY, 4);
        gc.setStroke(Color.web("393f3f"));
        gc.setLineWidth(2 / 430.0 * size);
        gc.strokePolyline(headTX, headTY, 4);
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Music/SFX">
        gc.setFont(new Font("Impact", 22 / 430.0 * size));
        gc.setFill(MENU.getMusic() ? Color.web("40ac4a") : Color.web("7f7f7f"));
        gc.fillText("MUSIC", 16 / 430.0 * size, 26 / 430.0 * size);
        if (fullscreen) {
            musicButtonFS.setX(16 / 430.0 * size);
            musicButtonFS.setY(3 / 430.0 * size);
            musicButtonFS.setW(59 / 430.0 * size);
            musicButtonFS.setH(24 / 430.0 * size);
            
            SFXButtonFS.setX(81 / 430.0 * size);
            SFXButtonFS.setY(3 / 430.0 * size);
            SFXButtonFS.setW(38 / 430.0 * size);
            SFXButtonFS.setH(22 / 430.0 * size);
        } else {
            musicButton.setX(16 / 430.0 * size);
            musicButton.setY(8 / 430.0 * size);
            musicButton.setW(59 / 430.0 * size);
            musicButton.setH(24 / 430.0 * size);
            
            SFXButton.setX(81 / 430.0 * size);
            SFXButton.setY(8 / 430.0 * size);
            SFXButton.setW(38 / 430.0 * size);
            SFXButton.setH(22 / 430.0 * size);
        }
        gc.setFill(MENU.getSFX() ? Color.web("40ac4a") : Color.web("7f7f7f"));
        gc.fillText("SFX", 85 / 430.0 * size, 26 / 430.0 * size);
//</editor-fold>
        return c;
    }

    /**
     *
     * @param size
     * @param scores
     * @param highs
     * @param names
     * @return
     */
    public Canvas getFullScreenBigOof(double size, ArrayList<Integer> scores, ArrayList<Boolean> highs, ArrayList<String> names) {
        Canvas c = new Canvas(size, size);
        GraphicsContext gc = c.getGraphicsContext2D();
        
        gc.setFont(new Font("Impact", 132 / 430.0 * size));
        gc.setFill(Color.web("b1600f"));
        gc.fillText("BIG OOF", 17 / 430.0 * size, 158 / 430.0 * size);
        
        gc.setFont(new Font("Impact", 38 / 430.0 * size));
        gc.setFill(Color.web("b1600f"));
        gc.fillText("HIGH SCORES", 106 / 430.0 * size, 239 / 430.0 * size);
        
        gc.setFont(new Font("Impact", 29 / 430.0 * size));
        gc.setFill(Color.web("b1600f"));
        gc.fillText("WORLD", 116 / 430.0 * size, 275 / 430.0 * size);
        gc.fillText("LOCAL", 234 / 430.0 * size, 275 / 430.0 * size);
        
        gc.setFont(new Font("Impact", 22 / 430.0 * size));
        gc.setFill(Color.web("b1600f"));
        gc.fillText("EASY", 33 / 430.0 * size, 320 / 430.0 * size);
        gc.fillText("MEDIUM", 18 / 430.0 * size, 347 / 430.0 * size);
        gc.fillText("HARD", 30 / 430.0 * size, 374 / 430.0 * size);
        gc.fillText("EXTREME", 16 / 430.0 * size, 401 / 430.0 * size);
        
        gc.setFill(Color.web("b1600f"));
        gc.fillRect(104 / 430.0 * size, 244 / 430.0 * size, 221 / 430.0 * size, 2 / 430.0 * size);
        gc.fillRect(213 / 430.0 * size, 246 / 430.0 * size, 2 / 430.0 * size, 184 / 430.0 * size);
        
        if (highs.contains(true)) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Impact", 34 / 430.0 * size));
            gc.fillText("NEW HIGHSCORE", 105 / 430.0 * size, 34 / 430.0 * size);
        }
        double y = 320 / 430.0 * size;
        double x;
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                if (i > 1) {
                    y += 27 / 430.0 * size;
                }
                x = 234 / 430.0 * size;
            } else {
                x = 125 / 430.0 * size;
            }
            if (highs.get(i)) {
                gc.setFill(Color.RED);
                gc.setFont(new Font("Impact", 22 / 430.0 * size));
                gc.fillText(String.valueOf(scores.get(i)) + " - " + names.get(i), x, y);
            } else {
                gc.setFill(Color.web("b1600f"));
                gc.setFont(new Font("Impact", 22 / 430.0 * size));
                gc.fillText(String.valueOf(scores.get(i)) + " - " + names.get(i), x, y);
            }
        }
        
        gc.setFill(Color.web("b30e0e"));
        gc.setFont(new Font("DejaVu Sans", 18 / 430.0 * size));
        gc.fillText("PRESS R TO RESTART", 230 / 430.0 * size, 427 / 430.0 * size);
        
        gc.setFill(Color.web("b1600f"));
        gc.setFont(new Font("Impact", 24 / 430.0 * size));
        gc.fillText("Score: " + grid.getApplesEaten(), 173 / 430.0 * size, 194 / 430.0 * size);
        
        return c;
    }

    /**
     *
     * @return A list of colors formatted like this: "rrggbb"
     */
    public String[] getColorScheme() {
        if (grid.getEdgeKills()) {
            String[] colorScheme = {blank, head, apple, rock, portalColors[0], backdropSafe, backdropKill, bg};
            return colorScheme;
        } else {
            String[] colorScheme = {blank, head, apple, rock, portalColors[0], backdropKill, backdropSafe, bg};
            return colorScheme;
        }
        
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
        backdropKill = "F61067";
        backdropSafe = "7AE7C7";
        rock = "53585e";
        applesEatenKill = "e9edbb";
        applesEatenSafe = "EA633A";
        toolPanel.updateButtonColors(getColorScheme());
    }

    /**
     * Either this or turnOffFullscreen(w,h) MUST be called during
     * initialization of Board for it to properly initialize graphics variables
     *
     * @param screenWidth Width of the screen
     * @param screenHeight Height of the screen
     */
    public void setFullscreen(double screenWidth, double screenHeight) {
        fullscreen = true;
        screenW = screenWidth;
        screenH = screenHeight;
        width = (int) screenWidth - outsideMargin;
        height = (int) screenHeight - outsideMargin;
        borderSize = 5;
        XMARGIN = Math.max((int) (screenWidth - screenHeight) / 2, 0);
        YMARGIN = Math.max((int) (screenHeight - screenWidth) / 2, 0) + 20;
        canvas = new Canvas(width, height);
        margin = (int) ((Math.min(screenWidth, screenHeight) - Math.min(XMARGIN, YMARGIN) - borderSize - edgeSize) / 16) / grid.getWidth();
        blockSize = (int) ((Math.min(screenWidth, screenHeight) - Math.min(XMARGIN, YMARGIN) - borderSize - edgeSize) / 16 * 15) / grid.getWidth();
        
        edgeSize = 5;
        if (screenWidth > screenHeight) { // it better be...jeez
            appleTextX = width - (XMARGIN / 2) - 50;
            appleTextY = (int) ((screenHeight / 2) - (fullScreenAppleFont.getSize() / 2));
        } else {
            appleTextX = width - (XMARGIN / 2) - 50;
            appleTextY = (int) (screenHeight - YMARGIN / 2) - 20;
        }
        double scalar = Math.min(screenWidth, screenHeight);
        easyButtonFS = new Button(12 / 430.0 * scalar, 292 / 430.0 * scalar, 194 / 430.0 * scalar, 51 / 430.0 * scalar);
        medButtonFS = new Button(219 / 430.0 * scalar, 292 / 430.0 * scalar, 194 / 430.0 * scalar, 51 / 430.0 * scalar);
        hardButtonFS = new Button(12 / 430.0 * scalar, 353 / 430.0 * scalar, 194 / 430.0 * scalar, 51 / 430.0 * scalar);
        impButtonFS = new Button(219 / 430.0 * scalar, 353 / 430.0 * scalar, 194 / 430.0 * scalar, 51 / 430.0 * scalar);
        musicButtonFS = new Button(12 / 430.0 * scalar, 18 / 430.0 * scalar, 55 / 430.0 * scalar, 37 / 430.0 * scalar);
        SFXButtonFS = new Button(83 / 430.0 * scalar, 18 / 430.0 * scalar, 28 / 430.0 * scalar, 37 / 430.0 * scalar);
        helpButtonFS = new Button(13 / 430.0 * scalar, 255 / 430.0 * scalar, 47 / 430.0 * scalar, 22 / 430.0 * scalar);
        
        for (Button b : sandboxButtonsFS) {
            b.setScale(scalar / 430.0);
        }
        drawBlocks();
    }

    /*
     * Either this or setFullscreen(w, h) MUST be called during initialization
     * of Board for it to properly initialize graphics variables
     *
     * @param w Width of the window
     * @param h Height of the window
     */
    /**
     *
     * @param w
     * @param h
     */
    public void turnOffFullscreen(int w, int h) {
        fullscreen = false;
        screenW = w;
        screenH = h;
        width = w;
        height = h;
        canvas = new Canvas(w, h);
        outsideMargin = 10;
        XMARGIN = 15;
        YMARGIN = 5;
        margin = 1;
        blockSize = 15;
        borderSize = 2;
        edgeSize = 2;
        appleTextX = XMARGIN + width / 2 - 20;
        appleTextY = h;
        double scalar = Math.min(w, h);
        easyButtonFS = new Button(12 / 430.0 * scalar, 292 / 430.0 * scalar, 194 / 430.0 * scalar, 51 / 430.0 * scalar);
        medButtonFS = new Button(219 / 430.0 * scalar, 292 / 430.0 * scalar, 194 / 430.0 * scalar, 51 / 430.0 * scalar);
        hardButtonFS = new Button(12 / 430.0 * scalar, 353 / 430.0 * scalar, 194 / 430.0 * scalar, 51 / 430.0 * scalar);
        impButtonFS = new Button(219 / 430.0 * scalar, 353 / 430.0 * scalar, 194 / 430.0 * scalar, 51 / 430.0 * scalar);
        musicButtonFS = new Button(12 / 430.0 * scalar, 18 / 430.0 * scalar, 55 / 430.0 * scalar, 37 / 430.0 * scalar);
        SFXButtonFS = new Button(83 / 430.0 * scalar, 18 / 430.0 * scalar, 28 / 430.0 * scalar, 37 / 430.0 * scalar);
        helpButtonFS = new Button(13 / 430.0 * scalar, 255 / 430.0 * scalar, 47 / 430.0 * scalar, 22 / 430.0 * scalar);
        for (Button b : sandboxButtonsFS) {
            b.setScale(1);
        }
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
        grid.addMainMenu(MENU);
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
        int[] dimensions = {margin * (GRIDSIZE - 1) + blockSize * GRIDSIZE, margin * (GRIDSIZE - 1) + blockSize * GRIDSIZE};
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
        if (this.grid.getEdgeKills()) {
            // update red border indicating that edge kills
            gc.setStroke(Color.web(backdropKill));
            gc.setFill(Color.web(backdropKill));
        } else {
            // update green border indicating that warp mode is on
            gc.setStroke(Color.web(backdropSafe));
            gc.setFill(Color.web(backdropSafe));
        }
        gc.fillRect(0, 0, this.width, this.height);
        gc.setLineWidth(edgeSize);
        int pixelSize = GRIDSIZE * blockSize + GRIDSIZE * margin;
        gc.setFill(Color.web(this.bg));
        gc.fillRect(XMARGIN - edgeSize / 2, YMARGIN - edgeSize / 2, pixelSize + edgeSize - 1, pixelSize + edgeSize - 1);
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
                temp.setWidth(blockSize);
                temp.setHeight(blockSize);
                temp.update(canvas);
                yPixel += margin + blockSize;
            }
            xPixel += margin + blockSize;
        }

        // update apples eaten
        if (grid.getEdgeKills()) {
            gc.setFill(Color.web(applesEatenKill));
        } else {
            gc.setFill(Color.web(applesEatenSafe));
        }
        if (fullscreen) {
            gc.setFont(fullScreenAppleFont);
            gc.fillText("" + this.getGrid().getApplesEaten(), appleTextX, appleTextY);
        } else {
            gc.setFont(appleFont);
            gc.fillText("" + this.getGrid().getApplesEaten(), appleTextX, appleTextY);
        }
        
        if (!this.lost && GS.isPostGame()) {
            this.lost = true;
        }
        if (fullscreen && grid.getDiffLevel() == 0) {
            // we're gonna go ahead and assume a normal width > height monitor shape here
            // draw toolbox
            Color bgColor = Color.web(getColorScheme()[getColorScheme().length - 2]);
            int[] xAdd = {5, 7, 6, 6, 3};
            double scl = Math.min(screenW, screenH);
            for (int i = 0; i < sandboxButtonsFS.length; i++) {
                if (i == 5) { // clear button
                    if (GS.isGame()) {
                        gc.setFill(invert(Color.web(getColorScheme()[getColorScheme().length - 2])).darker().darker());
                        gc.fillRect(toolButtonX / 430.0 * scl, (toolButtonY + (toolButtonSpace * i) + (toolButtonSize * i)) / 430.0 * scl, toolButtonSize / 430.0 * scl, toolButtonSize / 430.0 * scl);
                        gc.setFill(Color.WHITE);
                        gc.setFont(new Font("Tahoma", 7 / 430.0 * scl));
                        gc.fillText("RESET", (toolButtonX + 5) / 430.0 * scl, (toolButtonY + (toolButtonSpace * i) + (toolButtonSize * i) + (toolButtonSize / 2) + 2) / 430.0 * scl);
                    } else {
                        gc.setFill(invert(Color.web(getColorScheme()[getColorScheme().length - 2])).darker().darker());
                        gc.fillRect(toolButtonX / 430.0 * scl, (toolButtonY + (toolButtonSpace * i) + (toolButtonSize * i)) / 430.0 * scl, toolButtonSize / 430.0 * scl, toolButtonSize / 430.0 * scl);
                        gc.setFill(Color.WHITE);
                        gc.setFont(new Font("Tahoma", 7 / 430.0 * scl));
                        gc.fillText("CLEAR", (toolButtonX + 5) / 430.0 * scl, (toolButtonY + (toolButtonSpace * i) + (toolButtonSize * i) + (toolButtonSize / 2) + 2) / 430.0 * scl);
                    }
                } else {
                    gc.setFill(Color.web(getColorScheme()[i]));
                    gc.fillRect(toolButtonX / 430.0 * scl, (toolButtonY + (toolButtonSpace * i) + (toolButtonSize * i)) / 430.0 * scl, toolButtonSize / 430.0 * scl, toolButtonSize / 430.0 * scl);
                    gc.setFill(Color.WHITE);
                    gc.setFont(new Font("Tahoma", 7 / 430.0 * scl));
                    gc.fillText(sandboxButtonsFSNames[i], (toolButtonX + xAdd[i]) / 430.0 * scl, (toolButtonY + (toolButtonSpace * i) + (toolButtonSize * i) + (toolButtonSize / 2) + 2) / 430.0 * scl);
                }
            }
            gc.setFill(Color.BLUEVIOLET);
            gc.setLineWidth(1 / 430.0 * scl);
            gc.strokeRect((toolButtonX - 1) / 430.0 * scl, (toolButtonY + (toolButtonSpace * fullScreenToolNumber(toolPanel.getCurrentTool()) + (toolButtonSize * fullScreenToolNumber(toolPanel.getCurrentTool()))) - 1) / 430.0 * scl, (toolButtonSize + 2) / 430.0 * scl, (toolButtonSize + 2) / 430.0 * scl);
        }
    }

    /**
     *
     * @param toolPanelNum
     * @return
     */
    public int fullScreenToolNumber(int toolPanelNum) {
        switch (toolPanelNum) {
            default:
                return toolPanelNum;
        }
    }

    /**
     *
     * @param c
     * @param opacity
     * @return
     */
    public Color dim(Color c, double opacity) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), opacity);
    }

    /**
     * Resets game-by-game variables and prepares for next round - used for the
     * standard difficulty levels, 1-4
     */
    public void reset() {
        keyPresses = 0;
        this.lost = false;
        GS.setToPreGame();
        //createGrid();
        grid.setApplesEaten(0);
        grid.reset();
        grid.clear();
        grid.safeSetCell(21, 20, 1);
        grid.setPos(21, 20);
    }

    /**
     *
     * @param c
     * @return
     */
    public Color invert(Color c) {
        //System.out.println("r:" + c.getRed() + ", b:" + c.getBlue() + ", g:" + c.getGreen() + ", o:" + c.getOpacity());
        return new Color(1 - c.getRed(), 1 - c.getGreen(), 1 - c.getBlue(), c.getOpacity());
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
            if (grid.getDiffLevel() > 0) {
                grid.resetApplesEaten();
            }
            MM.setCurrent(4);
        }
        if (e.getCode() == KeyCode.N) {
            this.nightTheme = !this.nightTheme;
            this.setNightTheme(nightTheme);
        }
        if (e.getCode() == KeyCode.BACK_SPACE) {
            events += "BKSPC to menu | ";
            MM.setCurrent(0);
            reset();
            toolFrame.setVisible(false);
        }
        
        if (e.getCode() == KeyCode.Q && e.isShiftDown()) {
            Snake.toggleAI();
        }
        
        if (e.getCode() == KeyCode.EQUALS && e.isShiftDown()) {
            events += "Grid exported | ";
            System.out.println(grid.exportCode());
        }
        
        if (MM.getCurrent() == 2 && (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.LEFT)) {
            if (e.getCode() == KeyCode.RIGHT) {
                Snake.incrementHelpIndex();
            } else {
                Snake.decrementHelpIndex();
            }
        }
        
        if (MM.getCurrent() == 4 && grid.getDiffLevel() == 0) {
            if (null != e.getCode()) {
                switch (e.getCode()) {
                    case DIGIT1:
                        toolPanel.setCurrentTool(0);
                        break;
                    case DIGIT2:
                        toolPanel.setCurrentTool(1);
                        break;
                    case DIGIT3:
                        toolPanel.setCurrentTool(2);
                        break;
                    case DIGIT4:
                        toolPanel.setCurrentTool(3);
                        break;
                    case DIGIT5:
                        toolPanel.setCurrentTool(4);
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
    }

    /**
     *
     * @return the lowest int starting from ten that has no corresponding pair
     * in the grid
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
        double mouseX = e.getX();
        double mouseY = e.getY();
        // account for border outside of canvas
        mouseY -= outsideMargin;
        mouseX -= outsideMargin;
        int mX = (int) mouseX;
        int mY = (int) mouseY;
        // top right:
        // margin * x + xPos + (blockSize * (x-1)) : += blockSize
        //solve:
        //margin * (x+1)) + (blockSize * (x-1)) = z, z = margin * x + xPos + margin + blockSize * x - blockSize, z = x(margin + blockSize) + xPos + margin - blockSize, (z + blockSize - margin)/(margin + blockSize) = x
        int xVal = (mX + blockSize - XMARGIN) / (margin + blockSize) - 1;
        int yVal = (mY + blockSize - YMARGIN) / (margin + blockSize) - 1;
        
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
            // sandbox mode editing
            if (MM.getCurrent() == 4 && grid.getDiffLevel() == 0 && xVal >= 0 && xVal < grid.getWidth() && yVal >= 0 && yVal < grid.getLength()) {
                
                int tool = toolPanel.getCurrentTool();
                switch (tool) {
                    case 2:
                        // apple is second in list b/c no body tool
                        tool = 3;
                        break;
                    case 3:
                        // rock is third
                        tool = 4;
                        break;
                    case 4: // portal is fourth
                        tool = 5;
                        break;
                    default:
                        break;
                }
                switch (tool) {
                    case 4:
                    case 3:
                    case 0:
                        grid.setCell(xVal, yVal, tool);
                }
            }
        }
    }

    /**
     *
     * @param e
     */
    public void mouseReleased(MouseEvent e) {
        double mouseX = e.getX();
        double mouseY = e.getY();
        // account for border outside of canvas
        mouseY -= outsideMargin;
        mouseX -= outsideMargin;
        int mX = (int) mouseX;
        int mY = (int) mouseY;
        // top right:
        // margin * x + xPos + (blockSize * (x-1)) : += blockSize
        //solve: margin * (x+1)) + (blockSize * (x-1)) = z, z = margin * x + xPos + margin + blockSize * x - blockSize, z = x(margin + blockSize) + xPos + margin - blockSize, (z + blockSize - margin)/(margin + blockSize) = x
        int xVal = (mX + blockSize - XMARGIN) / (margin + blockSize) - 1;
        int yVal = (mY + blockSize - YMARGIN) / (margin + blockSize) - 1;
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
        if (fullscreen && MM.getCurrent() == 0) {
            mX -= Math.max(screenW - screenH, 0) / 2 - 10;
            mY -= Math.max(screenH - screenW, 0) / 2;
        }
        // top right:
        // margin * x + xPos + (blockSize * (x-1)) : += blockSize
        //solve:
        //margin * (x+1)) + (blockSize * (x-1)) = z, z = margin * x + xPos + margin + blockSize * x - blockSize, z = x(margin + blockSize) + xPos + margin - blockSize, (z + blockSize - margin)/(margin + blockSize) = x
        int xVal = (mX + blockSize - XMARGIN) / (margin + blockSize) - 1;
        int yVal = (mY + blockSize - YMARGIN) / (margin + blockSize) - 1;
        //xVal %= this.gridSize;
        //yVal %= this.gridSize;

        boolean leftClick = e.isPrimaryButtonDown();
        if (leftClick) {
            // left click

            if (MM.getCurrent() == 2) {
                Snake.incrementHelpIndex();
            }

            // sandbox mode editing
            if (MM.getCurrent() == 4 && grid.getDiffLevel() == 0 && xVal >= 0 && xVal < grid.getWidth() && yVal >= 0 && yVal < grid.getLength()) {
                int tool = toolPanel.getCurrentTool();
                switch (tool) {
                    case 2:
                        // apple is second in list b/c no body tool
                        tool = 3;
                        break;
                    case 3:
                        // rock is third
                        tool = 4;
                        break;
                    case 4: // portal is fourth
                        tool = 5;
                        break;
                    default:
                        break;
                }
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
            
            if (fullscreen) {
                int i = 0;
                for (Button b : sandboxButtonsFS) {
                    if (b.inBounds(mX, mY)) {
                        if (i == 5) { // clear button
                            if (!GS.isGame()) {
                                grid.clear();
                            } else {
                                Snake.resetSandbox();
                            }
                        } else {
                            toolPanel.setCurrentTool(i);
                        }
                    }
                    i++;
                }
            }

            // menu catching
            if (MM.getCurrent() == 0) {
                if (easyButton.inBounds(mX, mY) && !fullscreen || (fullscreen && easyButtonFS.inBounds(mX, mY))) {
                    // easy mode chosen
                    this.grid.setDiffLevel(1);
                    MM.setCurrent(4);
                    GS.setToPreGame();
                } else if (medButton.inBounds(mX, mY) && !fullscreen || (fullscreen && medButtonFS.inBounds(mX, mY))) {
                    // medium mode chosen
                    this.grid.setDiffLevel(2);
                    MM.setCurrent(4);
                    GS.setToPreGame();
                } else if (hardButton.inBounds(mX, mY) && !fullscreen || (fullscreen && hardButtonFS.inBounds(mX, mY))) {
                    // hard mode chosen
                    this.grid.setDiffLevel(3);
                    MM.setCurrent(4);
                    GS.setToPreGame();
                } else if (impButton.inBounds(mX, mY) && !fullscreen || (fullscreen && impButtonFS.inBounds(mX, mY))) {
                    // impossible mode chosen
                    this.grid.setDiffLevel(4);
                    MM.setCurrent(4);
                    GS.setToPreGame();
                } else if (musicButton.inBounds(mX, mY) && !fullscreen || (fullscreen && musicButtonFS.inBounds(mX, mY))) {
                    // toggle music
                    if (MENU.getMusic()) {
                        MENU.turnOffMusic();
                    } else {
                        MENU.turnOnMusic();
                    }
                } else if (SFXButton.inBounds(mX, mY) && !fullscreen || (fullscreen && SFXButtonFS.inBounds(mX, mY))) {
                    // toggle sfx
                    if (MENU.getSFX()) {
                        MENU.turnOffSFX();
                    } else {
                        MENU.turnOnSFX();
                    }
                } else if (helpButton.inBounds(mX, mY) && !fullscreen || (fullscreen && helpButtonFS.inBounds(mX, mY))) {
                    // help screen
                    MM.setCurrent(2);
                    StringSelection tmpSel = new StringSelection("github.com/tfbninja/snake");
                    Clipboard tmpClp = Toolkit.getDefaultToolkit().getSystemClipboard();
                    tmpClp.setContents(tmpSel, null);
                }
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
