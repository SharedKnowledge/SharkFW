package net.sharkfw.knowledgeBase.persistent;
import junit.framework.TestCase;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
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

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.junit.Assert.*;

/**
 * Created by Dustin Feurich
 */
public class TestSqlSharkKB {

    public static final String connection = "jdbc:sqlite:.\\src\\main\\java\\net\\sharkfw\\knowledgeBase\\persistent\\sql\\testShark.db";

    @Ignore
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


    @Ignore
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
        SqlSTSet stSet = null;
        stSet = new SqlSTSet(sqlSharkKB);
        SqlSNSemanticTag tagSN = new SqlSNSemanticTag(sis1, "testSNST", stSet.getStSetID(), sqlSharkKB);
        assertNotNull(tagSN);
        SqlSNSemanticTag tagSN2 = new SqlSNSemanticTag(sis2, "testSNST2", stSet.getStSetID(), sqlSharkKB);
        tagSN.setPredicate("TestPre", tagSN2);
        tagSN.setPredicate("TestPre2", tagSN2);
        Enumeration<String> result = tagSN.predicateNames();
        int i = 0;
        i++;
        //tagSN.removePredicate("TestPre", tagSN2);
        /*tag = new SqlSemanticTag(sis1, "testTag", stSet.getStSetID(), sqlSharkKB);
        timeTag = new SqlTimeSemanticTag(sis2, "testTimeTag", stSet.getStSetID(), sqlSharkKB, timeDusration, timeFrom);
        spatialTag = new SqlSpatialSemanticTag(sis3, "testSpatialTag", stSet.getStSetID(), sqlSharkKB, wkt);
        peerTag = new SqlPeerSemanticTag(sis4, "testPeerTag", stSet.getStSetID(), sqlSharkKB, addresses);
        assertNotNull(tag);
        assertNotNull(timeTag);
        assertNotNull(spatialTag);
        assertNotNull(peerTag);*/
        //SemanticTag tagFromDB = stSet.getSemanticTag(new String[]{"si1"});


    }

}

