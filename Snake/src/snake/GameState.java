package snake;

/**
 *
 * @author Tim Barber
 */
public class GameState {

    private boolean preGame;
    private boolean game;
    private boolean postGame;

    /**
     *
     * @param state The value of which state the game is currently in: pre,
     * during, or post
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
        preGame = true;
        game = false;
        postGame = false;
    }

    /**
     * Sets the game value to true and the others to false
     */
    public final void setToGame() {
        preGame = false;
        game = true;
        postGame = false;
    }

    /**
     * Sets the postGame value to true and the others to false
     */
    public final void setToPostGame() {
        preGame = false;
        game = false;
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
}
