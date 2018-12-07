package snake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.util.Pair;

/**
 *
 * @author Tim Barber
 */
public class Grid {

    /*
     * 0 - Blank
     * 1 - Snake head
     * 2 - Snake body
     * 3 - Apple
     */
    private int width;
    private int length;
    private int[][] playArea;
    private int[][] lastPlayArea;
    private static int[][] savedPlayArea;

    private boolean edgeKills = false;

    private Random random = new Random();

    private boolean gameOver = false;

    // snake vars
    private int direction = 1;
    private int tempDir = 1;
    private ArrayList<Pair<Integer, Integer>> pos = new ArrayList<>();
    private int snakeSize = 1;

    /*
     * Directions:
     * 1 = up
     * 2 = right
     * 3 = down
     * 4 = left
     */
    public Grid() {
        this.width = 10;
        this.length = 10;
        this.playArea = new int[this.length][this.width];
        this.lastPlayArea = this.playArea;
        this.savedPlayArea = new int[this.length][this.width];
        for (int i = 0; i < this.length; i++) {
            Arrays.fill(this.savedPlayArea[i], 0);
        }
    }

    public Grid(int width, int length, int startX, int startY) {
        this.width = width;
        this.length = length;
        this.playArea = new int[this.length][this.width];
        this.lastPlayArea = this.playArea;
        this.savedPlayArea = new int[this.length][this.width];
        for (int i = 0; i < this.length; i++) {
            Arrays.fill(this.savedPlayArea[i], 0);
        }
        this.pos.add(new Pair(startX, startY)); // add head to list
        setCell(startX, startY, 1); // init head
        newApple(); // add an apple
    }

    public void removeExtra() {
        while (pos.size() > snakeSize) {
            pos.remove(pos.size() - 1);
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                if (getCell(x, y) == 2 && !pos.contains(new Pair(x, y))) {
                    setCell(x, y, 0);
                }
            }
        }
    }

    public boolean getEdgeKills() {
        return this.edgeKills;
    }

    public void setEdgeKills(boolean choice) {
        this.edgeKills = choice;
    }

    public void clearApples() {
        if (countVal(3) != 0) {
            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.length - 1; y++) {
                    if (this.getCell(x, y) == 3) {
                        this.setCell(x, y, 0);
                    }
                }
            }
        }
    }

    public int[] newApple() {
        int[] pos = {-1, -1};
        while (pos[0] < 0 || pos[1] < 0 || this.isSnake(pos[0], pos[1])) {
            pos[0] = random.nextInt(this.width);
            pos[1] = random.nextInt(this.length);
        }
        this.setCell(pos[0], pos[1], 3);
        return pos;
    }

    public void setTail(int x, int y) {
        this.pos.set(this.pos.size() - 1, new Pair(x, y));
    }

    public void chopTail() {
        while (pos.size() > snakeSize) {
            this.pos.remove(this.pos.size() - 1);
        }
    }

    public boolean getGameOver() {
        return this.gameOver;
    }

    public int getWidth() {
        return this.width;
    }

    public int getLength() {
        return this.length;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getHeadX() {
        return this.pos.get(0).getKey();
    }

    public int getHeadY() {
        return this.pos.get(0).getValue();
    }

    public int[] getHeadPos() {
        int[] pos = {getHeadX(), getHeadY()};
        return pos;
    }

    public int getDirection() {
        return this.direction;
    }

    public void attemptSetDirection(int dir) {
        if (Math.abs(this.direction - dir) != 2 && Math.abs(this.tempDir - dir) != 2) {
            this.tempDir = dir;
        }
    }

    public int getSize() {
        return this.snakeSize;
    }

    public void setSize(int amt) {
        this.snakeSize = amt;
    }

    public void grow() {
        this.snakeSize++;
    }

    public int[] nextPos() {
        int[] newPos = this.getHeadPos();
        int[] xAdd = {0, 1, 0, -1};
        int[] yAdd = {-1, 0, 1, 0};
        newPos[0] += xAdd[direction - 1];
        newPos[1] += yAdd[direction - 1];
        return newPos;
    }

    public boolean isSnake(int xPos, int yPos) {
        return this.playArea[yPos][xPos] == 1 || this.playArea[yPos][xPos] == 2;
    }

    public boolean isBlank(int xPos, int yPos) {
        return this.playArea[yPos][xPos] == 0;
    }

    public boolean isApple(int xPos, int yPos) {
        return this.playArea[yPos][xPos] == 3;
    }

    public boolean isHead(int xPos, int yPos) {
        return this.playArea[yPos][xPos] == 1;
    }

    public boolean isBody(int xPos, int yPos) {
        return this.playArea[yPos][xPos] == 2;
    }

    public boolean isOccupied(int xPos, int yPos) {
        return this.playArea[yPos][xPos] != 0;
    }

    public void nextGen() {
        this.direction = this.tempDir;
        int nextX = nextPos()[0];
        int nextY = nextPos()[1];
        int headX = pos.get(0).getKey();
        int headY = pos.get(0).getValue();

        if (!this.edgeKills) {
            if (nextX < 0) {
                nextX = this.width - 1;
            } else if (nextX >= this.width) {
                nextX = 0;
            }
            if (nextY < 0) {
                nextY = this.length - 1;
            } else if (nextY >= this.length) {
                nextY = 0;
            }
        }

        if (this.isApple(nextX, nextY)) {
            // ate an apple
            grow();
            this.pos.add(this.pos.get(this.pos.size() - 1));
            clearApples();
            newApple();
        } else if (!this.edgeKills && (nextX >= this.width || nextY >= this.length || nextX < 0 || nextY < 0)) {
            // collision with wall or self
            this.gameOver = true;
        } else if (isSnake(nextX, nextY)) {
            this.gameOver = true;
        }

        if (this.isBlank(nextX, nextY)) {
            if (this.countVal(2) + 2 > pos.size()) {
                // if the amt of snake body + the head + the square about to be filled is more than the length, we need to chop the last part
                this.chopTail();
            }

            this.pos.add(0, new Pair(nextX, nextY)); // add segment in front
            this.setCell(nextX, nextY, 1); // update grid
            this.removeExtra();
            if (countVal(2) < pos.size() - 1) {
                pos.add(new Pair(headX, headY));
                this.setCell(headX, headY, 2);
            } else {
                this.setCell(headX, headY, 0);
            }

        }
    }

    public int[][] getPlayArea() {
        return this.playArea;
    }

    public int[][] getSavedPlayArea() {
        return this.savedPlayArea;
    }

    public void savePlayArea() {
        this.savedPlayArea = this.playArea;
    }

    public void revertToSaved() {
        this.lastPlayArea = this.playArea;
        this.playArea = this.savedPlayArea;
    }

    public void clear() {
        this.lastPlayArea = this.playArea;
        this.playArea = new int[this.length][this.width];
    }

    public void setCells(int xPosition, int yPosition, int[][] cells) {
        this.lastPlayArea = this.playArea;
        int[][] newArea = this.playArea;
        for (int y = yPosition; y < yPosition + cells.length; y++) {
            for (int x = xPosition; x < xPosition + cells[0].length; x++) {
                //System.out.println("x: " + x + ", xPosition: " + xPosition + "\ny: " + y + ", yPosition: " + yPosition);
                newArea[y][x] = cells[y - yPosition][x - xPosition];
            }
        }
        this.playArea = newArea;
    }

    public void setCell(int x, int y, int value) {
        this.lastPlayArea = this.playArea;
        this.playArea[y][x] = value;
    }

    public int getCell(int x, int y) {
        return this.playArea[y][x];
    }

    public int safeCheck(int xPos, int yPos) {
        try {
            return this.playArea[yPos][xPos];
        } catch (ArrayIndexOutOfBoundsException b) {
            return -1;
        }
    }

    public int countVal(int value) {
        /*
         * int count = 0;
         * for (int y = 0; y < this.length; y++) {
         * for (int x = 0; x < this.width; x++) {
         * if (this.playArea[y][x] == value) {
         * count++;
         * }
         * }
         * }
         */
        int count2 = 0;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.length; y++) {
                if (safeCheck(x, y) == value) {
                    count2++;
                }
            }
        }
        return count2;
    }

    public void setPlayArea(int[][] newPlayArea) {
        this.playArea = newPlayArea;
    }

    @Override
    public String toString() {
        String output = "";
        for (int y = 0; y < this.width; y++) {
            for (int x = 0; x < this.length; x++) {
                output += String.valueOf(this.playArea[y][x]);
                output += " ";
            }
            output += "\n";
        }
        return output;
    }
}
/*
 * The MIT License
 *
 * Copyright (c) 2018 Tim Barber.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
