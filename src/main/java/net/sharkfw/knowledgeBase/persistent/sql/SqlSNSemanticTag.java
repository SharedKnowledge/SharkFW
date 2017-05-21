package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.SNSemanticTag;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.jooq.*;
import org.jooq.impl.*;
import static org.jooq.impl.DSL.*;

/**
 * Created by Dustin Feurich
 */
public class SqlSNSemanticTag extends SqlSemanticTag implements SNSemanticTag {

    private int semanticNetId;

    public SqlSNSemanticTag(String[] sis, String name, int stSetID, SqlSharkKB sharkKB) throws SQLException {
        super(sis, name, "s_net", stSetID);
        semanticNetId = stSetID;
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
        String sql = create.insertInto(table("semantic_tag"),
                field("name"), field("tag_set"), field("tag_kind"))
                .values(inline(this.getName()), inline(this.getStSetID()), inline(this.getTagKind()))
                .getSQL();
        SqlHelper.executeSQLCommand(connection, sql);
        this.setId(SqlHelper.getLastCreatedEntry(connection, "semantic_tag"));
        SqlHelper.executeSQLCommand(connection, this.getSqlForSIs());
        create = DSL.using(connection, SQLDialect.SQLITE);
        String update = create.update(table("semantic_tag")).set(field("system_property"), inline(Integer.toString(this.getId()))).where(field("id").eq(inline(Integer.toString(this.getId())))).getSQL();

        try {
            SqlHelper.executeSQLCommand(connection, update);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void setPredicate(String type, SNSemanticTag target) {

        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
        String sql = create.insertInto(table("relation"),
                field("source_tag_id"), field("target_tag_id"), field("name"), field("semantic_net_id"))
                .values(inline(this.getId()), inline(target.getSystemProperty("ID")), inline(type), inline(semanticNetId))
                .getSQL();
        try {
            SqlHelper.executeSQLCommand(connection, sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void removePredicate(String type, SNSemanticTag target) {

        int i = Integer.valueOf(target.ID);
        DSLContext delete = DSL.using(connection, SQLDialect.SQLITE);
        String sql = delete.deleteFrom(table("relation")).where(field("id").eq(this.getId())).
                and(field("name").eq(type)).and(field("target_tag_id").eq(target.getSystemProperty("ID"))).getSQL();
        try {
            SqlHelper.executeSQLCommand(connection, sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Enumeration<String> predicateNames() {

        DSLContext se = DSL.using(connection, SQLDialect.SQLITE);
        String sql = se.select(field("name")).from("relation").where(field("source_tag_id").eq(inline(this.getId()))).getSQL();
        Result<Record> result =  se.fetch(sql);
        List<String> list = (List<String>) result.getValues("name");
        return Collections.enumeration(list);
    }


    @Override
    public Enumeration<SNSemanticTag> targetTags(String predicateName) {
        return null;
    }

    @Override
    public Enumeration<SNSemanticTag> sourceTags(String predicateName) {
        return null;
    }

    @Override
    public Enumeration<String> targetPredicateNames() {
        //TODO: ???
        return null;
    }


    @Override
    public void merge(SNSemanticTag toMerge) {

    }


}
