package net.sharkfw.knowledgeBase;

/**
 * A SemanticTag is a representation of a thing.
 *
 * <p>SemanticTags represent things from the real world. To address these things
 * every Semantic Tag has a name. Names alone are prone to ambiguities and
 * can cause problems when exchanginge information solely based on names. There
 * may be different names for the same thing (synonyms) or similar name for
 * different things (homonyms). Multi-language support is also problematic, as
 * long as only names are involved.</p>
 *
 * <p>Semantic tags in shark thus feature an additional identifier. The so called
 * <em>Subject Identifier</em> as described in ISO 13250 Topic Maps. Subject Identifiers (SI)
 * are URIs that point to an information resource which describes a thing in
 * an unambigious way. Shark makes use of this invention. Every semantic tag in
 * shark is required to have at least one SI. The maximum number of SIs per
 * tag is only restricted by the resources used to store and process them. Shark
 * generally assumes that SIs are URIs.</p>
 *
 * <p><strong>Example:</strong><br />
 * <p>
 * Consider a tag named <em>Java</em> with the SIs {https://secure.wikimedia.org/wikipedia/en/wiki/Java_%28programming_language%29,
 * http://www.oracle.com/technetwork/java/javase/overview/index.html}.<br />
 * It is rather obvious that this tag refers to the Java programming language.</p>
 *
 * <p>Now consider this tag, named <em>Java</em> with the Sis {https://secure.wikimedia.org/wikipedia/en/wiki/Java}.</p>
 *
 * <p>The latter tag describes the island <em>Java</em>. Despite having the same names,
 * the tags (or rather their semantics) can be told apart.</p>
 *
 * <p>Consider a third tag with the name <em>cool programming language</em> and the Si {https://secure.wikimedia.org/wikipedia/en/wiki/Java_%28programming_language%29, https://secure.wikimedia.org/wikipedia/de/wiki/Java_%28Programmiersprache%29}.<br />
 * Although this tag has a completely different name than the first one, they still refer to the same <em>thing</em>.
 * The Java programming language. This tag also features an SI which points to website in german.
 * Thus other tags, referring to that same website, will be recognized as being equal to this one.</p></p>
 *
 * <p><strong>Equality rule:</strong></br />
 * Two semantic tags are equal if at least one SI (as String) of each of is matching an SI from the other tag like:<br />
 * <code>si1.equals(si2);</code> <br />
 * </p>
 * 
 * @author thsc
 * @author mfi
 */
public interface SemanticTag extends SystemPropertyHolder {

    /**
    * A property name for storing the name value of a semantic tag.
    */
    public static final String NAME = "NAME";
    /**
    * A property name for storing the tag's sis.
    */
    public static final String SI = "SI";
    /**
    * A property tag for storing the tag's internal id
    */
    public static final String ID = "ID";

    /**
    * Return the name of this tag
    * @return A string containing the tag's name
    */
    public String getName();

    /**
    * Return the SIs of this tag
    * @return An array of strings containing the SIs of this tag
    */
    public String[] getSI();



    /**
     * Remove a given SI from the set of SIs.
     * Note: It is possible to create a semantic tag without a 
     * subject identifiere. Once a si is set it is impossible to
     * remove any si, though. The tag would loose its semantics in this case.
     * 
     * @param si The string to remove
     * @throws SharkKBException if the last si shall be removed
     */
    public void removeSI(String si) throws SharkKBException;

    /**
     * Add a string to the set of SIs
     * @param si The string to add
     */
    public void addSI(String si) throws SharkKBException;

    /**
     * Set the name of this tag
     * @param newName The string to use name
     */
    public void setName(String newName);

    /**
     * Merge one tag into this. Merging will simply add all the SIs from
     * <code>oc</code> to the SIs of this tag. Duplicates must be removed.
     *
     * TODO: Check if this is wise in respects to updating the si2id table
     * 
     * @param st The SemanticTag to merge into this one
     */
    public void merge(SemanticTag st);

    /**
     * Set whether this tag is a hidden tag or not.
     * Hidden tags will be removed from all fragmentation and way-finding
     * results that may occur, when using the standard KP.
     *
     * Generally speaking a hidden Tag, is a tag that must never leave a
     * local system.
     *
     * By default, all tags are non-hidden tags (public).
     *
     * @param isHidden <code>true</code> if the tag is to be hidden, <code>false</code> if the tag is to public (default).
     */
    public void setHidden(boolean isHidden);
    
    /**
     * 
     * @return status whether this tag shall be stored locally only (true) 
     * or not also transmitted (false)
     */
    public boolean hidden();
    
    /**
     * @return true if this tag does no contrain anything - it is an ANY tag
     */
    public boolean isAny();
    
    /**
     * 
     * @param other
     * @return true if the other tag is semantically identical to this
     * tag.
     */
    public boolean identical(SemanticTag other);
    
}
