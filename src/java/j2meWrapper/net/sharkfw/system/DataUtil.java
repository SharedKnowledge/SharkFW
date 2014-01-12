package net.sharkfw.system;



import net.sharkfw.knowledgeBase.DataSharkKB;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.sharkfw.wrapper.StringTokenizer;

/**
 *
 * @author RW
 */
public class DataUtil {

    public static boolean sameEntity(String[] si_a, String[] si_b) {

        if (si_a == null && si_b == null) {
            return true;
        }

        if (si_a == null && si_b != null) {
            return false;
        }

        if (si_a != null && si_b == null) {
            return false;
        }

        //Temp solution!! not very nice....
        if (DataUtil.sameDataEntity(si_a, si_b)) {
            return true;
        }
        /*(if (DataUtil.dataEntityContains(si_a[0], si_b[0])) {
        System.out.println("Same");
        return true;
        }*/

        for (int a = 0; a < si_a.length; a++) {
            for (int b = 0; b < si_b.length; b++) {
                if (si_a[a].equalsIgnoreCase(si_b[b])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Compares DataSemanticTags with URI parameters.
     * Simply removing the parameters string (whatever comes after the "?") will not work,
     * because some URIs use query strings (like YouTube links for example).
     * If we remove the query string and compare, they will always be the same.
     * This removes the 2 query keys offset and length and checks if the URL is still the same.
     * @param si_a SI of the first Tag
     * @param si_b SI of the 2nd tag
     * @return the same or not...
     */
    public static boolean sameDataEntity(String[] si_a, String[] si_b) {

        if (si_a == null && si_b != null) {
            return false;
        }

        if (si_a != null && si_b == null) {
            return false;
        }

        for (int a = 0; a < si_a.length; a++) {
            for (int b = 0; b < si_b.length; b++) {
                if (compareURIsWithParams(si_a[a], si_b[b])) {
                    return true;
                }
            }
        }
        return false;
        //}
    }

    /*public static boolean dataEntityContains(String si_a, String si_b) {

        if (si_a == null && si_b != null) {
            return false;
        }

        if (si_a != null && si_b == null) {
            return false;
        }

        //Both have to be dataURIs
        if (!isDataURI(si_b) || !isDataURI(si_a)) {
            return false;
        }

        //They need to be the same URI eventually...
        if (!compareURIsWithParams(si_a, si_b)) {
            return false;
        }

        //Does A (offset and length) contain B's offset and length?
        int offsetA = getOffsetFromURI(si_a);
        int lengthA = getLengthFromURI(si_a);
        int offsetB = getOffsetFromURI(si_b);
        int lengthB = getLengthFromURI(si_b);
        //WHICH ORDER?
        if (offsetA + lengthA < offsetB + lengthB + DataSharkEngine.MAXSEGMENTSIZE) {
            return true;
        }

        return false;
    }*/

    public static int getOffsetFromURI(String uri) {
        Hashtable urlQuery = parseQueryFromUri(uri);
        if (!urlQuery.containsKey(DataSharkKB.OFFSETURLPARAM)) {
            return -1;
        }
        Vector offsetVec = (Vector) urlQuery.get(DataSharkKB.OFFSETURLPARAM);
        Integer returnint = Integer.valueOf((String) offsetVec.elementAt(0));
        return returnint.intValue();
    }

    public static int getLengthFromURI(String uri) {
        Hashtable urlQuery = parseQueryFromUri(uri);
        if (!urlQuery.containsKey(DataSharkKB.LENGTHURLPARAM)) {
            return -1;
        }
        Vector offsetVec = (Vector) urlQuery.get(DataSharkKB.LENGTHURLPARAM);
        Integer returnint = Integer.valueOf((String) offsetVec.elementAt(0));
        return returnint.intValue();
    }

    public static String stripOffsetLengthFromURI(String uri) {
        /* I assume that if there is offset, there is length.
         * I also assume - for now, that offset comes before length, since
         * this is the way it is created in InMemoDataSTSet.
         * Also - they are at the end of the URI!
         * 
         * This should be implemented better to handle more dynamiclly the URI stripping.
         * Using the Hashtable will not work - it will not come back in the right order.
         * Hashtable has a tendency of saving everything backwards...
         */

        int offsetPosition = uri.indexOf(DataSharkKB.OFFSETURLPARAM);
        int lengthPosition = uri.indexOf(DataSharkKB.LENGTHURLPARAM);
        if (offsetPosition < 0 || lengthPosition < 0) {
            return uri;
        }
        String strippedUri = "";
        //String tempAfter = "";

        strippedUri = uri.substring(0, offsetPosition - 1);
        return strippedUri;
    }
    //Not J2ME complient....

    private static boolean compareURIsWithParams(String a, String b) {
        Hashtable aQuery = parseQueryFromUri(a);
        Hashtable bQuery = parseQueryFromUri(b);
        StringTokenizer stA = new StringTokenizer(a, "?");
        String aURI = stA.nextToken();
        StringTokenizer stB = new StringTokenizer(b, "?");
        String bURI = stB.nextToken();
        // = a.split("\\?")[0];
        // b.split("\\?")[0];
        //Are the URIs (without the query) the same?
        if (!aURI.equalsIgnoreCase(bURI)) {
            return false;
        }
        //boolean same = true;
        Enumeration aKeys = aQuery.keys();
        Enumeration bKeys = bQuery.keys();

        //Entire process with the a URI keys, skipping the offset and length tags.
        while (aKeys.hasMoreElements()) {
            String aKey = (String) aKeys.nextElement();
            if (aKey.equals(DataSharkKB.LENGTHURLPARAM) || aKey.equals(DataSharkKB.OFFSETURLPARAM)) {
                continue;
            }
            if (!bQuery.containsKey(aKey)) {
                return false;
            }
            Vector aValues = (Vector) aQuery.get(aKey);
            Vector bValues = (Vector) bQuery.get(aKey);
            if (aValues.size() != bValues.size()) {
                return false;
            }
            for (int i = 0; i < aValues.size(); i++) {
                if (!aValues.elementAt(i).equals(bValues.elementAt(i))) {
                    return false;
                }
            }
        }
        //Same process with the b URI keys
        while (bKeys.hasMoreElements()) {
            String bKey = (String) bKeys.nextElement();
            if (bKey.equals(DataSharkKB.LENGTHURLPARAM) || bKey.equals(DataSharkKB.OFFSETURLPARAM)) {
                continue;
            }
            if (!aQuery.containsKey(bKey)) {
                return false;
            }
            Vector aValues = (Vector) aQuery.get(bKey);
            Vector bValues = (Vector) bQuery.get(bKey);
            if (aValues.size() != bValues.size()) {
                return false;
            }
            for (int i = 0; i < aValues.size(); i++) {
                if (!aValues.elementAt(i).equals(bValues.elementAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    //NOT J2ME compatible.
    private static Hashtable parseQueryFromUri(String uri) {
        Hashtable queryMap = new Hashtable();
        String query = null;
        /*try {
        query = uri.split("\\?")[1];
        } catch (ArrayIndexOutOfBoundsException npx) {
        //No query! returning empty hashtable.
        return queryMap;
        }*/
        StringTokenizer st = new StringTokenizer(uri, "?");
        String uriOnly = st.nextToken();
        if (!st.hasMoreTokens()) {
            //No query, returning empty hashtable
            return queryMap;
        }
        query = st.nextToken();
        //String[] parameters = query.split("\\&");

        StringTokenizer querySt = new StringTokenizer(query, "&");

        //for (String param : parameters)
        while (querySt.hasMoreTokens()) {
            String param = querySt.nextToken();
            //In case of an empty String
            if (param.length() == 0) {
                continue;
            }
            Vector tempVec = null;
            //String[] keyValue = param.split("=");
            int locationOfEq = param.indexOf("=");
            String type = null;
            String value = null;
            if (locationOfEq > 0) {
                type = param.substring(0, locationOfEq); //keyValue[0];
                value = param.substring(locationOfEq + 1);//(keyValue.length > 1) ? keyValue[1] : "";
            } else {
                type = param;
                value = "";
            }
            if (queryMap.containsKey(type)) {
                tempVec = (Vector) queryMap.get(type);
                tempVec.addElement(value);
            } else {
                tempVec = new Vector();
                tempVec.addElement(value);
                queryMap.put(type, tempVec);
            }
        }
        return queryMap;
    }

    public static boolean isDataURI(String uri) {
        if (getOffsetFromURI(uri) >= 0) {
            return true;
        }
        return false;
    }
}
