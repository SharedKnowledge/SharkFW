package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoInformation;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Micha on 01.06.2017.
 */
public class SqlAsipInformationTest {

    public static final String JDBC = "jdbc:sqlite:";
    public static final String PATH = ".\\src\\test\\java\\net\\sharkfw\\knowledgeBase\\persistent\\sql\\";

    public static final String DB1 = "info1.db";
    public static final String DB2 = "info2.db";
    public static final String DB3 = "info3.db";
    public static final String DB4 = "info4.db";
    public static final String DB5 = "info5.db";
    public static final String DB6 = "info6.db";
    public static final String DB7 = "info7.db";
    public static final String DB8 = "info8.db";
    public static final String DB9 = "info9.db";
    public static final String DB10 = "info10.db";
    public static final String DB11 = "info11.db";
    public static final String DB12 = "info12.db";
    public static final String DB13 = "info13.db";
    public static final String DB14 = "info14.db";
    public static final String DB15 = "info15.db";
    public static final String DB16 = "info16.db";
    public static final String DB17 = "info17.db";
    public static final String DB18 = "info18.db";
    public static final String DB19 = "info19.db";

    public static final String CONNECTION1 = JDBC + PATH + DB1;
    public static final String CONNECTION2 = JDBC + PATH + DB2;
    public static final String CONNECTION3 = JDBC + PATH + DB3;
    public static final String CONNECTION4 = JDBC + PATH + DB4;
    public static final String CONNECTION5 = JDBC + PATH + DB5;
    public static final String CONNECTION6 = JDBC + PATH + DB6;
    public static final String CONNECTION7 = JDBC + PATH + DB7;
    public static final String CONNECTION8 = JDBC + PATH + DB8;
    public static final String CONNECTION9 = JDBC + PATH + DB9;
    public static final String CONNECTION10 = JDBC + PATH + DB10;
    public static final String CONNECTION11 = JDBC + PATH + DB11;
    public static final String CONNECTION12 = JDBC + PATH + DB12;
    public static final String CONNECTION13 = JDBC + PATH + DB13;
    public static final String CONNECTION14 = JDBC + PATH + DB14;
    public static final String CONNECTION15 = JDBC + PATH + DB15;
    public static final String CONNECTION16 = JDBC + PATH + DB16;
    public static final String CONNECTION17 = JDBC + PATH + DB17;
    public static final String CONNECTION18 = JDBC + PATH + DB18;
    public static final String CONNECTION19 = JDBC + PATH + DB19;

    static String infoName1 = "sadhjhasödgdsa hasdög ";
    static String infoName2 = "sadhjhasödg hasadgasdgsdög ";
    static String infoName3 = "sdgasdgasdgadhjhasödg hasdög ";
    static String infoName4 = "sadhjhasödg hasdöadsgadsgsg ";
    static String infoName5 = "sadhjhasödg hasdvcxbxcvnög ";

    static String infoContent1 = "nasdjvk sahd sahvu hvjkhsauv svh sruvhapruhsvuasrhv ";
    static String infoContent2 = " asvlkhrbhuvgh rluasvlui raviuhasrv ösharv uoarhgouarhuar sövörousv ";
    static String infoContent3 = "ijasrig hasurgh asurhgsuvh aöjshvj  s.kn bvkjsa dgblkv hsalkjvbhskjdh ";
    static String infoContent4 = "jsfdhlg jhasfdg shguirha g57 twhgeruga-ks vgALRtigqeu";
    static String infoContent5 = "nasdjvk sahd sahvu hvjkhsauv svh öhjlasraörsdughausrjh öosui haösrau ";

    static SemanticTag semanticTag1 = InMemoSharkKB.createInMemoSemanticTag("semanticTag1", "si:semanticTag1");
    static SemanticTag semanticTag2 = InMemoSharkKB.createInMemoSemanticTag("semanticTag2", "si:semanticTag2");
    static SemanticTag semanticTag3 = InMemoSharkKB.createInMemoSemanticTag("semanticTag3", new String[]{"si:semanticTag31", "si:semanticTag32"});
    static SemanticTag semanticTag4 = InMemoSharkKB.createInMemoSemanticTag("semanticTag4", "si:semanticTag4");
    static SemanticTag semanticTag5 = InMemoSharkKB.createInMemoSemanticTag("semanticTag5", "si:semanticTag5");
    static SemanticTag semanticTag6 = InMemoSharkKB.createInMemoSemanticTag("semanticTag6", "si:semanticTag6");
    static SemanticTag semanticTag7 = InMemoSharkKB.createInMemoSemanticTag("semanticTag7", "si:semanticTag7");
    static SemanticTag semanticTag8 = InMemoSharkKB.createInMemoSemanticTag("semanticTag8", "si:semanticTag8");

    static PeerSemanticTag peerSemanticTag1 = InMemoSharkKB.createInMemoPeerSemanticTag("peerSemanticTag1", "si:peerSemanticTag1", "addr:peerSemanticTag1");
    static PeerSemanticTag peerSemanticTag2 = InMemoSharkKB.createInMemoPeerSemanticTag("peerSemanticTag2", "si:peerSemanticTag2", "addr:peerSemanticTag2");
    static PeerSemanticTag peerSemanticTag3 = InMemoSharkKB.createInMemoPeerSemanticTag("peerSemanticTag3", new String[]{"si:peerSemanticTag31", "si:peerSemanticTag32"}, new String[]{"addr:peerSemanticTag31", "addr:peerSemanticTag32"});
    static PeerSemanticTag peerSemanticTag4 = InMemoSharkKB.createInMemoPeerSemanticTag("peerSemanticTag4", "si:peerSemanticTag4", "addr:peerSemanticTag4");
    static PeerSemanticTag peerSemanticTag5 = InMemoSharkKB.createInMemoPeerSemanticTag("peerSemanticTag5", "si:peerSemanticTag5", "addr:peerSemanticTag5");
    static PeerSemanticTag peerSemanticTag6 = InMemoSharkKB.createInMemoPeerSemanticTag("peerSemanticTag6", "si:peerSemanticTag6", "addr:peerSemanticTag6");
    static PeerSemanticTag peerSemanticTag7 = InMemoSharkKB.createInMemoPeerSemanticTag("peerSemanticTag7", "si:peerSemanticTag7", "addr:peerSemanticTag7");
    static PeerSemanticTag peerSemanticTag8 = InMemoSharkKB.createInMemoPeerSemanticTag("peerSemanticTag8", "si:peerSemanticTag8", "addr:peerSemanticTag8");

    // TODO Create Time- and SpatialSemanticTags

    static STSet set1 = InMemoSharkKB.createInMemoSTSet();
    static STSet set2 = InMemoSharkKB.createInMemoSTSet();
    static STSet set3 = InMemoSharkKB.createInMemoSTSet();
    static PeerSTSet peerSet1 = InMemoSharkKB.createInMemoPeerSTSet();
    static PeerSTSet peerSet2 = InMemoSharkKB.createInMemoPeerSTSet();
    static PeerSTSet peerSet3 = InMemoSharkKB.createInMemoPeerSTSet();

    private static void deleteFiles() throws IOException {
        try {
            Files.delete(FileSystems.getDefault().getPath(PATH + DB1));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB2));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB3));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB4));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB5));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB6));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB7));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB8));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB9));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB10));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB11));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB12));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB13));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB14));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB15));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB16));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB17));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB18));
            Files.delete(FileSystems.getDefault().getPath(PATH + DB19));
        } catch (NoSuchFileException e) {
        }
    }

    private static void initSets() {
        try {
            set1.merge(semanticTag1);
            set1.merge(semanticTag2);
            set2.merge(semanticTag3);
            set2.merge(semanticTag4);
            set2.merge(semanticTag5);
            set3.merge(semanticTag6);
            set3.merge(semanticTag7);
            set3.merge(semanticTag8);

            peerSet1.merge(peerSemanticTag1);
            peerSet1.merge(peerSemanticTag2);
            peerSet2.merge(peerSemanticTag3);
            peerSet2.merge(peerSemanticTag4);
            peerSet2.merge(peerSemanticTag5);
            peerSet3.merge(peerSemanticTag6);
            peerSet3.merge(peerSemanticTag7);
            peerSet3.merge(peerSemanticTag8);

        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void setUp() throws IOException {
        L.setLogLevel(L.LOGLEVEL_ALL);
        deleteFiles();
        initSets();
    }

    @Test
    public void addInformation_success() throws SharkKBException {
        L.d("Using database: " + DB1, this);
        SqlSharkKB sqlSharkKB = new SqlSharkKB(CONNECTION1, "org.sqlite.JDBC");
        ASIPSpace space = sqlSharkKB.createASIPSpace(semanticTag1, null, null, null, null, null, null, ASIPSpace.DIRECTION_IN);
        sqlSharkKB.addInformation("Test", "This is just a TEst!", space);
    }

    @Test
    public void addAndGetInformation_success() throws SharkKBException {
        L.d("Using database: " + DB2, this);
        SqlSharkKB sqlSharkKB = new SqlSharkKB(CONNECTION2, "org.sqlite.JDBC");
        ASIPSpace space = sqlSharkKB.createASIPSpace(null, semanticTag1, null, null, null, null, null, ASIPSpace.DIRECTION_IN);

        sqlSharkKB.addInformation(infoName1, infoContent1, space);

        Iterator<ASIPInformation> information = sqlSharkKB.getInformation(space);
        if (information.hasNext()) {
            SqlAsipInformation next = (SqlAsipInformation) information.next();
            Assert.assertEquals(infoName1, next.getName());
        } else {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void getSpecificInformation() throws SharkKBException {
        L.d("Using database: " + DB3, this);
        SqlSharkKB sqlSharkKB = new SqlSharkKB(CONNECTION3, "org.sqlite.JDBC");

        ASIPSpace space = sqlSharkKB.createASIPSpace(null, semanticTag1, null, null, null, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space2 = sqlSharkKB.createASIPSpace(semanticTag2, null, null, null, null, null, null, ASIPSpace.DIRECTION_IN);

        sqlSharkKB.addInformation(infoName1, infoContent1, space);
        sqlSharkKB.addInformation(infoName2, infoContent2, space2);

        Iterator<ASIPInformation> information = sqlSharkKB.getInformation(space);
        if (information.hasNext()) {
            SqlAsipInformation next = (SqlAsipInformation) information.next();
            Assert.assertEquals(next.getName(), infoName1);
        } else {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void getMultipleInformation() throws SharkKBException {
        L.d("Using database: " + DB4, this);
        SqlSharkKB sqlSharkKB = new SqlSharkKB(CONNECTION4, "org.sqlite.JDBC");

        ASIPSpace space = sqlSharkKB.createASIPSpace(semanticTag1, semanticTag2, peerSemanticTag1, peerSemanticTag2, null, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space2 = sqlSharkKB.createASIPSpace(semanticTag3, null, null, null, null, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space3 = sqlSharkKB.createASIPSpace(null, semanticTag4, null, null, null, null, null, ASIPSpace.DIRECTION_IN);

        sqlSharkKB.addInformation(infoName1, infoContent1, space);
        sqlSharkKB.addInformation(infoName2, infoContent2, space2);
        sqlSharkKB.addInformation(infoName3, infoContent3, space2);
        sqlSharkKB.addInformation(infoName4, infoContent4, space2);

        Iterator<ASIPInformation> information = sqlSharkKB.getInformation(space2);
        List<String> stringList = Arrays.asList(infoName2, infoName3, infoName4);
        int numberOfInformation = 0;
        while (information.hasNext()) {
            SqlAsipInformation next = (SqlAsipInformation) information.next();
            Assert.assertTrue(stringList.contains(next.getName()));
            numberOfInformation++;
        }
        Assert.assertEquals(3, numberOfInformation);
    }

    @Test
    public void getMultipleInformationWithComplexSpace() throws SharkKBException {
        L.d("Using database: " + DB5, this);
        SqlSharkKB sqlSharkKB = new SqlSharkKB(CONNECTION5, "org.sqlite.JDBC");

        ASIPSpace space = sqlSharkKB.createASIPSpace(set1, set2, peerSet1, peerSemanticTag1, peerSet2, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space2 = sqlSharkKB.createASIPSpace(set2, set1, peerSet3, peerSemanticTag2, peerSet1, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space3 = sqlSharkKB.createASIPSpace(set1, set2, peerSet2, null, null, null, null, ASIPSpace.DIRECTION_IN);

        sqlSharkKB.addInformation(infoName1, infoContent1, space);
        sqlSharkKB.addInformation(infoName2, infoContent2, space2);
        sqlSharkKB.addInformation(infoName3, infoContent3, space2);
        sqlSharkKB.addInformation(infoName4, infoContent4, space3);

        Iterator<ASIPInformation> information = sqlSharkKB.getInformation(space);
        int infos = 0;
        while (information.hasNext()) {
            SqlAsipInformation next = (SqlAsipInformation) information.next();
            infos++;
        }
        Assert.assertEquals(1, infos);
    }

    @Test
    public void removeInformation() throws SharkKBException {
        L.d("Using database: " + DB6, this);
        SqlSharkKB sqlSharkKB = new SqlSharkKB(CONNECTION6, "org.sqlite.JDBC");
        ASIPSpace space = sqlSharkKB.createASIPSpace(set1, set2, peerSet1, peerSemanticTag1, peerSet2, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space2 = sqlSharkKB.createASIPSpace(set2, set1, peerSet3, peerSemanticTag2, peerSet1, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space3 = sqlSharkKB.createASIPSpace(semanticTag5, semanticTag4, null, null, null, null, null, ASIPSpace.DIRECTION_IN);

        sqlSharkKB.addInformation(infoName1, infoContent1, space);
        sqlSharkKB.addInformation(infoName3, infoContent3, space2);
        sqlSharkKB.addInformation(infoName4, infoContent4, space2);
        sqlSharkKB.addInformation(infoName5, infoContent5, space3);

        sqlSharkKB.removeInformation(space);

        int allInfos = 0;
        Iterator<ASIPInformationSpace> allInformationSpaces = sqlSharkKB.getAllInformationSpaces();
        while (allInformationSpaces.hasNext()){
            ASIPInformationSpace next = allInformationSpaces.next();
            allInfos+=next.numberOfInformations();
        }
        L.d("All infos: " + allInfos, this);
        Assert.assertEquals(3, allInfos);
    }

    @Test
    public void multipleGetRequests() throws SharkKBException {
        L.d("Using database: " + DB7, this);
        SqlSharkKB sqlSharkKB = new SqlSharkKB(CONNECTION7, "org.sqlite.JDBC");
        ASIPSpace space = sqlSharkKB.createASIPSpace(set1, set2, peerSet1, peerSemanticTag1, peerSet2, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space2 = sqlSharkKB.createASIPSpace(set2, set1, peerSet3, peerSemanticTag2, peerSet1, null, null, ASIPSpace.DIRECTION_IN);

        sqlSharkKB.addInformation(infoName1, infoContent1, space);

        Iterator<ASIPInformation> information = sqlSharkKB.getInformation(space);
        if (information.hasNext()) {
            SqlAsipInformation next = (SqlAsipInformation) information.next();
            Assert.assertEquals(infoName1, next.getName());
        } else {
            Assert.assertTrue(false);
        }

        sqlSharkKB.addInformation(infoName2, infoContent2, space2);

        Iterator<ASIPInformation> information1 = sqlSharkKB.getInformation(space2);
        if (information1.hasNext()) {
            SqlAsipInformation next = (SqlAsipInformation) information1.next();
            Assert.assertEquals(infoName2, next.getName());
        } else {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void getInformationSpaces() throws SharkKBException {
        L.d("Using database: " + DB8, this);
        SqlSharkKB sqlSharkKB = new SqlSharkKB(CONNECTION8, "org.sqlite.JDBC");

        ASIPSpace space = sqlSharkKB.createASIPSpace(set1, set2, peerSet1, peerSemanticTag1, peerSet2, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space2 = sqlSharkKB.createASIPSpace(set2, set1, peerSet3, peerSemanticTag2, peerSet1, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space3 = sqlSharkKB.createASIPSpace(semanticTag5, semanticTag4, null, null, null, null, null, ASIPSpace.DIRECTION_IN);

        sqlSharkKB.addInformation(infoName1, infoContent1, space);
        sqlSharkKB.addInformation(infoName2, infoContent2, space2);
        sqlSharkKB.addInformation(infoName3, infoContent3, space2);
        sqlSharkKB.addInformation(infoName4, infoContent4, space3);

        Iterator<ASIPInformationSpace> informationSpaces = sqlSharkKB.informationSpaces();

        int numberOfSpaces = 0;

        while (informationSpaces.hasNext()) {
            numberOfSpaces++;
            ASIPInformationSpace next = informationSpaces.next();
            if (SharkCSAlgebra.identical(next.getASIPSpace(), space)) {
                Assert.assertTrue(true);
                Assert.assertEquals(1, next.numberOfInformations());
            } else if (SharkCSAlgebra.identical(next.getASIPSpace(), space2)) {
                Assert.assertTrue(true);
                Assert.assertEquals(2, next.numberOfInformations());
            } else if (SharkCSAlgebra.identical(next.getASIPSpace(), space3)) {
                Assert.assertTrue(true);
                Assert.assertEquals(1, next.numberOfInformations());
            } else {
                Assert.assertTrue(false);
            }
        }

        Assert.assertEquals(3, numberOfSpaces);
    }

    @Test
    public void removeSingleInformation() throws SharkKBException {
        L.d("Using database: " + DB9, this);
        SqlSharkKB sqlSharkKB = new SqlSharkKB(CONNECTION9, "org.sqlite.JDBC");
        ASIPSpace space = sqlSharkKB.createASIPSpace(set1, set2, peerSet1, peerSemanticTag1, peerSet2, null, null, ASIPSpace.DIRECTION_IN);

        sqlSharkKB.addInformation(infoName1, infoContent1, space);
        sqlSharkKB.addInformation(infoName2, infoContent2, space);
        sqlSharkKB.addInformation(infoName3, infoContent3, space);

        SqlAsipInformation firstInfo = null;

        Iterator<ASIPInformation> information = sqlSharkKB.getInformation(space);
        if (information.hasNext()) {
            SqlAsipInformation next = (SqlAsipInformation) information.next();
            if(firstInfo==null) firstInfo = next;
        } else {
            Assert.assertTrue(false);
        }

        sqlSharkKB.removeInformation(firstInfo, space);

        int informationTwo = sqlSharkKB.getNumberInformation();
        Assert.assertEquals(2, informationTwo);
    }

    @Test
    public void removeInformationSpace() throws SharkKBException {
        L.d("Using database: " + DB10, this);
        SqlSharkKB sqlSharkKB = new SqlSharkKB(CONNECTION10, "org.sqlite.JDBC");

        ASIPSpace space = sqlSharkKB.createASIPSpace(set1, set2, peerSet1, peerSemanticTag1, peerSet2, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space2 = sqlSharkKB.createASIPSpace(set2, set1, peerSet3, peerSemanticTag2, peerSet1, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space3 = sqlSharkKB.createASIPSpace(semanticTag5, semanticTag4, null, null, null, null, null, ASIPSpace.DIRECTION_IN);

        sqlSharkKB.addInformation(infoName1, infoContent1, space);
        sqlSharkKB.addInformation(infoName2, infoContent2, space2);
        sqlSharkKB.addInformation(infoName3, infoContent3, space2);
        sqlSharkKB.addInformation(infoName4, infoContent4, space3);

        int allInfos = 0;
        Iterator<ASIPInformationSpace> allInformationSpaces = sqlSharkKB.getAllInformationSpaces();
        while (allInformationSpaces.hasNext()){
            ASIPInformationSpace next = allInformationSpaces.next();
            allInfos+=next.numberOfInformations();
        }
        L.d("All infos: " + allInfos, this);

        sqlSharkKB.removeInformationSpace(space);

        int allInfos2 = 0;
        Iterator<ASIPInformationSpace> allInformationSpaces2 = sqlSharkKB.getAllInformationSpaces();
        while (allInformationSpaces2.hasNext()){
            ASIPInformationSpace next = allInformationSpaces2.next();
            allInfos2+=next.numberOfInformations();
        }
        L.d("All infos2: " + allInfos2, this);


        Iterator<ASIPInformationSpace> informationSpacesSecond = sqlSharkKB.informationSpaces();
        int numberOfSpaces = 0;
        while (informationSpacesSecond.hasNext()) {
            numberOfSpaces++;
            informationSpacesSecond.next();
        }

        Assert.assertEquals(2, numberOfSpaces);
    }

    @Test
    public void mergeInformation() throws SharkKBException {
        L.d("Using database: " + DB11, this);
        SqlSharkKB sqlSharkKB = new SqlSharkKB(CONNECTION11, "org.sqlite.JDBC");

        ASIPSpace space = sqlSharkKB.createASIPSpace(set1, set2, peerSet1, peerSemanticTag1, peerSet2, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space2 = sqlSharkKB.createASIPSpace(set2, set1, peerSet3, peerSemanticTag2, peerSet1, null, null, ASIPSpace.DIRECTION_IN);
        ASIPSpace space3 = sqlSharkKB.createASIPSpace(semanticTag5, semanticTag4, null, null, null, null, null, ASIPSpace.DIRECTION_IN);

        ASIPInformation asipInformation1 = new InMemoInformation();
        asipInformation1.setName(infoName1);
        asipInformation1.setContent(infoContent1);
        ASIPInformation asipInformation2 = new InMemoInformation();
        asipInformation2.setName(infoName2);
        asipInformation2.setContent(infoContent2);
        ASIPInformation asipInformation3 = new InMemoInformation();
        asipInformation3.setName(infoName3);
        asipInformation3.setContent(infoContent3);

        sqlSharkKB.mergeInformation(Arrays.asList(asipInformation1, asipInformation2, asipInformation3).iterator(), space);
        sqlSharkKB.addInformation(infoName3, infoContent3, space2);
        sqlSharkKB.addInformation(infoName4, infoContent4, space3);

        Iterator<ASIPInformationSpace> informationSpacesSecond = sqlSharkKB.informationSpaces();

        int numberOfSpaces = 0;

        while (informationSpacesSecond.hasNext()) {
            numberOfSpaces++;
            ASIPInformationSpace next = informationSpacesSecond.next();
            if (SharkCSAlgebra.identical(space, next.getASIPSpace())) {
                L.d("1", this);
                Assert.assertEquals(3, next.numberOfInformations());
            } else if (SharkCSAlgebra.identical(space2, next.getASIPSpace())) {
                L.d("2", this);
                Assert.assertEquals(1, next.numberOfInformations());
            } else if (SharkCSAlgebra.identical(space, next.getASIPSpace())) {
                L.d("3", this);
                Assert.assertEquals(1, next.numberOfInformations());
            }
        }
        Assert.assertEquals(3, numberOfSpaces);
    }

/*
    SELECT tag_set.set_kind, tag_set.direction, information.content_length,
    information.content_stream, information.content_type, information.name,
    information.property, semantic_tag.name, semantic_tag.property,
    semantic_tag.system_property, semantic_tag.t_duration, semantic_tag.t_start,
    semantic_tag.tag_kind, semantic_tag.wkt,
    subject_identifier.identifier, address.address_name
    FROM tag_set
    INNER JOIN information ON tag_set.info_id = information.id
    INNER JOIN ( semantic_tag
                         INNER JOIN subject_identifier ON semantic_tag.id = subject_identifier.tag_id
                         INNER JOIN address ON semantic_tag.id = address.tag_id)
    ON tag_set.tag_id = semantic_tag.id

    */

}
