package net.sharkfw.knowledgeBase.geom.jts;

import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

/**
 *
 * @author s0542709, s0542541
 * @version 1.0
 *
 *
 */
public class SpatialAlgebra extends net.sharkfw.knowledgeBase.geom.SpatialAlgebra {

    /**
     * Checks with Java Topology Suite (JTS) if Well-known text is valid.
     *
     * @param wkt
     * @return true if valid
     * @throws SharkKBException
     */
    @Override
    public boolean isValidWKT(String wkt) throws SharkKBException {
        Geometry jtsGeometry = null;
        WKTReader reader = new WKTReader();
        try {
            jtsGeometry = reader.read(wkt);
        } catch (ParseException ex) {
            throw new SharkKBException("WKT parsing problem");
        }
        IsValidOp validOp = new IsValidOp(jtsGeometry);
        return validOp.isValid();
    }

    /**
     * EWKT is not supported by Java Topology Suite (JTS)
     *
     * @param ewkt
     * @return true
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    @Override
    public boolean isValidEWKT(String ewkt) throws SharkKBException {
        return true;
    }

    /**
     * Checks with JTS-Library if two Shark-SpatialSemanticTags are identical.
     *
     * @param a
     * @param b
     * @return true if both SpatialSemanticTags identical
     * @throws SharkKBException
     */
    @Override
    public boolean identical(SpatialSemanticTag a, SpatialSemanticTag b) throws SharkKBException {
        SpatialSTSet tempA = InMemoSharkKB.createInMemoSpatialSTSet();
        SpatialSTSet tempB = InMemoSharkKB.createInMemoSpatialSTSet();
        tempA.merge(a);
        tempB.merge(b);
        return identical(tempA, tempB);
    }

    /**
     * Checks with JTS-Library if a Shark-SpatialSTSet and a SpatialSemanticTag
     * are identical.
     *
     * @param a
     * @param b
     * @return true if both SpatialSemanticTags identical
     * @throws SharkKBException
     */
    @Override
    public boolean identical(SpatialSTSet a, SpatialSemanticTag b) throws SharkKBException {
        SpatialSTSet tempB = InMemoSharkKB.createInMemoSpatialSTSet();
        tempB.merge(b);
        return identical(a, tempB);
    }

    /**
     * Checks with JTS-Library if two Shark-SpatialSTSets are identical.
     *
     * @param a
     * @param b
     * @return
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    @Override
    public boolean identical(SpatialSTSet a, SpatialSTSet b) throws SharkKBException {
        List<Geometry> jtsGeomsA = getListWithJTSGeometries(a);
        List<Geometry> jtsGeomsB = getListWithJTSGeometries(b);
        // Geometry with/as GeometryCollection does not work
        List<Geometry> jtsGeomsOnlyA = divideAllExistingGeometryCollections(jtsGeomsA);
        List<Geometry> jtsGeomsOnlyB = divideAllExistingGeometryCollections(jtsGeomsB);
        List<Geometry> jtsIntersectedGeoms = getIntersectsFromListsWithJTSGeommetries(jtsGeomsOnlyA, jtsGeomsOnlyB);
        List<Geometry> jtsIntersectedGeomsUnioned = unionTouchedJTSGeometries(jtsIntersectedGeoms);
        List<Geometry> jtsGeomsOnlyBUnioned = unionTouchedJTSGeometries(jtsGeomsOnlyB);
        return isListDifferent(jtsIntersectedGeomsUnioned, jtsGeomsOnlyBUnioned);
    }

    /**
     * Checks if a SpatialSemanticTag is in another SpatialSemanticTag.
     *
     * @param a
     * @param b
     * @return
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    @Override
    public boolean isIn(SpatialSemanticTag a, SpatialSemanticTag b) throws SharkKBException {
        SpatialSTSet tempA = InMemoSharkKB.createInMemoSpatialSTSet();
        SpatialSTSet tempB = InMemoSharkKB.createInMemoSpatialSTSet();
        tempA.merge(a);
        tempB.merge(b);
        return isIn(tempA, tempB);
    }

    /**
     * Checks if a SpatialSemanticTag is in an SpatialSTSet.
     *
     * @param a
     * @param b
     * @return
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    @Override
    public boolean isIn(SpatialSTSet a, SpatialSemanticTag b) throws SharkKBException {
        SpatialSTSet tempB = InMemoSharkKB.createInMemoSpatialSTSet();
        tempB.merge(b);
        return isIn(a, tempB);
    }

    /**
     * Checks if a SpatialSTSet is in another SpatialSTSet.
     *
     * @param a
     * @param b
     * @return
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    @Override
    public boolean isIn(SpatialSTSet a, SpatialSTSet b) throws SharkKBException {
        List<Geometry> jtsGeomsA = getListWithJTSGeometries(a);
        List<Geometry> jtsGeomsB = getListWithJTSGeometries(b);
        // Geometry with/as GeometryCollection does not work
        List<Geometry> jtsGeomsOnlyA = divideAllExistingGeometryCollections(jtsGeomsA);
        List<Geometry> jtsGeomsOnlyB = divideAllExistingGeometryCollections(jtsGeomsB);
        List<Geometry> jtsIntersectedGeoms = getIntersectsFromListsWithJTSGeommetries(jtsGeomsOnlyA, jtsGeomsOnlyB);
        List<Geometry> jtsIntersectedGeomsUnioned = unionTouchedJTSGeometries(jtsIntersectedGeoms);
        List<Geometry> jtsGeomsOnlyBUnioned = unionTouchedJTSGeometries(jtsGeomsOnlyB);
        return isListWithGeometriesCovered(jtsIntersectedGeomsUnioned, jtsGeomsOnlyBUnioned);
    }

    /**
     * Fragment must not be null!
     *
     * @param fragment
     * @param source
     * @param anchor
     * @return fragment
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    @Override
    public SpatialSTSet fragment(SpatialSTSet fragment, SpatialSTSet source, SpatialSemanticTag anchor) throws SharkKBException {
        if (fragment == null) {
            throw new SharkKBException("fragment is null");
        } else {
            Enumeration<SpatialSemanticTag> spatialSTEnum = source.spatialTags();
            while (spatialSTEnum.hasMoreElements()) {
                SpatialSemanticTag temp1 = spatialSTEnum.nextElement();
                if (temp1.identical(anchor)) {
                    fragment.merge(temp1);
                }
            }
        }
        return fragment;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     * @throws SharkKBException
     */
    private boolean isListDifferent(List<Geometry> a, List<Geometry> b) throws SharkKBException {
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        boolean isdifferent = false;
        for (int i = 0; i < a.size(); i++) {
            Geometry geomA = a.get(i);
            for (Geometry geomB : b) {
                if (geomA.covers(geomB)) {
                    try {
                        geomA = geomA.difference(geomB);
                    } catch (IllegalArgumentException ex) {
                        throw new SharkKBException("Differentiation with GeometryCollection is not allowed.");
                    }
                    a.remove(i);
                    if (!geomA.isEmpty()) {
                        a.add(i, geomA);
                    }
                }
            }
        }
        if (a.isEmpty()) {
            isdifferent = true;
        }
        return isdifferent;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     * @throws SharkKBException
     */
    private boolean isListWithGeometriesCovered(List<Geometry> a, List<Geometry> b) throws SharkKBException {
        boolean isCovered = false;
        for (Geometry geomA : a) {
            for (Geometry geomB : b) {
                try {
                    if (geomA.covers(geomB)) {
                        isCovered = true;
                    } else {
                        isCovered = false;
                        break;
                    }
                } catch (IllegalArgumentException ex) {
                    throw new SharkKBException("Covering with GeometryCollection is not allowed.");
                }
            }
            if (!isCovered) {
                break;
            }
        }
        return isCovered;
    }
    
    private List<Geometry> unionTouchedJTSGeometries(List<Geometry> geometries) throws SharkKBException
    {
      List<Geometry> resultGeometries = new ArrayList();
      boolean wasTouched = false;
      if (geometries.size() > 0)
      {
        Geometry firstGeom = geometries.get(0);
        for (Geometry geom : geometries) {
          try {
            boolean isTouch = (firstGeom.touches(geom) && !firstGeom.equals((Object)geom));
            if (isTouch) {
              resultGeometries.add(firstGeom.union(geom));
              wasTouched = true;
            }
            else {
              resultGeometries.add(firstGeom);
            }
          } catch (IllegalArgumentException ex) {
              throw new SharkKBException("Touch with GeometryCollection is not allowed.");
          }              
        }
        if (wasTouched) {
          resultGeometries = unionTouchedJTSGeometries(resultGeometries);
        }
      }
      return resultGeometries;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     * @throws SharkKBException
     */
    private List<Geometry> getIntersectsFromListsWithJTSGeommetries(List<Geometry> a, List<Geometry> b) throws SharkKBException {
        List<Geometry> tempIntersects = new ArrayList();
        for (Geometry geomA : a) {
            for (Geometry geomB : b) {
                try {
                    if (geomA.intersects(geomB)) {
                        tempIntersects.add(geomA);
                    }
                } catch (IllegalArgumentException ex) {
                    throw new SharkKBException("Intersection with GeometryCollection is not allowed.");
                }
            }
        }

        return tempIntersects;
    }

    /**
     *
     * @param geometries
     * @return
     * @throws SharkKBException
     */
    private List<Geometry> divideAllExistingGeometryCollections(List<Geometry> geometries) throws SharkKBException {
        List<Geometry> geomsWithoutGeometryCollection = new ArrayList();
        boolean wasDivided = false;
        for (Geometry geom : geometries) {
            String geomCollClassName = "com.vividsolutions.jts.geom.GeometryCollection";
            boolean isCollection = ((geom.getClass().getName().compareTo(geomCollClassName) == 0) ||
                                    (geom.getClass().getSuperclass().getName().compareTo(geomCollClassName) == 0));
            if (isCollection) {
                List<Geometry> dividedGeometries = divideGeometryCollection((GeometryCollection) geom);
                geomsWithoutGeometryCollection.addAll(dividedGeometries);
                wasDivided = true;
            } else {
                geomsWithoutGeometryCollection.add(geom);
            }
        }
        if (wasDivided) {
          geomsWithoutGeometryCollection = divideAllExistingGeometryCollections(geomsWithoutGeometryCollection);
        }
        return geomsWithoutGeometryCollection;
    }

    /**
     *
     * @param geomCollection
     * @return
     * @throws SharkKBException
     */
    private List<Geometry> divideGeometryCollection(GeometryCollection geomCollection) throws SharkKBException {
        List<Geometry> tempGeomList = new ArrayList();
        int numberOfGeoms = geomCollection.getNumGeometries();
        for (int i = 0; i < numberOfGeoms; i++) {
            tempGeomList.add(geomCollection.getGeometryN(i));
        }
        return tempGeomList;
    }

    /**
     *
     * @param spatialSTSet
     * @return
     * @throws SharkKBException
     */
    private List getListWithJTSGeometries(SpatialSTSet spatialSTSet) throws SharkKBException {
        List<String> wktGeometries = extractWKTGeometries(spatialSTSet);
        return convertToJTSGeometries(wktGeometries);
    }

    /**
     *
     * @param spatialSTSet
     * @return
     * @throws SharkKBException
     */
    private List<String> extractWKTGeometries(SpatialSTSet spatialSTSet) throws SharkKBException {
        List<String> wktGeometries = new ArrayList();
        Enumeration<SpatialSemanticTag> spatialSTEnum = spatialSTSet.spatialTags();
        while (spatialSTEnum.hasMoreElements()) {
            wktGeometries.add(spatialSTEnum.nextElement().getGeometry().getWKT());
        }
        return wktGeometries;
    }

    /**
     *
     * @param wktGeometries
     * @return
     * @throws SharkKBException
     */
    private List<Geometry> convertToJTSGeometries(List<String> wktGeometries) throws SharkKBException {
        List<Geometry> jtsGeometries = new ArrayList();
        WKTReader reader = new WKTReader();
        for (String wktGeom : wktGeometries) {
            try {
                Geometry tempGeom = reader.read(wktGeom);
                jtsGeometries.add(tempGeom);
            } catch (ParseException ex) {
                throw new SharkKBException("WKT parsing problem with SpatialSTSet ");
            }
        }
        return jtsGeometries;
    }

}
