package snake;

/**
 *
 * @author Tim Barber
 */
public abstract class squares {

    private int width;
    private int length;
    private int[][] playArea;

    /**
     *
     * @return the horizontal size of the grid
     */
    public int getWidth() {
        return width;
    }

    /**
     *
     * @return the vertical size of the grid
     */
    public int getLength() {
        return length;
    }

    /**
     *
     * @param width the new horizontal size of the grid
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     *
     * @param length the new vertical size of the grid
     */
    void setLength(int length) {
        this.length = length;
    }

    /**
     *
     * @return the grid as it's native int[][] type
     */
    public int[][] getPlayArea() {
        return playArea;
    }

    /**
     * This is not usually the best approach, please Override
     *
     * @param xPos The column number
     * @param yPos The row number
     * @return the int stored in the grid at (xPos, yPos). If xPos or yPos is
     * out of bounds, returns -1
     */
    public int safeCheck(int xPos, int yPos) {
        try {
            return playArea[yPos][xPos];
        } catch (ArrayIndexOutOfBoundsException b) {
            return -1;
        }
    }

    /**
     * @return the number of occupied spaces matching param type near (x, y)
     * @param x the x component of the chosen space
     * @param y the y component of the chosen space
     * @param type the spaces which contain an int matching type will be counted
     * @param radius the number of spaces out from the initial, e.g. a radius of
     * one will count squares in a 3x3 box excluding the middle square
     */
    public abstract int getNeighbors(int x, int y, int type, int radius);

    /**
     *
     * @param x The column number
     * @param y The row number
     * @param type Which neighbors should be counted
     * @see getNeighbors(int x, int y, int type, int radius);
     * @return same as getNeighbors(int x, int y, int type, int radius), but
     * with implied radius 1
     */
    public abstract int getNeighbors(int x, int y, int type);
}

/*
 * The MIT License
 *
 * Copyright (c) 2018 Tim Barber.
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
