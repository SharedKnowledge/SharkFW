package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;

public class SqlVocabulary implements SharkVocabulary {


    private int id;
    private SqlSharkKB sharkKB;
    private int location;
    private Connection connection;



    SqlVocabulary(SqlSTSet topics, SqlSTSet types,
                 SqlPeerSTSet peers, SqlTimeSTSet times, SqlSpatialSTSet locations, SqlSharkKB sharkKB) throws SharkKBException, SQLException {

        connection = getConnection(sharkKB);
        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
        String sql = create.insertInto(table("vocabulary"),
                field("topic_set"),field("type_set"), field("peer_set"), field("time_set"),
                field("location_set"))
                .values(inline(topics != null ? topics.getStSetID() : -1),inline(types != null ? types.getStSetID() : -1),
                        inline(peers != null ? peers.getStSetID() : -1),inline(times != null ? times.getStSetID() : -1),
                        inline(locations != null ? locations.getStSetID() : -1)).getSQL();
        SqlHelper.executeSQLCommand(connection, sql);
        id = SqlHelper.getLastCreatedEntry(connection, "vocabulary");

    }


    public int getId() {
        return id;
    }

    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        return null;
    }

    @Override
    public PeerSemanticTag getOwner() {
        return null;
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        return null;
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        return null;
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return null;
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        return null;
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
