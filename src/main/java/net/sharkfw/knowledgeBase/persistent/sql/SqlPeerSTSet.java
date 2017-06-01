package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoTimeSemanticTag;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;

/**
 * Created by dfe on 24.05.2017.
 */
public class SqlPeerSTSet extends SqlSTSet implements PeerSTSet {

    public SqlPeerSTSet(SqlSharkKB sharkKB) throws SQLException {
        super(sharkKB, "peer");
    }

    public SqlPeerSTSet(SqlSharkKB sharkKB, int id) {
        super(sharkKB, id);
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        try {
            return new SqlPeerSemanticTag(sis, name, this.getSqlSharkKB(), addresses);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SharkKBException();
        }
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return createPeerSemanticTag(name, sis, new String[]{address});
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return createPeerSemanticTag(name, new String[]{si}, addresses);
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) throws SharkKBException {
        return createPeerSemanticTag(name, new String[]{si}, new String[]{address});
    }


    @Override
    public PeerSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return new SqlPeerSemanticTag(sis[0], getStSetID(), getSqlSharkKB());
    }

    @Override
    public PeerSemanticTag getSemanticTag(String si) throws SharkKBException {
        return new SqlPeerSemanticTag(si, getStSetID(), getSqlSharkKB());
    }

    @Override
    public Enumeration<PeerSemanticTag> peerTags() {
        DSLContext getTags = DSL.using(this.getConnection(), SQLDialect.SQLITE);
        String tags = getTags.selectFrom(table("semantic_tag")).where(field("tag_kind")
                .eq(inline("peer"))).and(field("tag_set").eq(inline(this.getStSetID()))).getSQL();
        ResultSet rs = null;
        List<PeerSemanticTag> list = new ArrayList<>();
        int id = 0;
        try {
            rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), tags);
            while (rs.next()) {
                id = rs.getInt("id");
                list.add(new SqlPeerSemanticTag(id, getStSetID(), getSqlSharkKB()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (SharkKBException e) {
            e.printStackTrace();
            return null;
        }
        return Collections.enumeration(list);
    }

    @Override
    public PeerSTSet fragment(SemanticTag anchor) throws SharkKBException {
        return null;
    }

    @Override
    public PeerSTSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return null;
    }

    @Override
    public PeerSTSet contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp) throws SharkKBException {
        return null;
    }

    @Override
    public PeerSTSet contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        return null;
    }

    @Override
    public PeerSTSet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        return null;
    }

    @Override
    public PeerSTSet contextualize(STSet context) throws SharkKBException {
        return null;
    }

}