package com.amirulasri.carcontrol.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection con;

    public void getDBConn() throws SQLException {
        if (getCon() == null || getCon().isClosed()) {
            String url = "jdbc:mysql://localhost:3306/amirul_asri_projectswc4243";
            String user = "amirulasritestjavafx";
            String password = "ZlSLHwt*aE-8zRpO";
            setCon(DriverManager.getConnection(url, user, password));
        }
    }

    public static Connection getCon() {
        return con;
    }

    public static void setCon(Connection aCon) {
        con = aCon;
    }

    public static void closeConnection() throws SQLException {
        con.close();
    }
}
