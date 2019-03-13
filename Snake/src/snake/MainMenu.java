package snake;

import javafx.scene.image.ImageView;
import static snake.Snake.getImageView;

/**
 * This class controls background music and sfx, and correspondingly which
 * version of the main menu to show
 *
 * @author Tim Barber
 */
public class MainMenu extends Controller {

    private boolean music = true;
    private boolean sfx = true;
    private ImageView current;
    private ImageView OnOn;
    private ImageView OnOff;
    private ImageView OffOn;
    private ImageView OffOff;

    /**
     * Initializes the ImageView objects for the 4 menu screens
     */
    public MainMenu() {
        // set up files
        OnOn = getImageView("resources/art/updatedMenus/ButtonShadows/menuMOnSOn.png");
        OnOff = getImageView("resources/art/updatedMenus/ButtonShadows/menuMOnSOff.png");
        OffOn = getImageView("resources/art/updatedMenus/ButtonShadows/menuMOffSOn.png");
        OffOff = getImageView("resources/art/updatedMenus/ButtonShadows/menuMOffSOff.png");
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
     *
     * @return The ImageView object currently in use
     */
    public ImageView getMenu() {
        return current;
    }

    /*
     * Sets all vars to false
     */
    @Override
    public void turnOff() {
        music = false;
        sfx = false;
        current = OffOff;
    }

    public void turnOn() {
        music = true;
        sfx = true;
        current = OnOn;
    }

    /**
     * Sets the music var to false and chooses the right menu image based on
     * whether or not the SFX is on or off
     */
    public void turnOffMusic() {
        music = false;
        if (sfx) {
            current = OffOn;
        } else {
            current = OffOff;
        }
    }

    /**
     * Sets the SFX var to false and chooses the right menu image based on
     * whether or not the music is on or off
     */
    public void turnOffSFX() {
        sfx = false;
        if (music) {
            current = OnOff;
        } else {
            current = OffOff;
        }
    }

    /**
     * Sets the music var to true and chooses the right menu image based on
     * whether or not the SFX is on or off
     */
    public void turnOnMusic() {
        music = true;
        if (sfx) {
            current = OnOn;
        } else {
            current = OnOff;
        }
    }

    /**
     * Sets the SFX var to true and chooses the right menu image based on
     * whether or not the music is on or off
     */
    public void turnOnSFX() {
        sfx = true;
        if (music) {
            current = OnOn;
        } else {
            current = OffOn;
        }
    }
}
