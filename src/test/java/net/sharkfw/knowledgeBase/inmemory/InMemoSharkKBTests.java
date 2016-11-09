package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.L;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.SpatialAlgebra;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;

import static net.sharkfw.asip.ASIPSpace.DIRECTION_INOUT;
import static net.sharkfw.asip.ASIPSpace.LOCATIONS;

/**
 * Created by j4rvis on 15.08.16.
 */
public class InMemoSharkKBTests {

    private String name =  "Alice";
    private String si = "www.sharksystem.net/alice";

    private PeerSemanticTag peerSemanticTag;

    @Test
    public void createASIPSpace_justTags_mergesTags(){
        InMemoSharkKB sharkKB = new InMemoSharkKB();
        this.peerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag(name, si, null);

        try {
            ASIPSpace space = sharkKB.createASIPSpace((SemanticTag) null, null, null, this.peerSemanticTag, null, null, null, ASIPSpace.DIRECTION_NOTHING);
            PeerSemanticTag sender = space.getSender();
            Assert.assertTrue(SharkCSAlgebra.identical(sender, this.peerSemanticTag));
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void contextualize_nullPointerException() throws SharkKBException {

        SharkKB kb = new InMemoSharkKB();

        SemanticTag topic1 = kb.getTopicSTSet().createSemanticTag("Shark", "http://sharksystem.net/");
        SemanticTag topic2 = kb.getTopicSTSet().createSemanticTag("HTW", "http://www.htw-berlin.de/");

        PeerSemanticTag author1 = kb.getPeerSTSet().createPeerSemanticTag("Tim", "tim@mail.com", "");
        PeerSemanticTag author2 = kb.getPeerSTSet().createPeerSemanticTag("Harald", "harald@mail.com", "");

        ASIPSpace space1 = kb.createASIPSpace(topic1, null, author1, null, null, null, null, DIRECTION_INOUT);
        ASIPSpace space2 = kb.createASIPSpace(topic2, null, author2, null, null, null, null, DIRECTION_INOUT);
        ASIPSpace space3 = kb.createASIPSpace(topic1, null, author2, null, null, null, null, DIRECTION_INOUT);
        ASIPSpace space4 = kb.createASIPSpace(topic2, null, author1, null, null, null, null, DIRECTION_INOUT);


        kb.addInformation("Ein schöner Sharktext...", space1);

        kb.addInformation("HTW-Krimskrams", space2);
        kb.addInformation("Shark Something", space3);
        kb.addInformation("anderer HTW-Krimskrams", space4);



        STSet topicSet = InMemoSharkKB.createInMemoSTSet();
        topicSet.merge(topic2);
        ASIPInterest inter = InMemoSharkKB.createInMemoASIPInterest(topicSet,null,(PeerSTSet) null,null,null,null,null,DIRECTION_INOUT);

        //NullPointerException in AbstractSharkKB.getDefaultFPSet (Zeile 1141)
        // Die Zeiler unter mir muss genutzt werden, damit der Test laeuft.
        // Wurde auskommentiert, damit shark trotzdem gebuat werden kann.
        ASIPInterest ctx = kb.contextualize(inter);
        L.d(L.asipSpace2String(ctx), this);
    }
	
    @Test
    public void contextualize_castException() throws SharkKBException {

        SharkKB kb = new InMemoSharkKB();

        SemanticTag topic1 = kb.getTopicSTSet().createSemanticTag("Shark", "http://sharksystem.net/");
        SemanticTag topic2 = kb.getTopicSTSet().createSemanticTag("HTW", "http://www.htw-berlin.de/");

        SemanticTag type1 = kb.getTypeSTSet().createSemanticTag("TypeShark", "http://sharksystem.net/tpye");
        SemanticTag type2 = kb.getTypeSTSet().createSemanticTag("TypeGLIS", "http://www.glis.de/type");

        PeerSemanticTag author1 = kb.getPeerSTSet().createPeerSemanticTag("Tim", "tim@mail.com", "");
        PeerSemanticTag author2 = kb.getPeerSTSet().createPeerSemanticTag("Harald", "harald@mail.com", "");

        SharkGeometry geo = InMemoSharkGeometry.createGeomByWKT("52.123456, 13.123456, 10");
        String [] siTagsGeo = {"www.location.com/Loc1"};
        SpatialSemanticTag loc = kb.getSpatialSTSet().createSpatialSemanticTag("One Location", siTagsGeo,geo);
        SpatialSTSet locations = InMemoSharkKB.createInMemoSpatialSTSet();

        ASIPSpace space1 = kb.createASIPSpace(topic1, type1, author1, null, null, null, loc, DIRECTION_INOUT);
        ASIPSpace space2 = kb.createASIPSpace(topic2, type2, author2, null, null, null, loc, DIRECTION_INOUT);
        ASIPSpace space3 = kb.createASIPSpace(topic1, type1, author2, null, null, null, loc, DIRECTION_INOUT);
        ASIPSpace space4 = kb.createASIPSpace(topic2, type2, author1, null, null, null, loc, DIRECTION_INOUT);


        kb.addInformation("Ein schöner Sharktext...", space1);
        kb.addInformation("HTW-Krimskrams", space2);
        kb.addInformation("Shark Something", space3);
        kb.addInformation("anderer HTW-Krimskrams", space4);


        PeerSTSet authorSet = InMemoSharkKB.createInMemoPeerSTSet();
        authorSet.merge(author1);

        STSet typeSet = InMemoSharkKB.createInMemoSTSet();
        typeSet.merge(type1);

        STSet topicSet = InMemoSharkKB.createInMemoSTSet();
        topicSet.merge(topic2);


        //Another Information using Sets as dimensions:
        ASIPSpace space5WithSets = kb.createASIPSpace(topicSet, typeSet, authorSet, (PeerSTSet) null, null, null, locations, DIRECTION_INOUT);
        kb.addInformation("Eine Informationen mit Sets.", space5WithSets);

        //TypeCastError with the SpatialSemanticTag (/Sets) and a minimum of parameter.
        ASIPInterest inter1 = InMemoSharkKB.createInMemoASIPInterest(topicSet,null,(PeerSTSet) null,null,null,null,locations,DIRECTION_INOUT);
        ASIPInterest ctx1 = kb.contextualize(inter1);
        L.d(L.asipSpace2String(ctx1), this);

        //contextualize with all needed parameter for the project, just in case.
        ASIPInterest inter2 = InMemoSharkKB.createInMemoASIPInterest(topicSet,typeSet,authorSet,null,null,null,locations,DIRECTION_INOUT);
        ASIPInterest ctx2 = kb.contextualize(inter2);
        L.d(L.asipSpace2String(ctx2), this);
    }

//    @Test
    public void contextualize_NotTheExpectedResult() throws SharkKBException {

        SharkKB kb = new InMemoSharkKB();

        SemanticTag topic1 = kb.getTopicSTSet().createSemanticTag("Shark", "http://sharksystem.net/");
        SemanticTag topic2 = kb.getTopicSTSet().createSemanticTag("HTW", "http://www.htw-berlin.de/");
        SemanticTag topic3 = kb.getTopicSTSet().createSemanticTag("Berlin", "http://www.berlin.de/");


        SemanticTag type1 = kb.getTypeSTSet().createSemanticTag("TypeShark", "http://sharksystem.net/tpye");
        SemanticTag type2 = kb.getTypeSTSet().createSemanticTag("TypeGLIS", "http://www.glis.de/type");
        SemanticTag type3 = kb.getTypeSTSet().createSemanticTag("TypeHunt", "http://www.hunt.de/type");

        PeerSemanticTag author1 = kb.getPeerSTSet().createPeerSemanticTag("Tim", "tim@mail.com", "");
        PeerSemanticTag author2 = kb.getPeerSTSet().createPeerSemanticTag("Harald", "harald@mail.com", "");

        SpatialSemanticTag loc = null;
        SpatialSTSet locations = null;
/*

        SharkGeometry geo = InMemoSharkGeometry.createGeomByWKT("52.123456, 13.123456, 10");
        String [] siTagsGeo = {"www.location.com/Loc1"};
        loc = kb.getSpatialSTSet().createSpatialSemanticTag("One Location", siTagsGeo,geo);
        locations = InMemoSharkKB.createInMemoSpatialSTSet();
*/

        L.setLogLevel(L.LOGLEVEL_ALL);
        //after contextualize: in the list:

        // note: author is in sender dimension
        ASIPSpace space1 = kb.createASIPSpace(topic1, type1, author1, null, null, null, loc, DIRECTION_INOUT);
        //after contextualize: NOT in the list:
        ASIPSpace space2 = kb.createASIPSpace(topic2, type2, author2, null, null, null, loc, DIRECTION_INOUT);
        //after contextualize: in the list:
        ASIPSpace space3 = kb.createASIPSpace(topic3, type3, author1, null, null, null, loc, DIRECTION_INOUT);

        kb.addInformation("Ein schöner Sharktext...", space1);
        kb.addInformation("HTW-Krimskrams", space2);
        kb.addInformation("Shark Something", space3);

        PeerSTSet authorSet1 = InMemoSharkKB.createInMemoPeerSTSet();
        authorSet1.merge(author1);
        // Returns all possible Tags (3 Topics, 3 Types, 2 Authors)
        ASIPInterest inter1 = InMemoSharkKB.createInMemoASIPInterest(null,null,(PeerSTSet) null,null,null,null,locations,DIRECTION_INOUT);
        ASIPInterest ctx1 = kb.contextualize(inter1);
        L.d("+++++++++++++++++++++++++++++++++++++++++++", this);
        L.d(" first ctx result - should be whole kb", this);
        L.d("+++++++++++++++++++++++++++++++++++++++++++", this);
        L.d(L.asipSpace2String(ctx1), this);

        // Returns all possible Tags with author1 (in sender dimensionn! (2 Topics, 2 Types, 1 Authors)

        ASIPInterest inter2 = InMemoSharkKB.createInMemoASIPInterest(
                null, /* topics */
                null, /* types */
                author1, /* sender */
                null, /* approvers */
                null, /* receiver */
                null, /* times */
                locations,DIRECTION_INOUT /* direction */
        );

        ASIPInterest ctx2 = kb.contextualize(inter2);
        L.d("+++++++++++++++++++++++++++++++++++++++++++", this);
        L.d(" 2nd ctx result - author 1 only", this);
        L.d("+++++++++++++++++++++++++++++++++++++++++++", this);
        L.d(L.asipSpace2String(ctx2), this);

        //If the size of Topics is the same, contextualize does not work as expected
        L.d("ctx1 Topic-size (should be 3): "+ctx1.getTopics().size()+" ctx2 Topic-size (should be 2): "+ctx2.getTopics().size(), this);
        if(ctx1.getTopics().size() == ctx2.getTopics().size())
        {
            throw new SharkKBException("Same size of Topics after contextualize");
        }
        //If the size of Types is the same, contextualize does not work as expected
        L.d("ctx1 Type-size (should be 3): "+ctx1.getTypes().size()+" ctx2 Type-size (should be 2): "+ctx2.getTypes().size(), this);
        if(ctx1.getTypes().size() == ctx2.getTypes().size())
        {
            throw new SharkKBException("Same size of Types after contextualize");
        }
    }
}
