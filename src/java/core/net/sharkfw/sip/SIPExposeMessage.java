/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.sip;

import net.sharkfw.knowledgeBase.SharkCS;

/**
 *
 * @author micha
 */
public class SIPExposeMessage {
    
    private SharkCS interest;
    private SIPHeader header;
    private String serializedOutput;

    public SIPExposeMessage(SIPHeader header, SharkCS interest) {
        this.interest = interest;
        this.header = header;
//        this. serializedOutput = SIPSerializer.serializeExposeAsString(header, interest);
    }

    public SharkCS getInterest() {
        return interest;
    }

    public SIPHeader getHeader() {
        return header;
    }

    public String getSerializedOutput() {
        return serializedOutput;
    }
    
    
    
    
}
