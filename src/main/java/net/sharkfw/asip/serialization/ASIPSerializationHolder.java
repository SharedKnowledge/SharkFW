package net.sharkfw.asip.serialization;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.system.L;

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
    private String protocolConfig = "";
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
            String messageLengthTemp = protocolConfig.substring(fieldLengthFormat + fieldLengthVersion, jsonMessageBeginIndex);
            messageLengthTemp = messageLengthTemp.replaceFirst("^0+(?!$)", "");

            messageLength = Integer.parseInt(messageLengthTemp);

            if(message.length() >= jsonMessageBeginIndex + messageLength){
                serializedJSONMessage = message.substring(jsonMessageBeginIndex, jsonMessageBeginIndex + messageLength);
                if(message.length() > jsonMessageBeginIndex + messageLength){
                    content = message.substring(jsonMessageBeginIndex + messageLength);
                }
            } else {
                L.d("The message is too short.", this);
            }
        }
    }

    public ASIPSerializationHolder(ASIPMessage message, String jsonString, String content){
        this.prepareProtocolConfig(message, jsonString);
        this.serializedJSONMessage = jsonString;
        this.content = content;
    }

    private void prepareProtocolConfig(ASIPMessage message, String serializedMessage){


        if(message.getFormat().length() <= 4){
            protocolConfig += message.getFormat();
        }
        if(message.getVersion().length() <= 7){
            protocolConfig += message.getVersion();
        }
        protocolConfig += String.format("%09d", serializedMessage.length());
    }

    public String getProtocolConfig() {
        return protocolConfig;
    }

    public String getSerializedJSONMessage() {
        return serializedJSONMessage;
    }

    public String getContent() {
        return content;
    }

    public String asString(){
        String temp = "";
        temp += protocolConfig;
        temp += serializedJSONMessage;
        if(content!=null && !content.isEmpty()){
            temp += content;
        }
        return temp;
    }
}
