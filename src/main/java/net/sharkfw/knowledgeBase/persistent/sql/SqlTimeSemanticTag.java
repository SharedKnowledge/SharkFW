package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;

/**
 * Created by Dustin Feurich on 18.04.2017.
 */
public class SqlTimeSemanticTag extends SqlSemanticTag implements TimeSemanticTag {

    private long tagDuration;
    private long tagStart;

    public SqlTimeSemanticTag(String[] sis, String name, int stSetID, SqlSharkKB sharkKB, long tagDuration, long tagStart) throws SQLException {
        super(sis, name, "time", stSetID);
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
        sql.append("INSERT INTO semantic_tag (name, tag_set, tag_kind, t_duration, t_start) VALUES "
                + "(\'" + this.getName() + "\'," + this.getStSetID() + ",\"" + this.getTagKind() + "\"," + tagDuration + "," + tagStart + ");");
        SqlHelper.executeSQLCommand(connection, sql.toString());
        this.setId(SqlHelper.getLastCreatedEntry(connection, "semantic_tag"));
        SqlHelper.executeSQLCommand(connection, this.getSqlForSIs());

        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
        String update = create.update(table("semantic_tag")).set(field("system_property"), inline(Integer.toString(this.getId()))).where(field("id").eq(inline(Integer.toString(this.getId())))).getSQL();

        try {
            SqlHelper.executeSQLCommand(connection, update);
        } catch (SQLException e) {
            e.printStackTrace();
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
