package net.sharkfw.system;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by j4rvis on 2/7/17.
 */
public class KnowledgeUtils {

    public static ASIPSpace createCurrentTimeSpace(SharkKB kb, SemanticTag type) throws SharkKBException {
        TimeSemanticTag timeSemanticTag =
                kb.getTimeSTSet().createTimeSemanticTag(System.currentTimeMillis(), 0);
        return kb.createASIPSpace(null, type, null, null, null, timeSemanticTag, null, ASIPSpace.DIRECTION_OUT);
    }

    public static void setInfoWithName(SharkKB kb, ASIPSpace space, String name, String content) throws SharkKBException {
        kb.addInformation(name, content, space);
    }

    public static void setInfoWithName(SharkKB kb, ASIPSpace space, String name, InputStream content) throws SharkKBException, IOException {
        kb.addInformation(name, content, content.available(), space);
    }

    public static void setInfoWithName(SharkKB kb, ASIPSpace space, String name, byte[] content) throws SharkKBException, IOException {
        kb.addInformation(name, content, space);
    }

    public static void setInfoWithName(SharkKB kb, ASIPSpace space, String name, int content) throws SharkKBException {
        String s = String.valueOf(content);
        KnowledgeUtils.setInfoWithName(kb, space, name, s);
    }


    public static void setInfoWithName(SharkKB kb, ASIPSpace space, String name, long content) throws SharkKBException {
        String s = String.valueOf(content);
        KnowledgeUtils.setInfoWithName(kb, space, name, s);
    }

    public static void setInfoWithName(SharkKB kb, ASIPSpace space, String name, boolean content) throws SharkKBException {
        String booleanString;
        if (content) {
            booleanString = "TRUE";
        } else {
            booleanString = "FALSE";
        }
        KnowledgeUtils.setInfoWithName(kb, space, name, booleanString);
    }

    public static ASIPInformation getInfoByName(SharkKB kb, ASIPSpace space, String name) throws SharkKBException {
        Iterator<ASIPInformation> information = kb.getInformation(space);
        if (information == null) return null;

        while (information.hasNext()) {
            ASIPInformation next = information.next();
            if (next.getName().equals(name)) {
                return next;
            }
        }
        return null;
    }

    public static boolean getInfoAsBoolean(SharkKB kb, ASIPSpace space, String name) throws SharkKBException {
        ASIPInformation information = getInfoByName(kb, space, name);
        if (information != null) {
            if (information.getContentAsString().equals("TRUE")) {
                return true;
            } else if (information.getContentAsString().equals("FALSE")) {
                return false;
            }
        }
        return false;
    }

    public static String getInfoAsString(SharkKB kb, ASIPSpace space, String name) throws SharkKBException {
        ASIPInformation information = getInfoByName(kb, space, name);
        if (information != null) {
            return information.getContentAsString();
        }
        return null;
    }

    public static int getInfoAsInteger(SharkKB kb, ASIPSpace space, String name) throws SharkKBException {
        ASIPInformation information = getInfoByName(kb, space, name);
        if (information != null) {
            return Integer.parseInt(information.getContentAsString());
        }
        return 0;
    }


    public static Long getInfoAsLong(SharkKB kb, ASIPSpace space, String name) throws SharkKBException {
        ASIPInformation information = getInfoByName(kb, space, name);
        if (information != null) {
            return Long.parseLong(information.getContentAsString());
        }
        return 0L;
    }
}
