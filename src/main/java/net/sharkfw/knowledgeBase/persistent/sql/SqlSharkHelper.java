package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;

/**
 * Created by j4rvis on 5/31/17.
 */
public class SqlSharkHelper {

    static SqlSemanticTag getSemanticTag(SqlSharkKB sharkKB, SemanticTag semanticTag) throws SharkKBException {
        return null;
    }

    static SqlSemanticTag createSemanticTag(SqlSharkKB sharkKB, SemanticTag semanticTag){

        try{
            return getSemanticTag(sharkKB, semanticTag);
        } catch (SharkKBException e) {
            // TODO Create tag
            return null;
        }
    }

    static void removeSemanticTag(SqlSharkKB sharkKB, SemanticTag semanticTag){

    }

    static SqlAsipInformation createInformation(SqlSharkKB sharkKB, ASIPInformation information, ASIPSpace space){
        return null;
    }

    static SqlAsipInformation getInformation(SqlSharkKB sharkKB, ASIPSpace space, ASIPInformation information) throws SharkKBException, SQLException {
        Iterator<SqlAsipInformation> iterator = getInformation(sharkKB, space);
        while (iterator.hasNext()){
            SqlAsipInformation next = iterator.next();
            if(next.getName().equals(information.getName())) return next;
        }
        return null;
    }

    static Iterator<SqlAsipInformation> getInformation(SqlSharkKB sharkKB, ASIPSpace space) throws SharkKBException, SQLException {

        Connection connection = null;
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<TagContainer> containerList = getTags(sharkKB, space, false);

        ArrayList<Integer> infoIds = new ArrayList<>();
        for (TagContainer tagContainer : containerList) {
            getInfoId(connection, infoIds, tagContainer, space.getDirection());
        }

        List<SqlAsipInformation> informationList = new ArrayList<>();
/*        for (Integer id : infoIds) {
            informationList.add(new SqlAsipInformation(id, sharkKB));
        }*/

        return informationList.iterator();
    }

    static void addInformation(SqlSharkKB sharkKB, ASIPSpace space, Iterator<ASIPInformation> informationIterator){
        Iterator<SqlAsipInformation> asipInformationIterator = null;
        try {
            asipInformationIterator = getInformation(sharkKB, space);
        } catch (SharkKBException e) {
            e.printStackTrace();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(asipInformationIterator == null || !asipInformationIterator.hasNext()){
            try {
                List<TagContainer> containerList = getTags(sharkKB, space, true);



            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
    }

    private static void getInfoId(Connection connection, List<Integer> infoIds, TagContainer tagContainer, int direction){
        DSLContext getTags = DSL.using(connection, SQLDialect.SQLITE);
        String tags = getTags.selectFrom(table("tag_set"))
                .where(field("set_kind").eq(inline(tagContainer.setKind)))
                .and(field("tag_id").eq(inline(tagContainer.id)))
                .and(field("direction").eq(inline(direction))).getSQL();
        int id = 0;
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, tags) ){
            while (rs.next()) {
                id = rs.getInt("info_id");
                if(!infoIds.contains(id)) infoIds.add(id);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    private static void mapSTSet(SqlSharkKB sharkKB, boolean create, STSet set, int setKind, List list) throws SharkKBException {
        Iterator<SemanticTag> iterator = set.stTags();
        while (iterator.hasNext()){
            SemanticTag next = iterator.next();
            SqlSemanticTag sqlSemanticTag = null;
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
        SqlSemanticTag sqlSemanticTag = getSemanticTag(sharkKB, space.getSender());
        containerList.add(new TagContainer(sqlSemanticTag.getId(), ASIPSpace.DIM_SENDER));

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
