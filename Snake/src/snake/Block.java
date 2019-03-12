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
    private String name;

    /**
     * Default constructor - sets everything to 0
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
     * @param xPos the x position
     * @param yPos the y position
     * @param width the horizontal width
     * @param height the vertical height
     * @param color the color
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
     * @param xPos the x position
     * @param yPos the y position
     * @param width the horizontal width
     * @param height the vertical height
     * @param color the color
     * @param name the name
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
     * @param xPos the x position
     * @param yPos the y position
     * @param width the horizontal width
     * @param height the vertical height
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
     * @param xPos the new x position of the block
     */
    @Override
    public void setX(int xPos) {
        this.xPos = xPos;
    }

    /**
     *
     * @param yPos the new y position of the block
     */
    @Override
    public void setY(int yPos) {
        this.yPos = yPos;
    }

    /**
     *
     * @return the name of the block
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name the new name of the block
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param x the new x coordinate for the block
     * @param y the new y coordinate for the block
     */
    @Override
    public void setPos(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }

    /**
     *
     * @param width the new width of the block
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     *
     * @param height the new height of the block
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     *
     * @param color the new color of the block
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Draws a block on the canvas
     *
     * @param canvas the canvas to draw the block on
     */
    public void update(Canvas canvas) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(this.color);
        graphics.fillRect(getX(), getY(), getWidth(), getHeight());
    }

    /**
     *
     * @param canvas the canvas on which to draw
     * @param radius the radius of the corner
     */
    public void drawRounded(Canvas canvas, double radius) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(this.color);
        graphics.fillRoundRect(getX(), getY(), getWidth(), getHeight(), radius, radius);
    }

    /**
     *
     * @return the x position
     */
    public int getX() {
        return this.xPos;
    }

    /**
     *
     * @return the y position
     */
    public int getY() {
        return this.yPos;
    }

    /**
     *
     * @return the width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     *
     * @return the height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     *
     * @return the color
     */
    public Color getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return "X: " + this.xPos + ", Y: " + this.yPos + ", Width: " + this.width + ", Height: " + this.height + ", Color: " + this.color;
    }
}
