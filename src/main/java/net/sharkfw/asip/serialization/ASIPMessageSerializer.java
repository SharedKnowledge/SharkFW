package net.sharkfw.asip.serialization;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.L;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * @author j4rvis
 */
public abstract class ASIPMessageSerializer {

    private static final String CLASS = "ASIPSERIALIZER: ";

    public static final String CONTENT = "CONTENT";
    public static final String SIGNED = "SIGNED";
    public static final String INTEREST = "INTEREST";
    public static final String KNOWLEDGE = "KNOWLEDGE";
    public static final String RAW = "RAW";

    public static ASIPSerializationHolder serializeExpose(ASIPMessage header, ASIPSpace interest)
            throws SharkKBException, JSONException {

        JSONObject object = ASIPMessageSerializerHelper.serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(ASIPMessage.LOGICALSENDER, ASIPMessageSerializerHelper.serializeTag(header.getLogicalSender())); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not
        content.put(INTEREST, ASIPMessageSerializerHelper.serializeInterest(interest));
        object.put(CONTENT, content);

        ASIPSerializationHolder serializationHolder = new ASIPSerializationHolder(header, object.toString(), null);

        return serializationHolder;
    }

    public static ASIPSerializationHolder serializeInsert(ASIPMessage header, ASIPKnowledge knowledge)
            throws JSONException, SharkKBException {

        JSONObject object = ASIPMessageSerializerHelper.serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(ASIPMessage.LOGICALSENDER, ASIPMessageSerializerHelper.serializeTag(header.getLogicalSender())); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not

        ASIPKnowledgeConverter knowledgeConverter;
        if (knowledge instanceof SharkKB){
            knowledgeConverter = serializeKB((SharkKB) knowledge);
        } else {
            knowledgeConverter = serializeKnowledge(knowledge);
        }

        content.put(KNOWLEDGE, knowledgeConverter.getSerializedKnowledgeAsJSON());
        object.put(CONTENT, content);

        ASIPSerializationHolder serializationHolder = new ASIPSerializationHolder(header, object.toString(), knowledgeConverter.getContent());

        return serializationHolder;
    }

    public static ASIPSerializationHolder serializeRaw(ASIPMessage header, byte[] raw) throws SharkKBException {

        JSONObject object = ASIPMessageSerializerHelper.serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(ASIPMessage.LOGICALSENDER, ASIPMessageSerializerHelper.serializeTag(header.getLogicalSender())); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not
        content.put(RAW, raw.length);
        object.put(CONTENT, content);

        ASIPSerializationHolder serializationHolder = new ASIPSerializationHolder(header, object.toString(), raw);
        return serializationHolder;
    }

    public static ASIPSerializationHolder serializeRaw(ASIPMessage header, InputStream raw) throws SharkKBException {

        JSONObject object = ASIPMessageSerializerHelper.serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(ASIPMessage.LOGICALSENDER, ASIPMessageSerializerHelper.serializeTag(header.getLogicalSender())); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not
//        String text = null;
        byte[] byteArray = null;
        try {
//            raw.
//            try (Scanner scanner = new Scanner(raw, StandardCharsets.UTF_8.name())) {
//                text = scanner.useDelimiter("\\A").next();
//            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = raw.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            byteArray = buffer.toByteArray();

            content.put(RAW, byteArray.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                raw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        object.put(CONTENT, content);

        ASIPSerializationHolder serializationHolder = new ASIPSerializationHolder(header, object.toString(), byteArray);
        return serializationHolder;
    }

    public static boolean deserializeInMessage(ASIPInMessage message, ASIPSerializationHolder serializationHolder) {
        if (serializationHolder.getMessage().isEmpty()) {
            L.d(CLASS + "Stream is empty.");
            return false;
        }

        JSONObject object = null;

        try {
            object = new JSONObject(serializationHolder.getMessage());
        } catch (Exception e) {
            L.d(CLASS + e);
            return false;
        }

        // uncomment to see json output of serialization
//        L.d(object.toString(4));

//        String version = "";
//        String format = "";
        boolean encrypted = false;
        String encryptedSessionKey = "";
        boolean signed = false;
        long ttl = -1;
        int command = -1;
        PeerSemanticTag physicalSender = null;
        PeerSemanticTag receiverPeer = null;
        SpatialSemanticTag receiverLocation = null;
        TimeSemanticTag receiverTime = null;
        SemanticTag topic = null;
        SemanticTag type = null;

        String senderString = "";
        String logicalSenderString = "";
        String receiverPeerString = "";
        String receiverLocationString = "";
        String receiverTimeString = "";
        String topicString = "";
        String typeString = "";


//        if (object.has(ASIPMessage.VERSION))
//            version = object.getString(ASIPMessage.VERSION);
//        if (object.has(ASIPMessage.FORMAT))
//            format = object.getString(ASIPMessage.FORMAT);
        if (object.has(ASIPMessage.ENCRYPTED))
            encrypted = object.getBoolean(ASIPMessage.ENCRYPTED);
        if (object.has(ASIPMessage.ENCRYPTEDSESSIONKEY))
            encryptedSessionKey = object.getString(ASIPMessage.ENCRYPTEDSESSIONKEY);
        if (object.has(ASIPMessage.SIGNED))
            signed = object.getBoolean(ASIPMessage.SIGNED);
        if (object.has(ASIPMessage.TTL))
            ttl = object.getLong(ASIPMessage.TTL);
        if (object.has(ASIPMessage.COMMAND))
            command = object.getInt(ASIPMessage.COMMAND);

        if (object.has(ASIPMessage.PHYSICALSENDER)) {
            senderString = object.get(ASIPMessage.PHYSICALSENDER).toString();
            try {
                physicalSender = ASIPMessageSerializerHelper.deserializePeerTag(senderString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        if (object.has(ASIPMessage.RECEIVERPEER)) {
            receiverPeerString = object.get(ASIPMessage.RECEIVERPEER).toString();
            try {
                receiverPeer = ASIPMessageSerializerHelper.deserializePeerTag(receiverPeerString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        if (object.has(ASIPMessage.RECEIVERLOCATION)) {
            receiverLocationString = object.get(ASIPMessage.RECEIVERLOCATION).toString();
            try {
                receiverLocation = ASIPMessageSerializerHelper.deserializeSpatialTag(receiverLocationString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        if (object.has(ASIPMessage.RECEIVERTIME)) {
            receiverTimeString = object.get(ASIPMessage.RECEIVERTIME).toString();
            try {
                receiverTime = ASIPMessageSerializerHelper.deserializeTimeTag(receiverTimeString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        if (object.has(ASIPMessage.TOPIC)) {
            topicString = object.get(ASIPMessage.TOPIC).toString();
            try {
                topic = ASIPMessageSerializerHelper.deserializeTag(topicString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }

        if (object.has(ASIPMessage.TYPE)) {
            typeString = object.get(ASIPMessage.TYPE).toString();
            try {
                type = ASIPMessageSerializerHelper.deserializeTag(typeString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }

        message.setEncrypted(encrypted);
        message.setEncryptedSessionKey(encryptedSessionKey);
        message.setSigned(signed);
        message.setTtl(ttl);
        message.setCommand(command);
        message.setPhysicalSender(physicalSender);
        message.setReceiverPeer(receiverPeer);
        message.setReceiverSpatial(receiverLocation);
        message.setReceiverTime(receiverTime);
        message.setTopic(topic);
        message.setType(type);

        // Check if content isEmpty
        if (!object.has(ASIPMessageSerializer.CONTENT)) {
            return false;
        }

        JSONObject content = object.getJSONObject(ASIPMessageSerializer.CONTENT);

        if (content.has(ASIPMessage.LOGICALSENDER)) {
            logicalSenderString = content.get(ASIPMessage.LOGICALSENDER).toString();
            try {
                message.setLogicalSender(ASIPMessageSerializerHelper.deserializePeerTag(logicalSenderString));
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }

        if (ASIPPerformer(message, serializationHolder, command, content)) return false;

        return true;
    }

    private static boolean ASIPPerformer(ASIPInMessage message, ASIPSerializationHolder serializationHolder, int command, JSONObject content) {
        switch (command) {
            case ASIPMessage.ASIP_EXPOSE:
                try {
                    ASIPInterest interest = ASIPMessageSerializerHelper.deserializeASIPInterest(content.get(ASIPMessageSerializer.INTEREST).toString());
                    message.setInterest(interest);
                } catch (SharkKBException e) {
                    e.printStackTrace();
                    return true;
                }
                break;
            case ASIPMessage.ASIP_INSERT:
                if(serializationHolder.getContent()==null){
                    L.d("No content available", CLASS);
                    return true;
                }
                try {
                    ASIPKnowledgeConverter knowledgeConverter =
                            new ASIPKnowledgeConverter(
                                    content.get(ASIPMessageSerializer.KNOWLEDGE).toString(),
                                    serializationHolder.getContent());
                    message.setKnowledge(knowledgeConverter.getKnowledge());
                } catch (SharkKBException | ASIPSerializerException e) {
                    e.printStackTrace();
                    return true;
                }
                break;
            case ASIPMessage.ASIP_RAW:
                if(serializationHolder.getContent()==null){
                    L.d("No content available", CLASS);
                    return true;
                }
                byte[] raw = serializationHolder.getContent();
                message.setRaw(new ByteArrayInputStream(raw));
                break;
        }
        return false;
    }




    public static ASIPKnowledgeConverter serializeKnowledge(ASIPKnowledge knowledge) throws SharkKBException {
        return new ASIPKnowledgeConverter(knowledge);
    }

    public static ASIPKnowledgeConverter serializeKB(SharkKB kb) throws SharkKBException {
        ASIPKnowledgeConverter knowledgeConverter = new ASIPKnowledgeConverter(kb);
        JSONObject jsonObject = knowledgeConverter.getSerializedKnowledgeAsJSON();
        jsonObject.put(PropertyHolder.PROPERTIES, serializeProperties(kb));
        knowledgeConverter.setSerializedKnowledgeAsJSON(jsonObject);
        return knowledgeConverter;
    }

    public static JSONArray serializeProperties(SystemPropertyHolder target) throws SharkKBException {

        if (target == null) { return null; }

        Enumeration<String> propNamesEnum = target.propertyNames(false);
        if (propNamesEnum == null || !propNamesEnum.hasMoreElements()) {
            return new JSONArray();
        }
        JSONArray jsonArray = new JSONArray();
        while (propNamesEnum.hasMoreElements()) {
            String name = propNamesEnum.nextElement();
            String value = target.getProperty(name);

            JSONObject property = new JSONObject();
            property.put(PropertyHolder.NAME, name);
            property.put(PropertyHolder.VALUE, value);
            jsonArray.put(property);
        }

//        L.d(jsonArray.toString());

        return jsonArray;
    }
}
//
//
//class ASIPExpose extends ASIPMessageSerializer{
//    boolean getASIPPerformer(ASIPInMessage message, ASIPSerializationHolder serializationHolder, int command, JSONObject content){
//        try {
//            ASIPInterest interest = ASIPMessageSerializerHelper.deserializeASIPInterest(content.get(ASIPMessageSerializer.INTEREST).toString());
//            message.setInterest(interest);
//        } catch (SharkKBException e) {
//            e.printStackTrace();
//            return true;
//        }
//        return false;
//    }
//}
//
//class ASIPInsert extends ASIPMessageSerializer{
//    boolean getASIPPerformer(ASIPInMessage message, ASIPSerializationHolder serializationHolder, int command, JSONObject content){
//        if(serializationHolder.getContent()==null){
//            L.d("No content available", CLASS);
//            return true;
//        }
//        try {
//            ASIPKnowledgeConverter knowledgeConverter =
//                    new ASIPKnowledgeConverter(
//                            content.get(ASIPMessageSerializer.KNOWLEDGE).toString(),
//                            serializationHolder.getContent());
//            message.setKnowledge(knowledgeConverter.getKnowledge());
//        } catch (SharkKBException | ASIPSerializerException e) {
//            e.printStackTrace();
//            return true;
//        }
//        return false;
//    }
//}
//
//class ASIPRaw extends ASIPMessageSerializer{
//    boolean getASIPPerformer(ASIPInMessage message, ASIPSerializationHolder serializationHolder, int command, JSONObject content){
//        if(serializationHolder.getContent()==null){
//            L.d("No content available", CLASS);
//            return true;
//        }
//        byte[] raw = serializationHolder.getContent();
//        message.setRaw(new ByteArrayInputStream(raw));
//        return false;
//    }
//}
