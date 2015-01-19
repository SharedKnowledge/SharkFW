package net.sharkfw.knowledgeBase.geom.jts;

import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryCollectionIterator;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

/**
 *
 * @author s0542709, s0542541
 * @version 1.0
 *
 *
 */
public class SpatialAlgebra {

  /**
   * Checks with JTS-Library if Well-known text is valid.
   *
   * @param wkt
   * @throws SharkKBException
   */
  public static void checkWKTwithJTS(String wkt) throws SharkKBException {
    Geometry jtsGeometry = null;
    WKTReader reader = new WKTReader();
    try {
      jtsGeometry = reader.read(wkt);
    } catch (ParseException ex) {
      throw new SharkKBException("WKT parsing problem");
    }
    IsValidOp validOp = new IsValidOp(jtsGeometry);
    if (!validOp.isValid()) {
      throw new SharkKBException("WKT is not valid");
    }
  }

  /**
   * Checks with JTS-Library if two Shark-SpatialSemanticTags are identical.
   *
   * @param a
   * @param b
   * @return true if both SpatialSemanticTags identical
   * @throws SharkKBException
   */
  public static boolean identical(SpatialSemanticTag a, SpatialSemanticTag b) throws SharkKBException {
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
  public static boolean identical(SpatialSTSet a, SpatialSemanticTag b) throws SharkKBException {
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
  public static boolean identical(SpatialSTSet a, SpatialSTSet b) throws SharkKBException {
    List<Geometry> jtsGeomsA = getListWithJTSGeometries(a);
    List<Geometry> jtsGeomsB = getListWithJTSGeometries(b);
    // Geometry with/as GeometryCollection does not work
    List<Geometry> jtsGeomsOnlyA = divideExistingGeometryCollectionsInList(jtsGeomsA);
    List<Geometry> jtsGeomsOnlyB = divideExistingGeometryCollectionsInList(jtsGeomsB);    
    List<Geometry> jtsIntersectedGeoms = getIntersectsFromListsWithJTSGeommetries(jtsGeomsOnlyA, jtsGeomsOnlyB);
    return isListDifferent(jtsIntersectedGeoms, jtsGeomsOnlyB);
  }

  /**
   * Checks if a SpatialSemanticTag is in another SpatialSemanticTag.
   *
   * @param a
   * @param b
   * @return
   * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public static boolean isIn(SpatialSemanticTag a, SpatialSemanticTag b) throws SharkKBException {
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
  public static boolean isIn(SpatialSTSet a, SpatialSemanticTag b) throws SharkKBException {
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
  public static boolean isIn(SpatialSTSet a, SpatialSTSet b) throws SharkKBException {
    List<Geometry> jtsGeomsA = getListWithJTSGeometries(a);
    List<Geometry> jtsGeomsB = getListWithJTSGeometries(b);
    // Geometry with/as GeometryCollection does not work
    List<Geometry> jtsGeomsOnlyA = divideExistingGeometryCollectionsInList(jtsGeomsA);
    List<Geometry> jtsGeomsOnlyB = divideExistingGeometryCollectionsInList(jtsGeomsB);
    List<Geometry> jtsIntersectedGeoms = getIntersectsFromListsWithJTSGeommetries(jtsGeomsOnlyA, jtsGeomsOnlyB);
    return isListWithGeometriesCovered(jtsIntersectedGeoms, jtsGeomsOnlyB);
  }

  /**
   *
   * @param fragment
   * @param source
   * @param anchor
   * @return
   */
  public static SpatialSTSet fragment(SpatialSTSet fragment, SpatialSTSet source, SemanticTag anchor) {
    /*
     todo!
     */
    return null;
  }
  
  private static boolean isListDifferent(List<Geometry> a, List<Geometry> b) throws SharkKBException {
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
  
  private static boolean isListWithGeometriesCovered(List<Geometry> a, List<Geometry> b) throws SharkKBException {
    boolean isCovered = false;
    for (Geometry geomA : a) {
      for (Geometry geomB : b) {
        try {
          if (geomA.covers(geomB))
            isCovered = true;
          else 
          {
            isCovered = false;
            break;
          }
        } catch (IllegalArgumentException ex) {
          throw new SharkKBException("Covering with GeometryCollection is not allowed.");
        }
      }
      if (!isCovered)
        break;
    }
    return isCovered;
  }  
  
  private static List<Geometry> getIntersectsFromListsWithJTSGeommetries(List<Geometry> a, List<Geometry> b) throws SharkKBException {
    List<Geometry> tempIntersects = new ArrayList();
      for (Geometry geomA : a) {
        for (Geometry geomB : b) {
          try {
            if (geomA.intersects(geomB))
              tempIntersects.add(geomA);
          } catch (IllegalArgumentException ex) {
            throw new SharkKBException("Intersection with GeometryCollection is not allowed.");
          }
        }
      }

    return tempIntersects;
  }
  
  private static List<Geometry> divideExistingGeometryCollectionsInList(List<Geometry> geometries) throws SharkKBException
  {
    List<Geometry> geomsWithoutGeometryCollection = new ArrayList();
    for(Geometry geom : geometries) {
      if (geom instanceof GeometryCollection) {
        List<Geometry> dividedGeometries = divideGeometryCollection((GeometryCollection) geom);
        geomsWithoutGeometryCollection.addAll(dividedGeometries);
      } else {
        geomsWithoutGeometryCollection.add(geom);
      }      
    }
    return geomsWithoutGeometryCollection;
  }
  
  private static List<Geometry> divideGeometryCollection(GeometryCollection geomCollection) throws SharkKBException {
    List<Geometry> tempGeomList = new ArrayList();
    GeometryCollectionIterator iterator = new GeometryCollectionIterator(geomCollection);
    while (iterator.hasNext())
      tempGeomList.add((Geometry)iterator.next());
    return tempGeomList;
  }
  
  private static List getListWithJTSGeometries(SpatialSTSet spatialSTSet) throws SharkKBException {
    List<String> wktGeometries = extractWKTGeometries(spatialSTSet);
    return convertToJTSGeometries(wktGeometries);
  }
  
  private static List<String> extractWKTGeometries(SpatialSTSet spatialSTSet) throws SharkKBException {
    List<String> wktGeometries = new ArrayList();
    Enumeration<SpatialSemanticTag> spatialSTEnum = spatialSTSet.spatialTags();
    while (spatialSTEnum.hasMoreElements())
      wktGeometries.add(spatialSTEnum.nextElement().getGeometry().getWKT());
    return wktGeometries;
  }
  
  private static List<Geometry> convertToJTSGeometries(List<String> wktGeometries) throws SharkKBException {
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
