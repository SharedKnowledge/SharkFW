package net.sharkfw.knowledgeBase.geom.inmemory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sharkfw.knowledgeBase.geom.Point;

/**
 *
 * @author Florian Lehne
 */
public class InMemoPoint extends InMemoGeometry implements Point {

    public final static String SHARK_POINT_ST_NAME = "Ort";
    private double longitude;
    private double latitude;

    public InMemoPoint(String wkt) {
        Matcher matcher = Pattern.compile("(\\d+(\\.\\d+)?)").matcher(wkt);

        matcher.find();
        this.longitude = Double.parseDouble(matcher.group());
        matcher.find();
        this.latitude = Double.parseDouble(matcher.group());
    }

    public InMemoPoint(Double lon, Double lat) {
        this.longitude = lon;
        this.latitude = lat;
    }
    
    @Override
    public String getWKT() {
        return "POINT (" + this.longitude + " " + this.latitude + ")";
    }

    @Override
    public double getLong() {
        return this.longitude;
    }

    @Override
    public double getLat() {
        return this.latitude;
    }
}
