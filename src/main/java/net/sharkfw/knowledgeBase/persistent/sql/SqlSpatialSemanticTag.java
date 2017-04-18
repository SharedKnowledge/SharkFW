package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Dustin Feurich on 18.04.2017.
 */
public class SqlSpatialSemanticTag extends SqlSemanticTag implements SpatialSemanticTag {

    private String wkt;

    public SqlSpatialSemanticTag(String[] sis, String name, int stSetID, SqlSharkKB sharkKB, String wkt) throws SQLException {
        super(sis, name, "spatial", stSetID);
        this.wkt = wkt;
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder sql = new StringBuilder();
        sql.append("PRAGMA foreign_keys = ON; ");
        sql.append("INSERT INTO semantic_tag (name, tag_set, tag_kind, wkt) VALUES "
                + "(\'" + this.getName() + "\'," + this.getStSetID() + ",\"" + this.getTagKind()
                + "\",\"" + this.wkt + "\");");
        SqlHelper.executeSQLCommand(connection, sql.toString());
        this.setId(SqlHelper.getLastCreatedEntry(connection, "semantic_tag"));
        SqlHelper.executeSQLCommand(connection, this.getSqlForSIs());
    }

    @Override
    public SharkGeometry getGeometry() {
        try {
            return InMemoSharkGeometry.createGeomByWKT(wkt);
        } catch (SharkKBException e) {
            e.printStackTrace();
            return null;
        }
    }
}
