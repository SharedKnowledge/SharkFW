/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.p2ptube.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author RW
 */
public class AddMedium implements ActionListener {

    JFrame frame;
    File file;

    AddMedium(JFrame frame, File chooser) {
        //super("Open...");
        this.file = chooser;
        this.frame = frame;
    }

    public void actionPerformed(ActionEvent evt) { // Show dialog; this method does not return until dialog is closed
        JFileChooser c = new JFileChooser();
        // Demonstrate "Open" dialog:
        int rVal = c.showOpenDialog(frame);
        file = null;
        if (rVal == JFileChooser.APPROVE_OPTION) {
            file = c.getSelectedFile();
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {
            //Cancel
        }
    }
}
