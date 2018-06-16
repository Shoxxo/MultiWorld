package de.skulldrago.multiworld.cmds;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;

public class cmd_tpaheredeny implements CommandExecutor {
    Multiworld service = Multiworld.getPlugin();
    String prefix = service.getPrefix();
    File lang = new File("plugins/MultiWorld", "lang_de.yml");
    YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);

    MySQL sql = Multiworld.getPlugin().getMysql();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("Multiworld.tpahere")) {

                Connection conn = sql.getConnection();
                ResultSet rs = null;
                PreparedStatement st = null;

                try {
                    st = conn.prepareStatement(
                            "SELECT requestname FROM tphererequests WHERE name = '" + p.getName() + "'");
                    rs = st.executeQuery();

                    if (rs.next()) {
                        String player = rs.getString("requestname");

                        if (player != null) {
                            Player target = Bukkit.getPlayer(player);
                            if (target != null) {

                                if (cfg2.contains("Commands.Tpahereadeny.Denied")) {
                                    String msg = cfg2.getString("Commands.Tpahereadeny.Denied");
                                    msg = msg.replaceAll("&", "§");
                                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                    msg = msg.replaceAll("%player%", "" + p.getName() + "");
                                    target.sendMessage(msg);
                                } else {
                                    target.sendMessage(prefix + " §c" + p.getName() + " hat die Anfrage verweigert.");
                                }

                                sql.queryUpdate(
                                        "DELETE FROM tphererequests WHERE requestname='" + target.getName() + "'");

                            } else {
                                if (cfg2.contains("Commands.Tphereadeny.NotOnline")) {
                                    String msg = cfg2.getString("Commands.Tpahereadeny.NotOnline");
                                    msg = msg.replaceAll("&", "§");
                                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                    p.sendMessage(msg);
                                } else {
                                    p.sendMessage(prefix + " §cSpieler ist nicht online.");
                                }
                            }
                        } else {
                            if (cfg2.contains("Commands.Tpahereadeny.NoRequest")) {
                                String msg = cfg2.getString("Commands.Tpahereadeny.NoRequest");
                                msg = msg.replaceAll("&", "§");
                                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                p.sendMessage(msg);
                            } else {
                                p.sendMessage(prefix + " §cDu hast keine Teleportanfragen.");
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
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
