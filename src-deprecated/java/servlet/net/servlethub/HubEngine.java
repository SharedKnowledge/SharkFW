/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.servlethub;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.platform.J2SESharkEngine;

/**
 *
 * @author Jacob Zschunke
 */
public class HubEngine extends J2SESharkEngine {

    public HubEngine(SharkKB kb) {
        super(kb);
    }

    public void createHTTPServerMessageStub(HttpServletRequest request, HttpServletResponse response) {
        new HTTPServerMessageStub(request, response, kepStub);
    }
    
}
