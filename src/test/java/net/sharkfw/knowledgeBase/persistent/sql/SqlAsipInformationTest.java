package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Created by Micha on 01.06.2017.
 */
public class SqlAsipInformationTest {

    public static final String connection = "jdbc:sqlite:.\\src\\test\\java\\net\\sharkfw\\knowledgeBase\\persistent\\sql\\information.db";

    @BeforeClass
    public static void setUp() throws IOException {
        Path path = FileSystems.getDefault().getPath(".\\src\\test\\java\\net\\sharkfw\\knowledgeBase\\persistent\\sql\\information.db");
        Files.delete(path);
        L.setLogLevel(L.LOGLEVEL_ALL);
    }

    @Test
    public void addInformation_success() throws SharkKBException {
        SqlSharkKB sqlSharkKB = new SqlSharkKB(connection, "org.sqlite.JDBC");

        SemanticTag test = InMemoSharkKB.createInMemoSemanticTag("Test", "si:test");
        ASIPSpace space = sqlSharkKB.createASIPSpace(test, null, null, null, null, null, null, ASIPSpace.DIRECTION_IN);

        sqlSharkKB.addInformation("Test", "This is just a TEst!", space);
    }

    @Test
    public void addAndGetInformation_success() throws SharkKBException {
        SqlSharkKB sqlSharkKB = new SqlSharkKB(connection, "org.sqlite.JDBC");

        SemanticTag test = InMemoSharkKB.createInMemoSemanticTag("Test2", "si:test1244512");
        ASIPSpace space = sqlSharkKB.createASIPSpace(null, test, null, null, null, null, null, ASIPSpace.DIRECTION_IN);

        sqlSharkKB.addInformation("Test1231241241", "This is just a TEst!", space);

        Iterator<ASIPInformation> information = sqlSharkKB.getInformation(space);
        if(information.hasNext()){
            SqlAsipInformation next = (SqlAsipInformation) information.next();
            L.d("InfoId: " + next.getId(), this);
            L.d("InfoName: " + next.getName(), this);
        } else {
            Assert.assertTrue(false);
        }
    }

}
