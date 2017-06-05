package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.L;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DeleteWhereStep;
import org.jooq.InsertValuesStep4;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

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
import static org.jooq.impl.DSL.boolAnd;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.when;

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
        DSLContext sql = DSL.using(connection, SQLDialect.SQLITE);
        InsertValuesStep4<Record, Object, Object, Object, Object> insertInto = sql.insertInto(table("tag_set"), field("set_kind"), field("info_id"), field("tag_id"), field("direction"));

        for (SqlAsipInformation sqlAsipInformation : informationList) {
            for (TagContainer tagContainer : containerList) {
                insertInto.values(
                        inline(tagContainer.setKind),
                        inline(sqlAsipInformation.getId()),
                        inline(tagContainer.id),
                        inline(asipSpace.getDirection()));
            }
        }
        return insertInto.getSQL();
    }

    static SqlAsipInformation getInformation(SqlSharkKB sharkKB, ASIPSpace space, ASIPInformation information){
        try {
            List<SqlAsipInformation> informationList = SqlSharkHelper.getInformation(sharkKB, space);
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

    static List<SqlAsipInformation> getInformation(SqlSharkKB sharkKB, ASIPSpace space) throws SharkKBException, SQLException {

        Connection connection = createConnection(sharkKB);

        List<TagContainer> containerList = getTags(sharkKB, space, false);

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
            DSLContext sql = DSL.using(connection, SQLDialect.SQLITE);
            DSLContext sql1 = DSL.using(connection, SQLDialect.SQLITE);
            DeleteWhereStep<Record> deleteInformation = sql.deleteFrom(table("information"));
            DeleteWhereStep<Record> deleteTagSet = sql1.deleteFrom(table("tag_set"));

            Condition chainedTagIds = null;
            Condition chainedIds = null;

            List<SqlAsipInformation> informationList = new ArrayList<>();

            if(asipInformation==null){
                informationList.addAll(SqlSharkHelper.getInformation(sharkKB, space));
            } else {
                informationList.add(SqlSharkHelper.getInformation(sharkKB, space, asipInformation));
            }

            for (SqlAsipInformation sqlAsipInformation : informationList) {
                Condition infoId = field("info_id").eq(inline(sqlAsipInformation.getId()));
                Condition id = field("id").eq(inline(sqlAsipInformation.getId()));

                if(chainedTagIds==null) chainedTagIds=infoId;
                else chainedTagIds = chainedTagIds.or(infoId);
                if(chainedIds==null) chainedIds=id;
                else chainedIds = chainedIds.or(id);
            }

            if(chainedTagIds!=null){
                String sqlTagSet = deleteTagSet.where(chainedTagIds).getSQL();
                L.d(sqlTagSet, sqlTagSet);
                try{
                    SqlHelper.executeSQLCommand(connection, sqlTagSet);
                } catch (SQLException e){}
            }
            if (chainedIds!=null){
                String sqlIds = deleteInformation.where(chainedIds).getSQL();
                L.d(sqlIds, sqlIds);
                try{
                    SqlHelper.executeSQLCommand(connection, sqlIds);
                } catch (SQLException e){}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static List<SqlAsipInformation> addInformation(SqlSharkKB sharkKB, ASIPSpace space, Iterator<ASIPInformation> informationIterator) throws SQLException, SharkKBException {
        Connection connection = createConnection(sharkKB);

        List<TagContainer> containerList = getTags(sharkKB, space, true);

        // Create the information and get the Id

        ArrayList<SqlAsipInformation> informationList = new ArrayList<>();

        while (informationIterator.hasNext()){
            ASIPInformation next = informationIterator.next();
            informationList.add(new SqlAsipInformation(next, space, sharkKB));
        }

        String insertSet = prepareSqlInsertSet(connection, containerList, space, informationList);

        return executeInsertSuccess(connection, insertSet) ? informationList : null;
    }

    private static boolean executeInsertSuccess(Connection connection, String sql){
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

    /*select * from tag_set where ((((((((
            ((direction = 0 or set_kind = 0) and (tag_id = 1 or tag_id = 2))
            or set_kind = 1) and (tag_id = 3 or tag_id = 4 or tag_id = 5))
            or set_kind = 2) and (tag_id = 6 or tag_id = 7))
            or set_kind = 3) and tag_id = 6)
            or set_kind = 4) and (tag_id = 8 or tag_id = 9 or tag_id = 10))*/

    private static String prepareSqlStatement(Connection connection, List<TagContainer> containerList, int direction){
        /*DSLContext sql = DSL.using(connection, SQLDialect.SQLITE);
//        String sql = SELECT+ALL+FROM+TABLE_TAG_SET+WHERE+FIELD_DIRECTION+EQ+direction
        SelectConditionStep<Record> where = sql.selectFrom(table("tag_set")).where(field("direction").eq(inline(direction)));

        List<List<Condition>> conditions = new ArrayList<>();
        for (int i = 0; i<7; i++){
            conditions.add(new ArrayList<Condition>());
        }

        for (TagContainer container : containerList) {
            Condition tagId = field("tag_id").eq(inline(container.id));
            conditions.get(container.setKind).add(tagId);
        }

        for (int i = 0; i < conditions.size(); i++){
            List<Condition> conditionList = conditions.get(i);
            Condition chainedCondition = null;

            for (Condition condition : conditionList) {
                if(chainedCondition==null) chainedCondition = condition;
                else {
                    chainedCondition = chainedCondition.or(condition);
                }
            }
            if(chainedCondition!=null){
                where.or(field("set_kind").eq(inline(i))).and(chainedCondition);
            }
        }
        return where.getSQL();*/
        String sql = SELECT+ALL+FROM+TABLE_TAG_SET+WHERE+FIELD_DIRECTION+EQ+direction;

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
        return sql+ BC;
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
            DSLContext sql = DSL.using(connection, SQLDialect.SQLITE);
            String tagSet = sql.selectFrom(table("tag_set")).where(field("info_id").eq(inline(id))).getSQL();
//            HashMap<Integer, Integer> tagList = new HashMap<>();
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

//        Connection connection = createConnection(sqlSharkKB);
//        String sql = " SELECT tag_set.set_kind, tag_set.direction, information.content_length,\n" +
//                "\tinformation.id, information.content_stream, information.content_type, information.name, information.property,\n" +
//                "\tsemantic_tag.id, semantic_tag.name, semantic_tag.property, semantic_tag.system_property, semantic_tag.t_duration, " +
//                "semantic_tag.t_start, semantic_tag.tag_kind, semantic_tag.wkt,\n" +
//                "subject_identifier.identifier, address.address_name\n" +
//                "FROM tag_set\n" +
//                "INNER JOIN information ON tag_set.info_id = information.id\n" +
//                "INNER JOIN ( semantic_tag\n" +
//                "INNER JOIN subject_identifier ON semantic_tag.id = subject_identifier.tag_id\n" +
//                "INNER JOIN address ON semantic_tag.id = address.tag_id)\n" +
//                "ON tag_set.tag_id = semantic_tag.id";
//
//        List<ASIPInformationSpace> informationSpaces = new ArrayList<>();
//
//        HashMap<Integer, SqlAsipInformation> infoMap = new HashMap<>();
//        HashMap<Integer, SqlSemanticTag> tagMap = new HashMap<>();
//
//        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql) ){
//            while (rs.next()) {
//                int infoId = rs.getInt("information.info_id");
//
//                if(!infoMap.containsKey(infoId)){
//                    String infoName = rs.getString("information.name");
//                    byte[] infoContent = rs.getBytes("information.content_stream");
//                    String infoContentType = rs.getString("information.content_type");
//                    int infoContentLength = rs.getInt("information.content_length");
//
//                    SqlAsipInformation sqlAsipInformation = new SqlAsipInformation(infoId, infoContent, infoContentType, infoName, infoContentLength);
//                    infoMap.put(infoId, sqlAsipInformation);
//                }
//
//                new ASIPInformationSpace()
//
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return null;
    }

}
