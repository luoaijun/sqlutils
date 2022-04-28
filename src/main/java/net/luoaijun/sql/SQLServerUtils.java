package net.luoaijun.sql;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

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
public class SQLServerUtils {
    static Statement statement = null;
    Logger logger = Logger.getLogger(SQLServerUtils.class);

    public static Statement getStatement() {
        return statement;
    }

    public static void setStatement(Statement statement) {
        SQLServerUtils.statement = statement;
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
    public ResultSet executPreQuery(Connection conn, String sql, String... obj) {
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
            if(obj != null) {
                for (int i = 0; i < obj.length; i++) {
                    pps.setObject(i + 1, obj[i]);
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pps;
    }

    /**
     * 获取一个Sqlserver连接
     *
     * @return
     */
    public Connection getSqlServerConn(String connectionUrl) {

        // Create a variable for the connection string.
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection con = DriverManager.getConnection(connectionUrl);

            logger.info("连接成功");
            return con;
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
            logger.error("连接失败");

        } catch (ClassNotFoundException e) {
            logger.error("连接失败");
            e.printStackTrace();
        }

        return null;
    }


    public Connection getSqlServerConnection() {
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
    public void executeUpdateSql(Connection connection, String sql) {
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
    public ResultSet executeQuerySql(Connection connection, String sql) {
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
     * @param c
     */
    public void close(Connection c) {
        try {
            if(statement != null) {
                statement.close();
                logger.info("连接关闭");
            }
            if(c != null) {
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
    public <T> List<T> getBeanList(Connection conn, Class<T> clazz, String sql, String... para)
            throws Exception {
        List<T> list = new ArrayList<T>();
        PreparedStatement pps = SQLServerUtils.getPreStatement(conn, sql, para);
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
     * @param conn
     * @param sql
     * @param para
     * @return
     * @throws Exception
     */
    public List getList(Connection conn, String sql, String... para) {
        List list = new ArrayList<Map>();
        PreparedStatement pps = SQLServerUtils.getPreStatement(conn, sql, para);
        ResultSet rs = null;
        try {
            rs = pps.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            HashMap tmep = new HashMap();
            List<String> column = new ArrayList<String>();
            for (int i = 1; i <= columnCount; i++) {
                column.add(metaData.getColumnName(i));
            }

            while (rs.next()) {
                tmep = new HashMap();
                for (int i = 0; i < columnCount; i++) {
                    tmep.put(column.get(i), rs.getString(column.get(i).toString()));
                }
                list.add(tmep);
            }
            logger.info("读取数据成功");
            pps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
        return list;
    }

    /**
     * TODO 获取总列数
     *
     * @return
     * @throws SQLException
     */
    public int getCount(ResultSet resultSet) throws SQLException {
        int count = 0;
        if(resultSet.next()) {
            count = resultSet.getInt(1);
        }
        return count;

    }

    public void printSys(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            System.out.println();
        }
    }

    /**
     * @param conn
     * @param sql
     * @param obj
     * @return
     * @throws SQLException
     */
    public ResultSet getResultSet(Connection conn, String sql, String... obj) throws SQLException {
        PreparedStatement pps = SQLServerUtils.getPreStatement(conn, sql, obj);
        ResultSet resultSet = pps.executeQuery();
        return resultSet;
    }

}
