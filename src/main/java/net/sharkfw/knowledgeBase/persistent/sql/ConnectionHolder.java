package net.sharkfw.knowledgeBase.persistent.sql;

import java.sql.Connection;

/**
 * Created by j4rvis on 6/9/17.
 */
public interface ConnectionHolder {
    Connection getConnection();
    int getId();
}
