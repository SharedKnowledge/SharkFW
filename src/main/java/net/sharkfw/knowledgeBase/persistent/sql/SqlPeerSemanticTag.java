package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
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
public class SqlPeerSemanticTag extends SqlSemanticTag implements PeerSemanticTag {

    private String[] addresses;

    public SqlPeerSemanticTag(String[] sis, String name, int stSetID, SqlSharkKB sharkKB, String[] addresses) throws SQLException {
        super(sis, name, "peer", stSetID);
        this.addresses = addresses;
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder sql = new StringBuilder();
        sql.append("PRAGMA foreign_keys = ON; ");
        sql.append("INSERT INTO semantic_tag (name, tag_set, tag_kind) VALUES "
                + "(\'" + this.getName() + "\'," + this.getStSetID() + ",\"" + this.getTagKind()
                + "\");");
        SqlHelper.executeSQLCommand(connection, sql.toString());
        this.setId(SqlHelper.getLastCreatedEntry(connection, "semantic_tag"));
        SqlHelper.executeSQLCommand(connection, this.getSqlForSIs());

        StringBuilder sqlAddresses = new StringBuilder();
        sqlAddresses.append("PRAGMA foreign_keys = ON; ");
        sqlAddresses.append("INSERT INTO address (address_name, tag_id) VALUES ");
        for (int i = 0; i < this.addresses.length; i++)
        {
            if (i != this.addresses.length - 1)
            {
                sqlAddresses.append("(\'" + this.addresses[i] + "\'," + this.getId() + ")" + ',');
            }
            else
            {
                sqlAddresses.append("(\'" + this.addresses[i] + "\'," + this.getId() + ")" + "; ");
            }
        }
        SqlHelper.executeSQLCommand(connection, sqlAddresses.toString());

        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
        String update = create.update(table("semantic_tag")).set(field("system_property"), inline(Integer.toString(this.getId()))).where(field("id").eq(inline(Integer.toString(this.getId())))).getSQL();

        try {
            SqlHelper.executeSQLCommand(connection, update);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String[] getAddresses() {
        return new String[0];
    }

    @Override
    public void setAddresses(String[] addresses) {

    }

    @Override
    public void removeAddress(String address) {

    }

    @Override
    public void addAddress(String address) {

    }
}
