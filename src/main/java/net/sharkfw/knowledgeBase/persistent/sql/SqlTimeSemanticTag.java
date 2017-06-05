package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.*;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.EQ;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FROM;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;

/**
 * Created by Dustin Feurich on 18.04.2017.
 */
public class SqlTimeSemanticTag extends SqlSemanticTag implements TimeSemanticTag {

    private long tagDuration;
    private long tagStart;

    public SqlTimeSemanticTag(String[] sis, String name, SqlSharkKB sharkKB, long tagDuration, long tagStart) throws SQLException {
        super(sis, name, "time");
        this.tagDuration = tagDuration;
        this.tagStart = tagStart;
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder sql = new StringBuilder();
        sql.append("PRAGMA foreign_keys = ON; ");
        sql.append("INSERT INTO semantic_tag (name, tag_kind, t_duration, t_start) VALUES "
                + "(\'" + this.getName() + "\'" + ",\"" + this.getTagKind() + "\"," + tagDuration + "," + tagStart + ");");
        SqlHelper.executeSQLCommand(connection, sql.toString());
        this.setId(SqlHelper.getLastCreatedEntry(connection, "semantic_tag"));
        String sqlSIs = getSqlForSIs();
        if (sqlSIs != null) SqlHelper.executeSQLCommand(connection, getSqlForSIs());
        String update = UPDATE + TABLE_SEMANTIC_TAG + SET + FIELD_SYSTEM_PROPERTY + EQ + Integer.toString(this.getId())
                + WHERE + FIELD_ID + EQ + Integer.toString(this.getId());

        try {
            SqlHelper.executeSQLCommand(connection, update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SqlTimeSemanticTag(String si, SqlSharkKB sharkKB) throws SharkKBException {
        super(si, sharkKB);
        String sql = null;
        if (si != null) {
            sql = SELECT + ALL + FROM + TABLE_SEMANTIC_TAG + JOIN + TABLE_SUBJECT_IDENTIFIER + ON +
                    FIELD_SUBJECT_IDENTIFIER_IDENTIFIER + EQ + QU + si + QU + WHERE + TABLE_SEMANTIC_TAG + DOT + FIELD_ID + EQ + FIELD_TAG_ID;
        }
        else {
            throw new SharkKBException();
        }
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql)) {

            if (rs != null) {
                this.tagDuration = Integer.parseInt(rs.getString("t_duration"));
                this.tagStart = Integer.parseInt(rs.getString("t_start"));
            }
        } catch (SQLException e) {
            throw new SharkKBException(e.toString());
        }
    }

    public SqlTimeSemanticTag(int id, SqlSharkKB sharkKB) throws SharkKBException {
        super(id, sharkKB);
        String sql = SELECT + ALL + FROM + TABLE_SEMANTIC_TAG + WHERE + FIELD_ID + EQ + id;
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql)) {
            if (rs != null) {
                this.tagDuration = Integer.parseInt(rs.getString("t_duration"));
                this.tagStart = Integer.parseInt(rs.getString("t_start"));
            }
        } catch (SQLException e) {
            throw new SharkKBException(e.toString());
        }
    }




    @Override
    public long getFrom() {
        return tagStart;
    }

    @Override
    public long getDuration() {
        return tagDuration;
    }
}
