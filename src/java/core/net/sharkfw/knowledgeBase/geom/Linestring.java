package net.sharkfw.knowledgeBase.geom;

import java.util.ArrayList;

/**
 *
 * @author thsc
 */
public interface Linestring extends Geometry {
    
    public ArrayList<Point> getPoints();
}
