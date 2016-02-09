package net.sharkfw.knowledgeBase;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.InformationSpace;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;
import net.sharkfw.knowledgeBase.ASIPSpace;

/**
 * That implementation should only be a temporary solution.
 * 
 * We map the LASP space to KEP context points which is apparently a horror
 * regarding performace. Moreover, we loose LASP types. We have to create 
 * quickly solutions which are tailored for the actual storages e.g. filesystem or data bases.
 * @author thsc
 */

public class InformationSpace2ContextPoint implements InformationSpace {
    private ASIPSpace space;
    private ArrayList<ContextPoint> cpList;

    public InformationSpace2ContextPoint(AbstractSharkKB kb, ASIPSpace space) 
            throws SharkKBException {
        
        // let's split our space into a points which are in there.
        // get all thinkable coordinates in that space
        HashSet<ContextCoordinates> possibleCoordinates = 
                kb.possibleCoordinates(space);
        
        // lets create cps for each coordinate in the list
        Iterator<ContextCoordinates> ccIter = possibleCoordinates.iterator();
        while(ccIter.hasNext()) {
            ContextCoordinates cc = ccIter.next();
            this.cpList.add(kb.createContextPoint(cc));
        }
    }

    @Override
    public ASIPSpace getContextSpace() throws SharkKBException {
        return this.space;
    }

    @Override
    public void setContextSpace(ASIPSpace space) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * add info to each cp
     * @param source 
     */
    @Override
    public void addInformation(Information source) {
        Iterator<ContextPoint> cpIter = this.cpList.iterator();
        while(cpIter.hasNext()) {
            ContextPoint cp = cpIter.next();
            cp.addInformation(source);
        }
    }

    @Override
    public Information addInformation(InputStream is, long len) {
        Iterator<ContextPoint> cpIter = this.cpList.iterator();
        Information newInfo = null;
        
        if(cpIter.hasNext()) {
            ContextPoint cp = cpIter.next();
            newInfo = cp.addInformation(is, len);
        }
        
        // copy info the each other cp - that's really bad.
        while(cpIter.hasNext()) {
            ContextPoint cp = cpIter.next();
            cp.addInformation(newInfo);
        }
        
        return newInfo;
    }

    @Override
    public Information addInformation(byte[] content) {
        Iterator<ContextPoint> cpIter = this.cpList.iterator();
        Information newInfo = null;

        while(cpIter.hasNext()) {
            ContextPoint cp = cpIter.next();
            newInfo = cp.addInformation(content);
        }
        
        return newInfo;
    }

    @Override
    public Information addInformation(String content) {
        Iterator<ContextPoint> cpIter = this.cpList.iterator();
        Information newInfo = null;

        while(cpIter.hasNext()) {
            ContextPoint cp = cpIter.next();
            newInfo = cp.addInformation(content);
        }
        
        return newInfo;
    }

    @Override
    public Iterator<Information> getInformation(String name) {
        // cps are ment to hold copies of information  - only first one is visited
        Iterator<ContextPoint> cpIter = this.cpList.iterator();
        ContextPoint cp = cpIter.next();
        return cp.getInformation(name);
    }

    @Override
    public Iterator<Information> getInformation() {
        // cps are ment to hold copies of information  - only first one is visited
        Iterator<ContextPoint> cpIter = this.cpList.iterator();
        ContextPoint cp = cpIter.next();
        return cp.getInformation();
    }

    @Override
    public void removeInformation(Information toDelete) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberInformation() {
        // cps are ment to hold copies of information  - only first one is visited
        Iterator<ContextPoint> cpIter = this.cpList.iterator();
        ContextPoint cp = cpIter.next();
        return cp.getNumberInformation();
    }
}
