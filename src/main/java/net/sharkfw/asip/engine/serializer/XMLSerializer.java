package net.sharkfw.asip.engine.serializer;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoGenericTagStorage;
import net.sharkfw.knowledgeBase.inmemory.InMemoSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticNet;
import net.sharkfw.system.TimeLong;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;


/**
 * @author thsc
 */
public class XMLSerializer extends XMLDeserializer {

    private static final String SHARKCS_TAG = "cs";

    private static final String TOPICS_TAG = "topics";
    private static final String ORIGINATOR_TAG = "originator";
    private static final String PEERS_TAG = "peer";
    private static final String REMOTE_PEERS_TAG = "remotePeer";
    private static final String LOCATIONS_TAG = "location";
    private static final String TIMES_TAG = "times";
    private static final String DIRECTION_TAG = "direction";

    public static final String TAGS_ENUM_TAG = "tags";
    public static final String STSET_TAG = "stset";

    public static final String PREDICATES_TAG = "predicates";
    public static final String SUB_SUPER_TAG = "subs";
    public static final String PREDICATE_TAG = "pred";
    public static final String SUPER_TAG = "super";
    public static final String SOURCE_TAG = "source";
    public static final String TARGET_TAG = "target";

    public static final String TIME_FROM = "from";
    public static final String TIME_DURATION = "duration";

    private final String INDEX_TAG = "index";


    /**
     * Serializes an st set. Checks type of st set and adds relations of
     * taxonomy or semantic net is present.
     *
     * @param stset
     * @return
     * @throws SharkKBException
     */
    public String serializeSTSet(STSet stset) throws SharkKBException {
        if (stset == null) {
            return null;
        }

        // enum tags
        Enumeration<SemanticTag> tags = stset.tags();
        if (tags == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();

        buf.append(this.startTag(STSET_TAG));
        buf.append(this.startTag(TAGS_ENUM_TAG));

        // add tags
        while (tags.hasMoreElements()) {
            buf.append(this.serializeTag(tags.nextElement()));
        }

        buf.append(this.endTag(TAGS_ENUM_TAG));

        // add relations if any
        Enumeration<SemanticTag> tagEnum = stset.tags();
        if (stset instanceof SemanticNet || stset instanceof Taxonomy) {
            String serializedRelations = this.serializeRelations(tagEnum);
            if (serializedRelations != null) {
                buf.append(serializedRelations);
            }
        }

        buf.append(this.endTag(STSET_TAG));

        return buf.toString();
    }

    private String serializeRelations(Enumeration<SemanticTag> tagEnum) {

        if (tagEnum == null) {
            return null;
        }
        if (!tagEnum.hasMoreElements()) {
            return null;
        }

        SemanticTag tag = tagEnum.nextElement();

        boolean semanticNet;
        if (tag instanceof SNSemanticTag) {
            semanticNet = true;
        } else {
            if (tag instanceof TXSemanticTag) {
                semanticNet = false;
            } else {
                // no semantic net no taxonomy...
                return null;
            }
        }

        StringBuilder buf = new StringBuilder();

        boolean openTagWritten = false;

        if (semanticNet) {
            // buf.append(this.startTag(PREDICATES_TAG));
        } else {
            // buf.append(this.startTag(SUB_SUPER_TAG));
        }


        openTagWritten = semanticNetConstructor(tagEnum, tag, semanticNet, buf, openTagWritten);

        if (openTagWritten) {
            if (semanticNet) {
                buf.append(this.endTag(PREDICATES_TAG));
            } else {
                buf.append(this.endTag(SUB_SUPER_TAG));
            }
        }

        if (buf.length() > 0) {
            return buf.toString();
        } else {
            return null;
        }
    }

    private boolean semanticNetConstructor(Enumeration<SemanticTag> tagEnum, SemanticTag tag, boolean semanticNet, StringBuilder buf, boolean openTagWritten) {
        if (semanticNet) {

            // Semantic Net
            do {
                SNSemanticTag snTag = (SNSemanticTag) tag;

                // get tag for next round
                tag = null;
                if (tagEnum.hasMoreElements()) {
                    tag = tagEnum.nextElement();
                }

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
                                // going to write a predicate - open the whole predicate section if necessary
                                if (!openTagWritten) {
                                    openTagWritten = true;
                                    buf.append(this.startTag(PREDICATES_TAG));
                                }

                                SNSemanticTag target = targetEnum.nextElement();
                                String[] tSIs = target.getSI();
                                if (tSIs == null) {
                                    continue;
                                }

                                String targetSI = tSIs[0];

                                // write predicate
                                buf.append(this.startTag(PREDICATE_TAG));

                                // name
                                buf.append(this.startTag(NAME_TAG));
                                buf.append(predicateName);
                                buf.append(this.endTag(NAME_TAG));

                                // source
                                buf.append(this.startTag(SOURCE_TAG));
                                buf.append(this.startTag(SI_TAG));
                                buf.append(sourceSI);
                                buf.append(this.endTag(SI_TAG));
                                buf.append(this.endTag(SOURCE_TAG));

                                // target
                                buf.append(this.startTag(TARGET_TAG));
                                buf.append(this.startTag(SI_TAG));
                                buf.append(targetSI);
                                buf.append(this.endTag(SI_TAG));
                                buf.append(this.endTag(TARGET_TAG));

                                // end
                                buf.append(this.endTag(PREDICATE_TAG));
                            }
                        }
                    }
                }
            } while (tag != null);

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

                        // open this relations section
                        if (!openTagWritten) {
                            openTagWritten = true;
                            buf.append(this.startTag(SUB_SUPER_TAG));
                        }

                        // write predicate
                        buf.append(this.startTag(SUPER_TAG));

                        // source
                        buf.append(this.startTag(SOURCE_TAG));
                        buf.append(this.startTag(SI_TAG));
                        buf.append(sourceSI);
                        buf.append(this.endTag(SI_TAG));
                        buf.append(this.endTag(SOURCE_TAG));

                        // target
                        buf.append(this.startTag(TARGET_TAG));
                        buf.append(this.startTag(SI_TAG));
                        buf.append(targetSI);
                        buf.append(this.endTag(SI_TAG));
                        buf.append(this.endTag(TARGET_TAG));

                        // end
                        buf.append(this.endTag(SUPER_TAG));
                    }
                }
            } while (tagEnum.hasMoreElements());
        }
        return openTagWritten;
    }

    private String serializeTag(SemanticTag tag) throws SharkKBException {
        if (tag == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();

        buf.append(this.startTag(TAG_TAG));

        String name = tag.getName();
        if (name != null) {
            buf.append(this.startTag(NAME_TAG));
            buf.append(name);
            buf.append(this.endTag(NAME_TAG));
        }

        String[] sis = tag.getSI();
        if (sis != null) {
            for (int i = 0; i < sis.length; i++) {
                buf.append(this.startTag(SI_TAG));
                buf.append(sis[i]);
                buf.append(this.endTag(SI_TAG));
            }
        }

        // pst
        if (tag instanceof PeerSemanticTag) {
            PeerSemanticTag pst = (PeerSemanticTag) tag;

            String addr[] = pst.getAddresses();
            if (addr != null) {
                for (int i = 0; i < addr.length; i++) {
                    buf.append(this.startTag(ADDRESS_TAG));
                    buf.append(addr[i]);
                    buf.append(this.endTag(ADDRESS_TAG));
                }
            }
        }

        // tst
        if (tag instanceof TimeSemanticTag) {
            TimeSemanticTag tst = (TimeSemanticTag) tag;
            buf.append(this.startTag(TIME_FROM));
            buf.append(Long.toString(tst.getFrom()));
            buf.append(this.endTag(TIME_FROM));

            buf.append(this.startTag(TIME_DURATION));
            buf.append(Long.toString(tst.getDuration()));
            buf.append(this.endTag(TIME_DURATION));
        }

        // properties
        String serializedProperties = this.serializeProperties(tag);
        if (serializedProperties != null) {
            buf.append(serializedProperties);
        }

        buf.append(this.endTag(TAG_TAG));

        return buf.toString();

    }


    private String serializeProperties(SystemPropertyHolder target) throws SharkKBException {
        if (target == null) {
            return null;
        }

        Enumeration<String> propNamesEnum = target.propertyNames(false);
        if (propNamesEnum == null || !propNamesEnum.hasMoreElements()) {
            return this.emptyTag(PROPERTIES_TAG);
        }

        StringBuilder buf = new StringBuilder();

        buf.append(this.startTag(PROPERTIES_TAG));

        while (propNamesEnum.hasMoreElements()) {
            String name = propNamesEnum.nextElement();
            String value = target.getProperty(name);

            buf.append(this.startTag(PROPERTY_TAG));

            buf.append(this.startTag(NAME_TAG));
            buf.append(name);
            buf.append(this.endTag(NAME_TAG));

            buf.append(this.startTag(VALUE_TAG));

            // for safety reasons: put any value tag inside a CDATA section
            buf.append(XMLSerializer.CDATA_START_TAG);
            buf.append(value);
            buf.append(XMLSerializer.CDATA_END_TAG);

            buf.append(this.endTag(VALUE_TAG));

            buf.append(this.endTag(PROPERTY_TAG));
        }

        buf.append(this.endTag(PROPERTIES_TAG));

        return buf.toString();
    }

    public static final String CDATA_START_TAG = "<!CDATA[";
    public static final String CDATA_END_TAG = "]]>";

    private final int cdata_end_tag_length = XMLSerializer.CDATA_END_TAG.length();


    private SemanticTag getFirstTag(STSet stSet) throws SharkKBException {
        if (stSet == null) return null;
        Enumeration<SemanticTag> tagEnum = stSet.tags();
        if (tagEnum == null) return null;

        return tagEnum.nextElement();

    }

}