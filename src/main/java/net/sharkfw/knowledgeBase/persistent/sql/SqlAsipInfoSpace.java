package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKBException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;

public class SqlAsipInfoSpace implements ASIPInformationSpace {

    private List<SqlAsipInformation> infos = new ArrayList<>();
    private SqlAsipSpace space;

    public SqlAsipInfoSpace(SqlAsipSpace space) {
        this.space = space;
    }

    public void addInformation(SqlAsipInformation sqlAsipInformation){
        infos.add(sqlAsipInformation);
    }

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        return space;
    }

    @Override
    public int numberOfInformations() {
        return infos.size();
    }

    @Override
    public Iterator<ASIPInformation> informations() throws SharkKBException {
        return ((List<ASIPInformation>) (List<?>) infos).iterator();
    }
}
