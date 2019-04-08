package snake;

/**
 *
 * @author Tim Barber
 */
public class Button {

    private double x;
    private double y;
    private double w;
    private double h;
    private double scale;

    /**
     * Default, sets everything to 0
     */
    public Button() {
        this.x = 0;
        this.y = 0;
        this.w = 0;
        this.h = 0;
        this.scale = 1;
    }

    /**
     *
     * @param x top left x coordinate
     * @param y top left y coordinate
     * @param w width
     * @param h height
     */
    public Button(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.scale = 1;
    }

    /**
     *
     * @param x     top left x coordinate
     * @param y     top left y coordinate
     * @param w     width
     * @param h     height
     * @param scale scale
     */
    public Button(double x, double y, double w, double h, double scale) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.scale = scale;
    }

    /**
     *
     * @return height * scale
     */
    public double getH() {
        return h * scale;
    }

    /**
     *
     * @param h
     */
    public void setH(double h) {
        this.h = h;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     *
     * @return width * scale
     */
    public double getW() {
        return w * scale;
    }

    /**
     *
     * @param w width
     */
    public void setW(double w) {
        this.w = w;
    }

    /**
     *
     * @return top left x-coordinate * scale
     */
    public double getX() {
        return x * scale;
    }

    /**
     *
     * @param x top left x-coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     *
     * @return top left y-coordinate * scale
     */
    public double getY() {
        return y * scale;
    }

    /**
     *
     * @param y top left y-coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     *
     * @param mX mouse X
     * @param mY mouse Y
     * @return whether the mouse clicked over the button
     */
    public boolean inBounds(double mX, double mY) {
        return mX >= getX() && mY >= getY() && mX <= getX() + getW() && mY <= getY() + getH();
    }

    @Override
    public String toString() {
        return "Button{" + "x=" + x + ", y=" + y + ", w=" + w + ", h=" + h + ", scale=" + scale + "}";
    }

}

/*
 * The MIT License
 *
 * Copyright (c) 2019 Tim Barber.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
