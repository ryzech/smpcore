package net.ryzech.smpcore.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ryzech.smpcore.SmpCorePlugin;
import org.bukkit.Bukkit;

public class MySQL {

    public static SmpCorePlugin plugin;
    public FileUtils fileUtils;

    public MySQL(SmpCorePlugin plugin) {this.plugin = plugin;}
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    // connect
    public static void connect() {
        FileUtils fileUtils;
        fileUtils = new FileUtils(plugin);
        String host = fileUtils.getMain().getString("db.host");
        String port = fileUtils.getMain().getString("db.port");
        String database = fileUtils.getMain().getString("db.database");
        String username = fileUtils.getMain().getString("db.username");
        String password = fileUtils.getMain().getString("db.password");

        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?serverTimezone=UTC");
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(fileUtils.getMain().getInt("db.connections"));
        ds = new HikariDataSource(config);
    }

    // isConnected
    public static boolean isConnected() {
        return (!ds.isRunning() ? false : true);
    }

    // getConnection
    public static synchronized ResultSet query(String query) throws SQLException {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    ds.getConnection().prepareStatement(query).executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }

    public static synchronized boolean update(String query) throws SQLException {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    ds.getConnection().prepareStatement(query).execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        return false;
    }

    public static synchronized void close() throws SQLException {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (!ds.isClosed()) {
                    try {
                        ds.getConnection().close();
                        Bukkit.getLogger().info("SmpCore disconnected from the database.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static synchronized Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}