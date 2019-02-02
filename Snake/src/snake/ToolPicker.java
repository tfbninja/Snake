package snake;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 *
 * @author Timothy
 */
public class ToolPicker extends Window {

    private Canvas canvas;
    private ArrayList<Block> tools = new ArrayList<>();
    private Font font;

    private final int TOOLX = 10;
    private final int TOOLY = 10;
    private final int TOOLW = 30;
    private final int TOOLH = 30;
    private final int TOOLYSPACE = 15;

    private Block currentTool;
    private int toolNum = 0;

    /**
     *
     * @param title
     * @param width
     * @param height
     * @param xPos
     * @param yPos
     * @param scene
     */
    public ToolPicker(String title, int width, int height, int xPos, int yPos, Scene scene) {
        super(title, width, height, xPos, yPos, scene);
        currentTool = new Block(width - (TOOLW + TOOLX), TOOLY, TOOLW, TOOLH, Color.web("2b2b2b"));
        canvas = new Canvas(width, height);
    }

    /**
     *
     * @param color
     * @param name
     */
    public void addTool(Color color, String name) {
        tools.add(new Block(TOOLX, TOOLY + (tools.size() * (TOOLH + TOOLYSPACE)), TOOLW, TOOLH, color, name));
    }

    /**
     *
     * @return
     */
    public Canvas getCanvas() {
        return this.canvas;
    }

    /**
     *
     * @param fontToUse
     */
    public void setFont(Font fontToUse) {
        this.font = fontToUse;
    }

    /**
     *
     * @param index
     * @return
     */
    public String getName(int index) {
        return this.tools.get(index).getName();
    }

    /**
     *
     * @param index
     * @return
     */
    public Color getColor(int index) {
        return this.tools.get(index).getColor();
    }

    /**
     *
     * @param index
     * @return
     */
    public int getWidth(int index) {
        return this.tools.get(index).getWidth();
    }

    /**
     *
     * @param index
     * @return
     */
    public int getHeight(int index) {
        return this.tools.get(index).getHeight();
    }

    /**
     *
     * @param index
     * @return
     */
    public int getX(int index) {
        return this.tools.get(index).getX();
    }

    /**
     *
     * @param index
     * @return
     */
    public int getY(int index) {
        return this.tools.get(index).getY();
    }

    /**
     *
     */
    public void drawTools() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Rectangle showing the current tool
        currentTool.draw(canvas);

        for (int i = 0; i < tools.size(); i++) {
            gc.setFont(font);
            tools.get(i).draw(canvas);
            gc.fillText(tools.get(i).getName(), super.getWidth() - 4, tools.get(i).getY() - 5);
        }
    }
}
