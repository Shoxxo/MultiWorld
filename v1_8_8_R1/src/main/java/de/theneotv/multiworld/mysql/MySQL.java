package de.theneotv.multiworld.mysql;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import java.sql.*;

public class MySQL {

    private String host;
    private String user;
    private String password;
    private String database;
    private Connection conn;

    public MySQL() throws Exception {

        File file = new File("plugins/MultiWorld/", "mysql.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        String db = "database.";
        cfg.addDefault(db + "host", "localhost");
        cfg.addDefault(db + "user", "user");
        cfg.addDefault(db + "password", "password");
        cfg.addDefault(db + "database", "database");
        cfg.options().copyDefaults(true);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.host = cfg.getString(db + "host");
        this.user = cfg.getString(db + "user");
        this.password = cfg.getString(db + "password");
        this.database = cfg.getString(db + "database");

        this.openConnection();
    }

    public boolean openConnection() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String connectionCommand = "jdbc:mysql://"+host+"/"+database+"?user="+user+"&password="+password;
            Connection conn = DriverManager.getConnection(connectionCommand);
        /*Connection conn = DriverManager.getConnection(
                "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.user, this.password);

        */
            this.conn = conn;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public Connection getConnection() {
        return this.conn;
    }

    public boolean hasConnection() {
        try {
            return this.conn != null || this.conn.isValid(1);
        } catch (SQLException e) {
            return false;
        }
    }

    public void queryUpdate(String query) {
        Connection conn = this.conn;
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(query);
            st.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§c[MySQL] Kann update nicht senden '" + query + "'.");
        } finally {
            this.CloseRessources(null, st);
        }
    }

    public void CloseRessources(ResultSet rs, PreparedStatement st) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
            }
        }
    }

    public void closeConnection() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.conn = null;
        }
    }
}
