package net.sharkfw.knowledgeBase.persistent;
import junit.framework.TestCase;
import net.sharkfw.knowledgeBase.persistent.sql.SqlSTSet;
import net.sharkfw.knowledgeBase.persistent.sql.SqlSemanticTag;
import net.sharkfw.knowledgeBase.persistent.sql.SqlSharkKB;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Created by Dustin Feurich
 */
public class TestSqlSharkKB {

    public static final String connection = "jdbc:sqlite:.\\src\\main\\java\\net\\sharkfw\\knowledgeBase\\persistent\\sql\\testShark.db";

    @Before
    public void testConnectionAndBuild() {
        SqlSharkKB sqlSharkKB = new SqlSharkKB(connection);
        assertNotNull(sqlSharkKB);
    }

    @Test
    public void testSemanticTagCreation() throws SQLException {

        SqlSharkKB sqlSharkKB = new SqlSharkKB(connection, "org.sqlite.JDBC");
        String[] sis = new String[]{"si1", "si2", "si3"};
        SqlSemanticTag tag = null;
        SqlSTSet stSet = null;
        stSet = new SqlSTSet(sqlSharkKB);
        tag = new SqlSemanticTag(sis, "test", stSet.getStSetID(), sqlSharkKB);
        assertNotNull(tag);
    }

}

