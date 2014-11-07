/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.geom.jts;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;

/**
 *
 * @author s0542709
 */
public class SpatialAlgebra
{
  public static SpatialSTSet fragment(SpatialSTSet fragment, 
                                      SpatialSTSet source,
                                      SemanticTag anchor)
  {
    return null;
  }
  
  public static boolean identical(SpatialSemanticTag a, SpatialSemanticTag b)
  {
    return false;
  }
  
  public static boolean identical(SpatialSTSet a, SpatialSTSet b)
  {
    return false;
  }
  
  public static boolean isIn(SpatialSTSet a, SpatialSTSet b)
  {
    return false;
  }
  
  public static boolean isIn(SpatialSTSet a, SpatialSemanticTag b)
  {
    return false;
  }  
}
