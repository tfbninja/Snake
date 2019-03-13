package snake;

/**
 * This class controls the current state of the game
 *
 * @author Tim Barber
 */
public class GameState extends Controller {

    private boolean preGame;
    private boolean game;
    private boolean postGame;

    /**
     * Only constructor
     *
     * @param state The value of which state the game is currently in: pre,
     *              during, or post
     */
    public GameState(int state) {
        switch (state) {
            case 1:
                setToPreGame();
            case 2:
                setToGame();
            case 3:
                setToPostGame();
        }
    }

    /**
     * Sets the preGame value to true and the others to false
     */
    public final void setToPreGame() {
        turnOff();
        preGame = true;
    }

    /**
     * Sets the game value to true and the others to false
     */
    public final void setToGame() {
        turnOff();
        game = true;
    }

    /**
     * Sets the postGame value to true and the others to false
     */
    public final void setToPostGame() {
        turnOff();
        postGame = true;
    }

    /**
     *
     * @return Which state the game is in
     */
    public int getState() {
        if (preGame) {
            return 1;
        } else if (game) {
            return 2;
        } else if (postGame) {
            return 3;
        } else {
            return -1;
        }
    }

    /**
     *
     * @return Whether or not it's preGame
     */
    public boolean isPreGame() {
        return preGame;
    }

    /**
     *
     * @return Whether or not it's game time
     */
    public boolean isGame() {
        return game;
    }

    /**
     *
     * @return Whether or not it's postGame
     */
    public boolean isPostGame() {
        return postGame;
    }

    /*
     * Sets all vars to false
     */
    @Override
    public void turnOff() {
        preGame = false;
        game = false;
        postGame = false;
    }
}
