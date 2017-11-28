package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.SharkKBException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dfe on 06.06.2017.
 */
@Ignore
public class SqlSemanticTagTest {

    public static final String PATH = "jdbc:sqlite:.\\src\\test\\java\\net\\sharkfw\\knowledgeBase\\persistent\\sql\\info12.db";
    public static final String[] sis1 = new String[]{"si1", "si2"};
    public static final String[] sis2 = new String[]{"si3", "si4"};
    public static final String[] sis3 = new String[]{"si5", "si6"};
    public static final String[] sis4 = new String[]{"si7", "si8"};
    public static final String[] addresses = new String[]{"Treskowallee 8", "Wilhelminenhofstraße 6", "Rathenaustraße 10"};
    public static final String[] names = new String[]{"Tag 1", "Tag 2", "Tag 3" , "Tag 3", "Tag 3"};
    public static final long timeFrom = System.currentTimeMillis();
    public static final long timeDusration = 100000;
    public static final String wkt = "LINESTRING (30 10, 10 30, 40 40)";

    @Before
    public void testConnectionAndBuild() throws SQLException, ClassNotFoundException {
        SqlSharkKB sqlSharkKB = new SqlSharkKB(PATH, "org.sqlite.JDBC");
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

    @Test
    public void testSemanticTagCreation() throws SQLException, SharkKBException {

        SqlSharkKB sqlSharkKB = new SqlSharkKB(PATH, "org.sqlite.JDBC");
        SqlSemanticTag tagCreated1 = new SqlSemanticTag(sis1, names[0], sqlSharkKB);
        SqlSemanticTag tagCreated2 = new SqlSemanticTag(sis2, names[1], sqlSharkKB);
        SqlSemanticTag tagCreated3 = new SqlSemanticTag(sis3, names[2], sqlSharkKB);

        SqlSemanticTag tagRetrievedWithSI1 = new SqlSemanticTag(sis1[0], sqlSharkKB, "topic");
        assertNotNull(tagRetrievedWithSI1);
        assertArrayEquals(sis1, tagRetrievedWithSI1.getSis());
        assertEquals(names[0], tagRetrievedWithSI1.getName());
        SqlSemanticTag tagRetrievedWithSI2 = new SqlSemanticTag(sis2[0], sqlSharkKB, "topic");
        assertNotNull(tagRetrievedWithSI2);
        assertArrayEquals(sis2, tagRetrievedWithSI2.getSis());
        assertEquals(names[1], tagRetrievedWithSI2.getName());
        SqlSemanticTag tagRetrievedWithSI3 = new SqlSemanticTag(sis3[0], sqlSharkKB, "topic");
        assertNotNull(tagRetrievedWithSI3);
        assertArrayEquals(sis3, tagRetrievedWithSI3.getSis());
        assertEquals(names[2], tagRetrievedWithSI3.getName());

        SqlSemanticTag tagRetrievedWithID1 = new SqlSemanticTag(1, sqlSharkKB);
        assertNotNull(tagRetrievedWithID1);
        assertArrayEquals(sis1, tagRetrievedWithID1.getSis());
        assertEquals(names[0], tagRetrievedWithID1.getName());
        SqlSemanticTag tagRetrievedWithID2 = new SqlSemanticTag(2, sqlSharkKB);
        assertNotNull(tagRetrievedWithID2);
        assertArrayEquals(sis2, tagRetrievedWithID2.getSis());
        assertEquals(names[1], tagRetrievedWithID2.getName());
        SqlSemanticTag tagRetrievedWithID3 = new SqlSemanticTag(3, sqlSharkKB);
        assertNotNull(tagRetrievedWithID3);
        assertArrayEquals(sis3, tagRetrievedWithID3.getSis());
        assertEquals(names[2], tagRetrievedWithID3.getName());


    }

    @Test
    public void testPeerSemanticTag() throws SQLException, SharkKBException {

        SqlSharkKB sqlSharkKB = new SqlSharkKB(PATH, "org.sqlite.JDBC");
        SqlPeerSemanticTag tagCreated = new SqlPeerSemanticTag(sis4, names[3], sqlSharkKB, addresses);

        SqlPeerSemanticTag tagRetrieved = new SqlPeerSemanticTag(sis4[0], sqlSharkKB);
        assertArrayEquals(addresses, tagRetrieved.getAddresses());
        assertArrayEquals(sis4, tagRetrieved.getSI());
        assertEquals(names[3], tagRetrieved.getName());


        tagRetrieved.removeAddress(addresses[0]);
        String[] addressesAltered = new String[]{addresses[1], addresses[2]};
        assertArrayEquals(addressesAltered, tagRetrieved.getAddresses());
    }

    @Test
    public void testSharkKBProperties() throws SQLException, SharkKBException {

        SqlSharkKB sqlSharkKB = new SqlSharkKB(PATH, "org.sqlite.JDBC");
        sqlSharkKB.setProperty("Prop1", "one");
        sqlSharkKB.setProperty("Prop2", "two");
        sqlSharkKB.setProperty("Prop3", "three");
        sqlSharkKB.setProperty("Prop4", "four");
        sqlSharkKB.setProperty("Prop5", "five");

        assertEquals("one", sqlSharkKB.getProperty("Prop1"));
        assertEquals("two", sqlSharkKB.getProperty("Prop2"));
        assertEquals("three", sqlSharkKB.getProperty("Prop3"));
        assertEquals("four", sqlSharkKB.getProperty("Prop4"));
        assertEquals("five", sqlSharkKB.getProperty("Prop5"));

    }






}
