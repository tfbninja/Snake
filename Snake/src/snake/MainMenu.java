package snake;

import java.applet.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import static snake.Snake.getImageView;

/**
 *
 * @author Timothy
 */
public class MainMenu {

    private boolean music = true;
    private boolean sfx = true;
    private ImageView current;
    private ImageView OnOn;
    private ImageView OnOff;
    private ImageView OffOn;
    private ImageView OffOff;

    public MainMenu() {
        // set up files
        OnOn = getImageView("resources/art/updatedMenus/menuMOnSOn.png");
        OnOff = getImageView("resources/art/updatedMenus/menuMOnSOff.png");
        OffOn = getImageView("resources/art/updatedMenus/menuMOffSOn.png");
        OffOff = getImageView("resources/art/updatedMenus/menuMOffSOff.png");
    }

    public boolean getMusic() {
        return music;
    }

    public boolean getSFX() {
        return sfx;
    }

    public ImageView getMenu() {
        return current;
    }

    public void turnOffMusic() {
        music = false;
        if (sfx) {
            current = OffOn;
        } else {
            current = OffOff;
        }
    }

    public void turnOffSFX() {
        sfx = false;
        if (music) {
            current = OnOff;
        } else {
            current = OffOff;
        }
    }

    public void turnOnMusic() {
        music = true;
        if (sfx) {
            current = OnOn;
        } else {
            current = OnOff;
        }
    }

    public void turnOnSFX() {
        sfx = true;
        if (music) {
            current = OnOn;
        } else {
            current = OffOn;
        }
    }
}
