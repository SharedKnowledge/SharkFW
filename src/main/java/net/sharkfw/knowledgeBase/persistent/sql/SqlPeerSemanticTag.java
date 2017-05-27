package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;

/**
 * Created by Dustin Feurich on 18.04.2017.
 */
public class SqlPeerSemanticTag extends SqlSemanticTag implements PeerSemanticTag {

    private String[] addresses;

    /**
     * Write to DB
     * @param sis
     * @param name
     * @param stSetID
     * @param sharkKB
     * @param addresses
     * @throws SQLException
     */
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

    /**
     * Read from DB
     * @param si
     */
    public SqlPeerSemanticTag(String si, int stSet, SqlSharkKB sharkKb) throws SharkKBException {
        super(-1, si ,stSet, sharkKb);
        addresses = getAddresses();
    }

    /**
     * Read from DB
     * @param id
     */
    public SqlPeerSemanticTag(int id, int stSet, SqlSharkKB sharkKb) throws SharkKBException {
        super(id, null, stSet, sharkKb);
        addresses = getAddresses();
    }

    /**
     * Read from DB
     */
    public SqlPeerSemanticTag(int stSet, SqlSharkKB sharkKb) throws SharkKBException {
        super(-1, null, stSet, sharkKb);
        addresses = getAddresses();
    }

    @Override
    public String[] getAddresses() {
        DSLContext getAddresses = DSL.using(this.getConnection(), SQLDialect.SQLITE);
        String tags = getAddresses.selectFrom(table("address")).where(field("tag_id")
                .eq(inline(this.getSystemProperty("id")))).getSQL();
        ResultSet rs = null;
        List<String> list = new ArrayList<>();
        try {
            rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), tags);
            while (rs.next()) {
                list.add(rs.getString("address_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (String[]) list.toArray();
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
