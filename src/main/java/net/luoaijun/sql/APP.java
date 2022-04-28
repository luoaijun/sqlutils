package net.luoaijun.sql;

import com.alibaba.fastjson.JSON;
import com.mysql.cj.xdevapi.JsonArray;
import jdk.nashorn.internal.scripts.JD;

import java.sql.Connection;
import java.util.List;

/**
 * @author LUO AI JUN
 * @date 2022/4/27 9:55
 */
public class APP {
//        String url = "jdbc:mysql://120.79.211.234:3306/niuke";
    String url = "jdbc:sqlserver://cn1sw49:1433;databaseName=Poseidon;user=mdmuser;password=P@ssword";
    String user = "root";
    String password = "luoaijun";
//        String sql = "select * from product_tb";
    String sql = "select * from Proxy.dbo.cmb_bill_data";
    String version = "8";
    String filePath = "C:\\work\\data\\product_tb.json";

    public static void main(String[] args) {
        APP app = new APP();
        if(app.url.contains("mysql")) {
            app.mysqlServer();
        } else {
            app.sqlServer();
        }

    }

    public void mysqlServer() {
        MySQLUtils utils = new MySQLUtils();
        utils.connetDatabse(url, user, password, version);
        utils.isconnect();
        List list = utils.QueryAllData(sql);
        String s =JSON.toJSON(list).toString();
        JSONUtils.saveFile(s, filePath, false);
        utils.closeConnet();
    }

    public void sqlServer() {
        SQLServerUtils jdbcUtils = new SQLServerUtils();
        Connection conn = jdbcUtils.getSqlServerConn(url);
        List list = jdbcUtils.getList(conn, sql, null);
        JSONUtils.saveFile(JSON.toJSON(list).toString(), filePath, false);
        jdbcUtils.close(conn);
    }
}
