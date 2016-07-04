package net.sharkfw.kp;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

import java.util.ArrayList;

/**
 * Created by msc on 25.05.16.
 */
public class FilterKP extends KnowledgePort {

    private final ASIPInterest filter;
    private ArrayList<KPNotifier> notifiers;

    public FilterKP(SharkEngine se, ASIPInterest filter, KPNotifier notifier) {
        super(se);
        this.filter = filter;
        this.notifiers = new ArrayList<>();

        this.notifiers.add(notifier);
    }

    public FilterKP(SharkEngine engine, ASIPInterest filter){
        this(engine, filter, null);
    }

    public final void addNotifier(KPNotifier notifier){
        if(!this.notifiers.contains(notifier) && notifier != null){
            this.notifiers.add(notifier);
        }
    }

    public final void removeNotifier(KPNotifier notifier){
        if(this.notifiers.contains(notifier) && notifier != null){
            this.notifiers.remove(notifier);
        }
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {
        if(!this.notifiers.isEmpty()){
            for (KPNotifier notifier : this.notifiers){
                notifier.notifyKnowledgeReceived(asipKnowledge, asipConnection);
            }
        }
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {
        if(interest==null) {
            L.d("Interest should not be empty.", this);
            return;
        }

        if(SharkCSAlgebra.isIn(filter, interest)){

            if(!this.notifiers.isEmpty()){
                for (KPNotifier notifier : this.notifiers){
                    notifier.notifyInterestReceived(asipInterest, asipConnection);
                }
            }
        }
    }
}
