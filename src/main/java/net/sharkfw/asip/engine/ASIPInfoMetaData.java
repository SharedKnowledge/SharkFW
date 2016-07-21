/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.asip.engine;

/**
 *
 * @author j4rvis
 */
public class ASIPInfoMetaData {
    
    public final static String NAME = "NAME";
    public final static String OFFSET = "OFFSET";
    public final static String LENGTH = "LENGTH";
 
    private String name;
    private long offset;
    private long length;

    public ASIPInfoMetaData() {
    }

    public ASIPInfoMetaData(String name, long offset, long length) {
        this.name = name;
        this.offset = offset;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
    
    
    
}
