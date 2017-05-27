package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static net.sharkfw.knowledgeBase.persistent.sql.Constants.ASIP_SPACE_TOPICS;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;

public class SqlAsipSpace implements ASIPSpace {

    private int id;
    private SqlSharkKB sharkKB;
    private Connection connection;



    SqlAsipSpace(SqlSTSet topics, SqlSTSet types, int direction, SqlPeerSemanticTag sender,
                 SqlPeerSTSet receivers, SqlPeerSTSet approvers, SqlTimeSTSet times, SqlSpatialSTSet locations, SqlSharkKB sharkKB) throws SharkKBException, SQLException {

        connection = getConnection(sharkKB);
        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
        String sql = create.insertInto(table("asip_space"),
                field("topic_set"),field("type_set"), field("receiver_set"), field("approver_set"), field("time_set"),
                field("location_set"), field("sender_peer_tag"), field("direction"))
                .values(inline(topics != null ? topics.getStSetID() : -1),inline(types != null ? types.getStSetID() : -1),
                        inline(receivers != null ? receivers.getStSetID() : -1), inline(approvers != null ? approvers.getStSetID() : -1),inline(times != null ? times.getStSetID() : -1),
                        inline(locations != null ? locations.getStSetID() : -1), inline(sender.getId()), inline(direction)).getSQL();



        SqlHelper.executeSQLCommand(connection, sql);
        id = SqlHelper.getLastCreatedEntry(connection, "asip_space");

        }



    @Override
    public STSet getTopics() {
        return getSet("topic_set");
    }


    @Override
    public STSet getTypes() {

        return getSet("type_set");
    }

    @Override
    public int getDirection() {

        try {
            connection = getConnection(sharkKB);
        } catch (SharkKBException e) {
            e.printStackTrace();
            return -1;
        }
        DSLContext getSetId = DSL.using(connection, SQLDialect.SQLITE);
        String sql = getSetId.selectFrom(table("asip_space")).where(field("id")
                .eq(inline(id))).getSQL();
        ResultSet rs;
        try {
            rs = SqlHelper.executeSQLCommandWithResult(connection, sql);
            return rs.getInt("direction");
        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public PeerSemanticTag getSender() {

        try {
            connection = getConnection(sharkKB);
        } catch (SharkKBException e) {
            e.printStackTrace();
            return null;
        }
        DSLContext getSetId = DSL.using(connection, SQLDialect.SQLITE);
        String sql = getSetId.selectFrom(table("asip_space")).where(field("id")
                .eq(inline(id))).getSQL();
        ResultSet rs;
        int tagId = -1;
        try {
            rs = SqlHelper.executeSQLCommandWithResult(connection, sql);
            tagId = rs.getInt("sender_peer_tag");
            if (tagId !=  - 1) {
                return new SqlPeerSemanticTag(tagId, - 1, sharkKB);
            }
            else {
                return null;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (SharkKBException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public PeerSTSet getReceivers() {

        return getPeerSet("receiver_set");
    }

    @Override
    public PeerSTSet getApprovers() {

        return getPeerSet("approver_set");
    }

    @Override
    public TimeSTSet getTimes() {

        try {
            connection = getConnection(sharkKB);
        } catch (SharkKBException e) {
            e.printStackTrace();
            return null;
        }
        DSLContext getSetId = DSL.using(connection, SQLDialect.SQLITE);
        String sql = getSetId.selectFrom(table("asip_space")).where(field("id")
                .eq(inline(id))).getSQL();
        ResultSet rs;
        int setId = -1;
        try {
            rs = SqlHelper.executeSQLCommandWithResult(connection, sql);
            setId = rs.getInt("time_set");
            if (setId !=  - 1) {
                return new SqlTimeSTSet(sharkKB, setId);
            }
            else {
                return null;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public SpatialSTSet getLocations() {
        try {
            connection = getConnection(sharkKB);
        } catch (SharkKBException e) {
            e.printStackTrace();
            return null;
        }
        DSLContext getSetId = DSL.using(connection, SQLDialect.SQLITE);
        String sql = getSetId.selectFrom(table("asip_space")).where(field("id")
                .eq(inline(id))).getSQL();
        ResultSet rs;
        int setId = -1;
        try {
            rs = SqlHelper.executeSQLCommandWithResult(connection, sql);
            setId = rs.getInt("location_set");
            if (setId !=  - 1) {
                return new SqlSpatialSTSet(sharkKB, setId);
            }
            else {
                return null;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


    }


    private STSet getSet (String column) {
        try {
            connection = getConnection(sharkKB);
        } catch (SharkKBException e) {
            e.printStackTrace();
            return null;
        }
        DSLContext getSetId = DSL.using(connection, SQLDialect.SQLITE);
        String sql = getSetId.selectFrom(table("asip_space")).where(field("id")
                .eq(inline(id))).getSQL();
        ResultSet rs;
        int setId = -1;
        try {
            rs = SqlHelper.executeSQLCommandWithResult(connection, sql);
            setId = rs.getInt(column);
            if (setId !=  - 1) {
                return new SqlSTSet(sharkKB, setId);
            }
            else {
                return null;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    private PeerSTSet getPeerSet (String column) {
        try {
            connection = getConnection(sharkKB);
        } catch (SharkKBException e) {
            e.printStackTrace();
            return null;
        }
        DSLContext getSetId = DSL.using(connection, SQLDialect.SQLITE);
        String sql = getSetId.selectFrom(table("asip_space")).where(field("id")
                .eq(inline(id))).getSQL();
        ResultSet rs;
        int setId = -1;
        try {
            rs = SqlHelper.executeSQLCommandWithResult(connection, sql);
            setId = rs.getInt(column);
            if (setId !=  - 1) {
                return new SqlPeerSTSet(sharkKB, setId);
            }
            else {
                return null;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Connection getConnection(SqlSharkKB sharkKB) throws SharkKBException {
        try {
            Class.forName(sharkKB.getDialect());
            return DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SharkKBException();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SharkKBException();
        }

    }
}
