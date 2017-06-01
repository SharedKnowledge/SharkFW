package net.sharkfw.knowledgeBase.persistent.sql;
import junit.framework.TestCase;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import net.sharkfw.knowledgeBase.persistent.dump.DumpSharkGeometry;
import net.sharkfw.knowledgeBase.persistent.sql.*;
import org.jooq.*;
import org.jooq.impl.*;
import static org.jooq.impl.DSL.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.junit.Assert.*;

/**
 * Created by Dustin Feurich
 */
public class TestSqlSharkKB {

    public static final String connection = "jdbc:sqlite:.\\src\\main\\java\\net\\sharkfw\\knowledgeBase\\persistent\\sql\\testShark.db";
    public static final String[] sis1 = new String[]{"si1", "si2"};
    public static final String[] sis2 = new String[]{"si3", "si4"};
    public static final String[] sis3 = new String[]{"si5", "si6"};
    public static final String[] sis4 = new String[]{"si7", "si8"};

    //@Ignore
    @Before
    public void testConnectionAndBuild() throws SQLException, ClassNotFoundException {
        SqlSharkKB sqlSharkKB = new SqlSharkKB(connection, "org.sqlite.JDBC");
        Connection con;
        Class.forName(sqlSharkKB.getDialect());
        con = DriverManager.getConnection(sqlSharkKB.getDbAddress());
        ResultSet rs= SqlHelper.executeSQLCommandWithResult(con, "select 'drop table ' || name || ';' from sqlite_master where type = 'table';");
        String drops="";
        rs.next();
        while (rs.next()) {
            drops += rs.getString(1);
        }
        SqlHelper.executeSQLCommand(con, drops); //Drop database
        rs.close();
        assertNotNull(sqlSharkKB);
    }

    //@Ignore
    @Test
    public void testStSets() throws  SQLException, SharkKBException {

        SqlSharkKB sqlSharkKB = new SqlSharkKB(connection, "org.sqlite.JDBC");
        SqlTimeSTSet timeSet = new SqlTimeSTSet(sqlSharkKB);
        timeSet.createTimeSemanticTag(11111, 22222);
        timeSet.createTimeSemanticTag(33333, 44444);
        timeSet.createTimeSemanticTag(55555, 66666);

        Iterator<TimeSemanticTag> result = timeSet.tstTags();
        int i = 0;

        SqlSpatialSTSet spatialSet = new SqlSpatialSTSet(sqlSharkKB);
        SharkGeometry sg = InMemoSharkGeometry.createGeomByWKT("544");
        spatialSet.createSpatialSemanticTag("Test1", new String[]{"Si1"}, sg);
        spatialSet.createSpatialSemanticTag("Test2", new String[]{"Si2"}, sg);
        Enumeration<SpatialSemanticTag> tagsSpatialSet = spatialSet.spatialTags();
        assertEquals("544", tagsSpatialSet.nextElement().getGeometry().getWKT());
        assertEquals("544", tagsSpatialSet.nextElement().getGeometry().getWKT());

        String[] addresses = new String[]{"Treskowallee 8", "Wilhelminenhofstraße 6", "Rathenaustraße 10"};

        SqlPeerSTSet sqlPeerSet = new SqlPeerSTSet(sqlSharkKB);
        sqlPeerSet.createPeerSemanticTag( "TestTag1", sis1, addresses);
        sqlPeerSet.createPeerSemanticTag( "TestTag2", sis2, addresses[0]);
        sqlPeerSet.createPeerSemanticTag( "TestTag3", sis3, addresses);

        Enumeration<PeerSemanticTag> peerTags = sqlPeerSet.peerTags();
        assertArrayEquals(sis1, peerTags.nextElement().getSI());
        assertArrayEquals(sis2, peerTags.nextElement().getSI());
        assertArrayEquals(addresses, peerTags.nextElement().getAddresses());

    }


    //@Ignore
    @Test
    public void testSemanticTagCreation() throws SQLException, SharkKBException {

        SqlSharkKB sqlSharkKB = new SqlSharkKB(connection, "org.sqlite.JDBC");
        String[] addresses = new String[]{"Treskowallee 8", "Wilhelminenhofstraße 6", "Rathenaustraße 10"};
        long timeFrom = System.currentTimeMillis();
        long timeDusration = 100000;
        String wkt = "LINESTRING (30 10, 10 30, 40 40)";

        SqlSemanticTag tag = null;
        SqlTimeSemanticTag timeTag = null;
        SqlSpatialSemanticTag spatialTag = null;
        SqlPeerSemanticTag peerTag = null;
/*        SqlSNSemanticTag tagSN = new SqlSNSemanticTag(sis1, "testSNST", sqlSharkKB);
        assertNotNull(tagSN);
        SqlSNSemanticTag tagSN2 = new SqlSNSemanticTag(sis2, "testSNST2", sqlSharkKB);
        tagSN.setPredicate("TestPre", tagSN2);
        tagSN.setPredicate("TestPre2", tagSN2);
        Enumeration<String> result = tagSN.predicateNames();
        tagSN.sourceTags("TestPre");*/
        int i = 0;

        tag = new SqlSemanticTag(sis3, "testTag", sqlSharkKB);
        tag.setProperty("P1", "AA");
        tag.setProperty("P2", "BB");
        tag.setProperty("P3", "CC");

/*        SemanticTag tagReturned = stSet.getSemanticTag(sis3[0]);
        assertEquals("BB", tagReturned.getProperty("P2"));*/

        i++;
        //tagSN.removePredicate("TestPre", tagSN2);
        tag = new SqlSemanticTag(sis1, "testTag", sqlSharkKB);
        timeTag = new SqlTimeSemanticTag(sis2, "testTimeTag", sqlSharkKB, timeDusration, timeFrom);
        spatialTag = new SqlSpatialSemanticTag(sis3, "testSpatialTag", sqlSharkKB, wkt);
        peerTag = new SqlPeerSemanticTag(sis4, "testPeerTag", sqlSharkKB, addresses);
        assertNotNull(tag);
        assertNotNull(timeTag);
        assertNotNull(spatialTag);
        assertNotNull(peerTag);
        //SemanticTag tagFromDB = stSet.getSemanticTag(new String[]{"si1"});


    }

    //@Ignore
    @Test
    public void testAsipSpace() throws SQLException, SharkKBException {

        String[] addresses = new String[]{"Treskowallee 8", "Wilhelminenhofstraße 6", "Rathenaustraße 10"};
        SqlSharkKB sqlSharkKB = new SqlSharkKB(connection, "org.sqlite.JDBC");
        SqlSTSet topics = new SqlSTSet(sqlSharkKB, "topic");
        SqlSTSet types = new SqlSTSet(sqlSharkKB, "type");
        SqlPeerSTSet approvers = new SqlPeerSTSet(sqlSharkKB);
        SqlPeerSTSet receivers = new SqlPeerSTSet(sqlSharkKB);
        SqlPeerSTSet peers = new SqlPeerSTSet(sqlSharkKB);
        PeerSemanticTag sender = approvers.createPeerSemanticTag("senderTag", sis1, addresses);
        SqlSpatialSTSet locations = new SqlSpatialSTSet(sqlSharkKB);
        SqlTimeSTSet times = new SqlTimeSTSet(sqlSharkKB);
        int direction = 1;

        SqlAsipSpace space = new SqlAsipSpace(topics, types, direction, sender,
                receivers, approvers, times, locations, sqlSharkKB);
        SemanticTag tag = space.getApprovers().getSemanticTag(sis1);
        assertEquals("senderTag", tag.getName());

        SqlVocabulary vocabulary = new SqlVocabulary(topics, types,
                 peers, times, locations, sqlSharkKB);
        SqlKnowledge knowledge = new SqlKnowledge(vocabulary, sqlSharkKB);
        SqlAsipInfoSpace infoSpace = new SqlAsipInfoSpace(space, knowledge, sqlSharkKB);
        byte[] contentArray = "TestData".getBytes();
/*        SqlAsipInformation information = new SqlAsipInformation( "text", 5, contentArray, "info", infoSpace, sqlSharkKB);

        SqlAsipInformation testRead = new SqlAsipInformation(information.getId(), sqlSharkKB);
        assertArrayEquals(contentArray, testRead.getContentAsByte());*/

    }

    //@Ignore
    @Test
    public void testSemanticNet() throws SQLException, SharkKBException {

        SqlSharkKB sqlSharkKB = new SqlSharkKB(connection, "org.sqlite.JDBC");
        SqlSemanticNet semanticNet = new SqlSemanticNet(sqlSharkKB);

        SNSemanticTag snTag1 = semanticNet.createSemanticTag("SNTag1", sis1);
        SNSemanticTag snTag2 = semanticNet.createSemanticTag("SNTag2", sis1);
        SNSemanticTag snTag3 = semanticNet.createSemanticTag("SNTag3", sis1);

        semanticNet.setPredicate(snTag1, snTag2, "P1");
        semanticNet.setPredicate(snTag1, snTag3, "P2");
        semanticNet.setPredicate(snTag3, snTag2, "P3");

        semanticNet.removePredicate(snTag1, snTag2, "P1");

    }



}

