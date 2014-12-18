/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sync;

/**
 *
 * @author s0539501
 */
public interface SyncKnowledgeBaseListener {
    public void syncCPChanged(SyncContextPoint p);
}
