package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep4;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.when;

/**
 * Created by j4rvis on 5/31/17.
 */
public class SqlSharkHelper {

    static SqlSemanticTag getSemanticTag(SqlSharkKB sharkKB, SemanticTag semanticTag) throws SharkKBException {
        try {
            return new SqlSemanticTag(-1, semanticTag.getSI()[0], -1, sharkKB);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    static void removeSemanticTag(SqlSharkKB sharkKB, SemanticTag semanticTag){

    }

    private static Connection createConnection(SqlSharkKB sharkKB) throws SQLException {
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
                insertInto.values(tagContainer.setKind, sqlAsipInformation.getId(), tagContainer.id, asipSpace.getDirection());
            }
        }
        return insertInto.getSQL();
    }

    static List<SqlAsipInformation> getInformation(SqlSharkKB sharkKB, ASIPSpace space) throws SharkKBException, SQLException {

        Connection connection = createConnection(sharkKB);

        List<TagContainer> containerList = getTags(sharkKB, space, false);

        String sqlStatement = prepareSqlStatement(connection, containerList, space.getDirection());

        List<Integer> informationIds = getInformationIds(connection, sqlStatement);

        List<SqlAsipInformation> informationList = new ArrayList<>();
        for (Integer id : informationIds) {
            informationList.add(new SqlAsipInformation(id, space, sharkKB));
        }

        return informationList;
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
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql) ){
            if (rs.next()) return true;
            else return false;
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

    private static String prepareSqlStatement(Connection connection, List<TagContainer> containerList, int direction){
        DSLContext sql = DSL.using(connection, SQLDialect.SQLITE);
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
            where.and(field("set_kind").eq(inline(i))).and(chainedCondition);
        }
        return where.getSQL();
    }

//
//    private static void getInfoId(Connection connection, List<Integer> infoIds, TagContainer tagContainer, int direction){
//        DSLContext getTags = DSL.using(connection, SQLDialect.SQLITE);
//        String tags = getTags.selectFrom(table("tag_set"))
//                .where(field("set_kind").eq(inline(tagContainer.setKind)))
//                .and(field("tag_id").eq(inline(tagContainer.id)))
//                .and(field("direction").eq(inline(direction))).getSQL();
//        int id = 0;
//        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, tags) ){
//            while (rs.next()) {
//                id = rs.getInt("info_id");
//                if(!infoIds.contains(id)) infoIds.add(id);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    private static void mapSTSet(SqlSharkKB sharkKB, boolean create, STSet set, int setKind, List list) throws SharkKBException {
        Iterator<SemanticTag> iterator = set.stTags();
        while (iterator.hasNext()){
            SemanticTag next = iterator.next();
            SqlSemanticTag sqlSemanticTag;
            if(create){
                sqlSemanticTag = createSemanticTag(sharkKB, next);
            } else {
                sqlSemanticTag = getSemanticTag(sharkKB, next);
            }
            list.add(new TagContainer(sqlSemanticTag.getId(), setKind));
        }
    }

    static List<TagContainer> getTags(SqlSharkKB sharkKB, ASIPSpace space, boolean create) throws SharkKBException {
        List<TagContainer> containerList = new ArrayList<>();
        mapSTSet(sharkKB, create, space.getTopics(), ASIPSpace.DIM_TOPIC, containerList);
        mapSTSet(sharkKB, create, space.getTypes(), ASIPSpace.DIM_TYPE, containerList);
        mapSTSet(sharkKB, create, space.getApprovers(), ASIPSpace.DIM_APPROVERS,  containerList);
        mapSTSet(sharkKB, create, space.getReceivers(), ASIPSpace.DIM_RECEIVER, containerList);
        mapSTSet(sharkKB, create, space.getTimes(), ASIPSpace.DIM_TIME, containerList);
        mapSTSet(sharkKB, create, space.getLocations(), ASIPSpace.DIM_LOCATION, containerList);

        SqlSemanticTag sender;
        if(create){
            sender = createSemanticTag(sharkKB, space.getSender());
        } else {
            sender = getSemanticTag(sharkKB, space.getSender());
        }
        containerList.add(new TagContainer(sender.getId(), ASIPSpace.DIM_SENDER));

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

}
