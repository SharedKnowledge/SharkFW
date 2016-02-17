package net.sharkfw.asip.engine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.InformationCoordinates;
import net.sharkfw.knowledgeBase.InformationPoint;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import net.sharkfw.knowledgeBase.inmemory.InMemoGenericTagStorage;
import net.sharkfw.knowledgeBase.inmemory.InMemoSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticNet;

/**
 *
 * @author msc
 */
public class ASIPSerializer {
    
    public static final String HEADER = "HEADER";
    public static final String INTEREST = "INTEREST";
    public static final String KNOWLEDGE = "KNOWLEDGE";
    
    //Simple String Methods for Serialization
    
    public static String serializeExpose(ASIPMessage header, ASIPSpace interest) throws SharkKBException{
        return ASIPSerializer.serializeExposeJSON(header, interest).toString();
    }
    
    public static String serializeInsert(ASIPMessage header, ASIPKnowledge knowledge) throws SharkKBException{
        return ASIPSerializer.serializeInsertJSON(header, knowledge).toString();
    }
    
    public static String serializeHeader(ASIPMessage header) throws JSONException, SharkKBException{
        return ASIPSerializer.serializeHeaderJSON(header).toString();
    }

    public static String serializeInterest(ASIPSpace space) throws SharkKBException, JSONException {
        return ASIPSerializer.serializeInterestJSON(space).toString();
    }
    
    public static String serializeKnowledge(ASIPKnowledge knowledge) throws SharkKBException{
        return ASIPSerializer.serializeKnowledgeJSON(knowledge).toString();
    }
    
    public static String serializeTag(SemanticTag tag) throws JSONException, SharkKBException {
        return ASIPSerializer.serializeTagJSON(tag).toString();
    }
    
    public static String serializeSTSet(STSet stset) throws SharkKBException, JSONException {
        return ASIPSerializer.serializeSTSetJSON(stset).toString();
    }
    
    public static String serializeProperties(SystemPropertyHolder target) throws SharkKBException{
        return ASIPSerializer.serializePropertiesJSON(target).toString();
    }
    
    public static String serializeRelations(Enumeration<SemanticTag> tagEnum){
        return ASIPSerializer.serializeRelationsJSON(tagEnum).toString();
    }
        
    public static String serializeASIPSpace(ASIPSpace space) throws SharkKBException {
        return ASIPSerializer.serializeASIPSpaceJSON(space).toString();
    }
    
    // JSON Helper Methods
    
    public static JSONObject serializeExposeJSON(ASIPMessage header, ASIPSpace interest)
            throws SharkKBException, JSONException {
        
        JSONObject object = new JSONObject();
        object.put(HEADER, serializeHeaderJSON(header));
        object.put(INTEREST, serializeInterestJSON(interest));
        return object;
    }

    public static JSONObject serializeInsertJSON(ASIPMessage header, ASIPKnowledge knowledge)
            throws JSONException, SharkKBException{
        JSONObject object = new JSONObject();
        object.put(HEADER, serializeHeaderJSON(header));
        object.put(KNOWLEDGE, serializeKnowledgeJSON(knowledge));
        return object;
    }    
    
    public static JSONObject serializeHeaderJSON(ASIPMessage header) throws JSONException, SharkKBException {
        return new JSONObject()
            .put(ASIPMessage.ENCRYPTED, header.isEncrypted())
            .put(ASIPMessage.ENCRYPTEDSESSIONKEY, header.getEncyptedSessionKey())
            .put(ASIPMessage.VERSION, header.getVersion())
            .put(ASIPMessage.FORMAT, header.getFormat())
            .put(ASIPMessage.COMMAND, header.getCommand())
            .put(ASIPMessage.SENDER, serializeTagJSON(header.getSender()))
            .put(ASIPMessage.RECEIVERS, serializeSTSetJSON(header.getReceivers()))
            .put(ASIPMessage.SIGNATURE, header.getSignature());
    }
    
    public static JSONObject serializeInterestJSON(ASIPSpace space) throws SharkKBException, JSONException {
        return serializeASIPSpaceJSON(space);
    }
    
    public static JSONArray serializeInformationJSON(Iterator infos) throws SharkKBException{
        JSONArray infosArray = new JSONArray();
        while(infos.hasNext()){
            Information info = (Information) infos.next();
            JSONObject infoJSON = new JSONObject();

            infoJSON.put(Information.INFONAME, info.getName());
            infoJSON.put(Information.LASTMODIFIED, info.lastModified());
            infoJSON.put(Information.CREATIONTIME, info.creationTime());
            infoJSON.put(Information.CONTENTTYPE, info.getContentType());
            infoJSON.put(Information.CONTENTLENGTH, info.getContentLength());
            infoJSON.put(Information.CONTENTASBYTE, info.getContentAsByte());

            // TODO Properties necessarry?
            infoJSON.put(PropertyHolder.PROPERTIES, serializePropertiesJSON(info));

            // TODO Something missing?
            infosArray.put(infoJSON);
        }
        
        return infosArray;
    }
    
    public static JSONObject serializeKnowledgeJSON(ASIPKnowledge knowledge) throws SharkKBException{
        // TODO Knowledge Complete?
        /**
         * TODO Rework Knowledge to implement ASIP-bnf
         * - vocabulary
         * - {contextPointInformation}
         * -- contextSpace
         * -- {infoMetaData}
         * --- name
         * --- offset
         * --- length
         * - infoContents
         * 
        */
        if(knowledge==null) return null;
        
        JSONObject object = new JSONObject();
        JSONArray infoSpacesArray = new JSONArray();
        JSONArray infoPointsArray = new JSONArray();
        ASIPSpace vocabulary = knowledge.getVocabulary().asASIPSpace();
        Iterator infoSpaces = knowledge.informationSpaces();
        Iterator infoPoints = knowledge.informationPoints();
        
        object.put(ASIPKnowledge.VOCABULARY, serializeASIPSpaceJSON(vocabulary));
        
        while(infoSpaces.hasNext()){
            JSONObject spaceJSON = new JSONObject();
            
            ASIPInformationSpace space = (ASIPInformationSpace) infoSpaces.next();
            spaceJSON.put(ASIPInformationSpace.ASIPSPACE, serializeASIPSpaceJSON(space.getASIPSpace()));
            
            JSONArray infosArray = serializeInformationJSON(space.informations());
            spaceJSON.put(ASIPInformationSpace.INFORMATIONS, infosArray);
            
            infoSpacesArray.put(spaceJSON);
        }
        object.put(ASIPKnowledge.INFORMATIONSPACES, infoSpaces);
        
        while(infoPoints.hasNext()){
            InformationPoint point = (InformationPoint) infoPoints.next();
            JSONObject pointJSON = new JSONObject();
            
            InformationCoordinates coords = point.getInformationCoordinates();
            JSONObject infoCoordsJSON = new JSONObject();
            infoCoordsJSON.put(InformationCoordinates.TOPICS, serializeTagJSON(coords.getTopic()));
            infoCoordsJSON.put(InformationCoordinates.TYPES, serializeTagJSON(coords.getType()));
            infoCoordsJSON.put(InformationCoordinates.APPROVERS, serializeTagJSON(coords.getApprover()));
            infoCoordsJSON.put(InformationCoordinates.RECEIVERS, serializeTagJSON(coords.getReceiver()));
            infoCoordsJSON.put(InformationCoordinates.SENDER, serializeTagJSON(coords.getSender()));
            infoCoordsJSON.put(InformationCoordinates.LOCATIONS, serializeTagJSON(coords.getLocation()));
            infoCoordsJSON.put(InformationCoordinates.TIMES, serializeTagJSON(coords.getTime()));
            infoCoordsJSON.put(InformationCoordinates.DIRECTION, coords.getDirection());
            pointJSON.put(InformationPoint.INFOCOORDINATES, infoCoordsJSON);
            
            JSONArray infosArray = serializeInformationJSON(point.getInformation());
            pointJSON.put(InformationPoint.INFORMATIONS, infosArray);
            
            pointJSON.put(PropertyHolder.PROPERTIES, serializePropertiesJSON(point));
            
            infoPointsArray.put(pointJSON);
        }
        
        return object;
    }
    
    public static JSONObject serializeTagJSON(SemanticTag tag) throws JSONException, SharkKBException {
        
        JSONObject object = new JSONObject();
        
        object.put(SemanticTag.NAME, tag.getName());
        
        String[] sis = tag.getSI();
        JSONArray sisArray = new JSONArray();
        for(String si : sis){
            sisArray.put(si);
        }
        object.put(SemanticTag.SI, sisArray);
        
        // pst
        if(tag instanceof PeerSemanticTag) {
            PeerSemanticTag pst = (PeerSemanticTag) tag;
            
            String[] addresses = pst.getAddresses();
            JSONArray addrArray = new JSONArray();
            for(String addr : addresses){
                addrArray.put(addr);
            }
            object.put(PeerSemanticTag.ADDRESSES, addrArray);
        }

        // tst
        if(tag instanceof TimeSemanticTag) {
            TimeSemanticTag tst = (TimeSemanticTag) tag;
            object.put(TimeSemanticTag.FROM, tst.getFrom());
            object.put(TimeSemanticTag.DURATION, tst.getDuration());
        }
        
        // sst
        if(tag instanceof SpatialSemanticTag) {
            SpatialSemanticTag sst = (SpatialSemanticTag) tag;
            object.put(SpatialSemanticTag.GEOMETRY, sst.getGeometry());
        }
        
        object.put(PropertyHolder.PROPERTIES, serializePropertiesJSON(tag));
        
        return object;
    }
    
    public static JSONObject serializeSTSetJSON(STSet stset) throws SharkKBException, JSONException {
        
        //TODO Type of STSet?
        if(stset == null){
            return null;
        }
        
        JSONObject jsonObject = new JSONObject();
        String type = "";
        if(stset instanceof PeerSTSet)
            type = STSet.PEERSTSET;
        else if(stset instanceof TimeSTSet)
            type = STSet.TIMESTSET;
        else if(stset instanceof SpatialSTSet)
            type = STSet.SPATIALSTSET;
        else if(stset instanceof STSet)
            type = STSet.ANYSTSET;
        
        jsonObject.put(STSet.TYPE, type);
        
        JSONArray set = new JSONArray();
        Enumeration<SemanticTag> tags = stset.tags();
        while(tags.hasMoreElements()) {
            set.put(ASIPSerializer.serializeTagJSON(tags.nextElement()));
        }
        
        jsonObject.put(STSet.STSET, set);
        
        Enumeration<SemanticTag> tagEnum = stset.tags();
        if(stset instanceof SemanticNet || stset instanceof Taxonomy) {
            jsonObject.put(STSet.RELATIONS, serializeRelationsJSON(tagEnum));
        }
        
        return jsonObject;
    }
    
    public static JSONObject serializePropertiesJSON(SystemPropertyHolder target) throws SharkKBException{
        if(target == null) {
            return null;
        }
        
        Enumeration<String> propNamesEnum = target.propertyNames(false);
        if(propNamesEnum == null || !propNamesEnum.hasMoreElements()) {
            return new JSONObject();
        }
        JSONObject jsonOject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        while(propNamesEnum.hasMoreElements()){
            String name = propNamesEnum.nextElement();
            String value = target.getProperty(name);
            
            JSONObject property = new JSONObject();
            property.put(PropertyHolder.NAME, name);
            property.put(PropertyHolder.VALUE, value);
            jsonArray.put(property);
        }
        
        jsonOject.put(PropertyHolder.PROPERTIES, jsonArray);
        
        return jsonOject;
    }
    
    public static JSONObject serializeRelationsJSON(Enumeration<SemanticTag> tagEnum){
        
        if(tagEnum == null || !tagEnum.hasMoreElements())
            return null;

        SemanticTag tag = tagEnum.nextElement();
        boolean semanticNet;
        
        if(tag instanceof SNSemanticTag) {
            semanticNet = true;
        } else if(tag instanceof TXSemanticTag) {
            semanticNet = false;
        } else
            return null;
        
        JSONArray predicates = new JSONArray();
        JSONArray subSuperTags = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        
        if(semanticNet) {
            // Semantic Net
            do {
                SNSemanticTag snTag = (SNSemanticTag) tag;
                // get tag for next round
                tag = null;
                if(tagEnum.hasMoreElements())
                    tag = tagEnum.nextElement();
                
                String[] sSIs = snTag.getSI();
                if(sSIs != null) {
                    String sourceSI = sSIs[0];
                    
                    Enumeration<String> pNameEnum = snTag.predicateNames();
                    if(pNameEnum != null) {
                        while(pNameEnum.hasMoreElements()) {
                            String predicateName = pNameEnum.nextElement();                            
                            Enumeration<SNSemanticTag> targetEnum = 
                                    snTag.targetTags(predicateName);
                            if(targetEnum == null) {
                                continue;
                            }
                            while(targetEnum.hasMoreElements()) {
                                SNSemanticTag target = targetEnum.nextElement();
                                String[] tSIs = target.getSI();
                                if(tSIs == null) {
                                    continue;
                                }
                                
                                String targetSI = tSIs[0];
                                JSONObject predicate = new JSONObject();
                                
                                predicate.put(SemanticNet.NAME, predicateName);
                                predicate.put(SemanticNet.SOURCE, sourceSI);
                                predicate.put(SemanticNet.TARGET, targetSI);
                                
                                predicates.put(predicate);
                            }
                        }
                    }
                }
            } while(tag!=null);
            
            jsonObject.put(SemanticNet.PREDICATES, predicates);
            
        } else {
            // Taxonomy
            do {
                TXSemanticTag txTag = (TXSemanticTag) tag;
                // get tag for next round
                tag = null;
                if(tagEnum.hasMoreElements()) {
                    tag = tagEnum.nextElement();
                }
                
                String[] sSIs = txTag.getSI();
                if(sSIs != null) {
                    String sourceSI = sSIs[0];
                    
                    TXSemanticTag superTag = txTag.getSuperTag();
                    if(superTag != null) {
                        String[] tSIs = superTag.getSI();
                        if(tSIs == null) {
                            continue;
                        }

                        String targetSI = tSIs[0];
                        
                        JSONObject subSuperTag = new JSONObject();

                        subSuperTag.put(Taxonomy.SOURCE, sourceSI);
                        subSuperTag.put(Taxonomy.TARGET, targetSI);

                        subSuperTags.put(subSuperTag);
                    }
                }
            } while(tagEnum.hasMoreElements());
            
            jsonObject.put(Taxonomy.SUBSUPERTAGS, subSuperTags);
            
        }
        return jsonObject;
    }
        
    public static JSONObject serializeASIPSpaceJSON(ASIPSpace space) throws SharkKBException {
        if(space == null)
            return null;
        
        JSONObject jsonObject = new JSONObject();
         
        STSet topics = space.getTopics();
        if(topics != null && !topics.isEmpty()) {
            jsonObject.put(ASIPSpace.TOPICS, serializeSTSetJSON(topics));
        }
        
        // sender
        PeerSemanticTag sender = space.getSender();
        if(sender != null) {
            jsonObject.put(ASIPSpace.SENDER, serializeTagJSON(sender));
        }
        
        // approvers
        PeerSTSet approvers = space.getApprovers();
        if(approvers != null && !approvers.isEmpty()) {
            jsonObject.put(ASIPSpace.APPROVERS, serializeSTSetJSON(approvers));
        }
        
        // receivers
        PeerSTSet receivers = space.getReceivers();
        if(receivers != null && !receivers.isEmpty()) {
            jsonObject.put(ASIPSpace.RECEIVERS, serializeSTSetJSON(receivers));
        }
        
        // locations
        SpatialSTSet locations = space.getLocations();
        if(locations != null && !locations.isEmpty()) {
            jsonObject.put(ASIPSpace.LOCATIONS, serializeSTSetJSON(locations));
        }
        
        
        // times
        TimeSTSet times = space.getTimes();
        if(times != null && !times.isEmpty()) {
            jsonObject.put(ASIPSpace.TIMES, serializeSTSetJSON(times));
        }

        // direction
        jsonObject.put(ASIPSpace.DIRECTION, space.getDirection());
        
        return jsonObject;
    }
    
    // Deserialize
    
    public static ASIPMessage deserializeHeader(String header) throws SharkKBException{
        ASIPInMessage message = new ASIPInMessage();
        JSONObject jsonObject = new JSONObject(header);
        
        message.setEncrypted(jsonObject.getBoolean(ASIPMessage.ENCRYPTED));
        message.setEncyptedSessionKey(jsonObject.getString(ASIPMessage.ENCRYPTEDSESSIONKEY));
        message.setVersion(jsonObject.getString(ASIPMessage.VERSION));
        message.setFormat(jsonObject.getString(ASIPMessage.FORMAT));
        message.setCommand(jsonObject.getInt(ASIPMessage.COMMAND));
        
        String senderString = jsonObject.getString(ASIPMessage.SENDER);
        PeerSemanticTag sender = deserializePeerTag(senderString);
        message.setSender(sender);
        
        String receiverString = jsonObject.getString(ASIPMessage.RECEIVERS);
        STSet set = deserializeSTSet(receiverString);
        message.setReceivers(set);
        
        message.setSignature(jsonObject.getString(ASIPMessage.SIGNATURE));
        
        // TODO deserializeHeader message correct? IN/(OUT) ?
        
        return message;
    }
    
    public static ASIPSpace deserializeInterest(String interestString) throws SharkKBException {
        return deserializeASIPSpace(interestString);
    }
    
    /**
     * Deserializes knowledge and return a newly created knowledge object..
     * @param stream
     * @param knowledge
     * @return
     * @throws SharkKBException 
     */
    public static ASIPKnowledge deserializeKnowledge(InputStream stream) throws SharkKBException {
        
        
        
        return null; 
        // TODO deserializeKnowledge
        
        // return imkb.asKnowledge();
    }
    
    /**
     * Deserializes and merges knowledge into an existing knowledge base
     * @param target
     * @param knowledgeString
     * @throws SharkKBException 
     */
    public void deserializeAndMergeKnowledge(SharkKB target, String knowledgeString) throws SharkKBException {
        // deserialize vocabulary and merge into target
        SharkCS vocabulary = null; // shouldn't be null after deserialization
        Util.merge(target, vocabulary);
        
        // deserialize context
        
        /*
        LASP exchanges knowledge which contains semantically annotated 
        information. Each information is attached to a context space (!) not 
        only a single context point as in KEP. That actually is one major
        difference (enhancements) compared to KEP. Thus, we can deserialize
        ContextSpace objects and add information later...
        */
        
        /* we use deserializeASIPSpace in that class to create a context space out
            of a string */
        ASIPSpace cs = deserializeASIPSpace(target, knowledgeString);
        
        /* infos can be added now - tja und das muss man schlau machen
        wegen der eventuell großen Datenmengen. Man kann ein Infoobjekt
        eibnhängen, das aber nicht sofort alle Daten aus dem stream liest...
        Da InfoSpace nicht implementiert ist, kann man sich noch alles
        wünschen... ;)
        */
        // infoSpace.addInformation(??)
    }
    
    public static SemanticTag deserializeTag(STSet targetSet, String tagString) throws SharkKBException {
        
        // TODO What if targetSet equals null
        // TODO Types of SemanticTags?
        if(targetSet == null)
            targetSet = InMemoSharkKB.createInMemoSTSet();
        
        if(tagString == null)
            return null;
        
        JSONObject jsonObject = new JSONObject(tagString);
        Iterator siIterator = jsonObject.getJSONArray(SemanticTag.SI).iterator();
                
        List<String> list = new ArrayList<>();
        while(siIterator.hasNext()){
            list.add((String) siIterator.next());
        }
        
        String name = jsonObject.getString(SemanticTag.NAME);
        String[] sis =  new String[list.size()];
        sis = list.toArray(sis);
        
        SemanticTag tag = targetSet.createSemanticTag(name, sis);
        deserializeProperties(tag, tagString);
        return tag;
    }
    
    /**
     * static variant - not to be used in protocol parser
     * @param tag
     * @return
     * @throws SharkKBException 
     */
    public static SemanticTag deserializeTag(String tag) throws SharkKBException {
        // there is no specific set - create one
        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        return ASIPSerializer.deserializeTag(stSet, tag);
    }
    
    public static PeerSemanticTag deserializePeerTag(PeerSTSet targetSet, String tagString) throws SharkKBException {
        
        if(targetSet == null)
            targetSet = InMemoSharkKB.createInMemoPeerSTSet();
        
        if(tagString == null)
            return null;
        
        JSONObject jsonObject = new JSONObject(tagString);
        Iterator siIterator = jsonObject.getJSONArray(SemanticTag.SI).iterator();
        Iterator addressIterator = jsonObject.getJSONArray(PeerSemanticTag.ADDRESSES).iterator();
                
        List<String> list = new ArrayList<>();
        while(siIterator.hasNext()){
            list.add((String) siIterator.next());
        }
        
        List<String> addressesList = new ArrayList<>();
        while(addressIterator.hasNext()){
            addressesList.add((String) addressIterator.next());
        }
        
        String name = jsonObject.getString(SemanticTag.NAME);
        String[] sis =  new String[list.size()];
        sis = list.toArray(sis);
        String[] addresses = new String[addressesList.size()];
        addresses = addressesList.toArray(addresses);
        
        PeerSemanticTag tag = targetSet.createPeerSemanticTag(name, sis, addresses);
        deserializeProperties(tag, tagString);
        return tag;
    }
    
    /**
     * static variant - not to be used in protocol parser
     * @param tag
     * @return
     * @throws SharkKBException 
     */
    public static PeerSemanticTag deserializePeerTag(String tag) throws SharkKBException {
        // there is no specific set - create one
        PeerSTSet stSet = InMemoSharkKB.createInMemoPeerSTSet();
        return ASIPSerializer.deserializePeerTag(stSet, tag);
    }
    
    public static SpatialSemanticTag deserializeSpatialTag(SpatialSTSet targetSet, String tagString) throws SharkKBException {
        
        if(targetSet == null)
            targetSet = InMemoSharkKB.createInMemoSpatialSTSet();
        
        if(tagString == null)
            return null;
        
        JSONObject jsonObject = new JSONObject(tagString);
        Iterator siIterator = jsonObject.getJSONArray(SemanticTag.SI).iterator();
        Iterator geometryIterator = jsonObject.getJSONArray(SpatialSemanticTag.GEOMETRY).iterator();
                
        List<String> list = new ArrayList<>();
        while(siIterator.hasNext()){
            list.add((String) siIterator.next());
        }
        
        List<String> geometriesList = new ArrayList<>();
        while(geometryIterator.hasNext()){
            geometriesList.add((String) geometryIterator.next());
        }
        
        String name = jsonObject.getString(SemanticTag.NAME);
        String[] sis =  new String[list.size()];
        sis = list.toArray(sis);
        String[] geometries = new String[geometriesList.size()];
        geometries = geometriesList.toArray(geometries);
        
        SharkGeometry[] geoms = new SharkGeometry[geometries.length];
        for (int i = 0; i< geometries.length; i++) {
            geoms[i] = InMemoSharkGeometry.createGeomByEWKT(geometries[i]);
        }
        // TODO Geometries just adding the first geom
        SpatialSemanticTag tag = targetSet.createSpatialSemanticTag(name, sis, geoms[0]);
        deserializeProperties(tag, tagString);
        return tag;
    }
    
    /**
     * static variant - not to be used in protocol parser
     * @param tag
     * @return
     * @throws SharkKBException 
     */
    public static SpatialSemanticTag deserializeSpatialTag(String tag) throws SharkKBException {
        // there is no specific set - create one
        SpatialSTSet stSet = InMemoSharkKB.createInMemoSpatialSTSet();
        return ASIPSerializer.deserializeSpatialTag(stSet, tag);
    }
    
    public static TimeSemanticTag deserializeTimeTag(TimeSTSet targetSet, String tagString) throws SharkKBException {
        
        if(targetSet == null)
            targetSet = InMemoSharkKB.createInMemoTimeSTSet();
        
        if(tagString == null)
            return null;
        
        // TODO TimeTag Correct?
        
        JSONObject jsonObject = new JSONObject(tagString);
//        Iterator siIterator = jsonObject.getJSONArray(SemanticTag.SI).iterator();
//                
//        List<String> list = new ArrayList<>();
//        while(siIterator.hasNext()){
//            list.add((String) siIterator.next());
//        }
//        
//        String name = jsonObject.getString(SemanticTag.NAME);
//        String[] sis =  new String[list.size()];
//        sis = list.toArray(sis);
        Long from = jsonObject.getLong(TimeSemanticTag.FROM);
        Long duration = jsonObject.getLong(TimeSemanticTag.DURATION);
        
        TimeSemanticTag tag = targetSet.createTimeSemanticTag(from, duration);
        // TODO TimeTag no properties necessary?
//        deserializeProperties(tag, tagString);
        return tag;
    }
    
    /**
     * static variant - not to be used in protocol parser
     * @param tag
     * @return
     * @throws SharkKBException 
     */
    public static TimeSemanticTag deserializeTimeTag(String tag) throws SharkKBException {
        // there is no specific set - create one
        TimeSTSet stSet = InMemoSharkKB.createInMemoTimeSTSet();
        return ASIPSerializer.deserializeTimeTag(stSet, tag);
    }
    
    public static STSet deserializeAnySTSet(STSet stSet, String stSetString) throws SharkKBException{   
        // TODO useless???
        JSONObject jsonObject = new JSONObject(stSetString);
        String typeJSON = jsonObject.getString(STSet.TYPE);
        STSet set = stSet;
        switch (typeJSON) {
            case STSet.PEERSTSET:
                return ASIPSerializer.deserializePeerSTSet(set, stSetString);
            case STSet.TIMESTSET:
                return ASIPSerializer.deserializeTimeSTSet(set, stSetString);
            case STSet.SPATIALSTSET:
                return ASIPSerializer.deserializeSpatialSTSet(set, stSetString);
            case STSet.ANYSTSET:
                if(set==null) set = InMemoSharkKB.createInMemoSTSet();
                return ASIPSerializer.deserializeSTSet(set, stSetString);
            default:
                break;
        }
        
        return null;
    }
    
    public static STSet deserializeSTSet(STSet target, String stSetString) throws SharkKBException{
        
        // SemanticNet
        JSONObject jsonObject = new JSONObject(stSetString);
        JSONArray jsonArray = jsonObject.getJSONArray(STSet.STSET);
        Iterator stIterator = jsonArray.iterator();
        while(stIterator.hasNext()){
            deserializeTag(target, stIterator.next().toString());
        }
        return target;
    }
    
    public static PeerSTSet deserializePeerSTSet(STSet stSet, String stSetString) throws SharkKBException{
        STSet set = stSet;
        if(set==null) set = InMemoSharkKB.createInMemoPeerSTSet();
        return (PeerSTSet) ASIPSerializer.deserializeSTSet(set, stSetString);
    }
    
    public static TimeSTSet deserializeTimeSTSet(STSet stSet, String stSetString) throws SharkKBException{
        STSet set = stSet;
        if(set==null) set = InMemoSharkKB.createInMemoTimeSTSet();
        return (TimeSTSet) ASIPSerializer.deserializeSTSet(set, stSetString);
    }
    
    public static SpatialSTSet deserializeSpatialSTSet(STSet stSet, String stSetString) throws SharkKBException{
        STSet set = stSet;
        if(set==null) set = InMemoSharkKB.createInMemoSpatialSTSet();
        return (SpatialSTSet) ASIPSerializer.deserializeSTSet(set, stSetString);
    }
    
    public static STSet deserializeSTSet(String stSetString) throws SharkKBException{
        // there is no specific set - create one
        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        return ASIPSerializer.deserializeSTSet(stSet, stSetString);
    }
    
    public static void deserializeProperties(SystemPropertyHolder target, String properties) throws SharkKBException{
        if(target == null) return;
        if(properties == null) return;
        
        JSONObject jsonObject = new JSONObject(properties);
        JSONArray jsonArray = jsonObject.getJSONArray(PropertyHolder.PROPERTIES);
        Iterator iterator = jsonArray.iterator();
        
        while(iterator.hasNext()){
            JSONObject property = (JSONObject) iterator.next();
            String name = property.getString(PropertyHolder.NAME);
            String value = property.getString(PropertyHolder.VALUE);
            
            target.setProperty(name, value);
        }
    }
    
    public static void deserializeRelations(Taxonomy target, String relations){
        // TODO deserializeRelations
        if(target==null) return;
        if(relations==null) return;
        JSONObject jsonObject = new JSONObject(relations);
        JSONArray jsonArray = jsonObject.getJSONArray(Taxonomy.SUBSUPERTAGS);
        Iterator iterator = jsonArray.iterator();
        
        while(iterator.hasNext()){
            JSONObject relation = (JSONObject) iterator.next();
            String sourceSI = relation.getString(Taxonomy.SOURCE);
            String targetSI = relation.getString(Taxonomy.TARGET);
            try {
                TXSemanticTag sourceTag = (TXSemanticTag) target.getSemanticTag(sourceSI);
                if(sourceTag == null) continue;

                TXSemanticTag targetTag = (TXSemanticTag) target.getSemanticTag(targetSI);
                if(targetTag == null) continue;
                
                // set super tag
                sourceTag.move(targetTag);
            }
            catch(SharkKBException skbe) {
                // ignore and go ahead
            }
        }
        
    }
    
    public static void deserializeRelations(SemanticNet target, String relations){
        // TODO deserializeRelations
        if(target==null) return;
        if(relations==null) return;
        JSONObject jsonObject = new JSONObject(relations);
        JSONArray jsonArray = jsonObject.getJSONArray(SemanticNet.PREDICATES);
        Iterator iterator = jsonArray.iterator();
        
        
        while(iterator.hasNext()){
            JSONObject relation = (JSONObject) iterator.next();
            String sourceSI = relation.getString(SemanticNet.SOURCE);
            String targetSI = relation.getString(SemanticNet.TARGET);
            String predicateName = relation.getString(SemanticNet.NAME);
            try {
                SNSemanticTag sourceTag = (SNSemanticTag) target.getSemanticTag(sourceSI);
                if(sourceTag == null) continue;

                SNSemanticTag targetTag = (SNSemanticTag) target.getSemanticTag(targetSI);
                if(targetTag == null) continue;
                
                // set super tag
                sourceTag.setPredicate(predicateName, targetTag);
            }
            catch(SharkKBException skbe) {
                // ignore and go ahead
            }
        }
        
            
    
    }
        
    public static ASIPSpace deserializeASIPSpace(SharkKB kb, String spaceString) throws SharkKBException {
        
        ASIPInterest interest = InMemoSharkKB.createInMemoASIPInterest();
        
        JSONObject deserialized = new JSONObject(spaceString);
        JSONArray topicsArray = deserialized.getJSONArray(ASIPSpace.TOPICS);
        JSONArray typesArray = deserialized.getJSONArray(ASIPSpace.TYPES);
        JSONArray approversArray = deserialized.getJSONArray(ASIPSpace.APPROVERS);
        JSONArray receiversArray = deserialized.getJSONArray(ASIPSpace.RECEIVERS);
        JSONArray locationsArray = deserialized.getJSONArray(ASIPSpace.LOCATIONS);
        JSONArray timesArray = deserialized.getJSONArray(ASIPSpace.TIMES);
        JSONObject senderObject = deserialized.getJSONObject(ASIPSpace.SENDER);
        int direction = deserialized.getInt(ASIPSpace.DIRECTION);
        
        STSet topics = deserializeSTSet(interest.getTopics(), topicsArray.toString());
        STSet types = deserializeSTSet(interest.getTypes(), typesArray.toString());
        PeerSTSet approvers = deserializePeerSTSet(interest.getApprovers(), approversArray.toString());
        PeerSTSet receivers = deserializePeerSTSet(interest.getReceivers(), receiversArray.toString());
        SpatialSTSet locations = deserializeSpatialSTSet(interest.getLocations(), locationsArray.toString());
        TimeSTSet times = deserializeTimeSTSet(interest.getTimes(), timesArray.toString());
        // TODO Casting okay?
        PeerSemanticTag sender = (PeerSemanticTag) deserializeTag(null, senderObject.toString());
        
        interest.setTopics(topics);
        interest.setTypes(types);
        interest.setApprovers(approvers);
        interest.setReceivers(receivers);
        interest.setLocations(locations);
        interest.setTimes(times);
        interest.setSender(sender);
        interest.setDirection(direction);
        
        return interest;
    }
    
    public static ASIPSpace deserializeASIPSpace(String sharkCS) throws SharkKBException {
        InMemoSharkKB imkb = new InMemoSharkKB();
        return ASIPSerializer.deserializeASIPSpace(imkb, sharkCS);
    }
    
    public static ASIPMessage deserializeInsert(String message){
        // TODO deserializeInsert
        return null;
    }
    
    public static ASIPMessage deserializeExpose(String message){
        // TODO deserializeExpose
        return null;
    }
    
    private SemanticNet cast2SN(STSet stset) throws SharkKBException {
        SemanticNet sn;
        try {
            sn = (SemanticNet) stset;
        }
        catch(ClassCastException e) {
            InMemoSTSet imset = null;
            
            try {
                imset = (InMemoSTSet) stset;
            }
            catch(ClassCastException cce) {
                throw new SharkKBException("sorry, this implementation works with in memo shark kb implementation only");
            }
            
            InMemoGenericTagStorage tagStorage = imset.getTagStorage();
            sn = new InMemoSemanticNet(tagStorage);
        }
        
        return sn;
    }
}
