package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.peer.ASIPPort;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.SharkEngine;
import org.junit.Test;

import java.util.Iterator;

/**
 * Created by j4rvis on 14.09.16.
 */
public class SyncModuleTest {

    @Test
    public void UsageTest(){

        SharkEngine se = new J2SEAndroidSharkEngine(SharkKB storage);

        SharkKB storage = se.getStorage();

        // Later

        se.persistPort(ASIPPort port);
        // gib mir klassenNamen
        // gib mir deine Memento
        // speichere beides in der Internen KB
        // REACTIVATE
        // klassenObject erzeugen an Hand des KlassenNamens
        // kann scheitern dann pech
        // Constructor(Engine, Memento)
        // Aufruf Constructor

        se.removePersistedPort(ASIPPort port);

        Iterator<ASIPPort> ports = se.getPersistedPorts();

        se.reactivatePersistedPorts();

        SharkKB kb = se.getKB(String id, Class className);

        // KnowledgeBase

        String id = kb.getId() // Mit KlassenNamen

        // ASIPPort

        ASIPPort port = new ASIPPort(SharkEngine engine, ASIPMemento memento);

        port.setMemento(ASIPMemento memento);
        ASIPMemento memento = port.getMemento();

        // ASIPMemento

        // Alle Parameter bis auf Engine und SharkKB als Id
        memento.getSerializedString();

        // END Later

        // SyncManager

        SyncManager sm = se.getSyncManager();
        sm.allowInvitation(boolean true); // toggle Invitation

        Iterator<SyncComponents> iterator = sm.getSyncComponents(boolean invitedOnly);

        sm.remove(SyncComponent component);

        SyncComponent sc = sm.createSyncCommponent(
                SemanticTag uniqueName,
                PeerSTSet member,
                PeerSemanticTag owner,
                SharkKB kb,
                boolean writable
        );

        // Listener Interface

        sm.addInviteListener(SyncInviteListener listener);

        listener.onInvitation(SyncComponent component);

        // end listener

        // SyncComponent

        sc.addMember(PeerSemanticTag member);
        sc.removeMember(PeerSemanticTag member);



    }
}