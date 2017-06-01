package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

import org.junit.Test;

/**
 * Created by Micha on 01.06.2017.
 */
public class SqlAsipInformationTest {

    public static final String connection = "jdbc:sqlite:.\\src\\test\\java\\net\\sharkfw\\knowledgeBase\\persistent\\sql\\information.db";

    @Test
    public void addInformation_success() throws SharkKBException {
        SqlSharkKB sqlSharkKB = new SqlSharkKB(connection, "org.sqlite.JDBC");

        SemanticTag test = InMemoSharkKB.createInMemoSemanticTag("Test", "si:test");
        ASIPSpace space = sqlSharkKB.createASIPSpace(test, null, null, null, null, null, null, ASIPSpace.DIRECTION_IN);

        sqlSharkKB.addInformation("Test", "This is just a TEst!", space);
    }

}
