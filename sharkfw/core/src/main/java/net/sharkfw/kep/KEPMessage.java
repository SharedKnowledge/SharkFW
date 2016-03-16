package net.sharkfw.kep;

import net.sharkfw.kep.format.XMLSerializer;
import net.sharkfw.system.SharkNotSupportedException;

/**
 *
 * A KEP message has the following format:
 * Sending Peername (optinal, implied: anonymous)
 * Reply Adress (mandatory String)
 * Recipient Peernames (optional, implied: anonymous == all)
 * sec information (signiture, certificate) (optional, implied: empty)
 * KEP command (mandatory)
 * KEP parameters (depends on command)
 * 
 * KEP message parts are separated by a delimiter in the serialized format.
 * Delimiter is defined in this interface. 
 * 
 * [sender peername]&[reply addr]&[recipient peer]&[sec infos]&kep_cmd&[kep_parameter]
 * 
 * 
 * @author thsc
 */
public abstract class KEPMessage {
    //public static final int UNKNOWN = -1;

    /**
     * EXPOSE command for KEP
     */
    public static final int KEP_EXPOSE = 1;

    /**
     * INSERT command for KEP
     */
    public static final int KEP_INSERT = 2;

    /**
     * Returns whether or not an integer number is a valid KEPCommand
     * @param cmd The integer value to check
     * @return <code>true</code> if it is a valid KEP command, <code>false</code> otherwise.
     */
    public static boolean validKEPCommand(int cmd) {
        return (cmd >= 3 || cmd <= 5);
    }
    /**
     * The maximum message length to be read
     */
    public static final int MAXMSGLEN = 5000;
    //public static final int VERSIONLEN = 20;
    /**
     * The current KEP version.
     */
    public static final String THISVERSION = "KEP 1.0 ";
    /**
     * RDF based format
     */
    public static final int RDF = 1;
    /**
     * Generic XML-based format
     */
    public static final int XML = 2;
    /**
     * The maximum number of supported protocols
     */
    public static final int MAXNUMBER = 2;
    /**
     * Encoding charset
     */
    public static final String ENCODING = "UTF-8";
    private static KnowledgeSerializer xmlSerializer = null;

    /**
     * Returns an instance of <code>KnowledgeSerializer</code>. If the instance has been called for before it
     * will return the same instance.
     *
     * @param format constant denoting the desired format
     * @return The appropriate instance of the knowledge serializer
     * @throws SharkNotSupportedException
     */
    public static KnowledgeSerializer getKnowledgeSerializer(int format) throws SharkNotSupportedException {

        if (format == KEPMessage.XML) {
            if (KEPMessage.xmlSerializer == null) {
                KEPMessage.xmlSerializer = new XMLSerializer();
            }
            return xmlSerializer;
        } else {
            throw new SharkNotSupportedException("unsupported KEP format: " + format);
        }
    }

    /**
     * Returns an instance of the <code>CompactFormatSerializer</code>
     * @return an instance of the <code>CompactFormatSerializer</code>
     */
    public static KnowledgeSerializer getKnowledgeSerializer() {
        if (xmlSerializer == null) {
            xmlSerializer = new XMLSerializer();
        }
        return xmlSerializer;
    }
}
