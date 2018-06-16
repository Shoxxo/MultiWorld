package de.skulldrago.multiworld.cmds;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;

public class cmd_settp implements CommandExecutor {
    Multiworld service = Multiworld.getPlugin();
    String prefix = service.getPrefix();
    File lang = new File("plugins/MultiWorld", "lang_de.yml");
    YamlConfiguration cfg3 = YamlConfiguration.loadConfiguration(lang);

    MySQL sql = Multiworld.getPlugin().getMysql();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("Multiworld.settp")) {

                World w = p.getWorld();

                String worldname = w.getName();

                Connection conn = sql.getConnection();
                ResultSet rs = null;
                PreparedStatement st = null;

                try {
                    st = conn.prepareStatement("SELECT owner FROM worlds WHERE worldname = '" + worldname + "'");
                    rs = st.executeQuery();

                    if (rs.next()) {
                        String owner = rs.getString("owner");

                        if (owner.equals(p.getName())) {

                            Location loc = p.getLocation();

                            Double x = loc.getX();
                            Double y = loc.getY();
                            Double z = loc.getZ();
                            Float yaw = loc.getYaw();
                            Float pitch = loc.getPitch();

                            sql.queryUpdate("UPDATE worlds SET spawnx = '" + x + "', spawny = '" + y + "', spawnz = '"
                                    + z + "', spawnyaw = '" + yaw + "', spawnpitch = '" + pitch
                                    + "' WHERE worldname = '" + w.getName() + "'");

                            if (cfg3.contains("Commands.Settp.Finish")) {
                                String msg = cfg3.getString("Commands.Settp.Finish");
                                msg = msg.replaceAll("&", "§");
                                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                p.sendMessage(msg);
                            } else {
                                p.sendMessage(prefix + " §cSpawnpunkt deiner Welt erfolgreich verlegt.");
                            }
                        } else {
                            if (cfg3.contains("Commands.Settp.OnlyOwner")) {
                                String msg = cfg3.getString("Commands.Settp.OnlyOwner");
                                msg = msg.replaceAll("&", "§");
                                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                p.sendMessage(msg);
                            } else {
                                p.sendMessage(prefix + " §cNur der Owner dieser Welt kann den Spawnpunkt verlegen.");
                            }
                        }

                    }
                } catch (SQLException e) {

                    if (cfg3.contains("Commands.Settp.Error")) {
                        String msg = cfg3.getString("Commands.Settp.Error");
                        msg = msg.replaceAll("&", "§");
                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                        p.sendMessage(msg);
                    } else {
                        p.sendMessage(prefix + " §cSpawnpunkt konnte nicht verlegt werden");
                    }

                    e.printStackTrace();
                }
            } else {
                if (cfg3.contains("System.NoPermission")) {
                    String msg = cfg3.getString("System.NoPermission");
                    msg = msg.replaceAll("&", "§");
                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                    p.sendMessage(msg);
                } else {
                    p.sendMessage(prefix + " §cDu hast nicht die Permissions um diesen Befehl zu benutzen.");
                }
            }
        } else {
            if (cfg3.contains("System.OnlyPlayers")) {
                String msg = cfg3.getString("System.OnlyPlayers");
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
