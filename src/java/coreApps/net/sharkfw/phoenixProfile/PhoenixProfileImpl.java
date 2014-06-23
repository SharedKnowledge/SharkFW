/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.phoenixProfile;

import net.sharkfw.genericProfile.GenericProfile;

/**
 *
 * @author s0539758
 */
public class PhoenixProfileImpl implements PhoenixProfile {
    private final GenericProfile genericProfile;

    public PhoenixProfileImpl(GenericProfile genericProfile) {
        this.genericProfile = genericProfile;
    }
    
    @Override
    public String getBusinessCard() {
        // hole die daten..
        // this.genericProfile.getInformation("businessCard");
        
        return "dummy Value";
    }
    
}
