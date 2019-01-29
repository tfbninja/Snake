package snake;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.util.Random;
import javafx.util.Pair;

/**
 *
 * @author Tim Barber
 */
public class Grid implements squares {

    /*
     * 0 - Blank
     * 1 - Snake head
     * 2 - Snake body
     * 3 - Apple
     * 4 - Rock
     * 10 and higher - portals
     */
    private int width;
    private int length;
    private int[][] playArea;
    private static int[][] savedPlayArea;

    private boolean edgeKills = false;

    private Random random = new Random();

    private boolean gameOver = false;

    private int diffLevel = 1;
    private int minDiffLevel = 0;
    private int maxDiffLevel = 4;

    // snake vars
    private int direction = 0;
    private int tempDir = 0;
    private ArrayList<Pair<Integer, Integer>> pos = new ArrayList<>();
    private int snakeSize = 5;
    private int applesEaten = 0;

    // sounds
    private boolean soundOn = true;
    private Sound warp;
    private ArrayList<Sound> loseSounds = new ArrayList<>();
    private Sound bite;

    int[] applePos = new int[2];
    private int growBy = 1;

    private final int[] XADD = {0, 1, 0, -1};
    private final int[] YADD = {-1, 0, 1, 0};
    private int[] frameSpeeds = {3, 5, 4, 3, 2};
    private int sandboxGrow = 1;
    private int sandboxLen;
    private boolean sandboxEdge = false;
    private Pair<Integer, Integer> sandboxPos;


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
        this.savedPlayArea = new int[this.length][this.width];
        for (int i = 0; i < this.length; i++) {
            Arrays.fill(this.savedPlayArea[i], 0);
        }
        this.warp = new Sound("resources/sounds/warp.mp3");
        warp.setVolume(0.5);
        addDeathSounds();

        this.bite = new Sound("resources/sounds/bite2.wav");
    }

    public Grid(int width, int length, int startX, int startY) {
        this.width = width;
        this.length = length;
        this.playArea = new int[this.length][this.width];
        this.savedPlayArea = new int[this.length][this.width];
        for (int i = 0; i < this.length; i++) {
            Arrays.fill(this.savedPlayArea[i], 0);
        }
        this.pos.add(new Pair<>(startX, startY)); // add head to list
        setCell(startX, startY, 1); // init head
        this.warp = new Sound("resources/sounds/warp.mp3");
        warp.setVolume(0.5);
        addDeathSounds();
        this.bite = new Sound("resources/sounds/bite2.wav");
    }

    public Grid(int width, int length) {
        // for testing only
        this.width = width;
        this.length = length;
        this.playArea = new int[this.length][this.width];
        this.savedPlayArea = new int[this.length][this.width];
        for (int i = 0; i < this.length; i++) {
            Arrays.fill(this.savedPlayArea[i], 0);
        }
    }

    public void setSandboxEdgeKills(boolean val) {
        this.sandboxEdge = val;
    }

    public void setSandboxLen(int amt) {
        this.sandboxLen = amt;
    }

    public void setSandboxFrameSpeed(int val) {
        this.frameSpeeds[0] = val;
    }

    public void setSandboxHeadPos(int x, int y) {
        sandboxPos = new Pair<Integer, Integer>(x, y);
    }

    @Override
    public int[][] getPlayArea() {
        return this.playArea;
    }

    public int highestNumber() {
        int highest = 0;
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                if (safeCheck(x, y) > highest) {
                    highest = safeCheck(x, y);
                }
            }
        }
        return highest;
    }

    public void addPortal(int x1, int y1, int x2, int y2) {
        int portalNum = highestNumber() >= 10 ? highestNumber() + 1 : 10;
        this.setCell(x1, y1, portalNum);
        this.setCell(x2, y2, portalNum);
    }

    public boolean isPortal(int xPos, int yPos) {
        if (safeCheck(xPos, yPos) >= 10) {
            return true;
        }
        return false;
    }

    public int getContiguousSize(int xPos, int yPos) {
        int size = 1;
        int type = this.safeCheck(xPos, yPos);
        int[][] simplified = this.playArea;
        for (int y = 0; y < simplified.length; y++) {
            for (int x = 0; x < simplified[y].length; x++) {
                if (simplified[y][x] != type) {
                    simplified[y][x] = -1;
                }
            }
        }
        return size;
    }

    private int touchingNeighbors(int xPos, int yPos) {
        int count = 0;
        if (safeCheck(xPos - 1, yPos) == safeCheck(xPos, yPos)) {
            count++;
        }
        if (safeCheck(xPos + 1, yPos) == safeCheck(xPos, yPos)) {
            count++;
        }
        if (safeCheck(xPos, yPos - 1) == safeCheck(xPos, yPos)) {
            count++;
        }
        if (safeCheck(xPos, yPos + 1) == safeCheck(xPos, yPos)) {
            count++;
        }
        return count;
    }

    private String formatFilePath(String badlyFormattedPath) {
        // replaces all "\" or "\\" characters with a "/"
        return badlyFormattedPath.replaceAll("\\\\", "/").replaceAll("//", "/");
    }

    private void addDeathSounds() {
        // addes all files in resources/sounds/death to the list of sounds to play when game is lost
        File deathSoundsFolder = new File("resources/sounds/death");
        File[] directoryListing = deathSoundsFolder.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                //System.out.println(formatFilePath(child.getPath()));
                this.loseSounds.add(new Sound(formatFilePath(child.getPath())));
            }
        } else {
            System.out.println("Cannot find the resources/sounds/death folder... try setting the working directory to the folder that Snake.java or Snake.jar is contained in.");
        }
    }

    public void setSoundOn(boolean sound) {
        this.soundOn = sound;
    }

    public int getApplesEaten() {
        return this.applesEaten;
    }

    public void setGrowBy(int amt) {
        this.growBy = amt;
    }

    public void setSandboxGrowBy(int amt) {
        sandboxGrow = amt;
    }

    private void setObstacles() {
        this.setGrowBy(1);
        this.clear();
        switch (this.diffLevel) {
            case 0:
                System.out.println("Position set");
                this.edgeKills = sandboxEdge;
                this.setGrowBy(sandboxGrow);
                this.snakeSize = sandboxLen;
                this.pos.set(0, sandboxPos);
                setCell(pos.get(0).getKey(), pos.get(0).getValue(), 1); // init head
                clear();
                break;
            case 1:
                this.edgeKills = false;
                this.growBy = 2;
                clearObstacles();
                clearApples();
                newApple(); // add an apple
                setCell(pos.get(0).getKey(), pos.get(0).getValue(), 1); // init head
                break;
            case 2:
                this.edgeKills = true;
                this.growBy = 3;
                // set middle square as rock
                clearObstacles();
                clearApples();
                //setCell(this.width / 2, this.length / 2, 4);
                newApple(); // add an apple
                setCell(pos.get(0).getKey(), pos.get(0).getValue(), 1); // init head
                break;
            case 3:
                this.growBy = 4;
                this.edgeKills = false;
                clearObstacles();

                //add 5 random rocks
                for (int i = 0; i < 5; i++) {
                    int x = (int) (Math.random() * this.width);
                    int y = (int) (Math.random() * this.length);
                    while (getCell(x, y) != 0 || x == this.pos.get(0).getValue() || getNeighbors(x, y, 4, 2) > 0) {
                        // while the rock is about to be placed over a non-blank spot, or it is the same x value as the snake, or it has neighbors in a 2 cell radius, recalculate the position
                        x = (int) (Math.random() * this.width);
                        y = (int) (Math.random() * this.length);
                    }
                    setCell(x, y, 4);
                }
                clearApples();
                newApple(); // add an apple
                setCell(pos.get(0).getKey(), pos.get(0).getValue(), 1); // init head
                break;
            case 4:
                this.setGrowBy(5);
                this.edgeKills = false;
                // set alternating pattern of rocks around edge
                clearObstacles();
                if (this.width % 2 == 1) {
                    for (int x = 0; x < this.width; x += 7) {
                        setCell(x, 0, 4);
                        setCell(x, this.length - 1, 4);
                    }
                } else {
                    for (int x = 0; x < this.width / 2; x += 7) {
                        setCell(x, 0, 4);
                        setCell(x, this.length - 1, 4);
                    }
                    for (int x = this.width - 1; x > this.width / 2 + 1; x -= 2) {
                        setCell(x, 0, 4);
                        setCell(x, this.length - 1, 4);
                    }
                }
                if (this.length % 2 == 1) {
                    for (int y = 0; y < this.length; y += 7) {
                        setCell(0, y, 4);
                        setCell(this.width - 1, y, 4);
                    }
                } else {
                    for (int y = 0; y < this.length / 2; y += 7) {
                        setCell(0, y, 4);
                        setCell(this.width - 1, y, 4);
                    }
                    for (int y = this.length - 1; y > this.length / 2 + 1; y -= 2) {
                        setCell(0, y, 4);
                        setCell(this.width - 1, y, 4);
                    }
                }
                clearObstacles();
                clearApples();
                newApple(); // add an apple
                setCell(pos.get(0).getKey(), pos.get(0).getValue(), 1); // init head
                break;
            default:
                clearApples();
                newApple(); // add an apple
                setCell(pos.get(0).getKey(), pos.get(0).getValue(), 1); // init head
                break;
        }

    }

    public int getNeighbors(int x, int y, int type, int radius) {
        int count = 0;
        for (int tempX = x - radius; tempX <= x + radius; tempX++) {
            for (int tempY = y - radius; tempY <= y + radius; tempY++) {
                if (safeCheck(tempX, tempY) == type) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getNeighbors(int x, int y, int type) {
        return getNeighbors(x, y, type, 1);
    }

    private void clearObstacles() {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.length; y++) {
                if (getCell(x, y) == 4) {
                    setCell(x, y, 0);
                }
            }
        }
    }

    public void setDiffLevel(int level) {
        if (level <= maxDiffLevel && level >= minDiffLevel) {
            this.diffLevel = level;
            setObstacles();
        }
    }

    public int getDiffLevel() {
        return this.diffLevel;
    }

    public int getFrameSpeed() {
        return frameSpeeds[diffLevel];
    }

    public int getGensPerFrame() {
        int[] genRepeats = {1, 1, 1, 1, 1};
        return genRepeats[diffLevel];
    }

    private void removeExtra() {
        while (pos.size() > snakeSize) {
            pos.remove(pos.size() - 1);
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                if (getCell(x, y) == 2 && !pos.contains(new Pair<Integer, Integer>(x, y))) {
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

    public int[] getApplePos() {
        return applePos;
    }

    private int[] newApple() {
        int[] newPos = {-1, -1};
        while (newPos[0] < 0 || newPos[1] < 0 || this.isOccupied(newPos[0], newPos[1])) {
            newPos[0] = random.nextInt(this.width);
            newPos[1] = random.nextInt(this.length);
        }
        this.setCell(newPos[0], newPos[1], 3);
        applePos[0] = newPos[0];
        applePos[1] = newPos[1];
        return newPos;
    }

    public void setTail(int x, int y) {
        this.pos.set(this.pos.size() - 1, new Pair<Integer, Integer>(x, y));
    }

    public void chopTail() {
        while (pos.size() > snakeSize) {
            this.setCell(this.pos.remove(this.pos.size() - 1), 0);
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
        int[] headPos = {getHeadX(), getHeadY()};
        return headPos;
    }

    public int getDirection() {
        return this.direction;
    }

    public void attemptSetDirection(int dir) {
        if (Math.abs(this.direction - dir) != 2 && Math.abs(this.tempDir - dir) != 2) {
            this.tempDir = dir;
        }
    }

    public int getNorth() {
        return safeCheck(this.pos.get(0).getKey(), this.pos.get(0).getValue() - 1);
    }

    public int getEast() {
        return safeCheck(this.pos.get(0).getKey() - 1, this.pos.get(0).getValue());
    }

    public int getSouth() {
        return safeCheck(this.pos.get(0).getKey(), this.pos.get(0).getValue() + 1);
    }

    public int getWest() {
        return safeCheck(this.pos.get(0).getKey() + 1, this.pos.get(0).getValue());
    }

    public void turnRight() {
        this.tempDir++;
        tempDir = tempDir % 4;
    }

    public void turnLeft() {
        this.tempDir--;
        tempDir = tempDir % 4;
    }

    public int getSize() {
        return this.snakeSize;
    }

    public void setSize(int amt) {
        this.snakeSize = amt;
    }

    public void grow() {
        this.snakeSize += this.growBy;
    }

    public int[] nextPos() {
        int[] newPos = this.getHeadPos();

        //System.out.println("direction = " + direction);
        try {
            newPos[0] += XADD[direction - 1];
            newPos[1] += YADD[direction - 1];
        } catch (Exception e) {
            System.out.println("ERROR");
            System.out.println("Current direction: " + direction);
            System.out.println("New Position: " + newPos[0] + " " + newPos[1]);
        }
        return newPos;
    }

    public void setDirection(int dir) {
        this.direction = dir;
        this.tempDir = dir;
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

    public boolean isRock(int xPos, int yPos) {
        return this.playArea[yPos][xPos] == 4;
    }

    public Sound pick(ArrayList<Sound> list) {
        int index = (int) (Math.random() * list.size());
        return list.get(index);
    }

    public ArrayList<Pair<Integer, Integer>> find(int type) {
        ArrayList<Pair<Integer, Integer>> posList = new ArrayList<>();
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                if (safeCheck(x, y) == type) {
                    posList.add(new Pair<Integer, Integer>(x, y));

                }
            }
        }
        return posList;
    }

    public int[] otherPortalPos(int originalPortalX, int originalPortalY) {
        ArrayList<Pair<Integer, Integer>> portalLocations = find(safeCheck(originalPortalX, originalPortalY));
        int[] otherPos = {-1, -1};
        //if (safeCheck(originalPortalX, originalPortalY) > 10) {
        portalLocations.remove(new Pair<Integer, Integer>(originalPortalX, originalPortalY));
        otherPos[0] = portalLocations.get(0).getKey();
        otherPos[1] = portalLocations.get(0).getValue();
        //}
        return otherPos;
    }

    public void nextGen() {
        this.direction = this.tempDir;
        int nextX = nextPos()[0];
        int nextY = nextPos()[1];
        int headX = pos.get(0).getKey();
        int headY = pos.get(0).getValue();

        if (this.countVal(2) + 2 > pos.size()) {
            // if the amt of snake body + the head + the square about to be filled is more than the length, we need to chop the last part
            this.chopTail();
        }

        if (!this.edgeKills) {
            boolean playWarpSound = false;
            if (this.diffLevel < 4) {
                if (nextX < 0) {
                    nextX = this.width - 1;
                    playWarpSound = true;
                } else if (nextX >= this.width) {
                    nextX = 0;
                    playWarpSound = true;
                }
                if (nextY < 0) {
                    nextY = this.length - 1;
                    playWarpSound = true;
                } else if (nextY >= this.length) {
                    nextY = 0;
                    playWarpSound = true;
                }
                if (playWarpSound && soundOn) {
                    warp.play();
                }
            } else {
                // extreme mode, warp x with y
                boolean playWarpSound2 = false;
                while (nextX < 0 || nextX >= this.width || nextY < 0 || nextY >= this.length) {
                    if (nextX < 0) {
                        nextX = this.width - nextY - 1;
                        nextY = 0;
                        this.direction = 3;
                        this.tempDir = 3;
                        playWarpSound2 = true;
                    }
                    if (nextX >= this.width) {
                        nextX = this.width - nextY - 1;
                        nextY = this.length - 1;
                        this.direction = 1;
                        this.tempDir = 1;
                        playWarpSound2 = true;
                    }

                    if (nextY < 0) {
                        nextY = nextX;
                        nextX = this.width - 1;
                        this.direction = 4;
                        this.tempDir = 4;
                        playWarpSound2 = true;
                    }
                    if (nextY >= this.length) {
                        nextY = nextX;
                        nextX = 0;
                        this.direction = 2;
                        this.tempDir = 2;
                        playWarpSound2 = true;
                    }
                    if (playWarpSound2 && soundOn) {
                        warp.play();
                    }
                }
            }
        } else {
            // edge kills
            if (nextX < 0) {
                this.gameOver = true;
                if (soundOn) {
                    pick(loseSounds).play();
                }
            }
            if (nextX >= this.width) {
                this.gameOver = true;
                if (soundOn) {
                    pick(loseSounds).play();
                }
            }
            if (nextY < 0) {
                this.gameOver = true;
                if (soundOn) {
                    pick(loseSounds).play();
                }
            }
            if (nextY >= this.length) {
                this.gameOver = true;
                if (soundOn) {
                    pick(loseSounds).play();
                }
            }
        }

        if (!this.gameOver && this.isRock(nextX, nextY) || this.edgeKills && (nextX >= this.width || nextY >= this.length || nextX < 0 || nextY < 0)) {
            // collision with wall or rock
            this.gameOver = true;
            if (soundOn) {
                pick(loseSounds).play();
            }
        } else if (!this.gameOver && isSnake(nextX, nextY)) {
            // collision with self
            this.gameOver = true;
            if (soundOn) {
                pick(loseSounds).play();
            }
        } else if (!this.gameOver && this.isApple(nextX, nextY)) {
            // ate an apple
            this.applesEaten++;
            if (soundOn) {
                bite.play();
            }
            grow();
            this.pos.add(0, new Pair<Integer, Integer>(nextX, nextY)); // add segment in front
            this.setCell(nextX, nextY, 1); // update grid
            this.removeExtra();
            if (countVal(2) < pos.size() - 1) {
                pos.add(new Pair<Integer, Integer>(headX, headY));
                this.setCell(headX, headY, 2);
            } else {
                this.setCell(headX, headY, 0);
            }
            clearApples();
            newApple();
            //nextGen(); // don't pause
        } else if (!this.gameOver && this.isPortal(nextX, nextY)) {
            // if next square is a portal
            while (this.isPortal(nextX, nextY)) {
                int oldX = nextX, oldY = nextY;

                nextX = this.otherPortalPos(oldX, oldY)[0];
                nextY = this.otherPortalPos(oldX, oldY)[1];
                this.setCell(headX, headY, 2);

                headX = nextX + XADD[direction - 1];
                headY = nextY + YADD[direction - 1];
                this.safeSetCell(headX, headY, 1);
                this.pos.add(0, new Pair<Integer, Integer>(headX, headY)); // add segment in front
                this.removeExtra();
                if (countVal(2) < pos.size() - 1) {
                    pos.add(new Pair<Integer, Integer>(headX, headY));
                    this.safeSetCell(headX, headY, 2);
                } else {
                    this.safeSetCell(headX, headY, 0);
                }
            }
        } else if (!this.gameOver && this.isBlank(nextX, nextY)) {
            this.pos.add(0, new Pair<Integer, Integer>(nextX, nextY)); // add segment in front
            this.setCell(nextX, nextY, 1); // update grid
            this.removeExtra();
            if (countVal(2) < pos.size() - 1) {
                pos.add(new Pair<Integer, Integer>(headX, headY));
                this.setCell(headX, headY, 2);
            } else {
                this.setCell(headX, headY, 0);
            }
        }
    }

    /*
     * public int[][] getPlayArea() {
     * return this.playArea;
     * }
     */
    public int[][] getSavedPlayArea() {
        return this.savedPlayArea;
    }

    public void savePlayArea() {
        this.savedPlayArea = this.playArea;
    }

    public void clear() {
        this.playArea = new int[this.length][this.width];
    }

    public void setCells(int xPosition, int yPosition, int[][] cells) {
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
        this.playArea[y][x] = value;
    }

    public void safeSetCell(int x, int y, int value) {
        while (x < 0) {
            x = width + x;
        }
        while (x > width - 1) {
            x -= (width - 1);
        }
        while (y < 0) {
            y = length + y;
        }
        while (y > length - 1) {
            y -= (length - 1);
        }
        this.playArea[y][x] = value;
    }

    public void setCell(Pair<Integer, Integer> pos, int value) {
        this.playArea[pos.getValue()][pos.getKey()] = value;
    }

    public int getCell(int x, int y) {
        return this.playArea[y][x];
    }

    public int safeCheck(int xPos, int yPos) {
        if (xPos > width) {
            xPos = xPos % width;
        }
        if (yPos > length) {
            yPos = yPos % length;
        }
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
