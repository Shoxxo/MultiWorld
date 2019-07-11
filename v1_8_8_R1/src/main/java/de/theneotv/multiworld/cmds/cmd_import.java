package de.theneotv.multiworld.cmds;

import de.theneotv.multiworld.main.Multiworld;
import de.theneotv.multiworld.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class cmd_import implements CommandExecutor {
    Multiworld service = Multiworld.getPlugin();
    String prefix = service.getPrefix();
    File lang = new File("plugins/MultiWorld", "lang_de.yml");
    YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);

    MySQL sql = Multiworld.getPlugin().getMysql();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("Multiworld.import")) {
                World w = Bukkit.getWorld(args[0]);
                if (w != null) {

                    Connection conn = sql.getConnection();
                    ResultSet rs = null;
                    PreparedStatement st = null;

                    try {
                        st = conn.prepareStatement("SELECT * FROM worlds WHERE worldname='" + w.getName() + "'");
                        rs = st.executeQuery();

                        if (rs.next()) {
                            if (cfg2.contains("Commands.Import.Error")) {
                                String msg = cfg2.getString("Commands.Import.Error");
                                msg = msg.replaceAll("&", "§");
                                msg = msg.replaceAll("%world%", "" + w.getName() + "");
                                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                p.sendMessage(msg);
                            } else {
                                p.sendMessage(
                                        prefix + "§cDie Welt " + w.getName() + " existiert bereits in der Datenbank!");
                            }
                        } else {

                            Location loc = w.getSpawnLocation();
                            double x = loc.getX();
                            double y = loc.getY();
                            double z = loc.getZ();
                            float yaw = loc.getYaw();
                            float pitch = loc.getPitch();
                            String owner = "Server";
                            String locked = "false";

                            sql.queryUpdate(
                                    "INSERT INTO worlds (worldname, owner, locked, spawnx, spawny, spawnz, spawnyaw, spawnpitch) VALUES ('"
                                            + w.getName() + "', '" + owner + "', '" + locked + "', '" + x + "', '" + y
                                            + "', '" + z + "', '" + yaw + "', '" + pitch + "')");

                            if (cfg2.contains("Commands.Import.Finish")) {
                                String msg = cfg2.getString("Commands.Import.Finish");
                                msg = msg.replaceAll("&", "§");
                                msg = msg.replaceAll("%world%", "" + w.getName() + "");
                                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                p.sendMessage(msg);
                            } else {
                                p.sendMessage(prefix + "§aDie Welt " + w.getName() + " wurde erfolgreich importiert");
                            }

                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (cfg2.contains("Commands.Import.WorldError")) {
                        String msg = cfg2.getString("Commands.Import.WorldError");
                        msg = msg.replaceAll("&", "§");
                        msg = msg.replaceAll("%world%", "" + args[0] + "");
                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                        p.sendMessage(msg);
                    } else {
                        p.sendMessage(prefix + "§cDie Welt " + args[0] + " existiert nicht!");
                    }
                }
            } else {
                if (cfg2.contains("System.NoPermission")) {
                    String msg = cfg2.getString("System.NoPermission");
                    msg = msg.replaceAll("&", "§");
                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                    p.sendMessage(msg);

                } else {
                    p.sendMessage(prefix + " §cDu hast nicht die Permissions um diesen Befehl zu benutzen.");
                }
            }
        } else {
            if (cfg2.contains("System.OnlyPlayers")) {
                String msg = cfg2.getString("System.OnlyPlayers");
                msg = msg.replaceAll("&", "§");
                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                sender.sendMessage(msg);

            } else {
                sender.sendMessage(prefix + " §cNur Spieler duerfen diesen Befehl benutzen!");
            }
        }

        return true;
    }
}
