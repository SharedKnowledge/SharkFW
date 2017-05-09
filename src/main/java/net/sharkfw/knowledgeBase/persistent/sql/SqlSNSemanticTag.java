package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.SNSemanticTag;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Created by Dustin Feurich
 */
public class SqlSNSemanticTag extends SqlSemanticTag implements SNSemanticTag {

    private int semanticNetId;

    public SqlSNSemanticTag(String[] sis, String name, int stSetID, SqlSharkKB sharkKB, long tagDuration, long tagStart) throws SQLException {
        super(sis, name, "s_net", stSetID);
        semanticNetId = stSetID;
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder sql = new StringBuilder();
        sql.append("PRAGMA foreign_keys = ON; ");
        sql.append("INSERT INTO semantic_tag (name, tag_set, tag_kind) VALUES "
                + "(\'" + this.getName() + "\'," + this.getStSetID() + ",\"" + this.getTagKind() + "\")");
        SqlHelper.executeSQLCommand(connection, sql.toString());
        this.setId(SqlHelper.getLastCreatedEntry(connection, "semantic_tag"));
        SqlHelper.executeSQLCommand(connection, this.getSqlForSIs());
    }


    @Override
    public void setPredicate(String type, SNSemanticTag target) {
        StringBuilder sql = new StringBuilder();
        sql.append("PRAGMA foreign_keys = ON; ");
        sql.append("INSERT INTO relation (source_tag_id, target_tag_id, name, semantic_net_id) VALUES "
                + "(" + this.getId() + "," + target.ID + ",\"" + type + "\"," + semanticNetId + " )");
        try {
            SqlHelper.executeSQLCommand(connection, sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePredicate(String type, SNSemanticTag target) {

    }

    @Override
    public Enumeration<String> predicateNames() {
        return null;
    }

    @Override
    public Enumeration<String> targetPredicateNames() {
        return null;
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
    public void merge(SNSemanticTag toMerge) {

    }
}
