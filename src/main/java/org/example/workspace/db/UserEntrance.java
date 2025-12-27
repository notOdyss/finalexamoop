package org.example.workspace.db;

import java.sql.*;

public class UserEntrance {

    public static boolean register(String username, String password)
            throws Exception {

        String sql = "INSERT INTO users(username, password) VALUES (?, ?)";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        }
    }

    public static boolean login(String username, String password)
            throws Exception {

        String sql =
                "SELECT 1 FROM users WHERE username=? AND password=?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }
}
