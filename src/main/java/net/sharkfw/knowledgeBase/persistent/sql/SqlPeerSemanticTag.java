package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.ALL;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.AND;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.DELETE;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.EQ;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_ADDRESS_NAME;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_ID;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_SYSTEM_PROPERTY;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_TAG_ID;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FROM;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.QU;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.SELECT;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.SET;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.TABLE_ADDRESS;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.TABLE_SEMANTIC_TAG;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.UPDATE;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.WHERE;

/**
 * Created by Dustin Feurich on 18.04.2017.
 */
public class SqlPeerSemanticTag extends SqlSemanticTag implements PeerSemanticTag {

    private String[] addresses;

    /**
     * Write to DB
     *
     * @param sis
     * @param name
     * @param sharkKB
     * @param addresses
     * @throws SQLException
     */
    public SqlPeerSemanticTag(String[] sis, String name, SqlSharkKB sharkKB, String[] addresses) throws SQLException {
        super(sis, name, "peer");
        this.addresses = addresses;
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder sql = new StringBuilder();
//        sql.append("PRAGMA foreign_keys = ON; ");
        sql.append("INSERT INTO semantic_tag (name, tag_kind) VALUES " + "(\'" + this.getName() + "\'" + ",\"" + this.getTagKind() + "\");");
        SqlHelper.executeSQLCommand(connection, sql.toString());
        this.setId(SqlHelper.getLastCreatedEntry(connection, "semantic_tag"));
        SqlHelper.executeSQLCommand(connection, this.getSqlForSIs());
        setAddresses(this.addresses);
        String update = UPDATE + TABLE_SEMANTIC_TAG + SET + FIELD_SYSTEM_PROPERTY + EQ + Integer.toString(this.getId()) + WHERE + FIELD_ID + EQ + Integer.toString(this.getId());
        try {
            SqlHelper.executeSQLCommand(connection, update);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public SqlPeerSemanticTag(int id, String[] sis, String name, String property, String tagKind, String[] addresses) {
        super(id, sis, name, property, tagKind);
        this.addresses = addresses;
        setAddresses(this.addresses);
    }

    /**
     * Read from DB
     *
     * @param si
     */
    public SqlPeerSemanticTag(String si, SqlSharkKB sharkKb) throws SharkKBException {
        super(si, sharkKb);
        addresses = getAddresses();
    }

    /**
     * Read from DB
     *
     * @param id
     */
    public SqlPeerSemanticTag(int id, SqlSharkKB sharkKb) throws SharkKBException {
        super(id, sharkKb);
        addresses = getAddresses();
    }

    @Override
    public String[] getAddresses() {
        String tags = SELECT + ALL + FROM + TABLE_ADDRESS + WHERE + FIELD_TAG_ID + EQ + this.id;
        List<String> list = new ArrayList<>();
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), tags);){
            while (rs.next()) {
                list.add(rs.getString("address_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    @Override
    public void setAddresses(String[] addresses) {
        String sql = DELETE + FROM + TABLE_ADDRESS + WHERE + FIELD_TAG_ID + EQ + this.id;
        try {
            SqlHelper.executeSQLCommand(this.getConnection(), sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        addAddressesToDB(addresses);
    }

    @Override
    public void removeAddress(String address) {
        String sql = DELETE + FROM + TABLE_ADDRESS + WHERE + FIELD_TAG_ID + EQ + this.id + AND + FIELD_ADDRESS_NAME + EQ + QU + address + QU;
        try {
            SqlHelper.executeSQLCommand(this.getConnection(), sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAddress(String address) {
        addAddressesToDB(new String[]{address});
    }

    private void addAddressesToDB(String[] addresses) {
//        StringBuilder sqlAddresses = new StringBuilder();
////        sqlAddresses.append("PRAGMA foreign_keys = ON; ");
//        sqlAddresses.append("INSERT INTO address (address_name, tag_id) VALUES ");
//        for (int i = 0; i < this.addresses.length; i++) {
//            if (i != this.addresses.length - 1) {
//                sqlAddresses.append("(\'" + this.addresses[i] + "\'," + this.getId() + ")" + ',');
//            } else {
//                sqlAddresses.append("(\'" + this.addresses[i] + "\'," + this.getId() + ")" + "; ");
//            }
//        }
//        try {
//            SqlHelper.executeSQLCommand(connection, sqlAddresses.toString());
//            this.addresses = addresses;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        List<String> addressesList = Arrays.asList(getAddresses());
        ArrayList<String> strings = new ArrayList<>();

        for (String address : addresses) {
            if (!addressesList.contains(address)) {
                strings.add(address);
            }
        }

        String[] stringArr = new String[strings.size()];
        stringArr = strings.toArray(stringArr);

        if (stringArr.length > 0) {
            StringBuilder sqlAddresses = new StringBuilder();
            sqlAddresses.append("INSERT INTO address (address_name, tag_id) VALUES ");
            for (int i = 0; i < this.addresses.length; i++) {
                if (i == 0) {
                    sqlAddresses.append("(\'" + stringArr[i] + "\'," + this.getId() + ")");
                } else {
                    sqlAddresses.append(",(\'" + stringArr[i] + "\'," + this.getId() + ")");
                }
            }
            sqlAddresses.append(";");
            try {
                SqlHelper.executeSQLCommand(connection, sqlAddresses.toString());
                this.addresses = getAddresses();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
