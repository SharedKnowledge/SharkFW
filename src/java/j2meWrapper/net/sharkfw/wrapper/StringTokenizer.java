/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.wrapper;

import java.util.NoSuchElementException;

/**
 *
 * @author thsc
 */
public class StringTokenizer {
    private String s;
    private String d;
    
    private int first, lastPlus, len;
    private boolean more;
    private boolean lastToken;
            
    public StringTokenizer(String s, String d) {
        this.s = s;
        this.d = d;
        this.first = 0;
        this.lastToken = false;
        this.more = true;
        this.len = s.length();
        
        this.step();
    }
    
    private void step() {
        if(this.first >= this.len) {
            this.more = false;
            return;
        }
        
        this.lastPlus = this.s.indexOf(this.d , this.first);
        if(this.lastPlus == -1) {
            if(this.first == 0) {
                this.more = false;
            } else {
                this.lastToken = true;
            }
        } else {
            if(this.first == this.lastPlus) {
                // yet another delimiter directly behind the previous one
                this.first = this.first+d.length();
                step();
            }
        }
    }
    
   public boolean hasMoreTokens() {
        return this.more;
    }
    
    public String nextToken() {
        if(!this.more) {
            throw new NoSuchElementException();
        }
        
        String tmpS;
        
        if(this.lastToken) {
            tmpS = this.s.substring(this.first);
            this.more = false;
        } else {
            tmpS = this.s.substring(this.first, this.lastPlus);
            this.first = this.lastPlus + d.length();
            step();
        }
        
        return tmpS;
    }    
}
