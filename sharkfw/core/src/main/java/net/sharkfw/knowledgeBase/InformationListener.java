/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase;

/**
 * A listener interface to listen for changes on a given Information.
 * Events usually cover the adding and removing of content on an information.
 * 
 * @author s0539710
 */
public interface InformationListener {

    public void contentChanged();
    
    public void contentRemoved();
    
    public void contentTypeChanged();
}
