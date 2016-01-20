/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.wasp;

/**
 *
 * @author micha
 */
public class Address {

    public final static String URI = "URI";
    
    private String uri = "";
    
    public enum Endpoint{
        TCP,
        HTTP,
        MAIL
    }

    public Address(String uri) {
        this.uri = uri;
    }
    
    
}
