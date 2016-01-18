/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.wasp;

import java.util.LinkedList;
import net.sharkfw.wasp.interfaces.SemanticTag;

/**
 *
 * @author micha
 */
public class Interest {
    
    private LinkedList<BaseSemanticTag> topics;
    private LinkedList<BaseSemanticTag> types;
    private LinkedList<PeerSemanticTag> approvers;
    private PeerSemanticTag sender;
    private LinkedList<PeerSemanticTag> recipients;
    private LinkedList<SpatialSemanticTag> locations;
    private LinkedList<TimeSemanticTag> times;
    private Direction direction;

    public Interest(LinkedList<BaseSemanticTag> topics, LinkedList<BaseSemanticTag> types, LinkedList<PeerSemanticTag> approvers, PeerSemanticTag sender, LinkedList<PeerSemanticTag> recipients, LinkedList<SpatialSemanticTag> locations, LinkedList<TimeSemanticTag> times, Direction direction) {
        this.topics = topics;
        this.types = types;
        this.approvers = approvers;
        this.sender = sender;
        this.recipients = recipients;
        this.locations = locations;
        this.times = times;
        this.direction = direction;
    }

    public Interest() {
    }

    public LinkedList<BaseSemanticTag> getTopics() {
        return topics;
    }

    public void setTopics(LinkedList<BaseSemanticTag> topics) {
        this.topics = topics;
    }

    public LinkedList<BaseSemanticTag> getTypes() {
        return types;
    }

    public void setTypes(LinkedList<BaseSemanticTag> types) {
        this.types = types;
    }    

    public LinkedList<PeerSemanticTag> getApprovers() {
        return approvers;
    }

    public void setApprovers(LinkedList<PeerSemanticTag> approvers) {
        this.approvers = approvers;
    }

    public PeerSemanticTag getSender() {
        return sender;
    }

    public void setSender(PeerSemanticTag sender) {
        this.sender = sender;
    }

    public LinkedList<PeerSemanticTag> getRecipients() {
        return recipients;
    }

    public void setRecipients(LinkedList<PeerSemanticTag> recipients) {
        this.recipients = recipients;
    }

    public LinkedList<SpatialSemanticTag> getLocations() {
        return locations;
    }

    public void setLocations(LinkedList<SpatialSemanticTag> locations) {
        this.locations = locations;
    }

    public LinkedList<TimeSemanticTag> getTimes() {
        return times;
    }

    public void setTimes(LinkedList<TimeSemanticTag> times) {
        this.times = times;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    public void addSemanticTagToDim(SemanticTag tag, Dimension dim ){
        switch(dim){
            case TOPIC:
                if(!this.topics.contains(tag))
                    this.topics.add((BaseSemanticTag) tag);
                break;
            case TYPE:
                if(!this.types.contains(tag))
                    this.types.add((BaseSemanticTag) tag);
                break;
            case APPROVER:
                if(!this.approvers.contains(tag))
                    this.approvers.add((PeerSemanticTag) tag);
                break;
            case SENDER:
                if(!this.sender.equals(tag))
                    this.sender = (PeerSemanticTag) tag;
                break;
            case RECIPIENT:
                if(!this.recipients.contains(tag))
                    this.recipients.add((PeerSemanticTag) tag);
                break;
            case LOCATION:
                if(!this.locations.contains(tag))
                    this.locations.add((SpatialSemanticTag) tag);
                break;
            case TIME:
                if(!this.times.contains(tag))
                    this.times.add((TimeSemanticTag) tag);
                break;
        }
    }
    
    public void removeSemanticTagFromDim(SemanticTag tag, Dimension dim){
        switch(dim){
            case TOPIC:
                if(this.topics.contains(tag))
                    this.topics.remove((BaseSemanticTag) tag);
                break;
            case TYPE:
                if(this.types.contains(tag))
                    this.types.remove((BaseSemanticTag) tag);
                break;
            case APPROVER:
                if(this.approvers.contains(tag))
                    this.approvers.remove((PeerSemanticTag) tag);
                break;
            case SENDER:
                if(this.sender.equals(tag))
                    this.sender = null;
                break;
            case RECIPIENT:
                if(this.recipients.contains(tag))
                    this.recipients.remove((PeerSemanticTag) tag);
                break;
            case LOCATION:
                if(this.locations.contains(tag))
                    this.locations.remove((SpatialSemanticTag) tag);
                break;
            case TIME:
                if(this.times.contains(tag))
                    this.times.remove((TimeSemanticTag) tag);
                break;
        }
    }
    
    public void clearDimension(Dimension dim){
        switch(dim){
            case TOPIC:
                this.topics = null;
                break;
            case TYPE:
                this.types = null;
                break;
            case APPROVER:
                this.approvers = null;
                break;
            case SENDER:
                this.sender = null;
                break;
            case RECIPIENT:
                this.recipients = null;
                break;
            case LOCATION:
                this.locations = null;
                break;
            case TIME:
                this.times = null;
                break;
            case DIRECTION:
                this.direction = null;
                break;
        }
    }
}

