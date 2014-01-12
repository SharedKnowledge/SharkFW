package com.p2ptube.media.view;

import com.p2ptube.peer.P2PTubePeer;
import com.p2ptube.gui.MainWindow;
import com.p2ptube.kb.P2PTubeKB;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.sharkfw.knowledgeBase.AssociatedSTSet;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.DataSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.fs.data.FSDataSemanticTag;
import net.sharkfw.system.Util;

/**
 * Loading all types of Media, downloading them and playing them DURING download.
 * @author RW
 */
public class MediaLoader implements Runnable {

    private VideoPanel videoPanel;
    private DataSemanticTag currentTag;
    private Hashtable mediaInfo;
    private P2PTubePeer peer;
    private static double BUFFER_RATIO = 0.15; //buffer will load 15% or max buffer
    private static int MAXBUFFERSIZE = 1024 * 1024 * 4; //4MB is max buffer
    private VideoPlayer vp;
    private Hashtable remotePeers;
    private Image currentImage;
    private boolean running;
    private boolean imageLoaded;
    private boolean videoPlaying;
    private boolean audioPlaying;
    private int totalSize;
    private int currentSize;

    public MediaLoader(P2PTubePeer peer, VideoPanel panel, Hashtable mediaInfo) {
        this.mediaInfo = mediaInfo;
        this.videoPanel = panel;
        this.peer = peer;
        this.vp = new VideoPlayer(panel);
        running = false;
        imageLoaded = false;
        videoPlaying = false;
    }

    private String getMediaType() {
        if (this.currentTag == null) {
            return "none";
        }
        Enumeration supers = currentTag.getAssociatedTags(AssociatedSTSet.SUPERASSOC);
        while (supers != null && supers.hasMoreElements()) {
            DataSemanticTag superTag = (DataSemanticTag) supers.nextElement();
            return superTag.getName();
        }
        return null;
    }

    public void start() {
        this.showLoading();
        imageLoaded = false;
        videoPlaying = false;
        audioPlaying = false;
        this.running = true;
        Thread t = new Thread(this);
        t.start();
        //this.loadMedia();
    }

    private void showLoading() {
        FileInputStream fis = null;
        URL myurl = this.getClass().getResource("/com/p2ptube/gui/resources/loading.gif");

            Image img = videoPanel.getToolkit().getImage(myurl);
            this.currentImage = img;
            videoPanel.setImageSize(new Dimension(350, 130));
        /*try {
            String filename = (this.getClass().getResource("/com/p2ptube/gui/resources/loading.gif")).getFile().substring(6);
            fis = new FileInputStream(filename);
            //BufferedImage img = null;
            Image img = null;
            try {
                byte[] b = new byte[fis.available()];
                fis.read(b);
                img = videoPanel.getToolkit().createImage(b);//ImageIO.read(fis);
            } catch (IOException e) {
            }
            this.currentImage = img;
            videoPanel.setImageSize(new Dimension(300, 50));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MediaLoader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(MediaLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
    }

    private void showPlayingMusic() {
        System.out.println("playing music");
        FileInputStream fis = null;
        //try {
            /*String filename = (this.getClass().getResource("/com/p2ptube/gui/resources/music.gif")).getFile().substring(6);
            fis = new FileInputStream(filename);
            //BufferedImage img = null;
            Image img = null;
            try {
            byte[] b = new byte[fis.available()];
            fis.read(b);
            img = videoPanel.getToolkit().createImage(b);//ImageIO.read(fis);
            } catch (IOException e) {
            }*/

            URL myurl = this.getClass().getResource("/com/p2ptube/gui/resources/music.gif");

            Image img = videoPanel.getToolkit().getImage(myurl);
            this.currentImage = img;
            videoPanel.setImageSize(new Dimension(350, 130));

        /*} catch (FileNotFoundException ex) {
            Logger.getLogger(MediaLoader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(MediaLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
    }

    private void loadMedia(String type) {
        //get Media Type
        if (type == null || type.equals("none")) {
            type = P2PTubeKB.IMAGE_TYPE;
        }


        /*
         * Check media type - if it is n image wait until the end.
         * If it is audio or video, wait for a certain buffer (predefined) and
         * start playing when it reaches the buffer.
         */

        if (type.equals(P2PTubeKB.IMAGE_TYPE)) {

            ByteArrayInputStream bais = new ByteArrayInputStream(this.currentTag.getData(0, this.currentTag.availableMaxDataLength()));

            BufferedImage img = null;
            try {
                img = ImageIO.read(bais);
            } catch (IOException e) {
            }
            videoPanel.setImage(img);

        } else {

            if (type.equals(P2PTubeKB.AUDIO_TYPE)) {
                this.showPlayingMusic();
            }

            String filename = this.currentTag.getProperty(FSDataSemanticTag.DATAPROPERTYTAG);
            File file = new File(filename);

            File file2 = new File(file.toURI());
            System.out.println(file2.getPath());

            vp.setFilename(file2.getPath());
            vp.startPlaying();
        }
    }

    public void setMedia(Hashtable mediaInfo) {
        this.mediaInfo = mediaInfo;
        this.peer.getEngine().stopDownloader();
        this.vp.setPlaying(false);
    }

    public void run() {

        //Get the right tag
        //String name = (String) mediaInfo.get("name");
        totalSize = Integer.parseInt((String) mediaInfo.get("size"));
        //String originatorSI = (String) mediaInfo.get("originator");
        String mediaSI = (String) mediaInfo.get("SI");

        String peers = (String) this.mediaInfo.get("peers");
        String peersAvailableSize = (String) this.mediaInfo.get("peersAvailableSize");
        this.remotePeers = new Hashtable();
        String[] peersArray = Util.string2array(peers);
        String[] peersSizeArray = Util.string2array(peersAvailableSize);
        for (int i = 0; i < peersArray.length; i++) {
            //both arrays MUST be the same. If not, a 0 will be added as size.
            int size = (peersSizeArray[i] == null) ? 0 : Integer.parseInt(peersSizeArray[i]);
            this.remotePeers.put(peersArray[i], size);
        }
        //this.remotePeers = new Vector(Util.array2Enum(Util.string2array(peers)));

        try {
            this.currentTag = (DataSemanticTag) this.peer.getKB().getSTSet(ContextSpace.DIM_TOPIC).getSemanticTag(mediaSI);
        } catch (SharkKBException ex) {
            //Tag not found? technically imposible...
            return;
        }

        //get Media Type
        String type;
        if ((type = getMediaType()) == null || type.equals("none")) {
            type = P2PTubeKB.IMAGE_TYPE;
        }

        //Check if we have the entire file. Download it if not.
        //This is a download thread.
        try {
            if (this.currentTag.availableMaxDataLength() < totalSize) {
                this.peer.getEngine().getMediaSegments(mediaSI, totalSize, remotePeers);
            }
        } catch (SharkKBException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

        //updating image and progress bar
        while (running) {
            if (!videoPlaying && !imageLoaded) {
                videoPanel.setImage(this.currentImage);
            }
            currentSize = currentTag.availableMaxDataLength();
            double percent = (double) ((double) currentSize / (double) totalSize);
            int per = (int) (percent * 100);
            //System.out.println((per)+" "+(videoPlaying));
            videoPanel.setProgressBar(per);
            if ((per == 100 && videoPlaying) || (per == 100 && audioPlaying) || imageLoaded) {
                P2PTubeKB kb = this.peer.getKB();
                kb.createDataContextPointForExistingRemoteContextPoint(currentTag);
                break;
            }

            if (type.equals(P2PTubeKB.IMAGE_TYPE)) {
                if (currentTag.availableMaxDataLength() == totalSize) {
                    imageLoaded = true;
                    try {
                        //Avoiding problem with image loading.
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                    }
                    loadMedia(type);
                    //break;
                }
            } else {
                if (!audioPlaying && !videoPlaying) {
                    double buffersize = totalSize * BUFFER_RATIO;
                    System.out.println(buffersize);
                    if (currentTag.availableMaxDataLength() > (buffersize) || currentTag.availableMaxDataLength() > MAXBUFFERSIZE) {
                        if (type.equals(P2PTubeKB.AUDIO_TYPE)) {
                            this.showPlayingMusic();
                            audioPlaying = true;
                        } else {
                            videoPlaying = true;
                        }
                        loadMedia(type);
                    }
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
            }
        }
    }
}
