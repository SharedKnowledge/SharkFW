package net.sharkfw.knowledgeBase.persistent;
import junit.framework.TestCase;
import net.sharkfw.knowledgeBase.persistent.sql.SqlSharkKB;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Dustin Feurich
 */
public class TestSqlSharkKB {

    public static final String connection = "jdbc:sqlite:.\\src\\main\\java\\net\\sharkfw\\knowledgeBase\\persistent\\sql\\testShark.db";

    @Test
    public void testConnectionAndBuild()
    {
        SqlSharkKB sqlSharkKB = new SqlSharkKB(connection);
        assertNotNull(sqlSharkKB);
    }

}
