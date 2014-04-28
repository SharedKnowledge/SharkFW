package net.sharkfw.knowledgeBase;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * The fragmenation of dimensions can be configured using this class.
 * It keeps a number of possible options to control the way the fragmentation works
 *
 * TODO: Should be extended to handle allowed and forbidden properties as well
 *
 * @author thsc
 * @author mfi
 */
public class FragmentationParameter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7292106520692533611L;
    private boolean superAllowed = false;
    private boolean subAllowed = false;
    private int depth = 0;
    @SuppressWarnings("rawtypes")
    private Vector allowedPredicates = null;
    @SuppressWarnings("rawtypes")
    private Vector forbiddenPredicates = null;

    private boolean taxonomyInitialized = false;
    private boolean netInitialized = false;
    private boolean allInitialized = false;

    /** 
     * default: depth 0, no assocs allowed, no super or sub concepts
     */
    public FragmentationParameter() {}

    public FragmentationParameter(int depth) {
        this.subAllowed = false;
        this.superAllowed = false;
        this.depth = depth;
    }

    /**
     * Shortcut constructor passing only the values for hierarchical traversing
     * of netlike structures.
     * For taxonomies only.
     */
    public FragmentationParameter(boolean superAllowed, boolean subAllowed, int depth) {
        this.superAllowed = superAllowed;
        this.subAllowed = subAllowed;
        this.depth = depth;

        this.taxonomyInitialized = true;

    }
    
    private static final FragmentationParameter zeroFP = 
            new FragmentationParameter();
    
    public static FragmentationParameter getZeroFP() {
        return zeroFP;
    }
    
    private static final FragmentationParameter[] zeroFPs = {
        zeroFP, zeroFP, zeroFP, zeroFP, zeroFP, zeroFP, zeroFP
    };
    
    public static FragmentationParameter[] getZeroFPs() {
        return zeroFPs;
    }

    /**
     * Shortcut constructor allowing to pass a number of allowed and forbidden
     * association types for usage in netlike structures.
     * For AssociatedSTSets only
     */
    @SuppressWarnings("rawtypes")
    public FragmentationParameter(Vector allowedPredicates, Vector forbiddenPredicates, int depth) {
        this.allowedPredicates = allowedPredicates;
        this.forbiddenPredicates = forbiddenPredicates;
        this.depth = depth;
        
        this.netInitialized = true;
    }

    /**
     * Most general constructor that offers all customization options
     */
    @SuppressWarnings("rawtypes")
    public FragmentationParameter(boolean superAllowed, boolean subAllowed, Vector allowedAssocTypes, Vector forbiddenAssocTypes, int depth) {
        this.superAllowed = superAllowed;
        this.subAllowed = subAllowed;
        this.allowedPredicates = allowedAssocTypes;
        this.forbiddenPredicates = forbiddenAssocTypes;
        this.depth = depth;

        this.allInitialized = true;
    }

    @SuppressWarnings("rawtypes")
    private void initTaxonomyBySTSet() {
        // init by stSet
        if(this.allowedPredicates != null) {
            Enumeration aEnum = this.allowedPredicates.elements();
            while(aEnum.hasMoreElements() && (!this.subAllowed || !this.superAllowed)) {
                String aType = (String)aEnum.nextElement();
                if( aType.equalsIgnoreCase(SemanticNet.SUPERTAG) ) {
                    this.superAllowed = true;
                } else if( aType.equalsIgnoreCase(SemanticNet.SUBTAG) ) {
                    this.subAllowed = true;
                }
            }
        } else {
            // nothing set means anything
            this.subAllowed = true;
            this.superAllowed = true;
        }

        if( (this.subAllowed || this.superAllowed) && this.forbiddenPredicates != null) {
            Enumeration fEnum = this.forbiddenPredicates.elements();
            while(fEnum.hasMoreElements() && (this.superAllowed || this.subAllowed)) {
                String fType = (String) fEnum.nextElement();
                if ( this.superAllowed && fType.equalsIgnoreCase(SemanticNet.SUPERTAG) ) {
                    this.superAllowed = false;
                } else if( this.subAllowed && fType.equalsIgnoreCase(SemanticNet.SUBTAG)) {
                    this.subAllowed = false;
                }
            }
        }

        this.allInitialized = true;
    }

    /**
     * Return whether or not traversing super-relations is allowed
     * @return <code>true</code> if it allowed, <code>false</code> otherwise
     */
    public boolean getSuperAllowed() {
        if(this.allInitialized || this.taxonomyInitialized) {
            return this.superAllowed;
        }

        if(this.netInitialized) {
            this.initTaxonomyBySTSet();
        }

        return this.superAllowed;
    }

    /**
     * Return whether or not traversing super-realtions is allowed.
     * @return <code>true</code> if it is allowed, <code>false</code> otherwise
     */
    public boolean getSubAllowed() {
        if(this.allInitialized || this.taxonomyInitialized) {
            return this.subAllowed;
        }

        if(this.netInitialized) {
            this.initTaxonomyBySTSet();
        }

        return this.subAllowed;
    }

    /**
     * Return the maximum depth for traversal.
     * @return An integer value representing the maximum depth for traversal.
     */
    public int getDepth() {
        return this.depth;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void initSNByTaxonomy() {
        if(this.superAllowed) {
            this.allowedPredicates = new Vector();
            this.allowedPredicates.add(SemanticNet.SUPERTAG);
        }

        if(this.subAllowed) {
            if(this.allowedPredicates == null) {
                this.allowedPredicates = new Vector();
            }
            this.allowedPredicates.add(SemanticNet.SUBTAG);
        }

        this.allInitialized = true;
    }

    /**
     * Return an <code>Enumeration</code> of strings that contains all allowed
     * association types.
     *
     * @return An <code>Enumeration</code> of strings which are allowed to be traversed.
     */
    @SuppressWarnings("unchecked")
    public Enumeration<String> getAllowedPredicates() {
        if(!this.allInitialized && !this.netInitialized) {
            this.initSNByTaxonomy();
        }

        if(this.allowedPredicates == null) return null;
        return this.allowedPredicates.elements();


    }

    /**
     * Return an <code>Enumeration</code> of strings that contains all forbidden association types.
     *
     * @return An <code>Enumeration</code> of strings which are forbidden to be traversed.
     */
    @SuppressWarnings("unchecked")
    public Enumeration<String> getForbiddenPredicates() {
        if(!this.allInitialized && !this.netInitialized) {
            this.initSNByTaxonomy();
        }

        if(this.forbiddenPredicates == null) return null;
        return this.forbiddenPredicates.elements();
    }


    /**
     * Return whether or not a certain type string is allowed.
     *
     * If the string is in allowed and not in forbidden it is allowed
     * Otherwise it is NOT allowed.
     *
     * If one of the enumerations is null it is only checked against the other
     * (non null) enumeration. If both are null the returnvalue is always true.
     * 
     * @param type The string in question
     * @param allowedAssocTypesEnum The enumeration of allowed types
     * @param forbiddenAssocTypesEnum The enumeration of forbidden types
     * @return True if it allowed, false otherwise
     */
    @SuppressWarnings("rawtypes")
    public static boolean typeAllowed(String type, Enumeration allowedAssocTypesEnum, Enumeration forbiddenAssocTypesEnum) {
            boolean allowed = false;
            boolean forbidden = false;

            if(allowedAssocTypesEnum != null) {
                while(allowedAssocTypesEnum.hasMoreElements()) {
                    String allowedType = (String) allowedAssocTypesEnum.nextElement();
                    if(type.equalsIgnoreCase(allowedType)) {
                        allowed = true;
                        break;
                    }
                }
            } else {
                allowed = true;
            }

            if(forbiddenAssocTypesEnum != null) {
                while(forbiddenAssocTypesEnum.hasMoreElements()) {
                    String forbiddenType = (String) forbiddenAssocTypesEnum.nextElement();
                    if(type.equalsIgnoreCase(forbiddenType)) {
                        forbidden = true;
                        break;
                    }
                }
            } else {
                forbidden = false;
            }

            if(allowed && !forbidden) {
                return true;
            }

            return false;
    }


    /**
     * Compute the effectively allowed types, by testing every type.
     * On success it is added to the returnvalue, otherwise not.
     * 
     * @param types types to check
     * @param allowedAssocTypesEnum
     * @param forbiddenAssocTypesEnum
     * @return Vector of types which are in type enumeration and allowed
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Vector allowedTypes(Enumeration types, Enumeration allowedAssocTypesEnum, Enumeration forbiddenAssocTypesEnum) {
        Vector result = new Vector();

        while(types != null && types.hasMoreElements()) {
            String type = (String)types.nextElement();
            if (FragmentationParameter.typeAllowed(type, allowedAssocTypesEnum, forbiddenAssocTypesEnum)) {
                result.add(type);
            }
        }
       return result;
    }
}
