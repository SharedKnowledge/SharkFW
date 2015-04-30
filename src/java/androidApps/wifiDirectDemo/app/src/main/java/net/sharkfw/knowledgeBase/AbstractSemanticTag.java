package net.sharkfw.knowledgeBase;

/**
 * This class unites offers a place to extends all SemanticTags with additional functionality.
 * It serves as a superclass for all SemanticTags in the Shark framework.
 * 
 * Property are stored with a property holder that can be set.
 * If property holder is set and a property is ought to be stored the in memory property
 * holder is used.
 * 
 * @author mfi, thsc
 */
public abstract class AbstractSemanticTag extends PropertyHolderDelegate 
                                        implements SemanticTag, SystemPropertyHolder {
    
    /**
    * The property-name to denote hidden tags, that must not leave this KnowledgePort as
    * part of interests or knowledge.
    */
    public static final String HIDDEN = "AbstractST_hidden";
  
    private boolean hidden = false;
    private boolean HIDDEN_DEFAULT = false;
  
    protected AbstractSemanticTag(SystemPropertyHolder persistentHolder) {
        super(persistentHolder);
    }
    
    protected AbstractSemanticTag() {
        super();
    }
    
    @Override
    public void merge(SemanticTag toMerge) {
        SharkCSAlgebra.merge(this, toMerge);
    }
    
    /**
     * 
     * @param other
     * @return true if the other tag is semantically identical to this
     * tag.
     */
    public final boolean identical(SemanticTag other) {
        return SharkCSAlgebra.identical(this, other);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        return this.identical((SemanticTag) obj);
    }
    
    /**
     * Set this tag to be hidden.
     * @param isHidden true if the tag shall be hidden. False if not.
     */
    @Override
    public final void setHidden(boolean isHidden) {
        this.hidden = isHidden;
        this.persist();
    }
    
    @Override
    public final boolean hidden() {
        return this.hidden;
    }   
    
    /**
     * @return true if this tag does no contrain anything - it is an ANY tag
     */
    @Override
    public boolean isAny() {
        return SharkCSAlgebra.isAny(this.getSI());
    }
    
    public static final String TYPE_SYSTEM_PROPERTY_NAME = "ST_class";
    public static final String PLAIN_ST = "PLAIN_ST";
    public static final String SN_TX_ST = "SN_TX_ST";
    public static final String SN_TX_PST = "SN_TX_PST";
    public static final String SPATIAL_ST = "SST";
    public static final String TIME_ST = "TST";
    
    @Override
    public void persist() {
        super.persist();
        this.setSystemProperty(AbstractSemanticTag.HIDDEN, 
                Boolean.toString(this.hidden));
        
        String className;
        
        if(this instanceof PeerTXSemanticTag || this instanceof PeerSNSemanticTag) {
            className = SN_TX_PST;
        } 
        else if(this instanceof SpatialSemanticTag) {
            className = SPATIAL_ST;
        }
        else if(this instanceof TimeSemanticTag) {
            className = TIME_ST;
        }
        else if(this instanceof TXSemanticTag || this instanceof SNSemanticTag) {
            className = SN_TX_ST;
        }
        else {
            className = PLAIN_ST;
        }
        
        this.setSystemProperty(TYPE_SYSTEM_PROPERTY_NAME, className);
    }
    
    @Override
    public void refreshStatus() {
        super.refreshStatus();
        
        boolean isHidden = HIDDEN_DEFAULT;

        String hiddenString = this.getSystemProperty(AbstractSemanticTag.HIDDEN);
        if(hiddenString != null) {
            isHidden = Boolean.parseBoolean(hiddenString);
        }
        
        this.hidden = isHidden;
    }
    
    private AbstractSharkKB listener = null;
    
    void setListener(AbstractSharkKB listener) {
        this.listener = listener;
    }

    public void sisChanged() {
        if(this.listener != null) {
            this.listener.siChanged(this);
        }
    }
}
