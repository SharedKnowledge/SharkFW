package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

import java.util.Enumeration;

/**
 * Created by j4rvis on 2/27/17.
 */
public class FileDumpSemanticTag extends FileDumpPropertyHolder implements SemanticTag {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public String[] getSI() {
        return new String[0];
    }

    @Override
    public void removeSI(String si) throws SharkKBException {

    }

    @Override
    public void addSI(String si) throws SharkKBException {

    }

    @Override
    public void setName(String newName) {

    }

    @Override
    public void merge(SemanticTag st) {

    }

    @Override
    public void setHidden(boolean isHidden) {

    }

    @Override
    public boolean hidden() {
        return false;
    }

    @Override
    public boolean isAny() {
        return false;
    }

    @Override
    public boolean identical(SemanticTag other) {
        return false;
    }
}
