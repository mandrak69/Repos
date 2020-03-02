/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author Jovan
 *
 * Ova klasa sadrzi metode pomocu kojih se prilikom slanja upita u bazu
 * uspostavlja konekcija sa bazom.
 */
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbUtil {
   

    private static Connection conn = null;
    private static DbUtil instance = null;

    private DbUtil() {

    }

    public static DbUtil getInstance() {
        if (instance == null) {
            instance = new DbUtil();
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }

    static {
        String dbname = "primer";
        String port = "3306";
        String user = "root";
        String password = "";
        String url = "jdbc:mysql://localhost:" + port + "/" + dbname;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DbUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   

   
}
