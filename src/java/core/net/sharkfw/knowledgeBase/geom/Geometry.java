package net.sharkfw.knowledgeBase.geom;

/**
 * TODO: root class of any object describing geometry of a spatial semantic tag.
 * @author thsc
 */
public interface Geometry {
    public static final String SHARK_POINT_SI_PREFIX = "sharklong://";
    public String getWKT();
    public String getEWKT();
}
