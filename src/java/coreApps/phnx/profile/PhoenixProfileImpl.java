/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package phnx.profile;

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

    @Override
    public String getOrganization() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOrganization() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String setRoleInOrganization() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRoleInOrganization() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTravelDates() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
