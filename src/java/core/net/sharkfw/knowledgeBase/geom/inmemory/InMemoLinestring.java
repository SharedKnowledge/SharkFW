package net.sharkfw.knowledgeBase.geom.inmemory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sharkfw.knowledgeBase.geom.Linestring;
import net.sharkfw.knowledgeBase.geom.Point;

/**
 *
 * @author Florian Lehne
 */
public class InMemoLinestring implements Linestring {

    public final static String SHARK_LINESTRING_ST_NAME = "Route";
    public final static String SHARK_LINESTRING_SI_PREFIX = "sharklong://";
    private ArrayList<Point> points;

    public InMemoLinestring(String wkt) {
        this.points = new ArrayList<Point>();
        Matcher matcher = Pattern.compile("(\\d+(\\.\\d+)?)").matcher(wkt);

        ArrayList<Double> wktPoints = new ArrayList<Double>();

        while (matcher.find()) {
            wktPoints.add(Double.parseDouble(matcher.group()));
        }

        for (int i = 0; i < wktPoints.size(); i += 2) {
            this.points.add(new InMemoPoint(wktPoints.get(i), wktPoints.get(i + 1)));
        }
    }

    public InMemoLinestring(ArrayList<Point> points) {
        this.points = points;
    }

    public static String createSpatialSI(String longitude, String latitude) {
        return SHARK_LINESTRING_SI_PREFIX + longitude + "/" + latitude;
    }

    @Override
    public String getWKT() {
        StringBuilder sb = new StringBuilder();
        Iterator<Point> iter = this.points.iterator();

        sb.append("LINESTRING (");
        while (iter.hasNext()) {
            Point point = iter.next();

            sb.append(point.getLong());
            sb.append(" ");
            sb.append(point.getLat());

            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    @Override
    public ArrayList<Point> getPoints() {
        return this.points;
    }
}
