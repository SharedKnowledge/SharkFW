package net.sharkfw.asip.serialization;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;

import java.io.InputStream;

/**
 * Created by j4rvis on 12/7/16.
 */
public class ASIPSerializationHolder {

    private final int fieldLengthFormat = 4;
    private int fieldLengthVersion = 7;
    private int fieldLengthMessage = 9;

    /**
     * Length of the Information standing before the JSON object.
     * The first four bytes represent the format of the protocol. For ASIP it will be 'JSON'
     * The next seven bytes represent the versionof the protocol. For ASIP it will be 'ASIP1.0'
     * The last nine bytes will represent the length of the actual json message. The value will be prepended with zeros
     */
    private String protocolConfig;
    private String format;
    private String version;
    private int messageLength = 0;

    private String serializedJSONMessage;
    private int jsonMessageBeginIndex = fieldLengthFormat + fieldLengthVersion + fieldLengthMessage;
    // Only if raw or insert
    private String content;

    public ASIPSerializationHolder(String message) {
        if(!message.isEmpty()){
            protocolConfig = message.substring(0, jsonMessageBeginIndex);
            format = protocolConfig.substring(0, fieldLengthFormat);
            version = protocolConfig.substring(fieldLengthFormat, fieldLengthFormat + fieldLengthVersion);
            messageLength = Integer.getInteger(protocolConfig.substring(fieldLengthFormat + fieldLengthVersion, jsonMessageBeginIndex));

            serializedJSONMessage = message.substring(jsonMessageBeginIndex, jsonMessageBeginIndex + messageLength);
            content = message.substring(jsonMessageBeginIndex + messageLength);
        }
    }

    private void prepareProtocolConfig(ASIPMessage message){

        if(message.getFormat().length() <= 4){
            protocolConfig += message.getFormat();
        }
        if(message.getVersion().length() <= 7){
            protocolConfig += message.getVersion();
        }
//        protocolInfo += String.format("%09d", serializedMessage.length());
    }

    public ASIPSerializationHolder(ASIPOutMessage message, ASIPInterest interest){
        this.prepareProtocolConfig(message);
    }

    public ASIPSerializationHolder(ASIPOutMessage message, ASIPKnowledge knowledge){
        this.prepareProtocolConfig(message);
    }
    public ASIPSerializationHolder(ASIPOutMessage message, InputStream inputStream){
        this.prepareProtocolConfig(message);
    }
    public ASIPSerializationHolder(ASIPOutMessage message, byte[] bytes){
        this.prepareProtocolConfig(message);
    }
}
