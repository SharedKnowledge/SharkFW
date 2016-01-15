/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.wasp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

/**
 *
 * @author micha
 */
public class SemanticTagNet {
    
    enum Property{
        SOURCE,
        TARGET
    }
    
    private HashMap<Long, SemanticTag> tagList;
    private LinkedList<PropertyHolder> propertyList;
    private long tagId = 0;

    public SemanticTagNet() {
        this.tagList = new HashMap<>();
        this.propertyList = new LinkedList<>();
    }
    
    public void add(SemanticTag tag){
        if(!tagList.containsValue(tag)){
            tagList.put(tagId++, tag);
        }
    }
    
    public long getSemanticTagId(SemanticTag tag){
        if(tagList.containsValue(tag)){
            for(Entry<Long, SemanticTag> entry : tagList.entrySet()){
                if(entry.getValue() == tag){
                    return entry.getKey();
                }
            }
        }
        return -1;
    }
    
    public void addProperty(String propertyName, SemanticTag source, SemanticTag target) {
        long sourceId;
        long targetId;
        
        if(propertyName == null || source == null || target == null)
            return;
        
        if(!tagList.containsValue(source)){
            sourceId = tagId++;
            tagList.put(sourceId, source);
        } else {
            sourceId = getSemanticTagId(source);
        }
        
        if(!tagList.containsValue(target)){
            targetId = tagId++;
            tagList.put(targetId, target);
        } else {
            targetId = getSemanticTagId(target);
        }
        
        PropertyHolder property = new PropertyHolder(
                propertyName, 
                sourceId, 
                targetId);
        
        if(!propertyList.contains(property)){
            propertyList.add(property);
        }
    }
    
    public void removeProperty(String propertyName, SemanticTag source, SemanticTag target){

        if(propertyName == null || source == null || target == null)
            return;
        
        if(!tagList.containsValue(source) || !tagList.containsValue(target))
            return;
        
        long sourceId = getSemanticTagId(source);
        long targetId = getSemanticTagId(target);
        PropertyHolder property = new PropertyHolder(propertyName, sourceId, targetId);
        
        if(propertyList.contains(property)){
            propertyList.remove(property);
        }
    }
    
    public void removeSemanticTag(SemanticTag tag){
        
        if(!tagList.containsValue(tag))
            return;
        
        long id = getSemanticTagId(tag);
        
        LinkedList<PropertyHolder> containingProperties = getAllProperties(tag, tag);
        
        for(PropertyHolder property : propertyList){
            if(containingProperties.contains(property)){
                containingProperties.remove(property);
                propertyList.remove(property);
            }
        }
        tagList.remove(id);
    }
    
    public LinkedList<PropertyHolder> getAllProperties(SemanticTag source, SemanticTag target){
        LinkedList<PropertyHolder> list = new LinkedList<>();
        
        if(source == null && target == null)
            return null;
        
        list.addAll(getPropertiesAsPropertyEnum(Property.SOURCE, source));
        list.addAll(getPropertiesAsPropertyEnum(Property.TARGET, target));
        
        return list;
    }
    
    public LinkedList<PropertyHolder> getPropertiesAsPropertyEnum(Enum<Property> propertyType, SemanticTag tag){
        
        if(!tagList.containsValue(tag))
            return null;
        
        LinkedList<PropertyHolder> list = new LinkedList<>();
        
        long id = getSemanticTagId(tag);
        
        for(PropertyHolder property : propertyList){
            if(propertyType.equals(Property.SOURCE)){
                if(property.getSourceId() == id){
                    list.add(property);
                }
            } else if(propertyType.equals(Property.TARGET)){
                if(property.getTargetId() == id){
                    list.add(property);
                }
            }
        }
        return list;
    }
       
    
}
