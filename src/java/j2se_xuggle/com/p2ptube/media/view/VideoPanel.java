package com.p2ptube.media.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * The Swing JPanel used to show images and videos.
 * @author RW
 */
public class VideoPanel extends JPanel {

    /**
     * To avoid a warning...
     */
    private static final long serialVersionUID = -4752966848100689153L;
    private final ImageDraw mOnscreenPicture;
    private JProgressBar progressBar;
    private VideoPlayer videoPlayer;

    public VideoPanel() {
        super(new BorderLayout());
        mOnscreenPicture = new ImageDraw();
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setSize(this.getWidth(), 10);
        this.add(mOnscreenPicture, BorderLayout.CENTER);
        this.add(progressBar, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    public void stopPlaying() {
        if(videoPlayer!= null)
        this.videoPlayer.setPlaying(false);
    }

    public void setVideoPlayer(VideoPlayer vp) {
        this.videoPlayer = vp;
    }

    public void setProgressBar(int value) {
        progressBar.setValue(value);
    }

    public void setImageSize(Dimension newDim) {
        mOnscreenPicture.setImageSize(newDim);
    }

    public void setImage(Image aImage) {
        mOnscreenPicture.setImage(aImage);
        if (aImage != null) {
            if (aImage instanceof BufferedImage) {
                BufferedImage img = (BufferedImage) aImage;
                mOnscreenPicture.setImageSize(new Dimension(img.getWidth(), img.getHeight()));
            }
        }
        repaint();
    }
}
