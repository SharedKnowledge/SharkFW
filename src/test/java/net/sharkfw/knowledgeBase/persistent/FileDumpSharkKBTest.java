package net.sharkfw.knowledgeBase.persistent;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.persistent.fileDump.FileDumpSharkKB;
import net.sharkfw.system.L;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;

/**
 * Created by j4rvis on 3/1/17.
 */
public class FileDumpSharkKBTest {

    @Test
    public void createFileDumpSharkKB(){

        L.setLogLevel(L.LOGLEVEL_ALL);

        InMemoSharkKB inMemoSharkKB = new InMemoSharkKB();
        File testfile = new File("testfile");
        FileDumpSharkKB fileDumpSharkKB = new FileDumpSharkKB(inMemoSharkKB, testfile);

        String message = "This is just a test";

        try {
            fileDumpSharkKB.addInformation(message, InMemoSharkKB.createInMemoASIPInterest());
        } catch (SharkKBException e) {
            e.printStackTrace();
        }

        FileDumpSharkKB dumpSharkKB = new FileDumpSharkKB(testfile);

        try {
            Iterator<ASIPInformation> information = dumpSharkKB.getInformation(InMemoSharkKB.createInMemoASIPInterest());
//            L.d(information.next().getContentAsString(), this);
            Assert.assertEquals(message, information.next().getContentAsString());
        } catch (SharkKBException e) {
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() throws Exception {
        File file = new File("testfile");
        if(file.exists()){
            file.delete();
        }
    }
}
