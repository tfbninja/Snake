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

    public ToolPicker(String title, int width, int height, int xPos, int yPos, Scene scene) {
        super(title, width, height, xPos, yPos, scene);
        currentTool = new Block(width - (TOOLW + TOOLX), TOOLY, TOOLW, TOOLH, Color.web("2b2b2b"));
        canvas = new Canvas(width, height);
    }

    public void addTool(Color color, String name) {
        tools.add(new Block(TOOLX, TOOLY + (tools.size() * (TOOLH + TOOLYSPACE)), TOOLW, TOOLH, color, name));
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public void setFont(Font fontToUse) {
        this.font = fontToUse;
    }

    public String getName(int index) {
        return this.tools.get(index).getName();
    }

    public Color getColor(int index) {
        return this.tools.get(index).getColor();
    }

    public int getWidth(int index) {
        return this.tools.get(index).getWidth();
    }

    public int getHeight(int index) {
        return this.tools.get(index).getHeight();
    }

    public int getX(int index) {
        return this.tools.get(index).getX();
    }

    public int getY(int index) {
        return this.tools.get(index).getY();
    }

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
