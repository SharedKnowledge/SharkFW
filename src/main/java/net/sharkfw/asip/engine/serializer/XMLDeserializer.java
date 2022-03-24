package net.sharkfw.asip.engine.serializer;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoGenericTagStorage;
import net.sharkfw.knowledgeBase.inmemory.InMemoSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticNet;
import net.sharkfw.system.TimeLong;

import java.util.ArrayList;
import java.util.Iterator;

public class XMLDeserializer {
    protected final String TAG_TAG = "tag";
    protected final String NAME_TAG = "name";
    protected final String SI_TAG = "si";
    protected final String ADDRESS_TAG = "addr";
    protected final String PROPERTIES_TAG = "props";
    protected final String PROPERTY_TAG = "p";
    protected final String VALUE_TAG = "v";
    private final int cdata_start_tag_length = XMLSerializer.CDATA_START_TAG.length();
    private final int cdata_end_tag_length = XMLSerializer.CDATA_END_TAG.length();


    protected String startTag(String tag) {
        return "<" + tag + ">";
    }

    protected String endTag(String tag) {
        return "</" + tag + ">";
    }

    protected String emptyTag(String tag) {
        return "<" + tag + "/>";
    }

    /**
     * Deserializes semantic tag from string
     *
     * @param s
     */
    private SemanticTag deserializeTag(STSet targetSet, String s) throws SharkKBException {
        int index;
        String name;
        ArrayList<String> sis = new ArrayList<String>();
        ArrayList<String> addresses = new ArrayList<String>();

        name = this.stringBetween(NAME_TAG, s, 0);

        // sis
        boolean found;
        index = 0;
        do {
            found = false;
            String si = this.stringBetween(SI_TAG, s, index);
            if (si != null) {
                sis.add(si);
                index = s.indexOf(this.endTag(SI_TAG), index) + 1;
                found = true;
            }
        } while (found);

        // pst ?
        if (targetSet instanceof PeerSemanticNet
                || targetSet instanceof PeerTaxonomy
                || targetSet instanceof PeerSTSet
        ) {
            do {
                found = false;
                String addr = this.stringBetween(ADDRESS_TAG, s, index);
                if (addr != null) {
                    index = s.indexOf(this.endTag(ADDRESS_TAG), index) + 1;
                    addresses.add(addr);
                    found = true;
                }
            } while (found);
        }

        // create tag if some minimal things are found
        if (name == null && sis.isEmpty()) {
            return null;
        }

        SemanticTag target;
        if (targetSet instanceof PeerSemanticNet) {
            target = ((PeerSemanticNet) targetSet).createSemanticTag(
                    name,
                    this.arrayList2Array(sis),
                    this.arrayList2Array(addresses)
            );
        } else if (targetSet instanceof PeerTaxonomy) {
            target = ((PeerTaxonomy) targetSet).createPeerTXSemanticTag(
                    name,
                    this.arrayList2Array(sis),
                    this.arrayList2Array(addresses)
            );
        } else if (targetSet instanceof PeerSTSet) {
            target = ((PeerSTSet) targetSet).createPeerSemanticTag(
                    name,
                    this.arrayList2Array(sis),
                    this.arrayList2Array(addresses)
            );
        } else {
            target = targetSet.createSemanticTag(name, this.arrayList2Array(sis));
        }

        // properties
        this.deserializeProperties(target, s);

        return target;
    }

    private String[] arrayList2Array(ArrayList<String> source) {
        if (source.isEmpty()) return null;

        String[] ret = new String[source.size()];

        Iterator<String> sIter = source.iterator();
        int i = 0;
        while (sIter.hasNext()) {
            ret[i++] = sIter.next();
        }

        return ret;
    }

    private void deserializeProperties(SystemPropertyHolder target, String s) throws SharkKBException {
        if (s == null || target == null) {
            return;
        }

        if (s.equalsIgnoreCase(this.emptyTag(PROPERTIES_TAG))) {
            return;
        }

        int index = 0;

        String propsString = this.stringBetween(PROPERTIES_TAG, s, 0);
        if (propsString == null) {
            return;
        }

        boolean found;

        do {
            found = false;
            String propString = this.stringBetween(PROPERTY_TAG, propsString, index);
            if (propString != null) {

                String name = this.stringBetween(NAME_TAG, propString, 0);
                String value = this.stringBetween(VALUE_TAG, propString, 0);

                // cut off cdata section
                value = value.substring(this.cdata_start_tag_length, value.length() - this.cdata_end_tag_length);

                if (name != null) {
                    target.setProperty(name, value);
                }

                // next property
                index = propsString.indexOf(this.endTag(PROPERTY_TAG), index) + 1;
                found = true;
            }
        } while (found);
    }

    public boolean deserializeSTSet(STSet target, String serializedSTSet) throws SharkKBException {
        if (target == null || serializedSTSet == null) {
            return false;
        }

        String setString = this.stringBetween(XMLSerializer.STSET_TAG, serializedSTSet, 0);

        if (setString == null) {
            return false;
        }

        String tagsString = this.stringBetween(XMLSerializer.TAGS_ENUM_TAG, setString, 0);

        if (tagsString == null) {
            return false;
        }

        // parse tags
        boolean found;
        int index = 0;

        do {
            found = false;
            String tagString = this.stringBetween(TAG_TAG, tagsString, index);
            if (tagString != null) {
                found = true;

                this.deserializeTag(target, tagString);

                index = tagsString.indexOf(this.endTag(TAG_TAG), index) + 1;
            }
        } while (found);

        // more than a plain set ?
        SemanticNet sn = null;
        try {
            sn = this.cast2SN(target);
            this.deserializeRelations(sn, setString);
            // relations
        } catch (SharkKBException kb) {
            // just a simple set - ok, return
            return true;
        }

        return true;
    }

    private void deserializeRelations(Taxonomy target, String source) {
        String relationsString = this.stringBetween(XMLSerializer.SUB_SUPER_TAG, source, 0);

        if (relationsString == null) {
            return;
        }

        int index = 0;

        boolean found = false;
        do {
            found = false;
            String relationString = this.stringBetween(XMLSerializer.SUPER_TAG,
                    relationsString, index);

            if (relationString == null) continue;

            found = true;
            // adjust index for next try
            index = relationsString.indexOf(this.endTag(XMLSerializer.SUPER_TAG)) + 1;

            String superTagString = this.stringBetween(XMLSerializer.SOURCE_TAG, relationString, 0);
            if (superTagString == null) continue;

            String sourceSI = this.stringBetween(SI_TAG, relationString, 0);
            if (sourceSI == null) continue;

            String targetTagString = this.stringBetween(XMLSerializer.TARGET_TAG, relationString, 0);
            if (targetTagString == null) continue;

            String targetSI = this.stringBetween(SI_TAG, relationString, 0);
            if (targetSI == null) continue;

            try {
                TXSemanticTag sourceTag = (TXSemanticTag) target.getSemanticTag(sourceSI);
                if (sourceTag == null) continue;

                TXSemanticTag targetTag = (TXSemanticTag) target.getSemanticTag(targetSI);
                if (targetTag == null) continue;

                // set super tag
                sourceTag.move(targetTag);
            } catch (SharkKBException skbe) {
                // ignore and go ahead
                continue;
            }
        } while (found);
    }

    private void deserializeRelations(SemanticNet target, String source) {
        String relationsString = this.stringBetween(XMLSerializer.PREDICATES_TAG, source, 0);

        if (relationsString == null) return;

        int index = 0;

        boolean found = false;
        do {
            found = false;
            String predicateString = this.stringBetween(XMLSerializer.PREDICATE_TAG,
                    relationsString, index);

            if (predicateString == null) continue;

            found = true;
            // adjust index for next try
            index = relationsString.indexOf(this.endTag(XMLSerializer.PREDICATE_TAG), index) + 1;

            String nameString = this.stringBetween(NAME_TAG, predicateString, 0);
            if (nameString == null) continue;

            String sourceTagString = this.stringBetween(XMLSerializer.SOURCE_TAG, predicateString, 0);
            if (sourceTagString == null) continue;

            String sourceSI = this.stringBetween(SI_TAG, sourceTagString, 0);
            if (sourceSI == null) continue;

            String targetTagString = this.stringBetween(XMLSerializer.TARGET_TAG, predicateString, 0);
            if (targetTagString == null) continue;

            String targetSI = this.stringBetween(SI_TAG, targetTagString, 0);
            if (targetSI == null) continue;

            try {
                SNSemanticTag sourceTag = (SNSemanticTag) target.getSemanticTag(sourceSI);
                if (sourceTag == null) continue;

                SNSemanticTag targetTag = (SNSemanticTag) target.getSemanticTag(targetSI);
                if (targetTag == null) continue;

                sourceTag.setPredicate(nameString, targetTag);

            } catch (SharkKBException skbe) {
                // ignore and go ahead
                continue;
            }
        } while (found);
    }

    /**
     * Checks in source string if index is inside a cdata section. If so the first
     * index that is behind that cdata section is returned. -1 is returned otherwise.
     */
    private int endOfCDataSection(String source, int startIndex, int position) {
        int cdataStart = source.indexOf(XMLSerializer.CDATA_START_TAG, startIndex);

        // there is no cdata section
        if (cdataStart == -1) {
            return -1;
        }

        // there is one - where does is end?
        int cdataEnd = source.indexOf(XMLSerializer.CDATA_END_TAG, cdataStart);

        if (cdataEnd == -1) {
            // malformed structure!!
            return -1;
        }

        /* ok there is a cdata section
         * is position inside?
         */

        // is position in before cdata section? - ok
        if (position < cdataStart) {
            return -1;
        }

        // not before cdatasection

        // inside?
        if (position > cdataStart && position < cdataEnd) {
            // inside - return end of section
            return cdataEnd + this.cdata_end_tag_length + 1;
        }

        // it's behind that cdata section - maybe there is another one
        return this.endOfCDataSection(source, cdataEnd, position);
    }

    /**
     * Return string between a given tag. Null is returned if no tag can be found
     * which is also happens with malformed formats.
     *
     * @param tag    tag to look for
     * @param index  begin search at this index in source
     * @param source tag should be found in this string
     * @return
     */
    private String stringBetween(String tag, String source, int index) {
        if (source == null || tag == null || index < 0) {
            return null;
        }

        if (index >= source.length()) {
            return null;
        }

        /* <![CDATA[ An in-depth look at creating applications with XML, using <, >,]]>
         *
         */

        int startIndex = 0;
        int cdataEnd;

        do {
            cdataEnd = -1;
            startIndex = source.indexOf(this.startTag(tag), index);

            if (startIndex == -1) {
                return null;
            }

            // inside cdata section?
            cdataEnd = this.endOfCDataSection(source, index, startIndex);

            if (cdataEnd > -1) {
                index = cdataEnd;
            }

            // do again until tag outside cdata section was found
        } while (cdataEnd > -1);

        int endIndex;

        index = startIndex;

        do {
            endIndex = source.indexOf(this.endTag(tag), index);

            if (endIndex == -1) {
                return null;
            }

            // inside cdata section?
            cdataEnd = this.endOfCDataSection(source, index, endIndex);

            if (cdataEnd > -1) {
                index = cdataEnd;
            }

            // do again until tag outside cdata section was found
        } while (cdataEnd > -1);

        // cut start-Tag
        startIndex += this.startTag(tag).length();

        String retString = source.substring(startIndex, endIndex);

        if (retString.length() == 0) {
            return null;
        }

        return retString;
    }

    // TODO
    private boolean deserializeSTSet(SpatialSTSet locations, String partString) throws SharkKBException {
        // TODO - workaround until spatial and time tags finished
        return this.deserializeSTSet((STSet) locations, partString);
    }

    // TODO
    private boolean deserializeSTSet(TimeSTSet times, String partString) throws SharkKBException {

        int index = 0;
        boolean found = false;

        do {
            found = false;
            String fromString = this.stringBetween(XMLSerializer.TIME_FROM, partString, index);

            long from = TimeSemanticTag.FIRST_MILLISECOND_EVER;

            if (fromString != null) {
                found = true;
                from = TimeLong.parse(fromString);
            }

            String durationString = this.stringBetween(XMLSerializer.TIME_DURATION, partString, index);

            long duration = TimeSemanticTag.FOREVER;

            if (durationString != null) {
                found = true;
                duration = Long.parseLong(durationString);
            }

            if (found) {
                times.createTimeSemanticTag(from, duration);

                int fromIndex = partString.indexOf(this.endTag(XMLSerializer.TIME_FROM));
                int durationIndex = partString.indexOf(this.endTag(XMLSerializer.TIME_DURATION));

                index = fromIndex > durationIndex ? fromIndex : durationIndex;
                index++;
            }

        } while (found && index > 0);

        return !times.isEmpty();
    }

    private SemanticNet cast2SN(STSet stset) throws SharkKBException {
        SemanticNet sn;
        try {
            sn = (SemanticNet) stset;
        } catch (ClassCastException e) {
            InMemoSTSet imset = null;

            try {
                imset = (InMemoSTSet) stset;
            } catch (ClassCastException cce) {
                throw new SharkKBException("sorry, this implementation works with in memo shark kb implementation only");
            }

            InMemoGenericTagStorage tagStorage = imset.getTagStorage();
            sn = new InMemoSemanticNet(tagStorage);
        }

        return sn;
    }
}
