package de.theneotv.multiworld.main;

import de.theneotv.multiworld.cmds.*;
import de.theneotv.multiworld.completer.createCompleter;
import de.theneotv.multiworld.files.FileCheckWorld;
import de.theneotv.multiworld.listener.WorldListener;
import de.theneotv.multiworld.mysql.MySQL;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Multiworld extends JavaPlugin {
    public static Multiworld m;

    private static Multiworld plugin;
    private MySQL sql;

    public static Multiworld getPlugin() {
        return plugin;
    }

    public static Multiworld getInstance() {
        return m;
    }

    public static String concat(String[] s, int start, int end) {
        String[] args = Arrays.copyOfRange(s, start, end);
        return StringUtils.join(args, " ");
    }

    @Override
    public void onEnable() {
        m = this;
        plugin = this;

        File c = new File("plugins/MultiWorld", "config.yml");

        if (!(c.exists())) {
            loadConfig();
        }

        try {
            this.sql = new MySQL();
            sql.queryUpdate("CREATE TABLE IF NOT EXISTS worlds (worldname VARCHAR(25), owner VARCHAR(64), locked VARCHAR(25), type VARCHAR(25), spawnx DOUBLE, spawny DOUBLE, spawnz DOUBLE, spawnyaw FLOAT, spawnpitch FLOAT)");
            sql.queryUpdate("CREATE TABLE IF NOT EXISTS worldresidents (worldname VARCHAR(25), type VARCHAR(25), resident VARCHAR(25))");
            sql.queryUpdate("CREATE TABLE IF NOT EXISTS tprequests (name VARCHAR(64), requestname VARCHAR(64))");
            sql.queryUpdate("CREATE TABLE IF NOT EXISTS tphererequests (requestname VARCHAR(64), name VARCHAR(64))");
            sql.queryUpdate("CREATE TABLE IF NOT EXISTS worldplayers (name VARCHAR(64), max INT(5), numbers INT(5), vmax INT(5), vnumbers INT(5))");

            Bukkit.getConsoleSender().sendMessage(this.getPrefix() + " §a[MySQL] Verbindung zur Datenbank erfolgreich hergestellt!");
            registerCommands();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(this.getPrefix() + " §c[MySQL] Es konnte keine Verbundung zur Datenbank hergestellt werden!");
        }

        checkFiles();
        this.saveLang();
        registerCommands();
        worldAutoload();
        this.getServer().getPluginManager().registerEvents(new WorldListener(), this);
        lockworlds();

        Bukkit.getConsoleSender().sendMessage(this.getPrefix() + " §a[MultiWorld] Plugin wurde erfolgreich aktiviert!");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(this.getPrefix() + " §c[MultiWorld] Plugin wurde deaktiviert!");

    }

    public MySQL getMysql() {
        return this.sql;
    }

    public void checkFiles() {
        FileCheckWorld.checkfolder();
        FileCheckWorld.checkBackupVoid();
        FileCheckWorld.checkVoidSource();
        FileCheckWorld.checkVanillaW();
    }

    public void registerCommands() {
        getCommand("createworld").setTabCompleter(new createCompleter());
        getCommand("unlock").setExecutor(new cmd_unlock());
        getCommand("lock").setExecutor(new cmd_lock());
        getCommand("createworld").setExecutor(new cmd_create());
        getCommand("leaveworld").setExecutor(new cmd_leave());
        getCommand("deleteworld").setExecutor(new cmd_delete());
        getCommand("tpworld").setExecutor(new cmd_tpw());
        getCommand("addresident").setExecutor(new cmd_addresident());
        getCommand("delresident").setExecutor(new cmd_delresident());
        getCommand("setowner").setExecutor(new cmd_setowner());
        getCommand("worldinfo").setExecutor(new cmd_worldinfo());
        getCommand("settpworld").setExecutor(new cmd_settp());
        getCommand("createvoid").setExecutor(new cmd_createvoid());
        getCommand("worldlist").setExecutor(new cmd_list());
        getCommand("load").setExecutor(new cmd_load());
        getCommand("unload").setExecutor(new cmd_unload());
        getCommand("createserverworld").setExecutor(new cmd_createserver());
        getCommand("tpa").setExecutor(new cmd_tpa());
        getCommand("tpaccept").setExecutor(new cmd_tpaccept());
        getCommand("tpahere").setExecutor(new cmd_tpahere());
        getCommand("tpadeny").setExecutor(new cmd_tpadeny());
        getCommand("tpahereaccept").setExecutor(new cmd_tpahereaccept());
        getCommand("tpaheredeny").setExecutor(new cmd_tpaheredeny());
        getCommand("import").setExecutor(new cmd_import());
    }

    public void worldAutoload() {
        List<String> wautolist = this.getConfig().getStringList("System.Autoload");
        if (!(wautolist.isEmpty())) {
            for (int i = 0; i < wautolist.size(); i++) {
                String w = wautolist.get(i);
                Bukkit.getServer().createWorld(new WorldCreator(w));
                World wo = Bukkit.getWorld(w);
                Location loc = wo.getSpawnLocation();
                double x = loc.getX();
                double y = loc.getY();
                double z = loc.getZ();
                float yaw = loc.getYaw();
                float pitch = loc.getPitch();

                sql.queryUpdate("UPDATE worlds SET spawnx = '" + x + "', spawny = '" + y + "', spawnz = '" + z + "', spawnyaw = '" + yaw + "', spawnpitch = '" + pitch + "' WHERE worldname = '" + w + "'");
            }
        }
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public String getPrefix() {
        String prefix = "";
        if (this.getConfig().contains("System.Prefix")) {
            prefix = this.getConfig().getString("System.Prefix");
            prefix = prefix.replaceAll("&", "§");
            return prefix;
        } else {
            prefix = "§8[§4MultiWorld§8] §r";
            return prefix;
        }
    }

    public void saveLang() {
        if (!new File(getDataFolder(), "lang_de.yml").exists()) {
            saveResource("lang_de.yml", false);
        }
    }

    public void lockworlds() {
        List<World> worlds = Bukkit.getServer().getWorlds();

        for (World world : worlds) {
            String wname = world.getName();
            Connection conn = sql.getConnection();
            ResultSet rs = null;
            PreparedStatement st = null;

            try {
                st = conn.prepareStatement("SELECT owner, locked FROM worlds WHERE worldname = '" + wname + "'");
                rs = st.executeQuery();

                if (rs.next()) {
                    String owner = rs.getString("owner");
                    String locked = rs.getString("locked");

                    if (!(owner.equals("Server"))) {
                        locked = "true";
                        sql.queryUpdate(
                                "UPDATE worlds SET locked = '" + locked + "' WHERE worldname = '" + wname + "'");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasPlayedBefore()) {
            PlayerInventory inv = e.getPlayer().getInventory();
            for (int i = 0; i < 9; i++) {
                inv.setItem(i, new ItemStack(Material.SIGN));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.hasItem()) {
            if (e.getItem().getType() == Material.SIGN) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "multiworld create " + e.getPlayer().getUniqueId() + " normal normal");
            }
        }
    }
}
