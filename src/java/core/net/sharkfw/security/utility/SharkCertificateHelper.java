package net.sharkfw.security.utility;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ac
 * Methods used in SharkPkiStorage and SharkPkiKP for handling the incomming knowledge
 */
public class SharkCertificateHelper {

    private static final String LINKED_LIST_SEPARATOR_NAME = "<name>";
    private static final String LINKED_LIST_SEPARATOR_SIS = "<sis>";
    private static final String LINKED_LIST_SEPARATOR_ADR = "<adr>";
    private static final String LINKED_LIST_SEPARATOR_END = "<end>";

    /**
     * Converts a LinkedList with PeerSemanticTags into a byte array to store them as information.
     * The LinkedList is preferred to a PeerSTSet because of the ability to remove easily elements
     * from the list.
     * @param transmitterList
     * @return
     */
    public static byte[] getByteArrayFromLinkedList(LinkedList<PeerSemanticTag> transmitterList) {

        StringBuilder s = new StringBuilder();

        for (PeerSemanticTag p : transmitterList) {
            s.append(LINKED_LIST_SEPARATOR_NAME);
            s.append(p.getName());
            s.append(LINKED_LIST_SEPARATOR_SIS);
            for (int i = 0; i < p.getSI().length; i++) {
                s.append(p.getSI()[i]);
                if (i < p.getSI().length - 1) {
                    s.append(",");
                }
            }

            s.append(LINKED_LIST_SEPARATOR_ADR);
            for (int i = 0; i < p.getAddresses().length; i++) {
                s.append(p.getAddresses()[i]);
                if (i < p.getAddresses().length - 1) {
                    s.append(",");
                }
            }

            s.append(LINKED_LIST_SEPARATOR_END);
        }

        return String.valueOf(s).getBytes();
    }

    /**
     * Converts a valid byte array to a LinkeList of PeerSemanticTags. The advantages of the LinkedList
     * are explain in the method getByteArrayFromLinkedList.
     * @param transmitterList
     * @return
     */
    public static LinkedList<PeerSemanticTag> getLinkedListFromByteArray(byte[] transmitterList) {

        LinkedList<PeerSemanticTag> linkedList = new LinkedList<>();
        String listAsString = new String(transmitterList);

        List<String> listOfNames = extractStringByRegEx(listAsString, "(?<=" + LINKED_LIST_SEPARATOR_NAME + ")(.*?)(?=" + LINKED_LIST_SEPARATOR_SIS + ")");
        List<String> listOfSis = extractStringByRegEx(listAsString, "(?<=" + LINKED_LIST_SEPARATOR_SIS + ")(.*?)(?=" + LINKED_LIST_SEPARATOR_ADR + ")");
        List<String> listOfAdr = extractStringByRegEx(listAsString, "(?<=" + LINKED_LIST_SEPARATOR_ADR + ")(.*?)(?=" + LINKED_LIST_SEPARATOR_END + ")");

        for (int i = 0; i < listOfNames.size(); i++) {
            PeerSemanticTag peerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag(listOfNames.get(i), listOfSis.get(i).split(","), listOfAdr.get(i).split(","));
            linkedList.add(peerSemanticTag);
        }

        return linkedList;
    }

    /**
     * Simplification for the regex matching.
     * @param text
     * @param expression
     * @return
     */
    private static List<String> extractStringByRegEx(String text, String expression) {
        List<String> matchList = new ArrayList<>();
        Matcher matcher = Pattern.compile(expression).matcher(text);

        while (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                matchList.add(matcher.group(i));
            }
        }

        return matchList;
    }
}
