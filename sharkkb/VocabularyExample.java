package sharkkb;

import net.sharkfw.knowledgeBase.SharkVocabulary;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

/**
 *
 * @author thsc
 */
public class VocabularyExample {
    public static void main(String args[]) {
        SharkVocabulary v = new InMemoSharkKB();
        
        Knowledge k = InMemoSharkKB.createInMemoKnowledge(v);
        ContextPoint cp = null; // should not be empty
        k.addContextPoint(cp);
        SharkVocabulary kVocabulary = k.getVocabulary();
        
        SharkCS asSharkCS = kVocabulary.asSharkCS();
    }
}
