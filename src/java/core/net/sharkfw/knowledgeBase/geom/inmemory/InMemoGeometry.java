package net.sharkfw.knowledgeBase.geom.inmemory;

import java.util.ArrayList;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.geom.Geometry;
import net.sharkfw.knowledgeBase.geom.Linestring;
import net.sharkfw.knowledgeBase.geom.Point;

/**
 *
 * @author thsc
 */
public abstract class InMemoGeometry {

    public static Geometry createGeomByWKT(String wkt) {
        if (wkt.toUpperCase().startsWith("POINT")) {
            return new InMemoPoint(wkt);
        }
        if (wkt.toUpperCase().startsWith("LINESTRING")) {
            return new InMemoLinestring(wkt);
        }
        return null;
    }

    public static Point createPoint(Double lon, Double lat) {
        return new InMemoPoint(lon, lat);
    }

    public static Linestring createLinestring(ArrayList<Point> points) {
        return new InMemoLinestring(points);
    }

    public static String createSpatialSI(Geometry geom) {
        if (geom instanceof Point) {
            Point point = (Point) geom;
            return InMemoPoint.createSpatialSI(
                    Double.toString(point.getLong()), 
                    Double.toString(point.getLat())
                );
        }
        return SharkCS.ANYURL;
    }

    public static String createSpatialSI(String longitude, String latitude) {
        return InMemoPoint.SHARK_POINT_SI_PREFIX + longitude + "/" + latitude;
    }
}