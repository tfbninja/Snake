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
     * 5 - Unmatched portal
     * 10 and higher - portals
     */
    private int width;
    private int length;
    private int[][] playArea;
    private static int[][] savedPlayArea;
    private int startx;
    private int starty;

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
    private int initialSize = 5;
    private int snakeSize = initialSize;

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
    private int[][] sandboxPlayArea = new int[25][25];
    private GameState GS;

    /*
     * Directions:
     * 1 = up
     * 2 = right
     * 3 = down
     * 4 = left
     */
    /**
     *
     * @param width The horizontal number of squares
     * @param length The vertical number of squares
     * @param startX The x-coordinate of the snake's starting position
     * @param startY The y-coordinate of the snake's starting position
     */
    public Grid(int width, int length, int startX, int startY) {
        startx = startX;
        starty = startY;
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

    public void addGameState(GameState gs) {
        GS = gs;
    }

    /**
     *
     * @return The initial position of the snake
     */
    public int[] getStartPos() {
        int[] temp = {startx, starty};
        return temp;
    }

    /**
     *
     * @param amt The number of frames that should be shown per update cycle
     */
    public void setFrameSpeed(int amt) {
        this.frameSpeeds[0] = amt;
    }

    /**
     *
     * @param amt The number of frames that should be shown per update cycle
     * @param level The difficulty level to change
     */
    public void setFrameSpeed(int amt, int level) {
        this.frameSpeeds[level] = amt;
    }

    /**
     *
     * @param x The x-coordinate of the snake's new position
     * @param y The y-coordinate of the snake's new position
     */
    public void setPos(int x, int y) {
        startx = x;
        starty = y;
        pos.clear();
        pos.add(new Pair<Integer, Integer>(x, y));
        safeSetCell(x, y, 1);
    }

    /**
     *
     * @param grid The grid object to copy (most of) the values from
     */
    public void overwrite(Grid grid) {
        this.playArea = grid.playArea;
        this.snakeSize = grid.snakeSize;
        this.edgeKills = grid.edgeKills;
        this.diffLevel = grid.diffLevel;
        this.direction = grid.direction;
        this.growBy = grid.growBy;
        this.pos = grid.pos;
        this.frameSpeeds = grid.frameSpeeds;
        this.tempDir = grid.tempDir;
    }

    /**
     *
     * @return The initial length of the snake
     */
    public int getInitialLength() {
        return this.initialSize;
    }

    /**
     *
     * @return The increment value for the snake's size
     */
    public int getGrowBy() {
        return this.growBy;
    }

    /**
     *
     * @param type The kind of int to remove and set to zero in the playArea
     */
    public void removeAll(int type) {
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                if (safeCheck(x, y) == type) {
                    safeSetCell(x, y, 0);
                }
            }
        }
    }

    /**
     *
     * @return The coordinates of the first portal without a pair reading left
     * to right top down on the grid
     */
    public Pair<Integer, Integer> findUnmatchedPortal() {
        if (containsUnmatchedPortal() > -1) {
            for (int y = 0; y < length; y++) {
                for (int x = 0; x < width; x++) {
                    if (isPortal(x, y) && find(safeCheck(x, y)).size() == 1) {
                        return new Pair<Integer, Integer>(x, y);
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @return -1 if there are no unmatched portals, otherwise returns the
     * lowest unmatched portal number
     */
    public int containsUnmatchedPortal() {
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                if (isPortal(x, y) && find(safeCheck(x, y)).size() == 1) {
                    return safeCheck(x, y);
                }
            }
        }
        return -1;
    }

    /**
     *
     * @param playArea The two-dimensional list of ints describing various snake
     * objects
     */
    public void setSandbox(int[][] playArea) {
        this.sandboxPlayArea = playArea;
    }

    /**
     *
     * @param val Whether the walls kill the snake or not
     */
    public void setSandboxEdgeKills(boolean val) {
        this.sandboxEdge = val;
    }

    /**
     *
     * @param amt The initial length of the snake in sandbox mode
     */
    public void setSandboxLen(int amt) {
        this.sandboxLen = amt;
    }

    /**
     *
     * @param val The number frames that pass before another update cycle
     */
    public void setSandboxFrameSpeed(int val) {
        this.frameSpeeds[0] = val;
    }

    /**
     *
     * @param x The x-coordinate of the head in sandbox mode
     * @param y The x-coordinate of the head in sandbox mode
     */
    public void setSandboxHeadPos(int x, int y) {
        pos.clear();
        sandboxPos = new Pair<Integer, Integer>(x, y);
        pos.add(sandboxPos);
    }

    @Override
    public int[][] getPlayArea() {
        return this.playArea;
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void addPortal(int x1, int y1, int x2, int y2) {
        int portalNum = highestNumber() >= 10 ? highestNumber() + 1 : 10;
        this.setCell(x1, y1, portalNum);
        this.setCell(x2, y2, portalNum);
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean isPortal(int xPos, int yPos) {
        if (safeCheck(xPos, yPos) >= 10) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
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

    /**
     *
     * @param sound
     */
    public void setSoundOn(boolean sound) {
        this.soundOn = sound;
    }

    /**
     *
     * @return
     */
    public int getApplesEaten() {
        return this.applesEaten;
    }

    /**
     *
     * @param amt
     */
    public void setGrowBy(int amt) {
        this.growBy = amt;
    }

    /**
     *
     * @param amt
     */
    public void setSandboxGrowBy(int amt) {
        sandboxGrow = amt;
    }

    /**
     *
     */
    public void reset() {
        //applesEaten = 0;
        direction = 0;
        tempDir = 0;
        gameOver = false;
        snakeSize = initialSize;
    }

    /**
     *
     */
    public void resetSize() {
        snakeSize = initialSize;
    }

    /**
     *
     * @param amt
     */
    public void setApplesEaten(int amt) {
        applesEaten = amt;
    }

    private void setObstacles() {
        this.setGrowBy(1);
        this.clear();
        switch (this.diffLevel) {
            case 0:
                //System.out.println("Position set");
                this.edgeKills = sandboxEdge;
                this.playArea = this.sandboxPlayArea;
                this.setGrowBy(sandboxGrow);
                this.snakeSize = sandboxLen;
                this.pos.clear();
                if (sandboxPos != null) {
                    this.pos.add(sandboxPos);
                    startx = pos.get(0).getKey();
                    starty = pos.get(0).getValue();
                } else {
                    startx = 0;
                    starty = 0;
                    this.pos.add(new Pair<Integer, Integer>(0, 0));
                }
                //System.out.println("setting head cell at " + pos.get(0).getKey() + ", " + pos.get(0).getValue());
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

    /**
     *
     * @param level
     */
    public void setDiffLevel(int level) {
        if (level <= maxDiffLevel && level >= minDiffLevel) {
            this.diffLevel = level;
            setObstacles();
        }
    }

    /**
     *
     * @return
     */
    public int getDiffLevel() {
        return this.diffLevel;
    }

    /**
     *
     * @return
     */
    public int getFrameSpeed() {
        return frameSpeeds[diffLevel];
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @return
     */
    public boolean getEdgeKills() {
        return this.edgeKills;
    }

    /**
     *
     * @param choice
     */
    public void setEdgeKills(boolean choice) {
        this.edgeKills = choice;
    }

    /**
     *
     */
    public void clearApples() {
        if (diffLevel > 0) {
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
    }

    /**
     *
     * @return
     */
    public int[] getApplePos() {
        return applePos;
    }

    private int[] newApple() {
        ArrayList<Pair<Integer, Integer>> openSpots = find(0);
        try {
            Pair<Integer, Integer> spot = this.pickPair(openSpots);

            int[] newPos = {spot.getKey(), spot.getValue()};

            int tries = 0;
            while (newPos[0] < 0 || newPos[1] < 0 || this.isOccupied(newPos[0], newPos[1])) {
                tries++;
                if (tries > 2000) {
                    return null;
                }
                newPos[0] = random.nextInt(this.width);
                newPos[1] = random.nextInt(this.length);
            }

            this.setCell(newPos[0], newPos[1], 3);
            applePos[0] = newPos[0];
            applePos[1] = newPos[1];
            return newPos;
        } catch (Exception x) {
            return null;
        }
    }

    /**
     *
     * @param x
     * @param y
     */
    public void setTail(int x, int y) {
        this.pos.set(this.pos.size() - 1, new Pair<Integer, Integer>(x, y));
    }

    /**
     *
     */
    public void chopTail() {
        while (pos.size() > snakeSize) {
            this.setCell(this.pos.remove(this.pos.size() - 1), 0);
        }
    }

    /**
     *
     * @return
     */
    public boolean getGameOver() {
        return this.gameOver;
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
    public int getLength() {
        return this.length;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setLength(int length) {
        this.length = length;
    }

    /**
     *
     * @return
     */
    public int getHeadX() {
        return this.pos.get(0).getKey();
    }

    /**
     *
     * @return
     */
    public int getHeadY() {
        return this.pos.get(0).getValue();
    }

    /**
     *
     * @return
     */
    public int[] getHeadPos() {
        int[] headPos = {getHeadX(), getHeadY()};
        return headPos;
    }

    /**
     *
     * @return
     */
    public int getDirection() {
        return this.direction;
    }

    /**
     *
     * @param dir
     */
    public void attemptSetDirection(int dir) {
        if (Math.abs(this.direction - dir) != 2 && Math.abs(this.tempDir - dir) != 2) {
            this.tempDir = dir;
        }
    }

    /**
     *
     * @return
     */
    public int getNorth() {
        return safeCheck(this.pos.get(0).getKey(), this.pos.get(0).getValue() - 1);
    }

    /**
     *
     * @return
     */
    public int getEast() {
        return safeCheck(this.pos.get(0).getKey() - 1, this.pos.get(0).getValue());
    }

    /**
     *
     * @return
     */
    public int getSouth() {
        return safeCheck(this.pos.get(0).getKey(), this.pos.get(0).getValue() + 1);
    }

    /**
     *
     * @return
     */
    public int getWest() {
        return safeCheck(this.pos.get(0).getKey() + 1, this.pos.get(0).getValue());
    }

    /**
     *
     */
    public void turnRight() {
        this.tempDir++;
        tempDir = tempDir % 4;
    }

    /**
     *
     */
    public void turnLeft() {
        this.tempDir--;
        tempDir = tempDir % 4;
    }

    /**
     *
     * @return
     */
    public int getSize() {
        return this.snakeSize;
    }

    /**
     *
     * @param amt
     */
    public void setSize(int amt) {
        this.snakeSize = amt;
    }

    /**
     *
     */
    public void grow() {
        this.snakeSize += this.growBy;
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @param dir
     */
    public void setDirection(int dir) {
        this.direction = dir;
        this.tempDir = dir;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean isSnake(int xPos, int yPos) {
        return this.playArea[yPos][xPos] == 1 || this.playArea[yPos][xPos] == 2;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean isBlank(int xPos, int yPos) {
        return this.playArea[yPos][xPos] == 0;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean isApple(int xPos, int yPos) {
        return this.playArea[yPos][xPos] == 3;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean isHead(int xPos, int yPos) {
        return this.playArea[yPos][xPos] == 1;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean isBody(int xPos, int yPos) {
        return this.playArea[yPos][xPos] == 2;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean isOccupied(int xPos, int yPos) {
        return this.playArea[yPos][xPos] != 0;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean isRock(int xPos, int yPos) {
        return this.playArea[yPos][xPos] == 4;
    }

    /**
     *
     * @param list
     * @return
     */
    public Sound pick(ArrayList<Sound> list) {
        int index = (int) (Math.random() * list.size());
        return list.get(index);
    }

    public Pair<Integer, Integer> pickPair(ArrayList<Pair<Integer, Integer>> list) {
        int index = (int) (Math.random() * list.size());
        return list.get(index);
    }

    /**
     *
     * @param type
     * @return
     */
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

    /**
     *
     * @param originalPortalX
     * @param originalPortalY
     * @return
     */
    public int[] otherPortalPos(int originalPortalX, int originalPortalY) {
        ArrayList<Pair<Integer, Integer>> portalLocations = find(safeCheck(originalPortalX, originalPortalY));
        int[] otherPos = {-1, -1};
        //if (safeCheck(originalPortalX, originalPortalY) > 10) {
        portalLocations.remove(new Pair<>(originalPortalX, originalPortalY));
        try {
            otherPos[0] = portalLocations.get(0).getKey();
            otherPos[1] = portalLocations.get(0).getValue();
        } catch (IndexOutOfBoundsException e) {
            otherPos[0] = originalPortalX;
            otherPos[1] = originalPortalY;
        }
        //}
        return otherPos;
    }

    /**
     *
     * @param amt
     */
    public void setInitialSize(int amt) {
        initialSize = amt;
    }

    /**
     *
     */
    public void nextGen() {
        if (GS.isGame()) {
            if (this.snakeSize < 1) {
                GS.setToPostGame();
                pick(loseSounds).play();
                return;
            }

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
                    GS.setToPostGame();
                    if (soundOn) {
                        pick(loseSounds).play();
                    }
                }
                if (nextX >= this.width) {
                    GS.setToPostGame();
                    if (soundOn) {
                        pick(loseSounds).play();
                    }
                }
                if (nextY < 0) {
                    GS.setToPostGame();
                    if (soundOn) {
                        pick(loseSounds).play();
                    }
                }
                if (nextY >= this.length) {
                    GS.setToPostGame();
                    if (soundOn) {
                        pick(loseSounds).play();
                    }
                }
            }

            if (GS.isGame() && (this.isRock(nextX, nextY) || this.edgeKills && (nextX >= this.width || nextY >= this.length || nextX < 0 || nextY < 0))) {
                // collision with wall or rock
                GS.setToPostGame();
                if (soundOn) {
                    pick(loseSounds).play();
                }
            } else if (GS.isGame() && isSnake(nextX, nextY)) {
                // collision with self
                GS.setToPostGame();
                if (soundOn) {
                    pick(loseSounds).play();
                }
            } else if (GS.isGame() && this.isApple(nextX, nextY)) {
                // ate an apple
                this.applesEaten++;
                if (soundOn) {
                    bite.play();
                }
                grow();
                clearApples();
                newApple();
                if (this.snakeSize < 1) {
                    GS.setToPostGame();
                    pick(loseSounds).play();
                    return;
                }
                this.pos.add(0, new Pair<Integer, Integer>(nextX, nextY)); // add segment in front
                this.setCell(nextX, nextY, 1); // update grid
                this.removeExtra();
                if (countVal(2) < pos.size() - 1) {
                    pos.add(new Pair<>(headX, headY));
                    this.setCell(headX, headY, 2);
                } else {
                    this.setCell(headX, headY, 0);
                }
            } else if (GS.isGame() && this.isPortal(nextX, nextY)) {
                // if next square is a portal

                while (isPortal(nextX, nextY)) {
                    // while the square being teleported to contains a portal...
                    // teleport to the next portal
                    int oldX = nextX, oldY = nextY;
                    nextX = this.otherPortalPos(oldX, oldY)[0] + XADD[direction - 1];
                    nextY = this.otherPortalPos(oldX, oldY)[1] + YADD[direction - 1];
                }
                // set the last open square where the head used to be as a body segment
                // unless it teleported into itself, in which case die
                if (this.isBody(headX, headY)) {
                    GS.setToPostGame();
                    return;
                }
                this.safeSetCell(headX, headY, 2);

                headX = nextX;
                headY = nextY;
                while (isPortal(nextX, nextY)) {
                    // while the square being teleported to contains a portal...
                    // teleport to the next portal
                    int oldX = nextX, oldY = nextY;
                    nextX = this.otherPortalPos(oldX, oldY)[0] + XADD[direction - 1];
                    nextY = this.otherPortalPos(oldX, oldY)[1] + YADD[direction - 1];
                }
                this.safeSetCell(headX, headY, 1);

                this.pos.add(0, new Pair<>(headX, headY)); // add segment in front
                this.removeExtra();
                if (countVal(2) < pos.size() - 1) {
                    pos.add(new Pair<>(headX, headY));
                    this.safeSetCell(headX, headY, 2);
                } else {
                    this.safeSetCell(headX, headY, 0);
                }

            } else if (GS.isGame() && this.isBlank(nextX, nextY)) {
                this.pos.add(0, new Pair<>(nextX, nextY)); // add segment in front
                this.setCell(nextX, nextY, 1); // update grid
                this.removeExtra();
                if (countVal(2) < pos.size() - 1) {
                    pos.add(new Pair<>(headX, headY));
                    this.safeSetCell(headX, headY, 2);
                } else {
                    this.safeSetCell(headX, headY, 0);
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public int[][] getSavedPlayArea() {
        return Grid.savedPlayArea;
    }

    /**
     *
     */
    public void savePlayArea() {
        Grid.savedPlayArea = this.playArea;
    }

    /**
     *
     */
    public void clear() {
        this.playArea = new int[this.length][this.width];
    }

    /**
     *
     * @param xPosition
     * @param yPosition
     * @param cells
     */
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

    /**
     *
     * @param x
     * @param y
     * @param value
     */
    public void setCell(int x, int y, int value) {
        this.playArea[y][x] = value;
    }

    /**
     *
     * @param x
     * @param y
     * @param value
     */
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

    /**
     *
     * @param pos
     * @param value
     */
    public void setCell(Pair<Integer, Integer> pos, int value) {
        this.playArea[pos.getValue()][pos.getKey()] = value;
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public int getCell(int x, int y) {
        return this.playArea[y][x];
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
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

    /**
     *
     * @param square
     * @return
     */
    public int safeCheck(Pair<Integer, Integer> square) {
        return safeCheck(square.getKey(), square.getValue());
    }

    /**
     *
     * @param value
     * @return
     */
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

    /**
     *
     * @param newPlayArea
     */
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
