package net.sharkfw.asip.serialization;

import net.sharkfw.asip.engine.ASIPMessage;

import java.nio.charset.StandardCharsets;

/**
 * Created by j4rvis on 12/7/16.
 */
public class ASIPSerializationHolder {

    public final static int FIELD_LENGTH_FORMAT = 4;
    public final static int FIELD_LENGTH_VERSION = 7;
    public final static int FIELD_LENGTH_MESSAGE = 9;

    public final static int CONFIG_LENGTH = FIELD_LENGTH_FORMAT + FIELD_LENGTH_VERSION + FIELD_LENGTH_MESSAGE;

    /**
     * Length of the Information standing before the JSON object.
     * The first four bytes represent the format of the protocol. For ASIP it will be 'JSON'
     * The next seven bytes represent the versionof the protocol. For ASIP it will be 'ASIP1.0'
     * The last nine bytes will represent the length of the actual json message. The value will be prepended with zeros
     */
    private String protocolConfig = "";
    private int messageLength = 0;

    private String message;
    private byte[] content;
    private String format;
    private String version;

    public ASIPSerializationHolder(String message) throws ASIPSerializerException {
        if (!message.isEmpty()) {
            this.protocolConfig = message.substring(0, CONFIG_LENGTH);
            this.format = this.protocolConfig.substring(0, FIELD_LENGTH_FORMAT);
            this.version = this.protocolConfig.substring(FIELD_LENGTH_FORMAT, FIELD_LENGTH_FORMAT + FIELD_LENGTH_VERSION);
            String messageLengthTemp = this.protocolConfig.substring(FIELD_LENGTH_FORMAT + FIELD_LENGTH_VERSION, CONFIG_LENGTH);
            messageLengthTemp = messageLengthTemp.replaceFirst("^0+(?!$)", "");

            try {
                this.messageLength = Integer.parseInt(messageLengthTemp);
            } catch (NumberFormatException e) {
                throw new ASIPSerializerException("String can't be converted to an Integer: " + e.getMessage());
            }
        }
    }

    public ASIPSerializationHolder(ASIPMessage message, String jsonString, byte[] content) {
        this.prepareProtocolConfig(message, jsonString);
        this.message = jsonString;
        this.content = content;
    }

    private void prepareProtocolConfig(ASIPMessage message, String serializedMessage) {
        this.protocolConfig += message.getFormat();
        this.protocolConfig += message.getVersion();
        this.protocolConfig += String.format("%09d", serializedMessage.length());
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public long getContentLength() {
        return this.content!=null ? this.content.length : 0;
    }

    public boolean isASIP() {
        return this.version.equals("ASIP1.0");
    }

    public long getMessageLength() {
        return this.messageLength;
    }

    public byte[] messageAsUtf8Bytes(){
        return (this.protocolConfig + this.message).getBytes(StandardCharsets.UTF_8);
    }

    public long length() {
        return this.protocolConfig.length() + this.getMessageLength() + this.getContentLength();
    }
}
