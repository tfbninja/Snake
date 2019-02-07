package snake;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Timothy
 */
public class Block implements Locatable {

    private int xPos;
    private int yPos;
    private int width;
    private int height;
    private Color color;
    private 
    private String name;

    /**
     *
     */
    public Block() {
        this.xPos = 0;
        this.yPos = 0;
        this.width = 0;
        this.height = 0;
        this.color = Color.MINTCREAM;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @param width
     * @param height
     * @param color
     */
    public Block(int xPos, int yPos, int width, int height, Color color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @param width
     * @param height
     * @param color
     * @param name
     */
    public Block(int xPos, int yPos, int width, int height, Color color, String name) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.color = color;
        this.name = name;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @param width
     * @param height
     */
    public Block(int xPos, int yPos, int width, int height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.color = Color.BLACK;
    }

    /**
     *
     * @param xPos
     */
    @Override
    public void setX(int xPos) {
        this.xPos = xPos;
    }

    /**
     *
     * @param yPos
     */
    @Override
    public void setY(int yPos) {
        this.yPos = yPos;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param x
     * @param y
     */
    @Override
    public void setPos(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }

    /**
     *
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     *
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     *
     * @param color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     *
     * @param canvas
     */
    public void draw(Canvas canvas) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(this.color);
        graphics.fillRect(getX(), getY(), getWidth(), getHeight());
    }

    /**
     *
     * @param canvas
     * @param radius
     */
    public void drawRounded(Canvas canvas, double radius) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(this.color);
        graphics.fillRoundRect(getX(), getY(), getWidth(), getHeight(), radius, radius);
    }

    /**
     *
     * @return
     */
    public int getX() {
        return this.xPos;
    }

    /**
     *
     * @return
     */
    public int getY() {
        return this.yPos;
    }

    /**
     *
     * @return
     */
    public int getWidth() {
        return this.width;
    }

    /**
     *
     * @return
     */
    public int getHeight() {
        return this.height;
    }

    /**
     *
     * @return
     */
    public Color getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return "X: " + this.xPos + ", Y: " + this.yPos + ", Width: " + this.width + ", Height: " + this.height + ", Color: " + this.color;
    }
}
