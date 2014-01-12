package com.p2ptube.media.view;

import java.awt.Image;

/**
 * The Runnable implementation required by Swing to show a "changing" image (like in a file).
 * @author RW
 */
public class ImageRunnable implements Runnable {

    private final Image newImage;
    private ImageDraw parent;

    public ImageRunnable(Image newImage, ImageDraw component) {
        super();
        this.parent = component;
        this.newImage = newImage;
    }

    public void run() {
        this.parent.setLocalImage(newImage);
        this.parent.repaint();
    }
}
