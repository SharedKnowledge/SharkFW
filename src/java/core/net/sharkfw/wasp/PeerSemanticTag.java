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
public class PeerSemanticTag extends BaseSemanticTag {
    
    public static final String ADDRESSES = "ADDRESSES";
    
    private LinkedList<Address> addresses = null;

    public PeerSemanticTag(String name) {
        super(name);
        
        this.addresses = new LinkedList<>();
    }

    public LinkedList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(LinkedList<Address> addresses) {
        this.addresses = addresses;
    }
    
    public void addAddress(Address address){
        if(!this.addresses.contains(address))
            this.addresses.add(address);
    }
    
    public void removeAddress(Address address){
        if(this.addresses.contains(address))
            this.addresses.remove(address);
    }
    
    public void clearAddresses(){
        this.addresses = null;
    }

        
    
}
