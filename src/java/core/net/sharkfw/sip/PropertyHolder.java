/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.sip;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author micha
 */
public class PropertyHolder {
    
    private String propertyName;
    private long sourceId;
    private long targetId;

    public PropertyHolder(String propertyName, long sourceId, long targetId) {
        this.propertyName = propertyName;
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public boolean equals(PropertyHolder object) {
        if(this.propertyName.equals(object.getPropertyName()) 
                && this.sourceId == object.getSourceId()
                && this.targetId == object.getTargetId()){
            return true;
        }
        return false;
    }
    
    
}
