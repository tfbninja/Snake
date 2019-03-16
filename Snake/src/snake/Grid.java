package snake;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import javafx.util.Pair;

/**
 * Number crunching class that contains all fundamental game components
 *
 * @author Tim Barber
 */
public final class Grid extends squares implements Updateable, Loggable {

    /*
     * 0 - Blank
     * 1 - Snake head
     * 2 - Snake body
     * 3 - Apple
     * 4 - Rock
     * 5 - Unmatched portal
     * 10 and higher - portals
     */
    //<editor-fold defaultstate="collapsed" desc="instance vars">
    private int[][] playArea;
    private static int[][] savedPlayArea;
    private int[][] appleMap;
    private boolean applesFrozen = false;
    private int startx;
    private int starty;

    private boolean edgeKills = false;

    private Random random = new Random(LocalDateTime.now().getNano());
    private long seed = random.nextLong();
    private boolean useSameSeedOnReset = false;
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

    // makes sure we're not randomly picking the same sound over and over again
    private int deathCounter = 0;
    /*
     * Alright I get it, you're like "why the heck is he filling an arraylist
     * with 30 sound object, that works fine... for the regular difficulties.
     * On sandbox mode however, where you can have 624 apples on the board at a
     * time, that poor sound object is getting called constantly, and every
     * single time it gets called it has to wait for the sound to finish before
     * it plays again. So, now we have 30 sounds to split the work.
     */
    private ArrayList<Sound> bites = new ArrayList<>();
    private String biteLocation = "resources/sounds/bite.mp3";

    int[] applePos = new int[2];
    private int growBy = 1;

    private final int[] XADD = {0, 1, 0, -1};
    private final int[] YADD = {-1, 0, 1, 0};
    private int[] frameSpeeds = {3, 5, 4, 3, 2};
    private Pair<Integer, Integer> sandboxPos;
    private GameState GS;
    private final double RRPROB = 0.01;
    private final Sound RR = new Sound("resources/sounds/RR.mp3");

    private boolean extremeWarp = false;
    private boolean won = false;

    private String events = "";
//</editor-fold>

    /*
     * Directions:
     * 1 = up
     * 2 = right
     * 3 = down
     * 4 = left
     */
    /**
     *
     * @param width  The horizontal number of squares
     * @param length The vertical number of squares
     * @param startX The x-coordinate of the snake's starting position
     * @param startY The y-coordinate of the snake's starting position
     */
    public Grid(int width, int length, int startX, int startY) {
        super(width, length);
        appleMap = new int[width][length];
        startx = startX;
        starty = startY;
        playArea = new int[super.getLength()][super.getWidth()];
        Grid.savedPlayArea = new int[super.getLength()][super.getWidth()];
        for (int i = 0; i < super.getLength(); i++) {
            Arrays.fill(Grid.savedPlayArea[i], 0);
        }
        this.pos.add(new Pair<>(startX, startY)); // add head to list
        setCell(startX, startY, 1); // init head
        this.warp = new Sound("resources/sounds/warp.mp3");
        warp.setVolume(0.5);
        addDeathSounds();
        initBites(30);
        events += "Initialized. | ";
    }

    public void initBites(int amt) {
        bites.clear();
        for (int i = 0; i < amt; i++) {
            bites.add(new Sound(biteLocation));
        }
    }

    public void playBite() {
        bites.get(0).play();
        bites.add(new Sound(biteLocation));
        bites.remove(0);
    }

    /**
     * Returns the major events that happened while this class was initialized
     *
     * @return String of events
     */
    @Override
    public String getEvents() {
        return events + "end]";
    }

    /**
     * Returns the state of the important variables in this class
     *
     * @return String of variables
     */
    @Override
    public String getState() {
        return "[Init len: " + this.initialSize + ", "
                + "grow: " + this.growBy + ", "
                + "apples eaten: " + applesEaten + ", "
                + "length: " + pos.size() + ", "
                + "startx: " + startx + ", "
                + "starty: " + starty + ", "
                + "playArea: " + Arrays.deepToString(playArea) + ", "
                + "]";
    }

    /**
     * Returns playArea
     *
     * @return the int matrix holding the grid values
     */
    public int[][] getPlayArea() {
        return this.playArea;
    }

    /**
     *
     * @param s
     */
    public void setSeed(long s) {
        seed = s;
        this.useSameSeedOnReset = true;
        if (!GS.isGame()) {
            this.random.setSeed(seed);
        }
    }

    /**
     * This enables the setting that keeps the same seed for the apples
     */
    public void useSameSeedOnReset() {
        this.useSameSeedOnReset = true;
    }

    /**
     * This disables the setting that keeps the same seed for the apples
     */
    public void dontUseSameSeedOnReset() {
        this.useSameSeedOnReset = false;
    }

    /**
     * Prevents the master apple map from being changed
     */
    public void freezeApples() {
        this.applesFrozen = true;
    }

    /**
     * Allows the master apple map to be changed
     */
    public void unFreezeApples() {
        this.applesFrozen = false;
    }

    /**
     *
     * @param b
     */
    public void setExtremeStyleWarp(boolean b) {
        extremeWarp = b;
    }

    /**
     *
     * @param gs
     */
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
     * @param amt The number of frames that should be between every update cycle
     */
    public void setFrameSpeed(int amt) {
        this.frameSpeeds[0] = amt;
    }

    /**
     *
     * @param amt   The number of frames that should be between every update
     *              cycle
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
        pos.add(new Pair<>(x, y));
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
        this.initialSize = grid.initialSize;
        this.growBy = grid.growBy;
        this.pos = grid.pos;
        this.startx = grid.startx;
        this.starty = grid.starty;
        this.frameSpeeds = grid.frameSpeeds;
        this.tempDir = grid.tempDir;
        this.extremeWarp = grid.extremeWarp;
        this.useSameSeedOnReset = grid.useSameSeedOnReset;
        this.seed = grid.seed;
        setApples();
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
        for (int y = 0; y < super.getLength(); y++) {
            for (int x = 0; x < super.getWidth(); x++) {
                if (safeCheck(x, y) == type) {
                    safeSetCell(x, y, 0);
                }
            }
        }
    }

    /**
     *
     * @return The coordinates of the first portal without a pair reading left
     *         to right top down on the grid
     */
    public Pair<Integer, Integer> findUnmatchedPortal() {
        if (containsUnmatchedPortal() > -1) {
            for (int y = 0; y < super.getLength(); y++) {
                for (int x = 0; x < super.getWidth(); x++) {
                    if (isPortal(x, y) && find(safeCheck(x, y)).size() == 1) {
                        return new Pair<>(x, y);
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public boolean getExtremeWarp() {
        return this.extremeWarp;
    }

    /**
     *
     * @return
     */
    public boolean getUseSameSeed() {
        return this.useSameSeedOnReset;
    }

    /**
     *
     * @return
     */
    public long getSeed() {
        return this.seed;
    }

    /**
     *
     * @param b
     */
    public void setUseSameSeed(boolean b) {
        this.useSameSeedOnReset = b;
    }

    /**
     *
     * @return -1 if there are no unmatched portals, otherwise returns the
     *         lowest unmatched portal number
     */
    public int containsUnmatchedPortal() {
        for (int y = 0; y < super.getLength(); y++) {
            for (int x = 0; x < super.getWidth(); x++) {
                if (isPortal(x, y) && find(safeCheck(x, y)).size() == 1) {
                    return safeCheck(x, y);
                }
            }
        }
        return -1;
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
        sandboxPos = new Pair<>(x, y);
        pos.add(sandboxPos);
    }

    /**
     *
     * @return
     */
    public int highestNumber() {
        int highest = 0;
        for (int y = 0; y < super.getLength(); y++) {
            for (int x = 0; x < super.getWidth(); x++) {
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
        return safeCheck(xPos, yPos) >= 10;
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

    public static String formatFilePath(String badlyFormattedPath) {
        // replaces all "\" or "\\" characters with a "/"
        return badlyFormattedPath.replaceAll("\\\\", "/").replaceAll("//", "/");
    }

    private void addDeathSounds() {
        // addes all files in resources/sounds/death to the list of sounds to play when game is lost
        File deathSoundsFolder = new File("resources/sounds/death");
        File[] directoryListing = deathSoundsFolder.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                this.loseSounds.add(new Sound(formatFilePath(child.getPath())));
            }
            Collections.shuffle(loseSounds);
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
     * Sets the growBy variable
     *
     * @param amt the rate at which the snake grows
     */
    public void setGrowBy(int amt) {
        this.growBy = amt;
    }

    /**
     * Reverts the apples in playArea to their initial positions
     */
    public void revertToInitial() {
        for (int r = 0; r < playArea.length; r++) {
            for (int c = 0; c < playArea[r].length; c++) {
                if (appleMap[r][c] == 3) {
                    // if the map of apples contains an apple at the current spot, add it
                    playArea[r][c] = 3;
                } else if (playArea[r][c] == 3) {
                    // if the map of apples does not contain an apple at the current spot, set the spot on the main grid to blank
                    setCell(c, r, 0);
                }
            }
        }
    }

    /**
     * Resets game-by-game variables to prepare for next round
     */
    public void reset() {
        boolean temp = soundOn;
        this.soundOn = false;
        won = false;
        if (this.useSameSeedOnReset) {
            random.setSeed(seed);
        } else {
            random.setSeed(random.nextLong());
        }
        resetSnake();
        resetSize();
        revertToInitial();
        tempDir = 0;
        direction = 0;
        this.soundOn = temp;
    }

    /**
     * Resets the snake size variable to it's initial value
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
        if (diffLevel != 0) {
            this.setGrowBy(1);
            this.clear();
        }
        switch (this.diffLevel) {
            case 0:
                /*
                 * this.extremeWarp = false;
                 * this.edgeKills = sandboxEdge;
                 * this.playArea = this.sandboxPlayArea;
                 * this.setGrowBy(sandboxGrow);
                 * this.snakeSize = sandboxLen;
                 * this.pos.clear();
                 * if (sandboxPos != null) {
                 * this.pos.add(sandboxPos);
                 * startx = pos.get(0).getKey();
                 * starty = pos.get(0).getValue();
                 * } else {
                 * startx = 0;
                 * starty = 0;
                 * this.pos.add(new Pair<>(0, 0));
                 * }
                 * setCell(pos.get(0).getKey(), pos.get(0).getValue(), 1); //
                 * init head
                 * clear();
                 */
                break;
            case 1:
                this.extremeWarp = false;
                this.edgeKills = false;
                this.growBy = 2;
                clearObstacles();
                clearApples();
                setCell(pos.get(0).getKey(), pos.get(0).getValue(), 1); // init head
                newApple(); // add an apple
                break;
            case 2:
                this.extremeWarp = false;
                this.edgeKills = true;
                this.growBy = 3;
                // set middle square as rock
                clearObstacles();
                clearApples();
                //setCell(super.getWidth() / 2, super.getLength() / 2, 4);
                setCell(pos.get(0).getKey(), pos.get(0).getValue(), 1); // init head
                newApple(); // add an apple
                break;
            case 3:
                this.extremeWarp = false;
                this.growBy = 4;
                this.edgeKills = false;
                clearObstacles();

                //add 5 random rocks
                for (int i = 0; i < 5; i++) {
                    int x = (int) (random.nextDouble() * super.getWidth());
                    int y = (int) (random.nextDouble() * super.getLength());
                    while (getCell(x, y) != 0 || x == this.pos.get(0).getValue() || getNeighbors(x, y, 4, 2) > 0) {
                        // while the rock is about to be placed over a non-blank spot, or it is the same x value as the snake, or it has neighbors in a 2 cell radius, recalculate the position
                        x = (int) (random.nextDouble() * super.getWidth());
                        y = (int) (random.nextDouble() * super.getLength());
                    }
                    setCell(x, y, 4);
                }
                clearApples();
                setCell(pos.get(0).getKey(), pos.get(0).getValue(), 1); // init head
                newApple(); // add an apple
                break;
            case 4:
                this.setGrowBy(5);
                this.edgeKills = false;
                this.extremeWarp = true;
                clearObstacles();
                clearApples();
                setCell(pos.get(0).getKey(), pos.get(0).getValue(), 1); // init head
                newApple(); // add an apple
                break;
            default:
                clearApples();
                setCell(pos.get(0).getKey(), pos.get(0).getValue(), 1); // init head
                newApple(); // add an apple
                break;
        }
        setApples();
    }

    /**
     *
     * @return
     */
    public int[][] getAppleMap() {
        return this.appleMap;
    }

    /**
     * Sets the apple positions in the appleMap
     */
    public void setApples() {
        if (this.applesFrozen) {
            System.out.println("Warning, setapples did nothing, apples are frozen");
        } else {
            for (int r = 0; r < playArea.length; r++) {
                for (int c = 0; c < playArea[r].length; c++) {
                    int val = playArea[r][c];
                    if (val == 3) {
                        appleMap[r][c] = 3;
                    } else {
                        appleMap[r][c] = 0;
                    }
                }
            }
        }
    }

    /**
     *
     * @param customList
     */
    public void setApples(int[][] customList) {
        for (int r = 0; r < playArea.length; r++) {
            for (int c = 0; c < playArea[r].length; c++) {
                int val = customList[r][c];
                if (val == 3) {
                    appleMap[r][c] = 3;
                    playArea[r][c] = 3;
                } else {
                    appleMap[r][c] = 0;
                    if (playArea[r][c] == 3) {
                        playArea[r][c] = 0;
                    }
                }
            }
        }

    }

    @Override
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

    @Override
    public int getNeighbors(int x, int y, int type) {
        return getNeighbors(x, y, type, 1);
    }

    private void clearObstacles() {
        for (int x = 0; x < super.getWidth(); x++) {
            for (int y = 0; y < super.getLength(); y++) {
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
        for (int x = 0; x < super.getWidth(); x++) {
            for (int y = 0; y < super.getLength(); y++) {
                if (getCell(x, y) == 2 && !pos.contains(new Pair<>(x, y))) {
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
     * Clears all apples from the playArea
     */
    public void clearApples() {
        if (diffLevel > 0) {
            if (countVal(3) != 0) {
                for (int y = 0; y < super.getLength(); y++) {
                    for (int x = 0; x < super.getWidth() - 1; x++) {
                        if (this.getCell(x, y) == 3 || this.getCell(x, y) >= 10) {
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
        this.pos.set(this.pos.size() - 1, new Pair<>(x, y));
    }

    /**
     * Removes extra snake segments from the playArea
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
     * Returns the compass rose name corresponding to the direction variable
     *
     * @return North, South, East, or West (On weird case returns direction)
     */
    public String getDirectionName() {
        switch (direction) {
            case 1:
                return "North";
            case 2:
                return "East";
            case 3:
                return "South";
            case 4:
                return "West";
        }
        return "" + direction;
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
     * Literally turns right
     */
    public void turnRight() {
        this.tempDir++;
        tempDir = tempDir % 4;
    }

    /**
     * Literally turns left
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
     * Increases the snake size by the growth rate
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
        return safeCheck(xPos, yPos) == 3;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean isHead(int xPos, int yPos) {
        return safeCheck(xPos, yPos) == 1;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean isBody(int xPos, int yPos) {
        return safeCheck(xPos, yPos) == 2;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean isOccupied(int xPos, int yPos) {
        return safeCheck(xPos, yPos) != 0;
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
        int index = (int) (random.nextDouble() * list.size());
        return list.get(index);
    }

    /**
     *
     * @param list
     * @return
     */
    public Pair<Integer, Integer> pickPair(ArrayList<Pair<Integer, Integer>> list) {
        int index = (int) (random.nextDouble() * list.size());
        return list.get(index);
    }

    /**
     *
     * @param type
     * @return
     */
    public ArrayList<Pair<Integer, Integer>> find(int type) {
        ArrayList<Pair<Integer, Integer>> posList = new ArrayList<>();
        for (int y = 0; y < super.getLength(); y++) {
            for (int x = 0; x < super.getWidth(); x++) {
                if (safeCheck(x, y) == type) {
                    posList.add(new Pair<>(x, y));

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
     * Kills the snake
     */
    public void kill() {
        GS.setToPostGame();
    }

    /**
     * iT sHoUlD bE cAlLeD wIn
     */
    public void won() {
        won = true;
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public boolean willKill(int xPos, int yPos) {
        return safeCheck(xPos, yPos) != 0 && safeCheck(xPos, yPos) != 3;
    }

    /**
     *
     * @param type
     * @return
     */
    public boolean willKill(int type) {
        return type != 0 && type != 3;
    }

    /**
     *
     * @return
     */
    public int getLeft() {
        int x = this.pos.get(0).getKey();
        int y = this.pos.get(0).getValue();
        switch (direction) {
            case 2:
                return safeCheck(x, y - 1);
            case 3:
                return safeCheck(x + 1, y);
            case 4:
                return safeCheck(x, y + 1);
            default:
                return safeCheck(x - 1, y);
        }
    }

    /**
     *
     * @return
     */
    public int getRight() {
        int x = this.pos.get(0).getKey();
        int y = this.pos.get(0).getValue();
        switch (direction) {
            case 2:
                return safeCheck(x, y + 1);
            case 3:
                return safeCheck(x - 1, y);
            case 4:
                return safeCheck(x, y - 1);
            default:
                return safeCheck(x + 1, y);
        }
    }

    /**
     *
     * @return
     */
    public int getFront() {
        int x = this.pos.get(0).getKey();
        int y = this.pos.get(0).getValue();
        switch (direction) {
            case 2:
                return safeCheck(x + 1, y);
            case 3:
                return safeCheck(x, y + 1);
            case 4:
                return safeCheck(x - 1, y);
            default:
                return safeCheck(x, y - 1);
        }
    }

    /**
     * Resets game-by-game variables in preparation for next round. Also clears
     * grid of snake segments
     */
    public void resetSnake() {
        int[] headPos2 = getStartPos();
        removeAll(1);
        removeAll(2);
        setPos(headPos2[0], headPos2[1]);
    }

    /**
     * Plays a pseudo-random death sound (using the time as a seed). Doesn't
     * repeat sounds.
     */
    public void die() {
        events += " die at " + applesEaten + " | ";
        random.setSeed(LocalDateTime.now().getNano());
        if (!won) {
            if (random.nextInt((int) (1.0 / RRPROB)) == random.nextInt((int) (1 / RRPROB))) {
                if (soundOn) {
                    RR.play();
                }
            }
            if (deathCounter > 0) {
                Sound temp = loseSounds.get(deathCounter - 1);
                loseSounds.remove(deathCounter - 1);
                if (deathCounter >= loseSounds.size() - 4) { // we don't need to play EVERY sound before we use the other ones, but definitely not two in a row, that gets annoying
                    Collections.shuffle(loseSounds);
                    loseSounds.add(temp);
                    deathCounter = 0;
                }
            }
            if (soundOn) {
                loseSounds.get(deathCounter).play();
            }
            deathCounter++;
        }
        if (this.useSameSeedOnReset) {
            random.setSeed(seed);
        } else {
            random.setSeed(LocalDateTime.now().getNano());
        }
    }

    /**
     *
     */
    @Override
    public void update() {
        if (GS.isGame()) {
            if (countVal(3) == 0 && diffLevel > 0 && diffLevel <= 4) {
                newApple();
            }
            if (this.snakeSize < 1) {
                GS.setToPostGame();
                die();
                return;
            }

            boolean trigger = false;
            if (this.direction != tempDir) {
                trigger = true;
            }
            this.direction = this.tempDir;
            if (trigger) {
                events += getDirectionName().charAt(0);
            }
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
                if (this.extremeWarp) {
                    // extreme warp, warp x with y
                    boolean playWarpSound2 = false;
                    while (nextX < 0 || nextX >= super.getWidth() || nextY < 0 || nextY >= super.getLength()) {
                        if (nextX < 0) {
                            nextX = super.getWidth() - nextY - 1;
                            nextY = 0;
                            this.direction = 3;
                            this.tempDir = 3;
                            playWarpSound2 = true;
                        }
                        if (nextX >= super.getWidth()) {
                            nextX = super.getWidth() - nextY - 1;
                            nextY = super.getLength() - 1;
                            this.direction = 1;
                            this.tempDir = 1;
                            playWarpSound2 = true;
                        }

                        if (nextY < 0) {
                            nextY = nextX;
                            nextX = super.getWidth() - 1;
                            this.direction = 4;
                            this.tempDir = 4;
                            playWarpSound2 = true;
                        }
                        if (nextY >= super.getLength()) {
                            nextY = nextX;
                            nextX = 0;
                            this.direction = 2;
                            this.tempDir = 2;
                            playWarpSound2 = true;
                        }
                        if (playWarpSound2 && soundOn) {
                            warp.play();
                            events += "!";
                        }
                    }
                } else {
                    if (nextX < 0) {
                        nextX = super.getWidth() - 1;
                        playWarpSound = true;
                    } else if (nextX >= super.getWidth()) {
                        nextX = 0;
                        playWarpSound = true;
                    }
                    if (nextY < 0) {
                        nextY = super.getLength() - 1;
                        playWarpSound = true;
                    } else if (nextY >= super.getLength()) {
                        nextY = 0;
                        playWarpSound = true;
                    }
                    if (playWarpSound && soundOn) {
                        warp.play();
                    }
                }
            } else {
                // edge kills
                if (nextX < 0) {
                    GS.setToPostGame();
                    die();
                }
                if (nextX >= super.getWidth()) {
                    GS.setToPostGame();
                    die();
                }
                if (nextY < 0) {
                    GS.setToPostGame();
                    die();
                }
                if (nextY >= super.getLength()) {
                    GS.setToPostGame();
                    die();
                }
            }

            if (GS.isGame() && (this.isRock(nextX, nextY) || this.edgeKills && (nextX >= super.getWidth() || nextY >= super.getLength() || nextX < 0 || nextY < 0))) {
                // collision with wall or rock
                events += " Wall or Rock";
                GS.setToPostGame();
                die();
            } else if (GS.isGame() && isSnake(nextX, nextY)) {
                // collision with self
                events += " Snake";
                GS.setToPostGame();
                die();
            } else if (GS.isGame() && this.isApple(nextX, nextY)) {
                // ate an apple
                this.applesEaten++;
                events += "A";

                grow();
                clearApples();
                newApple();
                if (this.snakeSize < 1) {
                    GS.setToPostGame();
                    die();
                    return;
                }
                this.pos.add(0, new Pair<>(nextX, nextY)); // add segment in front
                this.setCell(nextX, nextY, 1); // update grid
                this.removeExtra();
                if (countVal(2) < pos.size() - 1) {
                    pos.add(new Pair<>(headX, headY));
                    this.setCell(headX, headY, 2);
                } else {
                    this.setCell(headX, headY, 0);
                }
                if (soundOn) {
                    playBite();
                }
            } else if (GS.isGame() && this.isPortal(nextX, nextY)) {
                // if next square is a portal
                events += "P";
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
                } else if (this.isApple(nextX, nextY)) {
                    // ate an apple
                    this.applesEaten++;
                    if (soundOn) {
                        playBite();
                    }
                    grow();
                    clearApples();
                    newApple();
                    if (this.snakeSize < 1) {
                        GS.setToPostGame();
                        die();
                        return;
                    }
                    this.pos.add(0, new Pair<>(nextX, nextY)); // add segment in front
                    this.setCell(nextX, nextY, 1); // update grid
                    this.removeExtra();
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
     * Not really used but saves the play area to the savedPlayArea variable
     */
    public void savePlayArea() {
        Grid.savedPlayArea = this.playArea;
    }

    /**
     * sets all squares to 0
     */
    public void clear() {
        this.playArea = new int[super.getLength()][super.getWidth()];
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
        if (value != 3 && playArea[y][x] != 3) {
            this.appleMap[y][x] = value;
        }
        this.playArea[y][x] = value;
    }

    /**
     *
     * @return
     */
    public String exportCode() {
        String output = "*****************\n";
        for (int r = 0; r < super.getLength(); r++) {
            for (int c = 0; c < super.getWidth(); c++) {
                if (safeCheck(c, r) != 0) {
                    output += ".setCell(" + c + ", " + r + ", " + safeCheck(c, r) + ");\n";
                }
            }
        }
        output += "*****************";
        return output;
    }

    /**
     *
     * @param x
     * @param y
     * @param value
     */
    public void safeSetCell(int x, int y, int value) {
        while (x < 0) {
            x = super.getWidth() + x;
        }
        while (x > super.getWidth() - 1) {
            x -= (super.getWidth() - 1);
        }
        while (y < 0) {
            y = super.getLength() + y;
        }
        while (y > super.getLength() - 1) {
            y -= (super.getLength() - 1);
        }
        setCell(x, y, value);
    }

    /**
     *
     * @param pos
     * @param value
     */
    public void setCell(Pair<Integer, Integer> pos, int value) {
        setCell(pos.getValue(), pos.getKey(), value);
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
        if (xPos > super.getWidth()) {
            xPos = xPos % super.getWidth();
        }
        if (yPos > super.getLength()) {
            yPos = yPos % super.getLength();
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
         * for (int y = 0; y < super.getLength(); y++) {
         * for (int x = 0; x < super.getWidth(); x++) {
         * if (this.playArea[y][x] == value) {
         * count++;
         * }
         * }
         * }
         */
        int count2 = 0;
        for (int x = 0; x < super.getWidth(); x++) {
            for (int y = 0; y < super.getLength(); y++) {
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
        if (!GS.isPostGame()) {
            setApples();
        }
    }

    /**
     *
     * @return
     */
    public boolean isClear() {
        return countVal(0) == super.getWidth() * super.getLength();
    }

    @Override
    public String toString() {
        String output = "Grid: " + Arrays.deepToString(playArea);
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
