package net.sharkfw.asip.serialization;

import net.sharkfw.asip.*;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoASIPKnowledge;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by j4rvis on 12/7/16.
 */
public class ASIPKnowledgeConverter {

    public final static String NAME = "NAME";
    public final static String OFFSET = "OFFSET";
    public final static String LENGTH = "LENGTH";
    public final static String CONTENT_TYPE = "CONTENT_TYPE";

    private ASIPKnowledge knowledge;
    private String serializedKnowledge = "";
    private byte[] content = null;
    private JSONObject serializedKnowledgeAsJSON;

    // Vice Versa Constructors

    /**
     * Converts the knowledge object to the jsonObject including the content meta information and the content itself.
     * @param knowledge
     * @throws SharkKBException
     */
    public ASIPKnowledgeConverter(ASIPKnowledge knowledge) throws SharkKBException {
        this.knowledge = knowledge;

        JSONObject object = new JSONObject();

        SharkVocabulary vocabulary = this.knowledge.getVocabulary();

        JSONObject serializedVocabulary = new JSONObject();

        if(vocabulary!=null){
            serializedVocabulary = ASIPMessageSerializerHelper.serializeVocabulary(vocabulary);
        }
        object.put(ASIPKnowledge.VOCABULARY, serializedVocabulary);

        JSONArray informationSpaceArray = new JSONArray();
        int currentOffset = 0;
        Iterator<ASIPInformationSpace> informationSpaceIterator = this.knowledge.informationSpaces();
        while (informationSpaceIterator.hasNext()){

            ASIPInformationSpace nextInformationSpace = informationSpaceIterator.next();
            ASIPSpace asipSpace = nextInformationSpace.getASIPSpace();
            Iterator<ASIPInformation> informationIterator = nextInformationSpace.informations();

            JSONObject jsonInformationSpace = new JSONObject();
            JSONArray jsonInformationArray = new JSONArray();

            while (informationIterator.hasNext()){
                ASIPInformation nextInformation = informationIterator.next();
                JSONObject jsonInformationObject = new JSONObject();

                jsonInformationObject.put(NAME, nextInformation.getName());
                jsonInformationObject.put(LENGTH, nextInformation.getContentLength());
                jsonInformationObject.put(OFFSET, currentOffset);
                jsonInformationObject.put(CONTENT_TYPE, nextInformation.getContentType());

                System.arraycopy(nextInformation.getContentAsByte(), 0, this.content, currentOffset,(int) nextInformation.getContentLength());
//                this.content += nextInformation.getContentAsString();
                currentOffset=this.content.length;

                jsonInformationArray.put(jsonInformationObject);
            }

            jsonInformationSpace.put(ASIPInformationSpace.ASIPSPACE, ASIPMessageSerializerHelper.serializeASIPSpace(asipSpace).toString());
            jsonInformationSpace.put(ASIPInformationSpace.INFORMATION, jsonInformationArray);
            informationSpaceArray.put(jsonInformationSpace);
        }

        object.put(ASIPKnowledge.INFORMATIONSPACES, informationSpaceArray);

        this.serializedKnowledge = object.toString();
        this.serializedKnowledgeAsJSON = object;
    }

    /**
     * Converts the jsonObject and the content to the given knowledge object.
     * @param serializedKnowledge
     * @param content
     * @throws SharkKBException
     */
    public ASIPKnowledgeConverter(String serializedKnowledge, byte[] content) throws SharkKBException, ASIPSerializerException {
        this.serializedKnowledge = serializedKnowledge;
        this.content = content;

        JSONObject jsonObject = new JSONObject(serializedKnowledge);
        this.serializedKnowledgeAsJSON = jsonObject;

        JSONObject vocabularyJSON = jsonObject.getJSONObject(ASIPKnowledge.VOCABULARY);

        SemanticNet topics = InMemoSharkKB.createInMemoSemanticNet();
        SemanticNet types = InMemoSharkKB.createInMemoSemanticNet();
        PeerTaxonomy peers = InMemoSharkKB.createInMemoPeerTaxonomy();
        SpatialSTSet locations = InMemoSharkKB.createInMemoSpatialSTSet();
        TimeSTSet times = InMemoSharkKB.createInMemoTimeSTSet();
        if(vocabularyJSON.has(SharkVocabulary.TOPICS)){
            ASIPMessageSerializerHelper.deserializeSTSet(topics, vocabularyJSON.get(SharkVocabulary.TOPICS).toString());
        }
        if(vocabularyJSON.has(SharkVocabulary.TYPES)){
            ASIPMessageSerializerHelper.deserializeSTSet(types, vocabularyJSON.get(SharkVocabulary.TYPES).toString());
        }
        if(vocabularyJSON.has(SharkVocabulary.PEERS)){
            ASIPMessageSerializerHelper.deserializePeerTaxonomy(peers, vocabularyJSON.get(SharkVocabulary.PEERS).toString());
        }
        if(vocabularyJSON.has(SharkVocabulary.LOCATIONS)){
            ASIPMessageSerializerHelper.deserializeSpatialSTSet(locations, vocabularyJSON.get(SharkVocabulary.LOCATIONS).toString());
        }
        if(vocabularyJSON.has(SharkVocabulary.TIMES)){
            ASIPMessageSerializerHelper.deserializeTimeSTSet(times, vocabularyJSON.get(SharkVocabulary.TIMES).toString());
        }

        Knowledge knowledge = new InMemoASIPKnowledge();
        SharkKB kb = new InMemoSharkKB(topics, types, peers, locations, times, knowledge);

        JSONArray informationSpacesArray = jsonObject.getJSONArray(ASIPKnowledge.INFORMATIONSPACES);

        for (int i = 0; i <informationSpacesArray.length(); i++) {
            JSONObject nextInformationSpace = informationSpacesArray.getJSONObject(i);

            if (nextInformationSpace.has(ASIPInformationSpace.ASIPSPACE) && nextInformationSpace.has(ASIPInformationSpace.INFORMATION)) {
                ASIPInterest interest = ASIPMessageSerializerHelper.deserializeASIPInterest(nextInformationSpace.getString(ASIPInformationSpace.ASIPSPACE));

                JSONArray informationJSONArray = nextInformationSpace.getJSONArray(ASIPInformationSpace.INFORMATION);
                for (int k = 0; k < informationJSONArray.length(); k++) {
                    JSONObject nextInformation = informationJSONArray.getJSONObject(k);

                    int offset = nextInformation.getInt(OFFSET);
                    int length = nextInformation.getInt(LENGTH);
                    String contentType = nextInformation.getString(CONTENT_TYPE);
                    String name = "";
                    if(nextInformation.has(NAME)){
                        name = nextInformation.getString(NAME);
                    }

                    try {
//                        String informationContent = this.content.substring(offset, offset + length);
                        byte[] infoContent = Arrays.copyOfRange(this.content, offset, offset + length);
                        ASIPInformation asipInformation = kb.addInformation(infoContent, interest);
                        asipInformation.setContentType(contentType);
                        if(!name.isEmpty()){
                            asipInformation.setName(name);
                        }
                    } catch (StringIndexOutOfBoundsException e){
                        throw new ASIPSerializerException("Message not complete yet");
                    }

                }

            }
        }
        if(jsonObject.has(PropertyHolder.PROPERTIES)){
            ASIPMessageSerializerHelper.deserializeProperties(kb, jsonObject.get(PropertyHolder.PROPERTIES).toString());
        }

        this.knowledge = kb;
    }

    // Getter

    public ASIPKnowledge getKnowledge() {
        return knowledge;
    }

    public String getSerializedKnowledge() {
        return serializedKnowledge;
    }

    public byte[] getContent() {
        return content;
    }

    public JSONObject getSerializedKnowledgeAsJSON() {
        return serializedKnowledgeAsJSON;
    }

    public void setSerializedKnowledgeAsJSON(JSONObject serializedKnowledgeAsJSON) {
        this.serializedKnowledgeAsJSON = serializedKnowledgeAsJSON;
    }
}
