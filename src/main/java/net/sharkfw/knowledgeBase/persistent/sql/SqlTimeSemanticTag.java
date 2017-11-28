package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.*;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.EQ;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FROM;


/**
 * Created by Dustin Feurich on 18.04.2017.
 */
public class SqlTimeSemanticTag extends SqlSemanticTag implements TimeSemanticTag {

    private long tagDuration;
    private long tagStart;

    public SqlTimeSemanticTag(String[] sis, String name, SqlSharkKB sharkKB, long tagDuration, long tagStart) throws SQLException {
        super(sis, name, "time", sharkKB);
        this.tagDuration = tagDuration;
        this.tagStart = tagStart;
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO semantic_tag (name, tag_kind, t_duration, t_start) VALUES "
                + "(\'" + this.getName() + "\'" + ",\"" + this.getTagKind() + "\"," + tagDuration + "," + tagStart + ");");
        SqlHelper.executeSQLCommand(this.getConnection(), sql.toString());
        this.setId(SqlHelper.getLastCreatedEntry(this.getConnection(), "semantic_tag"));
        SqlHelper.executeSQLCommand(this.getConnection(), this.getSqlForSIs());
        String update = UPDATE + TABLE_SEMANTIC_TAG + SET + FIELD_SYSTEM_PROPERTY + EQ + Integer.toString(this.getId())
                + WHERE + FIELD_ID + EQ + Integer.toString(this.getId());

        try {
            SqlHelper.executeSQLCommand(this.getConnection(), update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SqlTimeSemanticTag(String si, SqlSharkKB sharkKB) throws SharkKBException {
        super(si, sharkKB, "time");
        String sql = null;
        if (si != null) {
            sql = SELECT + ALL + FROM + TABLE_SEMANTIC_TAG + JOIN + TABLE_SUBJECT_IDENTIFIER + ON +
                    FIELD_SUBJECT_IDENTIFIER_IDENTIFIER + EQ + QU + si + QU + WHERE + TABLE_SEMANTIC_TAG + DOT + FIELD_ID + EQ + FIELD_TAG_ID;
        }
        else {
            throw new SharkKBException();
        }
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), sql)) {

            if (rs != null) {
                rs.next();
                this.tagDuration = rs.getLong("t_duration");
                this.tagStart = rs.getLong("t_start");
            }
        } catch (SQLException e) {
            throw new SharkKBException(e.toString());
        }
    }

    public SqlTimeSemanticTag(int id, SqlSharkKB sharkKB) throws SharkKBException {
        super(id, sharkKB);
        String sql = SELECT + ALL + FROM + TABLE_SEMANTIC_TAG + WHERE + FIELD_ID + EQ + id;
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), sql)) {
            if (rs != null) {
                rs.next();
                this.tagDuration = rs.getLong("t_duration");
                this.tagStart = rs.getLong("t_start");
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
