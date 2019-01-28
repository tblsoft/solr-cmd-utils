package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.DateUtils;

import java.sql.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by tblsoft on 10.11.18.
 */
public class JdbcReader extends AbstractReader {

    @Override
    public void read() {
        String sql = getProperty("sql", null);
        String jdbcDriver = getProperty("jdbcDriver", "com.mysql.jdbc.Driver");
        String user = getProperty("user", null);
        String password = getProperty("password", null);
        String jdbcUrl = getProperty("jdbcUrl", null);
        String zoneId = getProperty("zoneId", "UTC");

        boolean addMeta = getPropertyAsBoolean("addMeta", false);
        Long maxRows = getPropertyAsInteger("maxRows", Long.MAX_VALUE);

        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(jdbcUrl, user, password);
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);
            int columnCount = rs.getMetaData().getColumnCount();
            long rowNumber = 0;
            while (rs.next()) {
                if (rowNumber >= maxRows) {
                    break;
                }
                rowNumber++;
                Document document = new Document();
                for (int i = 1; i <= columnCount; i++) {
                    String key = rs.getMetaData().getColumnLabel(i);
                    String value = rs.getString(i);
                    String datatype = rs.getMetaData().getColumnClassName(i);

                    if(datatype.equals("java.sql.Timestamp") && value != null) {
                        value =  DateUtils.timestamp2String(rs.getTimestamp(i),zoneId);
                    }

                    if(value != null) {
                        document.addField(key, value);
                    }
                }
                if (addMeta) {
                    document.addField("rowNumber", String.valueOf(rowNumber));
                }
                executer.document(document);

            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException se) {
                    throw new RuntimeException(se);
                }
            }
        }

    }

}
