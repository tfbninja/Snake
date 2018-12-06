package snake;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String deltaTime;

    public Grid() {
        this.width = 10;
        this.length = 10;
        this.playArea = new int[this.length][this.width];
        this.lastPlayArea = this.playArea;
        this.savedPlayArea = new int[this.length][this.width];
        for (int i = 0; i < this.length; i++) {
            Arrays.fill(this.savedPlayArea[i], 0);
        }
        this.startTime = null;
        this.deltaTime = "";
    }

    public Grid(int width, int length, int numMines) {
        this.width = width;
        this.length = length;
        this.playArea = new int[this.length][this.width];
        this.lastPlayArea = this.playArea;
        this.savedPlayArea = new int[this.length][this.width];
        for (int i = 0; i < this.length; i++) {
            Arrays.fill(this.savedPlayArea[i], 0);
        }
        this.startTime = LocalDateTime.now();
        this.deltaTime = "";
    }

    public String stopTimer() {
        this.endTime = LocalDateTime.now();
        return this.getTimer(endTime);
    }

    public String getTimer() {
        String seconds = String.valueOf((LocalDateTime.now().getSecond() - this.startTime.getSecond()) % 60);
        String minutes = String.valueOf(LocalDateTime.now().getMinute() - this.startTime.getMinute());
        this.deltaTime = minutes + ":" + seconds;
        return this.deltaTime;
    }

    public String getTimer(LocalDateTime endTime) {
        String seconds = String.valueOf(endTime.now().getSecond() - this.startTime.getSecond());
        String minutes = String.valueOf(endTime.now().getMinute() - this.startTime.getMinute());
        this.deltaTime = minutes + ":" + seconds;
        return this.deltaTime;
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
        int count = 0;
        for (int y = 0; y < this.length; y++) {
            for (int x = 0; x < this.width; x++) {
                if (this.playArea[y][x] == value) {
                    count++;
                }
            }
        }
        int count2 = 0;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.length; y++) {
                if (safeCheck(x, y) == value) {
                    count2++;
                }
            }
        }
        return count;
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
