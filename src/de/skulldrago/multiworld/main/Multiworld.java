package de.skulldrago.multiworld.main;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import de.skulldrago.multiworld.cmds.cmd_addresident;
import de.skulldrago.multiworld.cmds.cmd_create;
import de.skulldrago.multiworld.cmds.cmd_createserver;
import de.skulldrago.multiworld.cmds.cmd_createvoid;
import de.skulldrago.multiworld.cmds.cmd_delete;
import de.skulldrago.multiworld.cmds.cmd_delresident;
import de.skulldrago.multiworld.cmds.cmd_import;
import de.skulldrago.multiworld.cmds.cmd_leave;
import de.skulldrago.multiworld.cmds.cmd_list;
import de.skulldrago.multiworld.cmds.cmd_load;
import de.skulldrago.multiworld.cmds.cmd_lock;
import de.skulldrago.multiworld.cmds.cmd_setowner;
import de.skulldrago.multiworld.cmds.cmd_worldinfo;
import de.skulldrago.multiworld.cmds.cmd_settp;
import de.skulldrago.multiworld.cmds.cmd_tpa;
import de.skulldrago.multiworld.cmds.cmd_tpaccept;
import de.skulldrago.multiworld.cmds.cmd_tpadeny;
import de.skulldrago.multiworld.cmds.cmd_tpahere;
import de.skulldrago.multiworld.cmds.cmd_tpahereaccept;
import de.skulldrago.multiworld.cmds.cmd_tpaheredeny;
import de.skulldrago.multiworld.cmds.cmd_tpw;
import de.skulldrago.multiworld.cmds.cmd_unload;
import de.skulldrago.multiworld.cmds.cmd_unlock;
import de.skulldrago.multiworld.completer.createCompleter;
import de.skulldrago.multiworld.files.FileCheckWorld;
import de.skulldrago.multiworld.listener.WorldListener;
import de.skulldrago.multiworld.mysql.MySQL;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class Multiworld extends JavaPlugin {
    public static Multiworld m;

    private static Multiworld plugin;

    public static Multiworld getPlugin() {
        return plugin;
    }

    private MySQL sql;

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
            sql.queryUpdate(
                    "CREATE TABLE IF NOT EXISTS worlds (worldname VARCHAR(25), owner VARCHAR(25), locked VARCHAR(25), type VARCHAR(25), spawnx DOUBLE, spawny DOUBLE, spawnz DOUBLE, spawnyaw FLOAT, spawnpitch FLOAT)");
            sql.queryUpdate(
                    "CREATE TABLE IF NOT EXISTS worldresidents (worldname VARCHAR(25), type VARCHAR(25), resident VARCHAR(25))");
            sql.queryUpdate("CREATE TABLE IF NOT EXISTS tprequests (name VARCHAR(25), requestname VARCHAR(25))");
            sql.queryUpdate("CREATE TABLE IF NOT EXISTS tphererequests (requestname VARCHAR(25), name VARCHAR(25))");
            sql.queryUpdate(
                    "CREATE TABLE IF NOT EXISTS worldplayers (name VARCHAR(25), max INT(5), numbers INT(5), vmax INT(5), vnumbers INT(5))");

            Bukkit.getConsoleSender().sendMessage(this.getPrefix() + " �aMySQL Verbindung hergestellt!");
            registerCommands();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(this.getPrefix() + " �cMySQL Verbindung fehlgeschlagen!");
        }

        checkFiles();
        this.saveLang();
        registerCommands();
        worldAutoload();
        this.getServer().getPluginManager().registerEvents(new WorldListener(), this);
        lockworlds();

        Bukkit.getConsoleSender().sendMessage(this.getPrefix() + " �aSkullTechMultiworld wurde erfolgreich geladen!");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(this.getPrefix() + " �cSkullTechMultiworld wurde deaktiviert!");

    }

    public MySQL getMysql() {
        return this.sql;
    }

    public static Multiworld getInstance() {
        return m;
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
        getCommand("wlist").setExecutor(new cmd_list());
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
                Double x = loc.getX();
                Double y = loc.getY();
                Double z = loc.getZ();
                Float yaw = loc.getYaw();
                Float pitch = loc.getPitch();

                sql.queryUpdate("UPDATE worlds SET spawnx = '" + x + "', spawny = '" + y + "', spawnz = '" + z
                        + "', spawnyaw = '" + yaw + "', spawnpitch = '" + pitch + "' WHERE worldname = '" + w + "'");
            }
        }
    }

    public static String concat(String[] s, int start, int end) {
        String[] args = (String[]) Arrays.copyOfRange(s, start, end);
        return StringUtils.join(args, " ");
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public String getPrefix() {
        String prefix = "";
        if (this.getConfig().contains("System.Prefix")) {
            prefix = this.getConfig().getString("System.Prefix");
            prefix = prefix.replaceAll("&", "�");
            return prefix;
        } else {
            prefix = "�8[�4Multiworld�8] �r";
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
                inv.setItem(i, new ItemStack(Material.WORKBENCH));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.hasItem()) {
            if (e.getItem().getType() == Material.WORKBENCH) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "multiworld create " + e.getPlayer().getUniqueId() + " normal normal");
            }
        }
    }
}
