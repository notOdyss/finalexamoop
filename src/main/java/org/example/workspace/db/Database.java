package org.example.workspace.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/workspace_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123456"; // свой пароль

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
