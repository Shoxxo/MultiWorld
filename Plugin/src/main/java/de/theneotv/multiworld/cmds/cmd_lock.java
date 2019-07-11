package de.theneotv.multiworld.cmds;

import de.theneotv.multiworld.main.Multiworld;
import de.theneotv.multiworld.mysql.MySQL;
import org.bukkit.Bukkit;
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
import java.util.List;

public class cmd_lock implements CommandExecutor {
    Multiworld service = Multiworld.getPlugin();
    String prefix = service.getPrefix();
    File lang = new File("plugins/MultiWorld", "lang_de.yml");
    YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);

    MySQL sql = Multiworld.getPlugin().getMysql();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("Multiworld.lock") || p.hasPermission("Multiworld.all")) {
                World w = p.getWorld();

                Connection conn = sql.getConnection();
                ResultSet rs = null;
                PreparedStatement st = null;

                ResultSet rs2 = null;
                PreparedStatement st2 = null;

                try {
                    st = conn.prepareStatement(
                            "SELECT worldname, owner FROM worlds WHERE worldname='" + w.getName() + "'");
                    rs = st.executeQuery();

                    st2 = conn.prepareStatement(
                            "SELECT resident FROM worldresidents WHERE worldname='" + w.getName() + "'");
                    rs2 = st2.executeQuery();

                    if ((rs.next())) {
                        String owner = rs.getString("owner");
                        List<Player> worldplayers = w.getPlayers();

                        if (owner.equals(p.getName()) || p.hasPermission("Multiworld.all")) {
                            worldplayers.remove(p);

                            for (Player t : worldplayers) {
                                if (!(t.hasPermission("Multiworld.admin"))) {
                                    st2 = conn
                                            .prepareStatement("SELECT resident FROM worldresidents WHERE worldname = '"
                                                    + w.getName() + "' AND resident = '" + t.getName() + "'");
                                    rs2 = st2.executeQuery();

                                    if (!(rs2.next())) {
                                        t.teleport(Bukkit.getWorld("world").getSpawnLocation());

                                        if (cfg2.contains("Commands.Lockworld.FinishPlayer")) {
                                            String msg = cfg2.getString("Commands.Lockworld.FinishPlayer");
                                            msg = msg.replaceAll("&", "§");
                                            msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                            t.sendMessage(msg);
                                        } else {
                                            t.sendMessage(
                                                    prefix + " §cDer Owner dieser Welt hat die Welt wieder gelockt.");
                                        }
                                    }
                                }
                            }
                        }
                        String locked = "true";

                        sql.queryUpdate(
                                "UPDATE worlds SET locked='" + locked + "' WHERE worldname='" + w.getName() + "'");

                        if (cfg2.contains("Commands.Lockworld.Finish")) {
                            String msg = cfg2.getString("Commands.Lockworld.Finish");
                            msg = msg.replaceAll("&", "§");
                            msg = msg.replaceAll("%prefix%", "" + prefix + "");
                            p.sendMessage(msg);
                        } else {
                            p.sendMessage(prefix
                                    + " §cDeine Welt ist jetzt gelockt und kann nur von dir und deinen Mitbewohnern betreten werden");
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
