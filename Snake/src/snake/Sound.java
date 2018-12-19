package snake;

import java.applet.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

/**
 *
 * @author Tim Barber
 */
// This class taken mostly verbatim from https://www.dreamincode.net/forums/topic/14083-incredibly-easy-way-to-play-sounds/
public class Sound {// Holds one audio file

    private AudioClip song; // Sound player
    private URL songPath; // Sound path

    public Sound(String filename) {
        try {
            songPath = new URL(filename); // Get the Sound URL
            song = Applet.newAudioClip(songPath); // Load the Sound
        } catch (Exception e) {
        } // Satisfy the catch
    }

    public void playSound() {
        song.loop(); // Play
    }

    public void stopSound() {
        song.stop(); // Stop
    }

    public void playSoundOnce() {
        song.play(); // Play only once
    }
}


/*
 * The MIT License
 *
 * Copyright (c) 2018 Tim Barber.
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
