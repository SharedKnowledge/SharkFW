package com.p2ptube.media.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JComponent;

/**
 * Used to draw a single image on the VideoPanel
 * @author RW
 */
public class ImageDraw extends JComponent {

    //Image img;
    private static final long serialVersionUID = 5584422798735147930L;
    private Image mImage;
    private Dimension mSize;
    private boolean running = true;

    public void setImage(Image image) {
        //SwingUtilities.invokeLater(new ImageRunnable(image, this));
        setLocalImage(image);
    }

    public void setLocalImage(Image img) {
        this.mImage = img;
        repaint();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setImageSize(Dimension newSize) {
        this.mSize = newSize;
        //this.setSize(mSize);
    }

    public ImageDraw() {
        this.mImage = null;
        this.setVisible(true);
        this.mSize = new Dimension(this.getWidth(), this.getHeight());
    }

    public void paintComponent(Graphics g) {
        if (mImage != null) {
            super.paintComponent(g);
            int thisWidth = this.getWidth();
            int thisHeight = this.getHeight();
            int imageWidth = mSize.width;
            int imageHeight = mSize.height;
            float wRatio = (float) imageWidth / (float) thisWidth;
            float hRatio = (float) imageHeight / (float) thisHeight;
            int newWidth = imageWidth;
            int newHeight = imageHeight;
            if ((wRatio > hRatio) && (wRatio > 1.0)) {
                newWidth = thisWidth;
                newHeight = (int) (imageHeight / wRatio);

            } else if ((hRatio > wRatio) && (hRatio > 1.0)) {
                newHeight = thisHeight;
                newWidth = (int) (imageWidth / hRatio);
            }
            int heightPos = (newHeight < thisHeight) ? ((thisHeight - newHeight) / 2) : 0;
            int widthPos = (newWidth < thisWidth) ? ((thisWidth - newWidth) / 2) : 0;

            g.drawImage(mImage, widthPos, heightPos, newWidth + widthPos, newHeight + heightPos, 0, 0, mImage.getWidth(this), mImage.getHeight(this), this);
        }
        if (running) {
            repaint();
        }
    }
}
