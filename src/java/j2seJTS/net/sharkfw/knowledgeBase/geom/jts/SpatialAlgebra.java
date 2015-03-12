package net.sharkfw.knowledgeBase.geom.jts;

import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
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
        jtsGeometry = jtsGeometry.union(); //because of self-intersection
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
        return super.isValidEWKT(ewkt);
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
        List<Geometry> jtsIntersectedGeomsFromGeomsAWithGeomsB = getIntersectsFromListsWithJTSGeommetries(jtsGeomsOnlyA, jtsGeomsOnlyB);
        List<Geometry> jtsGeomsOnlyBUnioned = unionTouchedJTSGeometries(jtsGeomsOnlyB);
        List<Geometry> jtsIntersectedGeomsAUnioned = unionTouchedJTSGeometries(jtsIntersectedGeomsFromGeomsAWithGeomsB);
        return isListWithGeometriesCovered(jtsGeomsOnlyBUnioned, jtsIntersectedGeomsAUnioned);
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
        SpatialSTSet tempAnchor = InMemoSharkKB.createInMemoSpatialSTSet();
        tempAnchor.merge(anchor);
        List<Geometry> jtsAnchorGeoms = getListWithJTSGeometries(tempAnchor);
        List<Geometry> jtsSourceGeoms = getListWithJTSGeometries(source);
        List<Geometry> jtsAnchorGeomsOnly = divideAllExistingGeometryCollections(jtsAnchorGeoms);
        List<Geometry> jtsSourceGeomsOnly = divideAllExistingGeometryCollections(jtsSourceGeoms);
        List<Geometry> jtsIntersectedSourceGeomsWithAnchorGeoms = getIntersectsFromListsWithJTSGeommetries(jtsSourceGeomsOnly, jtsAnchorGeomsOnly);
        List<Geometry> jtsAnchorGeomsOnlyUnioned = unionTouchedJTSGeometries(jtsAnchorGeomsOnly);
        List<Geometry> jtsIntersectedSourceGeomsWithAnchorGeomsUnioned = unionTouchedJTSGeometries(jtsIntersectedSourceGeomsWithAnchorGeoms);
        List<Geometry> jtsSelectedGeomsUnioned = new ArrayList();
        for (Geometry geomSource : jtsIntersectedSourceGeomsWithAnchorGeomsUnioned) {
          for (Geometry geomAnchor : jtsAnchorGeomsOnlyUnioned) {
            Geometry resultGeom = geomAnchor.intersection(geomSource);
            if (!resultGeom.isEmpty()) {
              jtsSelectedGeomsUnioned.add(resultGeom);
            }
          }
        }
        GeometryFactory factory = jtsAnchorGeoms.get(0).getFactory();
        Geometry resultGeom = factory.buildGeometry(jtsSelectedGeomsUnioned);
        SharkGeometry sharkGeom = InMemoSharkGeometry.createGeomByWKT(resultGeom.toText());
        if (fragment == null) {
            fragment = InMemoSharkKB.createInMemoSpatialSTSet();
        }
        fragment.merge(InMemoSharkKB.createInMemoSpatialSemanticTag(sharkGeom));
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
                    } else {
                      i--;
                      break;
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
        boolean isCovered = true;
        List<Geometry> coveredGeomsA = new ArrayList<Geometry>();
        for (Geometry geomA : a) {
            for (Geometry geomB : b) {
                try {
                    if (geomA.coveredBy(geomB) && !coveredGeomsA.contains(geomA)) {
                        coveredGeomsA.add(geomA);
                    }
                } catch (IllegalArgumentException ex) {
                    throw new SharkKBException("Covering with GeometryCollection is not allowed.");
                }
            }
        }
        for (Geometry geomA : a) {
            if (!coveredGeomsA.contains(geomA)) {
                isCovered = false;
                break;
            }
        }
        return isCovered;
    }

    private List<Geometry> unionTouchedJTSGeometries(List<Geometry> geometries) throws SharkKBException {
        List filteredGeometries = new ArrayList();
        for (Geometry geom : geometries) {
            if (geom.getGeometryType().compareTo("MultiPolygon") == 0) {
              List<Geometry> dividedPolygons = divideGeometryCollection((GeometryCollection)geom);
              List<Geometry> dividedUnionedPolygons = unionTouchedAtomicJTSGeometries(dividedPolygons);
              GeometryFactory factory = geom.getFactory();
              geom = factory.buildGeometry(dividedUnionedPolygons);
            }
            filteredGeometries.add(geom);
        }
        return unionTouchedAtomicJTSGeometries(filteredGeometries);
    }
    
    private List<Geometry> unionTouchedAtomicJTSGeometries(List<Geometry> geometries) throws SharkKBException {
        List<Geometry> resultGeometries = new ArrayList();
        boolean wasTouched = false;
        while (geometries.size() > 0) {
            Geometry geomA = geometries.get(0);
            boolean geomAWasUnioned = false;
            for (Geometry geomB : geometries) {
                try {
                    if (!geomA.equals((Object) geomB)) {
                        if (geomA.touches(geomB)) {
                            resultGeometries.add(geomA.union(geomB));
                            geometries.remove(geomB);
                            wasTouched = true;
                            geomAWasUnioned = true;
                            break;
                        }
                    }
                } catch (IllegalArgumentException ex) {
                    throw new SharkKBException("Touch with GeometryCollection is not allowed.");
                }
            }
            if (!geomAWasUnioned) {
                resultGeometries.add(geomA);
            }
            geometries.remove(geomA);
        }
        if (wasTouched) {
            resultGeometries = unionTouchedJTSGeometries(resultGeometries);
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
                    if (geomA.intersects(geomB) && !tempIntersects.contains(geomA)) {
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
            boolean isCollection = (geom.getClass().getName().compareTo(geomCollClassName) == 0);
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
