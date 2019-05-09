package snake;

import javafx.scene.image.ImageView;
import static snake.Snake.getImageView;

/**
 * This class controls background music and SFX, and correspondingly which
 * version of the main menu to show
 *
 * @author Tim Barber
 */
public class MainMenu extends Controller {

    private boolean music = true;
    private boolean sfx = true;
    /**
     * Initializes the ImageView objects for the 4 menu screens
     */
    public MainMenu() {
    }

    /**
     *
     * @return Whether the music icon is on (true) or off (false)
     */
    public boolean getMusic() {
        return music;
    }

    /**
     *
     * @return Whether the SFX icon is on (true) or off (false)
     */
    public boolean getSFX() {
        return sfx;
    }

    /**
     * Sets all vars to false
     */

    @Override
    public void turnOff() {
        music = false;
        sfx = false;
    }

    /**
     * Sets all vars to true
     */
    public void turnOn() {
        music = true;
        sfx = true;
    }

    /**
     * Sets the music var to false and chooses the right menu image based on
     * whether or not the SFX is on or off
     */
    public void turnOffMusic() {
        music = false;
    }

    /**
     * Sets the SFX var to false and chooses the right menu image based on
     * whether or not the music is on or off
     */
    public void turnOffSFX() {
        sfx = false;
    }

    /**
     * Sets the music var to true and chooses the right menu image based on
     * whether or not the SFX is on or off
     */
    public void turnOnMusic() {
        music = true;
    }

    /**
     * Sets the SFX var to true and chooses the right menu image based on
     * whether or not the music is on or off
     */
    public void turnOnSFX() {
        sfx = true;
    }
}
