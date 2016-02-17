/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.asip.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author msc
 */
public class ASIPInfoDataManager {
    
    public final static String INFOCONTENT = "INFOCONTENT";
    public final static String CONTEXTPOINTINFO = "CONTEXTPOINTINFO";
    
    private long currentOffset = 0;
    private final List<ASIPPointInformation> infoPoints;
    private byte[] infoContent;

    public ASIPInfoDataManager(Iterator<ASIPInformationSpace> infoSpaces) throws SharkKBException {
        this.infoPoints = new ArrayList<>();
        this.infoContent = new byte[0];
        while(infoSpaces.hasNext()){
            ASIPInformationSpace infoSpace = infoSpaces.next();
            
            ASIPPointInformation pointInfo = new ASIPPointInformation();
            pointInfo.setSpace(infoSpace.getASIPSpace());
            while(infoSpace.informations().hasNext()){
                Information info = (Information) infoSpace.informations().next();
                ASIPInfoMetaData data = new ASIPInfoMetaData();
                
                data.setName(info.getName());
                data.setLength(info.getContentLength());
                data.setOffset(currentOffset);
                
                byte[] content = info.getContentAsByte();
                byte[] result = new byte[infoContent.length + content.length];
                System.arraycopy(infoContent, 0, result, 0, infoContent.length);
                System.arraycopy(content, 0, result, infoContent.length, content.length);
                infoContent = result;
                currentOffset=infoContent.length;
                
                pointInfo.addInfoData(data);
            }
            infoPoints.add(pointInfo);
        }
    }
    
    public Iterator<ASIPPointInformation> getPointInfromations(){
        return infoPoints.iterator();
    }
    
    public byte[] getInfoContent() {
        return infoContent;
    }
    
}
