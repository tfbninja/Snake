package snake;

/**
 *
 * @author Tim Barber
 */
/**
 *
 * This class controls which mode should be displayed: 2d or 3d
 */
public class ViewManager extends Controller {

    private boolean Mode_3d = false;

    /**
     * Pretty self-explanatory
     *
     * @param val
     */
    public ViewManager(boolean val) {
        Mode_3d = val;
    }

    /**
     * The state of the 3d_mode variable
     *
     * @return
     */
    public boolean get3dMode() {
        return Mode_3d;
    }

    @Override
    public void turnOff() {
        Mode_3d = false;
    }

    /**
     * Turns on the variable to true
     */
    public void turnOn() {
        Mode_3d = true;
    }

    /**
     * toggles the variable
     */
    public void toggle() {
        Mode_3d = !Mode_3d;
    }
}


/*
 * The MIT License
 *
 * Copyright (c) 2019 Tim Barber.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
