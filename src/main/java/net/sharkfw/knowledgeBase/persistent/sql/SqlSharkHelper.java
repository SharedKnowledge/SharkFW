package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.L;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static net.sharkfw.asip.ASIPSpace.DIM_APPROVERS;
import static net.sharkfw.asip.ASIPSpace.DIM_LOCATION;
import static net.sharkfw.asip.ASIPSpace.DIM_SENDER;
import static net.sharkfw.asip.ASIPSpace.DIM_TIME;

/**
 * Created by j4rvis on 5/31/17.
 */
public class SqlSharkHelper {

    // TABLE
    public final static String TABLE_INFORMATION = " information";
    public final static String TABLE_TAG_SET = " tag_set";
    public final static String TABLE_SEMANTIC_TAG = " semantic_tag";
    public final static String TABLE_ADDRESS = " address";
    public final static String TABLE_SUBJECT_IDENTIFIER = " subject_identifier";
    public final static String TABLE_RELATION = " relation";
    public final static String TABLE_KNOWLEDGE_BASE = " knowledge_base";

    // FIELDS
    public final static String FIELD_ID = " id";
    public final static String FIELD_SUBJECT_IDENTIFIER_IDENTIFIER = " identifier";
    public final static String FIELD_TAG_ID = " tag_id";
    public final static String FIELD_INFO_ID = " info_id";
    public final static String FIELD_SET_KIND = " set_kind";
    public final static String FIELD_DIRECTION = " direction";
    public final static String FIELD_NAME = " name";
    public final static String FIELD_SYSTEM_PROPERTY = " system_property";
    public final static String FIELD_PROPERTY = " property";
    public final static String FIELD_TAG_KIND = " tag_kind";
    public final static String FIELD_WKT = " wkt";
    public final static String FIELD_TIME_DURATION = " t_duration";
    public final static String FIELD_TIME_START = " t_start";
    public final static String FIELD_ADDRESS_NAME = " address_name";
    public final static String FIELD_SOURCE_TAG_ID = " source_tag_id";
    public final static String FIELD_TARGET_TAG_ID = " target_tag_id";
    public final static String FIELD_CONTENT_STREAM = " content_stream";
    public final static String FIELD_CONTENT_TYPE = " content_type";
    public final static String FIELD_CONTENT_LENGTH = " content_length";
    public final static String FIELD_OWNER_TAG = " owner_tag";
    public final static String FIELD_FOREIGN_KEYS = " foreign_keys";

    // METHODS
    public final static String INSERTINTO = " INSERT INTO";
    public final static String DELETE = " DELETE";
    public final static String UPDATE = " UPDATE";
    public final static String PRAGMA = " PRAGMA";
    public final static String JOIN = " JOIN";
    public final static String SELECT = " SELECT";
    public final static String FROM = " FROM";
    public final static String WHERE = " WHERE";
    public final static String ON = " ON";
    public final static String EQ = " =";
    public final static String BO = "(";
    public final static String BC = ")";
    public final static String QU = "\"";
    public final static String ALL = " *";
    public final static String DOT = ".";
    public final static String OR = " OR";
    public final static String AND = " AND";
    public final static String VALUES = " VALUES";
    public final static String SET = " SET";
    public final static String DISTINCT = " DISTINCT";
    public final static String COUNT = " COUNT";
    public final static String AS = " AS";
    public final static String HAVING = " HAVING";
    public final static String GROUPBY = " GROUP BY";


    static SqlSemanticTag getSemanticTag(SqlSharkKB sharkKB, SemanticTag semanticTag) throws SharkKBException {
        if (semanticTag==null) throw new SharkKBException("No SemanticTag given.");
        return new SqlSemanticTag(semanticTag.getSI()[0], sharkKB);
    }
    static SqlPeerSemanticTag getPeerSemanticTag(SqlSharkKB sharkKB, SemanticTag semanticTag) throws SharkKBException {
        if (semanticTag==null) throw new SharkKBException("No SemanticTag given.");
        return new SqlPeerSemanticTag(semanticTag.getSI()[0], sharkKB);
    }

    static SqlSemanticTag createSemanticTag(SqlSharkKB sharkKB, SemanticTag semanticTag) {
        try{
            return getSemanticTag(sharkKB, semanticTag);
        } catch (SharkKBException e) {
            try {
                return new SqlSemanticTag(semanticTag.getSI(), semanticTag.getName(), sharkKB);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    static SqlPeerSemanticTag createPeerSemanticTag(SqlSharkKB sharkKB, PeerSemanticTag semanticTag) {
        try{
            return getPeerSemanticTag(sharkKB, semanticTag);
        } catch (SharkKBException e) {
            try {
                return new SqlPeerSemanticTag(semanticTag.getSI(), semanticTag.getName(), sharkKB, semanticTag.getAddresses());
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    static Connection createConnection(SqlSharkKB sharkKB) throws SQLException {
        try {
            Class.forName(sharkKB.getDialect());
            return DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e){
            e.printStackTrace();
            throw new SQLException(e.getCause());
        }
        return null;
    }

    static SqlAsipInformation addInformation(SqlSharkKB sharkKB, ASIPSpace space, ASIPInformation information) throws SQLException, SharkKBException {
        Connection connection = createConnection(sharkKB);
        List<TagContainer> containerList = getTags(sharkKB, space, true);
        SqlAsipInformation sqlAsipInformation = new SqlAsipInformation(information, space, sharkKB);
        String insertSet = prepareSqlInsertSet(connection, containerList, space, Collections.singletonList(sqlAsipInformation));
        return executeInsertSuccess(connection, insertSet) ? sqlAsipInformation : null;
    }

    private static String prepareSqlInsertSet(Connection connection, List<TagContainer> containerList, ASIPSpace asipSpace, List<SqlAsipInformation> informationList){

        String sql = INSERTINTO+TABLE_TAG_SET+BO+FIELD_SET_KIND+","+FIELD_INFO_ID+","+FIELD_TAG_ID+","+FIELD_DIRECTION+BC+VALUES;

        for (SqlAsipInformation sqlAsipInformation : informationList) {
            for (TagContainer tagContainer : containerList) {
                sql=sql+BO+tagContainer.setKind+","+sqlAsipInformation.getId()+","+tagContainer.id+","+asipSpace.getDirection()+BC+",";
            }
            sql = sql.substring(0, sql.length()-1);
        }
        return sql;
    }

    static SqlAsipInformation getInformation(SqlSharkKB sharkKB, ASIPSpace space, ASIPInformation information){
        try {
            List<SqlAsipInformation> informationList = SqlSharkHelper.getInformation(sharkKB, space, false);
            for (SqlAsipInformation sqlAsipInformation : informationList) {
                if(sqlAsipInformation.getName().equals(information.getName())){
                    return sqlAsipInformation;
                }
            }
        } catch (SharkKBException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    static List<SqlAsipInformation> getInformation(SqlSharkKB sharkKB, ASIPSpace space, boolean create) throws SharkKBException, SQLException {

        Connection connection = createConnection(sharkKB);

        List<TagContainer> containerList = getTags(sharkKB, space, create);

        String sqlStatement = prepareSqlStatement(connection, containerList, space.getDirection());

        L.d(sqlStatement, sqlStatement);

        List<Integer> informationIds = getInformationIds(connection, sqlStatement);

        List<SqlAsipInformation> informationList = new ArrayList<>();
        for (Integer id : informationIds) {
            informationList.add(new SqlAsipInformation(id, space, sharkKB));
        }

        return informationList;
    }

    public static void removeInformation(SqlSharkKB sharkKB, ASIPSpace space, ASIPInformation asipInformation) throws SharkKBException {
        try {
            Connection connection = SqlSharkHelper.createConnection(sharkKB);
            String deleteInformation = DELETE+FROM+TABLE_INFORMATION;
            String deleteTagSet = DELETE+FROM+TABLE_TAG_SET;

            String chainedTagIds = "";
            String chainedIds = "";

            List<SqlAsipInformation> informationList = new ArrayList<>();

            if(asipInformation==null){
                informationList.addAll(SqlSharkHelper.getInformation(sharkKB, space, false));
            } else {
                informationList.add(SqlSharkHelper.getInformation(sharkKB, space, asipInformation));
            }

            for (SqlAsipInformation sqlAsipInformation : informationList) {
                String infoId = FIELD_INFO_ID+EQ+sqlAsipInformation.getId();
                String id = FIELD_ID+EQ+sqlAsipInformation.getId();

                if(chainedTagIds.isEmpty()) chainedTagIds=infoId;
                else chainedTagIds = chainedTagIds+OR+infoId;
                if(chainedIds.isEmpty()) chainedIds=id;
                else chainedIds = chainedIds+OR+id;
            }

            if(!chainedTagIds.isEmpty()){
                try{
                    SqlHelper.executeSQLCommand(connection, deleteTagSet+WHERE+chainedTagIds);
                } catch (SQLException e){}
            }
            if (!chainedIds.isEmpty()){
                try{
                    SqlHelper.executeSQLCommand(connection, deleteInformation+WHERE+chainedIds);
                } catch (SQLException e){}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static List<SqlAsipInformation> addInformation(SqlSharkKB sharkKB, ASIPSpace space, Iterator<ASIPInformation> informationIterator) throws SQLException, SharkKBException {
        Connection connection = createConnection(sharkKB);

        List<TagContainer> containerList = getTags(sharkKB, space, true);

        ArrayList<SqlAsipInformation> informationList = new ArrayList<>();

        while (informationIterator.hasNext()){
            ASIPInformation next = informationIterator.next();
            informationList.add(new SqlAsipInformation(next, space, sharkKB));
        }

        String insertSet = prepareSqlInsertSet(connection, containerList, space, informationList);

        return executeInsertSuccess(connection, insertSet) ? informationList : null;
    }

    private static boolean executeInsertSuccess(Connection connection, String sql){
        L.d(sql, sql);
        try {
            SqlHelper.executeSQLCommand(connection, sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static List<Integer> getInformationIds(Connection connection, String sql){
        ArrayList<Integer> list = new ArrayList<>();
        int id = 0;
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql) ){
            while (rs.next()) {
                id = rs.getInt("info_id");
                if(!list.contains(id)) list.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getNumberOfInformation(Connection connection){
        String sql = "SELECT * FROM information;";
        int size = 0;
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql) ){
            while (rs.next()) {
                size++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return size;
    }

    private static String prepareSqlStatement(Connection connection, List<TagContainer> containerList, int direction){
        String sql = SELECT+DISTINCT+COUNT+BO+FIELD_INFO_ID+BC+AS+" count,"+FIELD_INFO_ID+","+FIELD_SET_KIND+","+FIELD_DIRECTION+FROM+TABLE_TAG_SET+WHERE+FIELD_DIRECTION+EQ+direction;

        List<List<String>> conditions = new ArrayList<>();
        for (int i = 0; i<7; i++){
            conditions.add(new ArrayList<String>());
        }

        for (TagContainer container : containerList) {
            String tagId = FIELD_TAG_ID+EQ+container.id;
            conditions.get(container.setKind).add(tagId);
        }

        boolean first = true;

        for (int i = 0; i < conditions.size(); i++){
            List<String> conditionList = conditions.get(i);
            String chainedCondition = "";

            for (String condition : conditionList) {
                if(chainedCondition.isEmpty()) chainedCondition = condition;
                else {
                    chainedCondition = chainedCondition+OR+condition;
                }
            }
            if(!chainedCondition.isEmpty()){
                if(first){
                    first=false;
                    sql = sql+AND+ BO + BO +FIELD_SET_KIND+EQ+i+AND+ BO +chainedCondition+ BC + BC;
                } else {
                    sql = sql+OR+ BO +FIELD_SET_KIND+EQ+i+AND+ BO +chainedCondition+ BC + BC;
                }
            }
        }
        return sql+BC+GROUPBY+FIELD_INFO_ID+HAVING+" count = "+containerList.size();
    }

    private static void mapSTSet(SqlSharkKB sharkKB, boolean create, STSet set, int setKind, List list) throws SharkKBException {
        if (set==null) return;
        Iterator<SemanticTag> iterator = set.stTags();
        while (iterator.hasNext()){
            SemanticTag next = iterator.next();
            SqlSemanticTag sqlSemanticTag = getTag(sharkKB, next, create);
            list.add(new TagContainer(sqlSemanticTag.getId(), setKind));
        }
    }

    static List<TagContainer> getTags(SqlSharkKB sharkKB, ASIPSpace space, boolean create) throws SharkKBException {
        List<TagContainer> containerList = new ArrayList<>();
        mapSTSet(sharkKB, create, space.getTopics(), ASIPSpace.DIM_TOPIC, containerList);
        mapSTSet(sharkKB, create, space.getTypes(), ASIPSpace.DIM_TYPE, containerList);
        mapSTSet(sharkKB, create, space.getApprovers(), DIM_APPROVERS,  containerList);
        mapSTSet(sharkKB, create, space.getReceivers(), ASIPSpace.DIM_RECEIVER, containerList);
        mapSTSet(sharkKB, create, space.getTimes(), DIM_TIME, containerList);
        mapSTSet(sharkKB, create, space.getLocations(), DIM_LOCATION, containerList);

        if(space.getSender()!=null){
            SqlSemanticTag sender = getTag(sharkKB, space.getSender(), create);
            containerList.add(new TagContainer(sender.getId(), DIM_SENDER));
        }

        return containerList;
    }

    private static class TagContainer{
        int id;
        int setKind;

        public TagContainer(int id, int setKind) {
            this.id = id;
            this.setKind = setKind;
        }
    }

    private static SqlSemanticTag getTag(SqlSharkKB sharkKB, SemanticTag semanticTag, boolean create) throws SharkKBException {
        SqlSemanticTag sender;
        if(create){
            if(semanticTag instanceof PeerSemanticTag){
                sender = createPeerSemanticTag(sharkKB, (PeerSemanticTag) semanticTag);
            } else {
                sender = createSemanticTag(sharkKB, semanticTag);
            }
        } else {
            if(semanticTag instanceof PeerSemanticTag){
                sender = getPeerSemanticTag(sharkKB, semanticTag);
            } else {
                sender = getSemanticTag(sharkKB, semanticTag);
            }
        }
        return sender;
    }


    public static List<ASIPInformationSpace> getInfoSpaces(SqlSharkKB sharkKB, ASIPSpace asipSpace) throws SQLException, SharkKBException {

        List<SqlAsipInformationSpace> sqlAsipInformationSpaces = new ArrayList<>();

        Connection connection = createConnection(sharkKB);
        List<Integer> informationIds = getInformationIds(connection, "SELECT id AS info_id FROM information;");
        for (Integer id : informationIds) {
            String tagSet = SELECT+ALL+FROM+TABLE_TAG_SET+WHERE+FIELD_INFO_ID+EQ+id;
            ArrayList<TagContainer> tagContainers = new ArrayList<>();
            SqlAsipSpace sqlAsipSpace = new SqlAsipSpace();

            try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, tagSet)){
                while (rs.next()) {
                    sqlAsipSpace.setDirection(rs.getInt("direction"));
                    int tagId = rs.getInt("tag_id");
                    int setKind = rs.getInt("set_kind");
                    tagContainers.add(new TagContainer(tagId, setKind));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            for (TagContainer container : tagContainers) {
                switch (container.setKind){
                    case ASIPSpace.DIM_TOPIC:
                    case ASIPSpace.DIM_TYPE:
                        SqlSemanticTag tag = new SqlSemanticTag(container.id, sharkKB);
                        sqlAsipSpace.addTag(tag, container.setKind);
                        break;
                    case ASIPSpace.DIM_APPROVERS:
                    case ASIPSpace.DIM_RECEIVER:
                    case ASIPSpace.DIM_SENDER:
                        SqlPeerSemanticTag peer = new SqlPeerSemanticTag(container.id,  sharkKB);
                        sqlAsipSpace.addTag(peer, container.setKind);
                        break;
                    case DIM_TIME:
                        // TODO Implement Time
                        SqlSemanticTag time = new SqlSemanticTag(container.id, sharkKB);
                        sqlAsipSpace.addTag(time, container.setKind);
                        break;
                    case DIM_LOCATION:
                        // TODO Implement Spatial
                        SqlSemanticTag location = new SqlSemanticTag(container.id, sharkKB);
                        sqlAsipSpace.addTag(location, container.setKind);
                        break;
                }
            };

            SqlAsipInformation sqlAsipInformation = new SqlAsipInformation(id, sqlAsipSpace, sharkKB);
            boolean added = false;

            for (SqlAsipInformationSpace sqlAsipInformationSpace : sqlAsipInformationSpaces) {
                ASIPSpace sqlAsipInfoSpaceASIPSpace = sqlAsipInformationSpace.getASIPSpace();
                if(SharkCSAlgebra.identical(sqlAsipInfoSpaceASIPSpace, sqlAsipSpace)){
                    sqlAsipInformationSpace.addInformation(sqlAsipInformation);
                    added = true;
                }
            }

            if(!added){
                SqlAsipInformationSpace sqlAsipInformationSpace = new SqlAsipInformationSpace(sharkKB, sqlAsipSpace);
                sqlAsipInformationSpace.addInformation(sqlAsipInformation);
                sqlAsipInformationSpaces.add(sqlAsipInformationSpace);
            }
        }

        return (List<ASIPInformationSpace>) (List<?>) sqlAsipInformationSpaces;
    }

}
