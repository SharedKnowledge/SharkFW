package net.sharkfw.knowledgeBase.persistent.sql;


import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;

public class SqlKnowledge implements Knowledge {

    private int id;
    private SqlSharkKB sharkKB;
    private int location;
    private Connection connection;

    SqlKnowledge(SqlVocabulary vocabulary) throws SharkKBException, SQLException {

        connection = getConnection(sharkKB);
        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
        String sql = create.insertInto(table("knowledge"),
                field("vocabulary")).values(inline(vocabulary.getId())).getSQL();
        SqlHelper.executeSQLCommand(connection, sql);
        id = SqlHelper.getLastCreatedEntry(connection, "knowledge");

    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String name, String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String name, byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String name, InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public void removeInformation(ASIPInformation info, ASIPSpace infoSpace) throws SharkKBException {

    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> getInformationSpaces(ASIPSpace space) throws SharkKBException {
        return null;
    }

    @Override
    public void removeInformationSpace(ASIPSpace space) throws SharkKBException {

    }

    @Override
    public void removeInformation(ASIPSpace space) throws SharkKBException {

    }

    @Override
    public SharkVocabulary getVocabulary() {
        return null;
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        return 0;
    }

    private Connection getConnection(SqlSharkKB sharkKB) throws SharkKBException {
        try {
            Class.forName(sharkKB.getDialect());
            return DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SharkKBException();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SharkKBException();
        }

    }
}
