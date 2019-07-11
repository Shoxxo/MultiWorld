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

public class cmd_delresident implements CommandExecutor {
    Multiworld service = Multiworld.getPlugin();
    String prefix = service.getPrefix();
    File lang = new File("plugins/MultiWorld", "lang_de.yml");
    YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);

    MySQL sql = Multiworld.getPlugin().getMysql();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.hasPermission("Multiworld.residents") || p.hasPermission("Multiworld.all")) {
                    World w = p.getWorld();

                    Connection conn = sql.getConnection();
                    ResultSet rs = null;
                    PreparedStatement st = null;

                    try {
                        st = conn.prepareStatement(
                                "SELECT worldname, owner FROM worlds WHERE worldname='" + w.getName() + "'");
                        rs = st.executeQuery();
                        if (rs.next()) {
                            String owner = rs.getString("Owner");
                            if (owner.equals(p.getName())) {
                                Player target = Bukkit.getPlayer(args[0]);
                                if (target != null) {
                                    sql.queryUpdate("DELETE FROM worldresidents WHERE resident = '" + target.getName()
                                            + "' and worldname = '" + w.getName() + "'");

                                    target.teleport(Bukkit.getWorld("world").getSpawnLocation());

                                    if (cfg2.contains("Commands.Delresident.FinishPlayer")) {
                                        String msg = cfg2.getString("Commands.Delresident.FinishPlayer");
                                        msg = msg.replaceAll("&", "§");
                                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                        target.sendMessage(msg);
                                    } else {
                                        target.sendMessage(prefix + " §cDu wurdest als Mitbewohner entfernt.");
                                    }

                                    if (cfg2.contains("Commands.Delresident.Finish")) {
                                        String msg = cfg2.getString("Commands.Delresident.Finish");
                                        msg = msg.replaceAll("&", "§");
                                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                        msg = msg.replaceAll("%player%", "" + target.getName() + "");
                                        p.sendMessage(msg);
                                    } else {
                                        p.sendMessage(prefix + " §c" + target.getName()
                                                + " wurde nun als Mitbewohner entfernt");
                                    }

                                } else {
                                    if (cfg2.contains("Commands.Delresident.NotOnline")) {
                                        String msg = cfg2.getString("Commands.Delresident.NotOnline");
                                        msg = msg.replaceAll("&", "§");
                                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                        p.sendMessage(msg);

                                    } else {
                                        p.sendMessage(prefix + " §cDer Spieler ist nicht online.");
                                    }
                                }
                            } else {
                                if (cfg2.contains("Commands.Delresident.OnlyOwner")) {
                                    String msg = cfg2.getString("Commands.Delresident.OnlyOwner");
                                    msg = msg.replaceAll("&", "§");
                                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                    p.sendMessage(msg);

                                } else {
                                    p.sendMessage(prefix + " §cNur der Owner der Welt darf dies tun.");
                                }
                            }
                        }
                    } catch (SQLException e) {
                        if (cfg2.contains("Commands.Delresident.Error")) {
                            String msg = cfg2.getString("Commands.Delresident.Error");
                            msg = msg.replaceAll("&", "§");
                            msg = msg.replaceAll("%prefix%", "" + prefix + "");
                            p.sendMessage(msg);
                        } else {
                            p.sendMessage(prefix + " §cVorgang wurde wegen Fehler abgebrochen.");
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
        } else {
            if (cfg2.contains("Commands.Delresident.WrongSyntax")) {
                String msg = cfg2.getString("Commands.Delresident.WrongSyntax");
                msg = msg.replaceAll("&", "§");
                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                sender.sendMessage(msg);

            } else {
                sender.sendMessage(prefix + " §cFalsche Syntax. Bitte benutze /delresident <Spielername>");
            }
        }
        return true;
    }

}
