package snake;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Timothy
 */
public class MenuManager {

    private ArrayList<String> menuNames;
    private ArrayList<Boolean> currentlyDisplaying;

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

    public int getCurrent() {
        return this.currentlyDisplaying.indexOf(true);
    }

    public void setCurrent(int index) {
        clearDisplaying(menuNames.size());
        this.currentlyDisplaying.set(index, true);
    }

    public void setMain() {
        clearDisplaying(menuNames.size());
        this.currentlyDisplaying.set(0, true);
    }

    public String getName(int index) {
        return this.menuNames.get(index);
    }

    public boolean isOn(int index) {
        return this.currentlyDisplaying.get(index);
    }

    public boolean isOff(int index) {
        return !this.currentlyDisplaying.get(index);
    }
}
