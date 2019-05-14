package snake;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Tim Barber
 */
public class ImagePlayer {

    private String imagePath;
    private ArrayList<ImageView> images;

    /**
     * Initialize the imageplayer with the folder directory of images
     * @param folderPath 
     */
    public ImagePlayer(String folderPath) {
        imagePath = folderPath;
        images = new ArrayList<>();

        File container = new File(folderPath);
        File[] directoryListing = container.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                this.images.add(getImageView(child.getAbsolutePath()));
            }
        } else {
            System.out.println("Cannot find the " + folderPath + " folder... try setting the working directory to the folder that Snake.java or Snake.jar is contained in.");
        }
    }

    /**
     * 
     * @return the imageview arraylist
     */
    public ArrayList<ImageView> getImages() {
        return images;
    }

    /**
     * 
     * @param num
     * @return the imageview numth in the arraylist
     */
    public ImageView getFrame(int num) {
        if (num < images.size() && num >= 0f) {
            return images.get(num);
        }
        return null;
    }

    private ImageView getImageView(String filename) {
        try {
            FileInputStream tempStream = new FileInputStream(filename);
            Image tempImg = new Image(tempStream);
            ImageView iv = new ImageView();
            iv.setImage(tempImg);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setCache(true);
            return iv;
        } catch (FileNotFoundException f) {
            System.out.println(f.getLocalizedMessage());
            return null;
        }
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
