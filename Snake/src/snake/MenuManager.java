package snake;

import java.util.ArrayList;

/**
 *
 * @author Tim Barber
 */
public class MenuManager {

    private ArrayList<String> menuNames;
    private ArrayList<Boolean> currentlyDisplaying;

    /**
     *
     * @param menuNames
     */
    public MenuManager(ArrayList<String> menuNames) {
        this.menuNames = menuNames;
        this.currentlyDisplaying = new ArrayList<>(menuNames.size());
        clearDisplaying(menuNames.size());
        this.currentlyDisplaying.set(0, true);
    }

    private void clearDisplaying(int size) {
        this.currentlyDisplaying.clear();
        for (int i = 0; i < size; i++) {
            this.currentlyDisplaying.add(false);
        }
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
        clearDisplaying(menuNames.size());
        this.currentlyDisplaying.set(index, true);
    }

    /**
     * Shows the first menu option in the list
     */
    public void setMain() {
        clearDisplaying(menuNames.size());
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
}
