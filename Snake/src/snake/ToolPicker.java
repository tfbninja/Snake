package snake;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import javafx.scene.text.Font;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 *
 * @author Timothy
 */
public class ToolPicker {

    private Canvas canvas;
    private final int WIDTH;
    private final int HEIGHT;
    private ArrayList<String> toolNames = new ArrayList<>();
    private ArrayList<Color> toolColors = new ArrayList<>();
    private Font font;

    private final int MINTOOLW = 50;
    private final int MINTOOLH = 50;
    private int toolSize = 15;
    private int toolXMargin;
    private int toolYMargin;
    private int toolsPerLine;

    public ToolPicker(int width, int height) {
        WIDTH = width;
        HEIGHT = height;

        System.out.println("tools per line: " + toolsPerLine + ", toolXMargin: " + toolXMargin + ", toolYMargin: " + toolYMargin);
        canvas = new Canvas(width, height);
    }

    public void addTool(String name, Color color) {
        toolNames.add(name);
        toolColors.add(color);
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public void setFont(Font fontToUse) {
        this.font = fontToUse;
    }

    public void drawTools() {
        toolsPerLine = WIDTH / (2 * MINTOOLW);
        toolXMargin = (WIDTH - toolsPerLine * MINTOOLW) / (2 + toolsPerLine);
        toolYMargin = HEIGHT > 100 ? MINTOOLH / 2 : HEIGHT / 20;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        //gc.fillRect(WIDTH / 2, 0, 1, HEIGHT);
        int xCounter = WIDTH / 2 - toolSize / 2;
        int yCounter = toolYMargin;
        int toolsOnCurrentLine = 0;
        for (int i = 0; i < toolNames.size(); i++) {
            toolsOnCurrentLine++;
            gc.setFill(toolColors.get(i));
            gc.setFont(font);
            gc.fillRect(xCounter, yCounter, toolSize, toolSize);
            gc.fillText(toolNames.get(i), xCounter - 10, yCounter - 5);
            xCounter += this.MINTOOLH - toolSize * 2;
            if (toolsOnCurrentLine < this.toolsPerLine) {
                xCounter += toolSize * 2;
            } else {
                yCounter += MINTOOLH + this.toolYMargin;
                xCounter = WIDTH / 2 - toolSize / 2;
            }
        }
    }
}
