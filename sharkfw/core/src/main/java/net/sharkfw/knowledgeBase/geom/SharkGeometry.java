package net.sharkfw.knowledgeBase.geom;

/**
 * TODO: root class of any object describing geometry of a spatial semantic tag.
 *
 * @author thsc
 */
public interface SharkGeometry {

    public static final String SHARK_POINT_SI_PREFIX = "sharklong://";

    /**
     *
     * @return vector geometry objects as Well-Known Text
     */
    public String getWKT();

    /**
     *
     * @return vector geometry objects as Extended Well-Known Text (include epsg
     * code of spatial reference system)
     */
    public String getEWKT();

    /**
     *
     * @return epsg code of spatial reference system
     */
    public int getSRS();
}
