package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;

public class SqlSemanticNet implements SemanticNet {

    private Connection connection;
    private int stSetID;
    private SqlSharkKB sqlSharkKB;

    /**
     * Write SemanticNet to database
     */
    public SqlSemanticNet(SqlSharkKB sharkKB) throws SQLException {
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.sqlSharkKB = sharkKB;
        StringBuilder sql = new StringBuilder();
//        sql.append("PRAGMA foreign_keys = ON; ");
        sql.append("INSERT INTO semantic_net VALUES (NULL);");
        SqlHelper.executeSQLCommand(connection, sql.toString());
        stSetID = SqlHelper.getLastCreatedEntry(connection, "semantic_net");
    }


    @Override
    public void setPredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException {
        source.setPredicate(type, target);
    }

    @Override
    public void removePredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException {
        source.removePredicate(type, target);
    }

    @Override
    public SNSemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
//        try {
//            return new SqlSNSemanticTag(sis, name, sqlSharkKB);
//        } catch (SQLException e) {
//            throw new SharkKBException(e.toString());
//        }
        return null;
    }

    @Override
    public SNSemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return createSemanticTag(name, new String[]{si});
    }

    @Override
    public void removeSemanticTag(String si) throws SharkKBException {

    }

    @Override
    public void removeSemanticTag(String[] sis) throws SharkKBException {

    }

    @Override
    public void removeSemanticTag(SemanticTag tag) throws SharkKBException {

    }

    @Override
    public void setEnumerateHiddenTags(boolean hide) {

    }

    @Override
    public Enumeration<SemanticTag> tags() throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<SemanticTag> stTags() throws SharkKBException {
        return null;
    }

    @Override
    public STSet asSTSet() {
        return null;
    }



    @Override
    public void removeSemanticTag(SNSemanticTag tag) throws SharkKBException {

    }

    @Override
    public SNSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return null;
    }

    @Override
    public SNSemanticTag getSemanticTag(String si) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<SemanticTag> getSemanticTagByName(String pattern) throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet fragment(SemanticTag anchor) throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp) throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet contextualize(STSet context) throws SharkKBException {
        return null;
    }

    @Override
    public void merge(STSet stSet) throws SharkKBException {

    }

    @Override
    public void addListener(STSetListener listen) {

    }

    @Override
    public void removeListener(STSetListener listener) throws SharkKBException {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void merge(SemanticNet remoteSemanticNet) throws SharkKBException {

    }

    @Override
    public SNSemanticTag merge(SemanticTag source) throws SharkKBException {
        return null;
    }

    @Override
    public void add(SemanticTag tag) throws SharkKBException {

    }

    @Override
    public FragmentationParameter getDefaultFP() {
        return null;
    }

    @Override
    public void setDefaultFP(FragmentationParameter fp) {

    }
}
