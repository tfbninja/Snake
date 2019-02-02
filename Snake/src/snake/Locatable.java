package snake;

/**
 *
 * @author Timothy
 */
public interface Locatable {

    /**
     *
     * @param x
     * @param y
     */
    public void setPos(int x, int y);

    /**
     *
     * @param x
     */
    public void setX(int x);

    /**
     *
     * @param y
     */
    public void setY(int y);

    /**
     *
     * @return
     */
    public int getX();

    /**
     *
     * @return
     */
    public int getY();
}
