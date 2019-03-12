package snake;

/**
 *
 * @author Tim Barber
 */
public interface Locatable {

    /**
     *
     * @param x the x position
     * @param y the y position
     */
    public void setPos(int x, int y);

    /**
     *
     * @param x the x position
     */
    public void setX(int x);

    /**
     *
     * @param y the y position
     */
    public void setY(int y);

    /**
     *
     * @return the x position
     */
    public int getX();

    /**
     *
     * @return the y position
     */
    public int getY();
}
