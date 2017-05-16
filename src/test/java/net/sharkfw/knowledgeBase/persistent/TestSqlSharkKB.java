package net.sharkfw.knowledgeBase.persistent;
import junit.framework.TestCase;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import net.sharkfw.knowledgeBase.persistent.sql.*;
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
    public void testSemanticTagCreation() throws SQLException, SharkKBException {

        SqlSharkKB sqlSharkKB = new SqlSharkKB(connection, "org.sqlite.JDBC");
        String[] sis1 = new String[]{"si1", "si2"};
        String[] sis2 = new String[]{"si3", "si4"};
        String[] sis3 = new String[]{"si5", "si6"};
        String[] sis4 = new String[]{"si7", "si8"};
        String[] addresses = new String[]{"Treskowallee 8", "Wilhelminenhofstraße 6", "Rathenaustraße 10"};
        long timeFrom = System.currentTimeMillis();
        long timeDusration = 100000;
        String wkt = "LINESTRING (30 10, 10 30, 40 40)";

        SqlSemanticTag tag = null;
        SqlTimeSemanticTag timeTag = null;
        SqlSpatialSemanticTag spatialTag = null;
        SqlPeerSemanticTag peerTag = null;
/*        SqlSTSet stSet = null;
        stSet = new SqlSTSet(sqlSharkKB);*/
        SqlSNSemanticTag tagSN = new SqlSNSemanticTag(sis1, "testSNST", 5, sqlSharkKB);

        /*tag = new SqlSemanticTag(sis1, "testTag", stSet.getStSetID(), sqlSharkKB);
        timeTag = new SqlTimeSemanticTag(sis2, "testTimeTag", stSet.getStSetID(), sqlSharkKB, timeDusration, timeFrom);
        spatialTag = new SqlSpatialSemanticTag(sis3, "testSpatialTag", stSet.getStSetID(), sqlSharkKB, wkt);
        peerTag = new SqlPeerSemanticTag(sis4, "testPeerTag", stSet.getStSetID(), sqlSharkKB, addresses);
        assertNotNull(tag);
        assertNotNull(timeTag);
        assertNotNull(spatialTag);
        assertNotNull(peerTag);*/
        //SemanticTag tagFromDB = stSet.getSemanticTag(new String[]{"si1"});
        int i;


    }

}

