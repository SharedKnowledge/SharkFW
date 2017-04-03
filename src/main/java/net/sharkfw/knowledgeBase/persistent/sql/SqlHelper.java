package net.sharkfw.knowledgeBase.persistent.sql;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Created by Dustin Feurich on 03.04.2017.
 */
public class SqlHelper {

    private SqlHelper()
    {
        //static usage only
    }

    public static void importSQL(Connection conn, InputStream in) throws SQLException
    {
        Scanner s = new Scanner(in);
        s.useDelimiter("(;(\r)?\n)|(--\n)");
        Statement st = null;
        conn.setAutoCommit(true);
        try
        {
            st = conn.createStatement();
            while (s.hasNext())
            {
                String line = s.next();
                if (line.startsWith("/*!") && line.endsWith("*/"))
                {
                    int i = line.indexOf(' ');
                    line = line.substring(i + 1, line.length() - " */".length());
                }

                if (line.trim().length() > 0)
                {
                    st.executeUpdate(line);
                }
            }
        }
        finally
        {
            if (st != null) st.close();

        }
    }
}
