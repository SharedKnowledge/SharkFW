/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.p2ptube.listModel;

/**
 *
 * @author RW
 */
public class PeerInList {

    private String name;
    private String SI;

    public PeerInList(String name, String SI) {
        this.name = name;
        this.SI = SI;
    }

    public String getSI() {
        return SI;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.getName();
    }

}
