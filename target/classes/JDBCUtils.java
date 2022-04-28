package com.servier.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * TODO JDBC 相关工具类
 *
 * @author 罗爱军
 * @date 2018年3月8日
 * @email 3191287315@qq.com
 * @package JDBC-StudentInfoSystemcom.luoaijun.libraryJDBC.java
 * @describe TODO:
 * @include :
 * @category :
 */
public class JDBCUtils {
    static Statement statement = null;

    public static Statement getStatement() {
        return statement;
    }

    public static void setStatement(Statement statement) {
        JDBCUtils.statement = statement;
    }

    /**
     * TODO 使用prepareStatement 执行添加删除操作
     *
     * @param conn
     * @param sql
     * @param obj
     */
    public static void excutePreUpdate(Connection conn, String sql, String... obj) {
        PreparedStatement pps = null;
        pps = getPreStatement(conn, sql, obj);
        try {
            pps.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * TODO 使用prepareStatement 执行查询修改操作
     *
     * @param conn
     * @param sql
     * @param obj
     * @return resultSet
     */
    public static ResultSet executPreQuery(Connection conn, String sql, String... obj) {
        PreparedStatement pps = null;
        pps = getPreStatement(conn, sql, obj);
        ResultSet resultSet = null;
        try {
            resultSet = pps.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultSet;
    }

    /**
     * TODO 获取一个prepareStatementi
     *
     * @param conn
     * @param sql
     * @param obj
     * @return
     */
    public static PreparedStatement getPreStatement(Connection conn, String sql, String... obj) {
        PreparedStatement pps = null;
        try {
            pps = conn.prepareStatement(sql);
            for (int i = 0; i < obj.length; i++) {
                pps.setObject(i + 1, obj[i]);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pps;
    }

    /**
     * 获取一个Sqlserver连接
     * @return
     */
    public Connection getSqlServerConn() {

        // Create a variable for the connection string.
        String connectionUrl = "jdbc:sqlserver://cn1sw49:1433;databaseName=Poseidon;user=mdmuser;password=P@ssword";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection con = DriverManager.getConnection(connectionUrl);

            return con;
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("连接失败");

        } catch (ClassNotFoundException e) {
            System.out.println("连接失败");
            e.printStackTrace();
        }

        return null;
    }


    public Connection getSqlServerConnection()  {
        String driver = null;
        String jdbcUrl = null;
        String user = null;
        String password = null;

        try {
            InputStream inStream = getClass().getClassLoader().getResourceAsStream("db.properties");
            Properties properties = new Properties();
            properties.load(inStream);
            driver = properties.getProperty("sqlserver_driver");
            jdbcUrl = properties.getProperty("sqlserver_url");
            user = properties.getProperty("sqlserver_user");
            password = properties.getProperty("sqlserver_password");
            Class.forName(driver);// 加载驱动 Java项目中可以不加载，Javaweb中必须加载
            Connection connection = (Connection) DriverManager.getConnection(jdbcUrl, user, password);
            return connection;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Connection getInjectConnect(String connStr) {
        return null;
    }

    /**
     * TODO 使用statemen 执行删除添加操作
     *
     * @param connection
     * @param sql
     * @return 无返回
     */
    public static void execteUpdateSql(Connection connection, String sql) {
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * TODO 使用statement 获取一个更新查询操作的resultSet
     *
     * @param connection
     * @param sql
     * @return ResultSet
     */
    public static ResultSet execteQuerySql(Connection connection, String sql) {
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultSet;
    }

    /**
     * TODO 关闭jdbc相关连接
     *
     * @param statement
     * @param c
     */
    public static void close(Statement statement, Connection c) {
        try {
            if (statement != null) {
                statement.close();
            }
            if (c != null) {
                c.close();
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * TODO 获取一个list<T>
     *
     * @param clazz
     * @param sql
     * @param para
     * @return
     * @throws Exception
     */
    public static <T> List<T> getBeanList(Connection conn, Class<T> clazz, String sql, String... para)
            throws Exception {
        List<T> list = new ArrayList<T>();
        PreparedStatement pps = JDBCUtils.getPreStatement(conn, sql, para);
        ResultSet resultSet = pps.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columCount = metaData.getColumnCount();

        while (resultSet.next()) {
            T t = clazz.newInstance();
            for (int i = 0; i < columCount; i++) {
                String columName = metaData.getColumnLabel(i + 1);
                Object values = resultSet.getObject(i + 1);
                Field field = clazz.getDeclaredField(columName);
                field.setAccessible(true);
                field.set(t, values);
            }
            list.add(t);
        }
        return list;

    }

    /**
     * TODO 获取总列数
     *
     * @return
     * @throws SQLException
     */
    public static int getCount(ResultSet resultSet) throws SQLException {
        int count = 0;
        if (resultSet.next()) {
            count = resultSet.getInt(1);
        }
        return count;

    }

    public static void printSys(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            System.out.println();
        }
    }

    public static ResultSet getResultSet(Connection conn, String sql, String... obj) throws SQLException {
        PreparedStatement pps = JDBCUtils.getPreStatement(conn, sql, obj);
        ResultSet resultSet = pps.executeQuery();
        return resultSet;
    }

}
