package net.sharkfw.asip.serialization;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.L;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author j4rvis
 */
@SuppressWarnings("Duplicates")
public class ASIPMessageSerializer {

    private static final String CLASS = "ASIPSERIALIZER: ";

    public static final String CONTENT = "CONTENT";
    public static final String SIGNED = "SIGNED";
    public static final String INTEREST = "INTEREST";
    public static final String KNOWLEDGE = "KNOWLEDGE";
    public static final String RAW = "RAW";

    public static String serializeExpose(ASIPMessage header, ASIPSpace interest)
            throws SharkKBException, JSONException {

        JSONObject object = ASIPMessageSerializerHelper.serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(ASIPMessage.LOGICALSENDER, ASIPMessageSerializerHelper.serializeTag(header.getLogicalSender())); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not
        content.put(INTEREST, ASIPMessageSerializerHelper.serializeInterest(interest));
        object.put(CONTENT, content);

        ASIPSerializationHolder serializationHolder = new ASIPSerializationHolder(header, object.toString(), null);

        return serializationHolder.asString();
    }

    public static String serializeInsert(ASIPMessage header, ASIPKnowledge knowledge)
            throws JSONException, SharkKBException {

        JSONObject object = ASIPMessageSerializerHelper.serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(ASIPMessage.LOGICALSENDER, ASIPMessageSerializerHelper.serializeTag(header.getLogicalSender())); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not

        ASIPKnowledgeConverter knowledgeConverter;
        if (knowledge instanceof SharkKB){
            knowledgeConverter = ASIPMessageSerializerHelper.serializeKB((SharkKB) knowledge);
        } else {
            knowledgeConverter = ASIPMessageSerializerHelper.serializeKnowledge(knowledge);
        }

        content.put(KNOWLEDGE, knowledgeConverter.getSerializedKnowledgeAsJSON());
        object.put(CONTENT, content);

        // TODO Use holder to set Content!
        ASIPSerializationHolder serializationHolder = new ASIPSerializationHolder(header, object.toString(), knowledgeConverter.getContent());

        return serializationHolder.asString();
    }

    public static String serializeRaw(ASIPMessage header, byte[] raw) throws SharkKBException {

        JSONObject object = ASIPMessageSerializerHelper.serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(ASIPMessage.LOGICALSENDER, ASIPMessageSerializerHelper.serializeTag(header.getLogicalSender())); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not
        content.put(RAW, raw.length);
        object.put(CONTENT, content);

        ASIPSerializationHolder serializationHolder = new ASIPSerializationHolder(header, object.toString(), new String(raw, StandardCharsets.UTF_8));
        return serializationHolder.asString();
    }

    public static String serializeRaw(ASIPMessage header, InputStream raw) throws SharkKBException {

        JSONObject object = ASIPMessageSerializerHelper.serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(ASIPMessage.LOGICALSENDER, ASIPMessageSerializerHelper.serializeTag(header.getLogicalSender())); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not
        String text = null;
        try {
            try (Scanner scanner = new Scanner(raw, StandardCharsets.UTF_8.name())) {
                text = scanner.useDelimiter("\\A").next();
            }
            content.put(RAW, text.length());
        } finally {
            try {
                raw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        object.put(CONTENT, content);

        ASIPSerializationHolder serializationHolder = new ASIPSerializationHolder(header, object.toString(), text);
        return serializationHolder.asString();
    }

    public static boolean deserializeInMessage(ASIPInMessage message, String parsedStream) {
        if (parsedStream.isEmpty()) {
//            L.d(CLASS + "Stream is empty.");
            return false;
        }

        //
        ASIPSerializationHolder serializationHolder = null;
        try {
            serializationHolder = new ASIPSerializationHolder(parsedStream);
        } catch (ASIPSerializerException e) {
            L.d(e.getMessage());
            return false;
        }

        JSONObject object = null;

        try {
            object = new JSONObject(serializationHolder.getSerializedJSONMessage());
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

        switch (command) {
            case ASIPMessage.ASIP_EXPOSE:
                try {
                    ASIPInterest interest = ASIPMessageSerializerHelper.deserializeASIPInterest(content.get(ASIPMessageSerializer.INTEREST).toString());
                    message.setInterest(interest);
                } catch (SharkKBException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            case ASIPMessage.ASIP_INSERT:
                try {
                    ASIPKnowledgeConverter knowledgeConverter =
                            new ASIPKnowledgeConverter(
                                    content.get(ASIPMessageSerializer.KNOWLEDGE).toString(),
                                    serializationHolder.getContent());
                    message.setKnowledge(knowledgeConverter.getKnowledge());
                } catch (SharkKBException | ASIPSerializerException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            case ASIPMessage.ASIP_RAW:
                byte[] raw = serializationHolder.getContent().getBytes(StandardCharsets.UTF_8);
                message.setRaw(new ByteArrayInputStream(raw));
                break;
        }
        return true;
    }
}
