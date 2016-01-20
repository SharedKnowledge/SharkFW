/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.wasp;

import java.util.LinkedList;

/**
 *
 * @author micha
 */
public class SpatialSemanticTag extends BaseSemanticTag {

    public static final String LOCATIONS = "LOCATIONS";
    
    private LinkedList<Location> locations = null;

    public SpatialSemanticTag(String name) {
        super(name);
        
        this.locations = new LinkedList<>();
    }

    public LinkedList<Location> getLocations() {
        return locations;
    }

    public void setLocations(LinkedList<Location> locations) {
        this.locations = locations;
    }
    
    public void addLocation(Location location){
        if(!this.locations.contains(location))
            this.locations.add(location);
    }
    
    public void removeLocation(Location location){
        if(this.locations.contains(location))
            this.locations.remove(location);
    }
    
    public void clearLocations(){
        this.locations = null;
    }    
}
