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
public class TimeSemanticTag extends BaseSemanticTag{
    
    public static final String TIMES = "TIMES";
    
    private LinkedList<Time> times = null;

    public TimeSemanticTag(String name) {
        super(name);
        
        this.times = new LinkedList<>();
    }

    public LinkedList<Time> getTimes() {
        return times;
    }

    public void setTimes(LinkedList<Time> times) {
        this.times = times;
    }
    
    public void addTime(Time location){
        if(!this.times.contains(location))
            this.times.add(location);
    }
    
    public void removeTime(Time location){
        if(this.times.contains(location))
            this.times.remove(location);
    }
    
    public void clearLocations(){
        this.times = null;
    }    
}
