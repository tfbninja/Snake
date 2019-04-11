package snake;

import java.util.ArrayList;

/**
 * This class controls what should be displayed on screen at all times
 *
 * @author Tim Barber
 */
public class MenuManager extends Controller {

    private ArrayList<String> menuNames;
    private ArrayList<Boolean> currentlyDisplaying;

    /**
     *
     * @param menuNames
     */
    public MenuManager(ArrayList<String> menuNames) {
        this.menuNames = menuNames;
        this.currentlyDisplaying = new ArrayList<>(menuNames.size());
        turnOff();
        this.currentlyDisplaying.set(0, true);
    }

    /**
     *
     * @return
     */
    public int getCurrent() {
        return this.currentlyDisplaying.indexOf(true);
    }

    /**
     *
     * @param index
     */
    public void setCurrent(int index) {
        turnOff();
        this.currentlyDisplaying.set(index, true);
    }

    /**
     * Shows the first menu option in the list
     */
    public void setMain() {
        turnOff();
        this.currentlyDisplaying.set(0, true);
    }

    /**
     *
     * @param index
     * @return
     */
    public String getName(int index) {
        return this.menuNames.get(index);
    }

    /**
     *
     * @param index
     * @return
     */
    public boolean isOn(int index) {
        return this.currentlyDisplaying.get(index);
    }

    /**
     *
     * @param index
     * @return
     */
    public boolean isOff(int index) {
        return !this.currentlyDisplaying.get(index);
    }

    /*
     * Sets all menus to off
     */
    @Override
    public final void turnOff() {
        this.currentlyDisplaying.clear();
        for (String menuName : menuNames) {
            this.currentlyDisplaying.add(false);
        }
    }
}
