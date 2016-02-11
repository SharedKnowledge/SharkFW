package net.sharkfw.knowledgeBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

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
    public ASIPSpace getASIPSpace() throws SharkKBException {
        return this.space;
    }

    @Override
    public void setASIPSpace(ASIPSpace space) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<InformationPoint> informationPoints() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
