package com.amirulasri.carcontrol.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection con;

    public static Connection getDBConn() throws SQLException {
        if (con == null || con.isClosed()) {
            String url = "jdbc:mysql://localhost:3306/amirul_asri_projectswc4243";
            String user = "amirulasritestjavafx";
            String password = "ZlSLHwt*aE-8zRpO";
            con = DriverManager.getConnection(url, user, password);
            return con;
        } else {
            return con;
        }
    }

    public static void closeConnection() throws SQLException {
        con.close();
    }
}
