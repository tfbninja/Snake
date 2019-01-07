package snake;

import java.net.URL;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;
import java.io.File;

/**
 *
 * @author Tim Barber
 */
public class Sound { // Holds one audio file

    private URL resource;
    private MediaPlayer mediaPlayer;
    private Media media;
    private Clip clip;

    //
    public Sound(String filename) {
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
            try {
                try {
                    try {
                        // taken from https://stackoverflow.com/questions/30587437
                        clip = AudioSystem.getClip();
                        clip.open(AudioSystem.getAudioInputStream(new File(filename)));
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

    public void setVolume(double amt) {
        // number between 0 and 1
        this.mediaPlayer.setVolume(amt);

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float) (Math.log(amt) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
    }

    public void toggleMute() {
        this.mediaPlayer.setMute(!this.mediaPlayer.muteProperty().getValue());
        BooleanControl muteControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
        muteControl.setValue(muteControl negate
    

    );
    }

    public void loop() {
        // taken from https://stackoverflow.com/questions/30587437
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void play() {
        // hey... it works alright? Don't question it
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.play();
    }

    public void stop() {
        mediaPlayer.stop();
        clip.stop();
    }

    public void pause() {
        mediaPlayer.pause();
        clip.stop();
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
