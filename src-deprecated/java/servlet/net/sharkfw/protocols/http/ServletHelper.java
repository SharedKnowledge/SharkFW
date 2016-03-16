/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols.http;

/**
 *
 * @author desty
 */
public class ServletHelper {

    private static ServletHelper instance;
    private HTTPStreamStub stub = null;

    private ServletHelper() {
    }

    public static ServletHelper getInstance() {
        if(instance == null) {
            instance = new ServletHelper();
        }
        return instance;
    }

    public void setStub(HTTPStreamStub stub) {
        this.stub = stub;
    }

    public HTTPStreamStub getStub() {
        return this.stub;
    }
}
