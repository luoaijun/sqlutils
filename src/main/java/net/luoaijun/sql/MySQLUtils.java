package net.luoaijun.sql;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LUO AI JUN
 * @date 2022/4/27 9:56
 */
public class MySQLUtils {
    Logger logger = Logger.getLogger(MySQLUtils.class);
    public static PreparedStatement stmt = null;
    public static Connection conn = null;
    String sql = "select * from userdata";
    String url = "jdbc:mysql://localhost:3306/paoding";
    String user = "root";
    String password = "";


    public void connetDatabse(String url, String user,
                              String password, String version) {
        try {
            if(version.equals("8")) {
                Class.forName("com.mysql.cj.jdbc.Driver");

            } else {
                Class.forName("com.mysql.jdbc.Driver");
            }
            if(conn != null) {
                conn.close();
                conn = null;
            }
            conn = DriverManager.getConnection(url, user, password);
            logger.info("Connection is Successful");
            if(stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnet() {
        try {
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isconnect() {

        if(conn == null) {
            logger.error("MySQL Connet Fail");
            return false;
        } else {
            logger.info("MySQL Connet Successful");
            return true;
        }
    }


    public List QueryAllData(String sql) {
        isconnect();

        ResultSet rs = null;
        List column = new ArrayList();
        Map tmep = new HashMap<String, String>();
        List result = new ArrayList();
        try {
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                column.add(metaData.getColumnName(i));
            }

            while (rs.next()) {
                tmep = new HashMap();
                for (int i = 0; i < columnCount; i++) {
                    tmep.put(column.get(i), rs.getString(column.get(i).toString()));
                }
                result.add(tmep);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean QuerySingle(String user_name) {

        String sql = "select * from userdata where user_name='" + user_name
                + "'";

        try {
            return QueryUser(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean QuerySingle(String user_name, String password)
            throws SQLException {

        String sql = "select * from userdata where user_name='" + user_name
                + "' And password='" + password + "'";
        return QueryUser(sql);
    }

    /**
     * @param sql
     * @return
     * @throws SQLException
     */

    public boolean QueryUser(String sql) throws SQLException {

        isconnect();

        ResultSet rs = stmt.executeQuery(sql);

        if(rs.next()) {
            logger.warn("This user already exists");

            System.out.println(rs.getInt(1) + "\t" + rs.getString("user_name")
                    + "\t" + rs.getString("password") + "\t"
                    + rs.getString("phone"));
            rs.close();
            return true;
        }
        return false;

    }

}