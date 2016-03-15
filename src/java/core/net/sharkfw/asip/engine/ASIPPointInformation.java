/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.asip.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.asip.ASIPSpace;

/**
 *
 * @author msc
 */
public class ASIPPointInformation {
    
    public final static String ASIPSPACE = "ASIPSPACE";
    public final static String INFOMETADATA = "INFOMETADATA";
    
    private ASIPSpace space;
    private List<ASIPInfoMetaData> infoData;

    public ASIPPointInformation() {
        this.infoData = new ArrayList<>();
    }

    public ASIPPointInformation(ASIPSpace space, List<ASIPInfoMetaData> infoData) {
        this.space = space;
        this.infoData = infoData;
    }

    public ASIPSpace getSpace() {
        return space;
    }

    public void setSpace(ASIPSpace space) {
        this.space = space;
    }

    public Iterator<ASIPInfoMetaData> getInfoData() {
        return infoData.iterator();
    }

    public void setInfoData(List<ASIPInfoMetaData> infoData) {
        this.infoData = infoData;
    }
    
    public void addInfoData(ASIPInfoMetaData data){
        if(data==null) return;
        infoData.add(data);
    }
    
    
    
}
