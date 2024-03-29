package net.sharkfw.asip.serialization;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import net.sharkfw.knowledgeBase.inmemory.*;
import net.sharkfw.system.L;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by j4rvis on 12/7/16.
 */
public class ASIPMessageSerializerHelper {

    // SERIALIZATION

    public static JSONObject serializeInterest(ASIPSpace space) throws SharkKBException, JSONException {
        return ASIPMessageSerializerHelper.serializeASIPSpace(space);
    }

    public static JSONObject serializeHeader(ASIPMessage header) throws JSONException, SharkKBException {
        return new JSONObject()
                .put(ASIPMessage.VERSION, header.getVersion())
                .put(ASIPMessage.FORMAT, header.getFormat())
                .put(ASIPMessage.ENCRYPTED, header.isEncrypted())
                .put(ASIPMessage.ENCRYPTEDSESSIONKEY, header.getEncryptedSessionKey())
                .put(ASIPMessage.SIGNED, header.isSigned())
                .put(ASIPMessage.TTL, header.getTtl())
                .put(ASIPMessage.COMMAND, header.getCommand())
                .put(ASIPMessage.PHYSICALSENDER, (header.getPhysicalSender() != null) ?
                        serializeTag(header.getPhysicalSender()): "")
                .put(ASIPMessage.RECEIVERPEER, (header.getReceiverPeer() != null) ?
                        serializeTag(header.getReceiverPeer()) : "")
                .put(ASIPMessage.RECEIVERLOCATION, (header.getReceiverSpatial() != null) ?
                        serializeTag(header.getReceiverSpatial()) : "")
                .put(ASIPMessage.RECEIVERTIME, (header.getReceiverTime() != null) ?
                        serializeTag(header.getReceiverTime()) : "")
                .put(ASIPMessage.TOPIC, (header.getTopic() != null) ?
                        serializeTag(header.getTopic()) : "")
                .put(ASIPMessage.TYPE, (header.getType() != null) ?
                        serializeTag(header.getType()) : "");
    }

    public static JSONObject serializeVocabulary(SharkVocabulary vocabulary) throws SharkKBException {
        JSONObject jsonObject = new JSONObject();

        if(vocabulary!=null){
            jsonObject.put(SharkVocabulary.TOPICS, serializeSTSet(vocabulary.getTopicSTSet()));
            jsonObject.put(SharkVocabulary.TYPES, serializeSTSet(vocabulary.getTypeSTSet()));
            jsonObject.put(SharkVocabulary.PEERS, serializeSTSet(vocabulary.getPeerSTSet()));
            jsonObject.put(SharkVocabulary.LOCATIONS, serializeSTSet(vocabulary.getSpatialSTSet()));
            jsonObject.put(SharkVocabulary.TIMES, serializeSTSet(vocabulary.getTimeSTSet()));
        }

        return jsonObject;
    }

    public static JSONObject serializeTag(SemanticTag tag) throws JSONException, SharkKBException {

        JSONObject object = new JSONObject();

        object.put(SemanticTag.NAME, tag.getName());

        String[] sis = tag.getSI();
        JSONArray sisArray = new JSONArray();
        for (String si : sis) {
            sisArray.put(si);
        }
        object.put(SemanticTag.SI, sisArray);

        // pst
        if (tag instanceof PeerSemanticTag) {
            PeerSemanticTag pst = (PeerSemanticTag) tag;

            String[] addresses = pst.getAddresses();
            JSONArray addrArray = new JSONArray();
            if(addresses!=null && addresses.length > 0 && addresses[0]!=null){
                for (String addr : addresses) {
                    addrArray.put(addr);
                }
            }
            object.put(PeerSemanticTag.ADDRESSES, addrArray);
        }

        // tst
        if (tag instanceof TimeSemanticTag) {
            TimeSemanticTag tst = (TimeSemanticTag) tag;
            object.put(TimeSemanticTag.FROM, tst.getFrom());
            object.put(TimeSemanticTag.DURATION, tst.getDuration());
        }

        // sst
        if (tag instanceof SpatialSemanticTag) {
            SpatialSemanticTag sst = (SpatialSemanticTag) tag;
            object.put(SpatialSemanticTag.GEOMETRY, sst.getGeometry().getWKT());
        }

        object.put(PropertyHolder.PROPERTIES, ASIPMessageSerializer.serializeProperties(tag).toString());

        return object;
    }

    public static JSONObject serializeSTSet(STSet stset) throws SharkKBException, JSONException {

        //TODO Type of STSet?
        if (stset == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        String type = "";
        if (stset instanceof PeerSTSet)
            type = STSet.PEERSTSET;
        else if (stset instanceof TimeSTSet)
            type = STSet.TIMESTSET;
        else if (stset instanceof SpatialSTSet)
            type = STSet.SPATIALSTSET;
        else if (stset instanceof STSet)
            type = STSet.ANYSTSET;

        jsonObject.put(STSet.TYPE, type);

        JSONArray set = new JSONArray();
        Enumeration<SemanticTag> tags = stset.tags();
        while (tags.hasMoreElements()) {
            set.put(serializeTag(tags.nextElement()));
        }
        jsonObject.put(STSet.STSET, set);

        if (stset instanceof SemanticNet || stset instanceof Taxonomy) {
            if (stset.tags().hasMoreElements())
                jsonObject.put(STSet.RELATIONS, serializeRelations(stset.tags()));
        }
        return jsonObject;
    }

    public static JSONObject serializeRelations(Enumeration<SemanticTag> tagEnum) {


        if (tagEnum == null || !tagEnum.hasMoreElements()) {
            return null;
        }

        SemanticTag tag = tagEnum.nextElement();
        boolean semanticNet;

        if (tag instanceof SNSemanticTag) {
            semanticNet = true;
        } else if (tag instanceof TXSemanticTag) {
            semanticNet = false;
        } else {
            return null;
        }


        JSONArray predicates = new JSONArray();
        JSONArray subSuperTags = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        if (semanticNet) {
            // Semantic Net
            do {
                SNSemanticTag snTag = (SNSemanticTag) tag;
                // get tag for next round
                tag = null;
                if (tagEnum.hasMoreElements())
                    tag = tagEnum.nextElement();

                String[] sSIs = snTag.getSI();
                if (sSIs != null) {
                    String sourceSI = sSIs[0];

                    Enumeration<String> pNameEnum = snTag.predicateNames();
                    if (pNameEnum != null) {
                        while (pNameEnum.hasMoreElements()) {
                            String predicateName = pNameEnum.nextElement();
                            Enumeration<SNSemanticTag> targetEnum =
                                    snTag.targetTags(predicateName);
                            if (targetEnum == null) {
                                continue;
                            }
                            while (targetEnum.hasMoreElements()) {
                                SNSemanticTag target = targetEnum.nextElement();
                                String[] tSIs = target.getSI();
                                if (tSIs == null) {
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
            } while (tag != null);

            jsonObject.put(SemanticNet.PREDICATES, predicates);

        } else {
            // Taxonomy
            do {
                TXSemanticTag txTag = (TXSemanticTag) tag;
                // get tag for next round
                tag = null;
                if (tagEnum.hasMoreElements()) {
                    tag = tagEnum.nextElement();
                }

                String[] sSIs = txTag.getSI();
                if (sSIs != null) {
                    String sourceSI = sSIs[0];

                    TXSemanticTag superTag = txTag.getSuperTag();
                    if (superTag != null) {
                        String[] tSIs = superTag.getSI();
                        if (tSIs == null) {
                            continue;
                        }

                        String targetSI = tSIs[0];

                        JSONObject subSuperTag = new JSONObject();

                        subSuperTag.put(Taxonomy.SOURCE, sourceSI);
                        subSuperTag.put(Taxonomy.TARGET, targetSI);

                        subSuperTags.put(subSuperTag);
                    }
                }
            } while (tagEnum.hasMoreElements());

            jsonObject.put(Taxonomy.SUBSUPERTAGS, subSuperTags);

        }
        return jsonObject;
    }

    public static JSONObject serializeASIPSpace(ASIPSpace space) throws SharkKBException {
        if (space == null)
            return null;

        JSONObject jsonObject = new JSONObject();

        STSet topics = space.getTopics();
        if (topics != null && !topics.isEmpty()) {
            jsonObject.put(ASIPSpace.TOPICS, serializeSTSet(topics));
        }

        // types
        STSet types = space.getTypes();
        if (types != null && !types.isEmpty()) {
            jsonObject.put(ASIPSpace.TYPES, serializeSTSet(types));
        }

        // sender
        PeerSemanticTag sender = space.getSender();
        if (sender != null) {
            jsonObject.put(ASIPSpace.SENDER, serializeTag(sender));
        }

        // approvers
        PeerSTSet approvers = space.getApprovers();
        if (approvers != null && !approvers.isEmpty()) {
            jsonObject.put(ASIPSpace.APPROVERS, serializeSTSet(approvers));
        }

        // receivers
        PeerSTSet receivers = space.getReceivers();
        if (receivers != null && !receivers.isEmpty()) {
            jsonObject.put(ASIPSpace.RECEIVERS, serializeSTSet(receivers));
        }

        // locations
        SpatialSTSet locations = space.getLocations();
        if (locations != null && !locations.isEmpty()) {
            jsonObject.put(ASIPSpace.LOCATIONS, serializeSTSet(locations));
        }

        // times
        TimeSTSet times = space.getTimes();
        if (times != null && !times.isEmpty()) {
            jsonObject.put(ASIPSpace.TIMES, serializeSTSet(times));
        }

        // direction
        jsonObject.put(ASIPSpace.DIRECTION, space.getDirection());

        return jsonObject;
    }


    public static ASIPInterest deserializeASIPInterest(String spaceString) throws SharkKBException {

        if(spaceString == null || spaceString.isEmpty()) return null;

        String topicsString = null;
        String typesString = null;
        String senderString = null;
        String approversString = null;
        String receiverString = null;
        String locationsString = null;
        String timesString = null;
        int direction = -1;

        ASIPInterest interest = InMemoSharkKB.createInMemoASIPInterest();

        JSONObject parsed = new JSONObject(spaceString);

        try {
            if (parsed.has(ASIPSpace.TOPICS)) topicsString = parsed.get(ASIPSpace.TOPICS).toString();
            if (parsed.has(ASIPSpace.TYPES)) typesString = parsed.get(ASIPSpace.TYPES).toString();
            if (parsed.has(ASIPSpace.SENDER)) senderString = parsed.get(ASIPSpace.SENDER).toString();
            if (parsed.has(ASIPSpace.APPROVERS)) approversString = parsed.get(ASIPSpace.APPROVERS).toString();
            if (parsed.has(ASIPSpace.RECEIVERS)) receiverString = parsed.get(ASIPSpace.RECEIVERS).toString();
            if (parsed.has(ASIPSpace.LOCATIONS)) locationsString = parsed.get(ASIPSpace.LOCATIONS).toString();
            if (parsed.has(ASIPSpace.TIMES)) timesString = parsed.get(ASIPSpace.TIMES).toString();
            if (parsed.has(ASIPSpace.DIRECTION)) direction = parsed.getInt(ASIPSpace.DIRECTION);
        } catch (JSONException e) {
            L.d("" + e);
        }


        STSet topics;
        if (topicsString != null) {
            topics = ASIPMessageSerializerHelper.deserializeSTSet(null, topicsString);
            interest.setTopics(topics);
        }
        STSet types;
        if (typesString != null) {
            types = ASIPMessageSerializerHelper.deserializeSTSet(null, typesString);
            interest.setTypes(types);
        }
        PeerSTSet approvers;
        if (approversString != null) {
            approvers = ASIPMessageSerializerHelper.deserializePeerSTSet(null, approversString);
            interest.setApprovers(approvers);
        }
        PeerSTSet receivers;
        if (receiverString != null) {
            receivers = ASIPMessageSerializerHelper.deserializePeerSTSet(null, receiverString);
            interest.setReceivers(receivers);
        }
        SpatialSTSet locations;
        if (locationsString != null) {
            locations = ASIPMessageSerializerHelper.deserializeSpatialSTSet(null, locationsString);
            interest.setLocations(locations);
        }
        TimeSTSet times;
        if (timesString != null) {
            times = ASIPMessageSerializerHelper.deserializeTimeSTSet(null, timesString);
            interest.setTimes(times);
        }
        if (senderString != null) {
            PeerSemanticTag sender = ASIPMessageSerializerHelper.deserializePeerTag(null, senderString);
            interest.setSender(sender);
        }
        if (direction != -1) interest.setDirection(direction);

        return interest;
    }

    // DESERIALIZATION

    public static SemanticTag deserializeTag(STSet targetSet, String tagString) throws SharkKBException {

        // TODO Types of SemanticTags?
        if (targetSet == null)
            targetSet = InMemoSharkKB.createInMemoSTSet();

        if (tagString == null || tagString.isEmpty())
            return null;

        JSONObject jsonObject = new JSONObject(tagString);

        List<String> list = new ArrayList<>();
        JSONArray semanticTagArray = jsonObject.getJSONArray(SemanticTag.SI);
        for (int i = 0; i < semanticTagArray.length(); i++) {
            list.add(semanticTagArray.get(i).toString());
        }

        String name = jsonObject.getString(SemanticTag.NAME);
        String[] sis = new String[list.size()];
        sis = list.toArray(sis);

        SemanticTag tag = targetSet.createSemanticTag(name, sis);
        deserializeProperties(tag, tagString);
        return tag;
    }

    /**
     * static variant - not to be used in protocol parser
     *
     * @param tag
     * @return
     * @throws SharkKBException
     */
    public static SemanticTag deserializeTag(String tag) throws SharkKBException {

        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        return deserializeTag(stSet, tag);
    }

    public static PeerSemanticTag deserializePeerTag(STSet targetSet, String tagString) throws SharkKBException {

        if (targetSet == null)
            targetSet = InMemoSharkKB.createInMemoPeerSTSet();

        if (tagString == null || tagString.isEmpty()) {
            return null;
        }

        JSONObject jsonObject = new JSONObject(tagString);

        List<String> list = new ArrayList<>();
        JSONArray semanticTagArray = jsonObject.getJSONArray(SemanticTag.SI);
        for (int i = 0; i < semanticTagArray.length(); i++) {
            list.add(semanticTagArray.get(i).toString());
        }

        List<String> addressesList = new ArrayList<>();
        JSONArray addressArray = jsonObject.getJSONArray(PeerSemanticTag.ADDRESSES);
        for (int i = 0; i < addressArray.length(); i++) {
            addressesList.add((String) addressArray.get(i));
        }

        String name = jsonObject.getString(SemanticTag.NAME);
        String[] sis = new String[list.size()];
        sis = list.toArray(sis);
        String[] addresses = new String[addressesList.size()];
        addresses = addressesList.toArray(addresses);

        PeerSTSet inMemoPeerSTSet = InMemoSharkKB.createInMemoPeerSTSet();

        PeerSemanticTag tag = inMemoPeerSTSet.createPeerSemanticTag(name, sis, addresses);

        deserializeProperties(tag, tagString);

        targetSet.merge(inMemoPeerSTSet);

        return tag;
    }

    /**
     * static variant - not to be used in protocol parser
     *
     * @param tag
     * @return
     * @throws SharkKBException
     */
    public static PeerSemanticTag deserializePeerTag(String tag) throws SharkKBException {

        PeerSTSet stSet = InMemoSharkKB.createInMemoPeerSTSet();
        return deserializePeerTag(stSet, tag);
    }

    public static SpatialSemanticTag deserializeSpatialTag(STSet targetSet, String tagString) throws SharkKBException {

        if (targetSet == null)
            targetSet = InMemoSharkKB.createInMemoSpatialSTSet();

        if (tagString == null || tagString.isEmpty())
            return null;

        JSONObject jsonObject = new JSONObject(tagString);

        // Name
        String name = jsonObject.getString(SemanticTag.NAME);

        // Sis
        List<String> siList = new ArrayList<>();
        JSONArray siArray = jsonObject.getJSONArray(SemanticTag.SI);
        for (int i = 0; i < siArray.length(); i++) {
            siList.add(siArray.get(i).toString());
        }

        // Geometrie
        String geometrieString = (String) jsonObject.get(SpatialSemanticTag.GEOMETRY);
//        List<String> geometriesList = new ArrayList<>();
//        for (int i = 0; i < geometriesArray.length(); i++) {
//            geometriesList.add(geometriesArray.get(i).toString());
//        }

        String[] sis = new String[siList.size()];
        sis = siList.toArray(sis);
//        String[] geometries = new String[geometriesList.size()];
//        geometries = geometriesList.toArray(geometries);

        SharkGeometry geom = InMemoSharkGeometry.createGeomByWKT(geometrieString);
//        for (int i = 0; i < geometries.length; i++) {
//            geoms[i] = InMemoSharkGeometry.createGeomByEWKT(geometries[i]);
//        }
        // TODO Geometries just adding the first geom
        SpatialSemanticTag tag = ((SpatialSTSet) targetSet).createSpatialSemanticTag(name, sis, geom);
        deserializeProperties(tag, tagString);
        return tag;
    }

    /**
     * static variant - not to be used in protocol parser
     *
     * @param tag
     * @return
     * @throws SharkKBException
     */
    public static SpatialSemanticTag deserializeSpatialTag(String tag) throws SharkKBException {
        // there is no specific set - create one
        SpatialSTSet stSet = InMemoSharkKB.createInMemoSpatialSTSet();
        return deserializeSpatialTag(stSet, tag);
    }

    public static TimeSemanticTag deserializeTimeTag(STSet targetSet, String tagString) throws SharkKBException {

        if (targetSet == null)
            targetSet = InMemoSharkKB.createInMemoTimeSTSet();

        if (tagString == null || tagString.isEmpty())
            return null;

        JSONObject jsonObject = new JSONObject(tagString);
        Long from = jsonObject.getLong(TimeSemanticTag.FROM);
        Long duration = jsonObject.getLong(TimeSemanticTag.DURATION);

        TimeSemanticTag tag = ((TimeSTSet) targetSet).createTimeSemanticTag(from, duration);
        return tag;
    }

    /**
     * static variant - not to be used in protocol parser
     *
     * @param tag
     * @return
     * @throws SharkKBException
     */
    public static TimeSemanticTag deserializeTimeTag(String tag) throws SharkKBException {
        // there is no specific set - create one
        TimeSTSet stSet = InMemoSharkKB.createInMemoTimeSTSet();
        return deserializeTimeTag(stSet, tag);
    }

    public static STSet deserializeSTSet(STSet target, String stSetString) throws SharkKBException {
        if(stSetString == null || stSetString.isEmpty()) {
            return null;
        }

        if (target == null) {
            target = InMemoSharkKB.createInMemoSTSet();
        }

        JSONObject jsonObject = new JSONObject(stSetString);

        JSONArray semanticTagsArray = jsonObject.optJSONArray(STSet.STSET);
        for (int i = 0; i < semanticTagsArray.length(); i++) {
            JSONObject tag = semanticTagsArray.getJSONObject(i);
            if (tag.has(PeerSemanticTag.ADDRESSES)) {
                deserializePeerTag(target, tag.toString());
            } else if (tag.has(SpatialSemanticTag.GEOMETRY)) {
                deserializeSpatialTag(target, tag.toString());
            } else if (tag.has(TimeSemanticTag.DURATION) || jsonObject.has(TimeSemanticTag.FROM)) {
                deserializeTimeTag(target, tag.toString());
            } else {
                deserializeTag(target, tag.toString());
            }
        }

        if (target instanceof SemanticNet) {
            deserializeRelations((SemanticNet) target, stSetString);
            return target;
        } else if (target instanceof PeerTaxonomy) {
            deserializeRelations((Taxonomy) target, stSetString);
        }

        return target;
    }

    public static PeerSTSet deserializePeerSTSet(STSet set, String stSetString) throws SharkKBException {
        if (set == null) set = InMemoSharkKB.createInMemoPeerSTSet();
        PeerSTSet peerSTSet = (PeerSTSet) set;
        return (PeerSTSet) deserializeSTSet(peerSTSet, stSetString);
    }

    public static PeerTaxonomy deserializePeerTaxonomy(STSet set, String stSetString) {
        if (set == null) set = InMemoSharkKB.createInMemoPeerTaxonomy();
        PeerTaxonomy peerSTSet = (PeerTaxonomy) set;
        try {
            deserializeSTSet(peerSTSet, stSetString);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        return peerSTSet;
    }

    public static TimeSTSet deserializeTimeSTSet(STSet set, String stSetString) throws SharkKBException {
        if (set == null) set = InMemoSharkKB.createInMemoTimeSTSet();
        TimeSTSet timeSTSet = (TimeSTSet) set;
        return (TimeSTSet) deserializeSTSet(timeSTSet, stSetString);
    }

    public static SpatialSTSet deserializeSpatialSTSet(STSet set, String stSetString) throws SharkKBException {
        if (set == null) set = InMemoSharkKB.createInMemoSpatialSTSet();
        SpatialSTSet spatialSTSet = (SpatialSTSet) set;
        return (SpatialSTSet) deserializeSTSet(spatialSTSet, stSetString);
    }

    public static void deserializeProperties(SystemPropertyHolder target, String properties) throws SharkKBException {

        if (target == null) {
            return;
        }
        if (properties.isEmpty() || properties.equals("[]")) {
            return;
        }

//        if(properties.startsWith("{")){
//            properties = "[" +properties + "]";
//        }

//        L.d(properties);

//        [{"NAME":"Shark_System_Last_Modified","VALUE":"1497022278967"}]

        JSONObject jsonObject = new JSONObject(properties);

//        JSONArray jsonArray = new JSONArray(properties);

//        Iterator<Object> iterator = jsonArray.iterator();
//        while (iterator.hasNext()){
//            JSONObject jsonObject = (JSONObject) iterator.next();
        if (jsonObject.has(PropertyHolder.PROPERTIES) && !jsonObject.isNull(PropertyHolder.PROPERTIES)) {

            if (!(jsonObject.get(PropertyHolder.PROPERTIES) instanceof JSONArray)) return;

            JSONArray propertiesArray = jsonObject.getJSONArray(PropertyHolder.PROPERTIES);
            for (int i = 0; i < propertiesArray.length(); i++) {

                JSONObject property = propertiesArray.getJSONObject(i);
                String name = property.getString(PropertyHolder.NAME);
                String value = property.getString(PropertyHolder.VALUE);

                target.setProperty(name, value);
            }
        }
//        }
    }

    public static void deserializeRelations(Taxonomy target, String relations) {
        if (target == null) return;
        if (relations == null) return;
        JSONObject jsonObject = new JSONObject(relations);

        if (!jsonObject.has(Taxonomy.SUBSUPERTAGS)) return;
        JSONArray propertiesArray = jsonObject.getJSONArray(Taxonomy.SUBSUPERTAGS);
        for (int i = 0; i < propertiesArray.length(); i++) {
            JSONObject relation = propertiesArray.getJSONObject(i);
            String sourceSI = relation.getString(Taxonomy.SOURCE);
            String targetSI = relation.getString(Taxonomy.TARGET);
            try {
                TXSemanticTag sourceTag = (TXSemanticTag) target.getSemanticTag(sourceSI);
                if (sourceTag == null) continue;

                TXSemanticTag targetTag = (TXSemanticTag) target.getSemanticTag(targetSI);
                if (targetTag == null) continue;

                // set super tag
                sourceTag.move(targetTag);
            } catch (SharkKBException skbe) {
                // ignore and go ahead
            }
        }

    }

    public static void deserializeRelations(SemanticNet target, String relations) {
        if (target == null) return;
        if (relations == null) return;
        JSONObject jsonObject = new JSONObject(relations);

        if (!jsonObject.has(SemanticNet.PREDICATES)) return;
        JSONArray propertiesArray = jsonObject.getJSONArray(SemanticNet.PREDICATES);
        for (int i = 0; i < propertiesArray.length(); i++) {
            JSONObject relation = propertiesArray.getJSONObject(i);
            String sourceSI = relation.getString(SemanticNet.SOURCE);
            String targetSI = relation.getString(SemanticNet.TARGET);
            String predicateName = relation.getString(SemanticNet.NAME);
            try {
                SNSemanticTag sourceTag = (SNSemanticTag) target.getSemanticTag(sourceSI);
                if (sourceTag == null) continue;

                SNSemanticTag targetTag = (SNSemanticTag) target.getSemanticTag(targetSI);
                if (targetTag == null) continue;

                // set super tag
                sourceTag.setPredicate(predicateName, targetTag);
            } catch (SharkKBException skbe) {
                // ignore and go ahead
            }
        }


    }

}
