package net.sharkfw.knowledgeBase.geom;

import net.sharkfw.knowledgeBase.SharkKBException;

import java.util.Locale;

/**
 * @author Max Oehme (546545)
 */
public class PointGeometry implements SharkGeometry {

    private double x, y;
    private final String wktPattern = "POINT(%f %f)";

    public PointGeometry(SharkGeometry sharkGeometry) throws SharkKBException {
        if (isPoint(sharkGeometry.getWKT())){
            parseWkt(sharkGeometry.getWKT());
        } else {
            throw new SharkKBException("Geometry is not a Point!");
        }
    }

    public PointGeometry(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String getWKT() {
        return String.format(Locale.ROOT, wktPattern, x, y);
    }

    @Override
    public String getEWKT() {
        return null;
    }

    @Override
    public int getSRS() {
        return 0;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public static boolean isPoint(String wkt) {
        return wkt.contains("POINT");
    }

    private void parseWkt(String wkt) {
        this.x = Double.parseDouble(wkt.substring(wkt.indexOf('(')+1, wkt.indexOf(' ')));
        this.y = Double.parseDouble(wkt.substring(wkt.indexOf(' ')+1, wkt.indexOf(')')));
    }
}
