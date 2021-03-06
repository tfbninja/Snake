package snake;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class manages audio files
 *
 * @author Tim Barber
 */
public class Sound {

    private String filename;
    private URL resource;
    private MediaPlayer mediaPlayer;
    private Media media;
    private Clip clip;
    private BooleanControl muteControl;
    private double volumeLevel = 1.0;
    FloatControl gainControl;
    private boolean isWav = false;

    //
    /**
     *
     * @param filename
     */
    public Sound(String filename) {
        this.filename = filename;
        // this block of code can deal with at least .mp3 and .wav, possibly more
        // however I can't figure out how to make a mediaPlayer loop
        // taken from http://www.java2s.com/Code/Java/JavaFX/Playmp3file.htm
        resource = getClass().getResource(filename);
        media = new Media(resource.toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(1.0);

        // this block of code only works on .wav files as far as I can tell, but
        // it can loop
        if (filename.contains(".wav")) {
            isWav = true;
            try {
                try {
                    try {
                        // taken from https://stackoverflow.com/questions/30587437
                        clip = AudioSystem.getClip();
                        clip.open(AudioSystem.getAudioInputStream(new File(filename)));
                        muteControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
                        gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    } catch (UnsupportedAudioFileException c) {
                        System.out.println("Unsuppported audio file " + filename);
                    }
                } catch (IOException b) {
                    System.out.println("Input/output exception with file " + filename);
                }
            } catch (LineUnavailableException a) {
                System.out.println("Unable to find audio file " + filename);
            }
        }
    }

    /**
     * Sets up object using the method that I have found to work for mp3 files
     *
     * @param filename file path
     * @param asMP3    which method to use
     */
    public Sound(String filename, boolean asMP3) {
        this.filename = filename;
        // this block of code can deal with at least .mp3 and .wav, possibly more
        // however I can't figure out how to make a mediaPlayer loop
        // taken from http://www.java2s.com/Code/Java/JavaFX/Playmp3file.htm
        resource = getClass().getResource(filename);
        media = new Media(resource.toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(1.0);

        if (filename.contains(".wav") && !asMP3) {
            isWav = true;
            try {
                try {
                    try {
                        // taken from https://stackoverflow.com/questions/30587437
                        clip = AudioSystem.getClip();
                        clip.open(AudioSystem.getAudioInputStream(new File(filename)));
                        muteControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
                        gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    } catch (UnsupportedAudioFileException c) {
                        System.out.println("Unsuppported audio file " + filename);
                    }
                } catch (IOException b) {
                    System.out.println("Input/output exception with file " + filename);
                }
            } catch (LineUnavailableException a) {
                System.out.println("Unable to find audio file " + filename);
            }
        }
    }

    /**
     *
     * @return
     */
    public String getFilename() {
        return filename;
    }

    /**
     *
     * @param amt
     */
    public void setVolume(double amt) {
        if (filename.contains(".mp3")) {
            // number between 0 and 1
            if (amt != 0) {
                volumeLevel = amt;
            }
            this.mediaPlayer.setVolume(amt);
        } else {
            gainControl.setValue((float) volumeLevel);
        }
    }

    /**
     * Toggles the mute setting
     */
    public void toggleMute() {
        if (isWav) {
            muteControl.setValue(!muteControl.getValue());
        }
        this.mediaPlayer.setMute(!this.mediaPlayer.muteProperty().getValue());
    }

    /**
     * Mutes this sound
     */
    public void mute() {
        this.mediaPlayer.setMute(true);
        //gainControl.setValue(0);
        if (isWav) {
            muteControl.setValue(true);
        }
    }

    /**
     * Unmutes this sound
     */
    public void unmute() {
        this.mediaPlayer.setMute(false);
        //gainControl.setValue((float) volumeLevel);
        if (isWav) {
            muteControl.setValue(false);
        }
    }

    /**
     * Loops this sound until stopped
     */
    public void loop() {
        // taken from https://stackoverflow.com/questions/30587437
        //clip.start();
        clip.loop(999);
    }

    /**
     *
     * @return Whether this sound object is playing or not
     */
    public boolean isPlaying() {
        return mediaPlayer.currentRateProperty().get() != 0;
    }

    /**
     * Plays this sound once
     */
    public void play() {
        // hey... it works alright? Don't question it
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.play();
        if (isWav) {
            clip.setMicrosecondPosition(0);
            clip.start();
        }
    }

    /**
     * Stops this sound
     */
    public void stop() {
        mediaPlayer.stop();
        clip.stop();
    }

    /**
     * Pauses this sound
     */
    public void pause() {
        mediaPlayer.pause();
        clip.stop();
    }

    /**
     * Returns the filename used by this Sound object
     * @return
     */
    @Override
    public String toString() {
        return filename;
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
