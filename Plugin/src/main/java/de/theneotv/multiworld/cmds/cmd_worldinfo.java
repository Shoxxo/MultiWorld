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
import java.util.ArrayList;
import java.util.List;

public class cmd_worldinfo implements CommandExecutor {
    Multiworld service = Multiworld.getPlugin();
    String prefix = service.getPrefix();
    File lang = new File("plugins/MultiWorld", "lang_de.yml");
    YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);

    MySQL sql = Multiworld.getPlugin().getMysql();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                String world = p.getWorld().getName();
                World w = Bukkit.getWorld(world);

                Connection conn = sql.getConnection();
                ResultSet rs = null;
                PreparedStatement st = null;
                ResultSet rs2 = null;
                PreparedStatement st2 = null;

                try {
                    st = conn.prepareStatement("SELECT * FROM worlds WHERE worldname = '" + w.getName() + "'");
                    rs = st.executeQuery();

                    st2 = conn.prepareStatement("SELECT * FROM worldresidents WHERE worldname = '" + w.getName() + "'");
                    rs2 = st2.executeQuery();

                    if (rs.next()) {
                        String owner = rs.getString("owner");

                        List<String> residents = new ArrayList<String>();
                        if (rs2.next()) {
                            while (rs2.next()) {
                                residents.add(rs2.getString("resident"));
                            }
                        }
                        p.sendMessage("§3=============================" + "\n");
                        p.sendMessage("§3= Name: " + w.getName() +        "\n");
                        p.sendMessage("§3=============================" + "\n");
                        p.sendMessage("§3= Besitzer: " + owner +          "\n");
                        p.sendMessage("§3= Bewohner: " + residents);
                        p.sendMessage("§3=============================" + "\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
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

        } else if (args.length == 1) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                String world = args[0];
                World w = Bukkit.getWorld(world);
                if (w != null) {
                    Connection conn = sql.getConnection();
                    ResultSet rs = null;
                    PreparedStatement st = null;
                    ResultSet rs2 = null;
                    PreparedStatement st2 = null;

                    try {
                        st = conn.prepareStatement("SELECT * FROM worlds WHERE worldname = '" + w.getName() + "'");
                        rs = st.executeQuery();

                        st2 = conn.prepareStatement(
                                "SELECT * FROM worldresidents WHERE worldname = '" + w.getName() + "'");
                        rs2 = st2.executeQuery();

                        if (rs.next()) {
                            String owner = rs.getString("owner");

                            List<String> residents = new ArrayList<String>();
                            if (rs2.next()) {
                                while (rs2.next()) {
                                    residents.add(rs2.getString("resident"));
                                }
                            }

                            p.sendMessage("?3" + w.getName() + ": " + "\n");
                            p.sendMessage("?3----------------------" + "\n");
                            p.sendMessage("?3Besitzer: " + owner + "\n");
                            p.sendMessage("?3Bewohner: " + residents);

                        } else {
                            if (cfg2.contains("Commands.Tpworld.Error")) {
                                String msg = cfg2.getString("Commands.Tpworld.Error");
                                msg = msg.replaceAll("&", "§");
                                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                p.sendMessage(msg);
                            } else {
                                p.sendMessage(prefix + " §cFehler: Welt existiert nicht");
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (cfg2.contains("Commands.Tpworld.Error")) {
                        String msg = cfg2.getString("Commands.Tpworld.Error");
                        msg = msg.replaceAll("&", "§");
                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                        p.sendMessage(msg);
                    } else {
                        p.sendMessage(prefix + " §cFehler: Welt existiert nicht");
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

        } else {
            if (cfg2.contains("Commands.Worldinfo.WrongSyntax")) {
                String msg = cfg2.getString("Commands.Worldinfo.WrongSyntax");
                msg = msg.replaceAll("&", "§");
                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                sender.sendMessage(msg);
            } else {
                sender.sendMessage(prefix + " §cFalsche Syntax. Benutze bitte /worldinfo oder /worldinfo <Weltname>");
            }
        }
        return true;
    }
}
