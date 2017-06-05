package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
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
public class SqlTimeSTSet extends SqlSTSet implements TimeSTSet {

    public SqlTimeSTSet(SqlSharkKB sharkKB) throws SQLException {
        super(sharkKB, "time", null);
    }

    public SqlTimeSTSet(SqlSharkKB sharkKB, int id) {
        super(sharkKB, id);
    }

    @Override
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) throws SharkKBException {
        try {
            return new SqlTimeSemanticTag(null, "time", this.getSqlSharkKB(), duration, from);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SharkKBException();
        }
    }

    @Override
    public Enumeration<TimeSemanticTag> timeTags() throws SharkKBException {
        return Collections.enumeration(tstTagsList());

    }

    @Override
    public Iterator<TimeSemanticTag> tstTags() throws SharkKBException {
        return tstTagsList().iterator();
    }

    private List<TimeSemanticTag> tstTagsList() throws SharkKBException {
        DSLContext getTags = DSL.using(this.getConnection(), SQLDialect.SQLITE);
        String tags = getTags.selectFrom(table("semantic_tag")).where(field("name")
                .eq(inline("time"))).and(field("tag_set").eq(inline(this.getStSetID()))).getSQL();
        ResultSet rs = null;
        List<TimeSemanticTag> list = new ArrayList<>();
        try {
            rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), tags);
            while (rs.next()) {
                list.add(new InMemoTimeSemanticTag(rs.getLong("t_duration"), rs.getLong("t_start")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SharkKBException();
        }
        return list;
    }


    @Override
    public TimeSTSet fragment(TimeSemanticTag anchor) throws SharkKBException {
        return null;
    }

    @Override
    public TimeSTSet contextualize(TimeSTSet context, FragmentationParameter fp) throws SharkKBException {
        return null;
    }

}
