package de.skulldrago.multiworld.cmds;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;

public class cmd_tpahereaccept implements CommandExecutor {
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
				ResultSet rs2 = null;
				PreparedStatement st2 = null;
				ResultSet rs3 = null;
				PreparedStatement st3 = null;

				try {
					st = conn.prepareStatement("SELECT requestname FROM tphererequests WHERE name='" + p.getName() + "'");
					rs = st.executeQuery();
					
					if (rs.next()) {
						String player = rs.getString("requestname");

						if (player != null) {
							Player target = Bukkit.getPlayer(player);
							if (target != null) {
								World w = target.getWorld();

								st2 = conn.prepareStatement("SELECT owner, locked FROM worlds WHERE worldname = '" + w.getName() + "'");
								rs2 = st2.executeQuery();
								if (rs2.next()) {
									String owner = rs2.getString("owner");
									String Locked = rs2.getString("locked");

									st3 = conn.prepareStatement("SELECT resident FROM worldresidents WHERE worldname = '" + w.getName() + "'");
									rs3 = st3.executeQuery();

									List<String> residents = new ArrayList<String>();
									if (rs3.next()) {
										while (rs3.next()) {
											residents.add(rs3.getString("resident"));
										}
									}
									if (Locked.equals("true")) {
										if (owner.equals(p.getName()) || residents.contains(p.getName())) {
											p.teleport(target);
											
											if (cfg2.contains("Commands.Tpahereaccept.Finish")) {
												String msg = cfg2.getString("Commands.Tpahereaccept.Finish");
												msg = msg.replaceAll("&", "§");
												msg = msg.replaceAll("%prefix%", "" + prefix + "");
												msg = msg.replaceAll("%player%", "" + p.getName() + "");
												target.sendMessage(msg);
											} else {
												target.sendMessage(prefix + " §c" + p.getName() + " wurde zu dir teleportiert.");
											}
											if (cfg2.contains("Commands.Tpahereaccept.FinishPlayer")) {
												String msg = cfg2.getString("Commands.Tpahereaccept.FinishPlayer");
												msg = msg.replaceAll("&", "§");
												msg = msg.replaceAll("%prefix%", "" + prefix + "");
												msg = msg.replaceAll("%player%", "" + target.getName() + "");
												p.sendMessage(msg);
											} else {
												p.sendMessage("§cDu wurdest zu " + target.getName() + " teleportiert.");
											}

											sql.queryUpdate("DELETE FROM tphererequests WHERE requestname = '" + target.getName() + "'");

										} else {
											if (cfg2.contains("Commands.Tpahereaccept.Locked")) {
												String msg = cfg2.getString("Commands.Tpahereaccept.Locked");
												msg = msg.replaceAll("&", "§");
												msg = msg.replaceAll("%prefix%", "" + prefix + "");
												target.sendMessage(msg);
											} else {
												target.sendMessage("§cDeine Welt ist gelockt, um den Spieler in deine Welt zu teleportieren entsperre deine Welt mit /unlock.");
											}
										}
									} else {
										p.teleport(target);
										if (cfg2.contains("Commands.Tpahereaccept.Finish")) {
											String msg = cfg2.getString("Commands.Tpahereaccept.Finish");
											msg = msg.replaceAll("&", "§");
											msg = msg.replaceAll("%prefix%", "" + prefix + "");
											msg = msg.replaceAll("%player%", "" + p.getName() + "");
											target.sendMessage(msg);
										} else {
											target.sendMessage(prefix + " §c" + p.getName() + " wurde zu dir teleportiert.");
										}
										if (cfg2.contains("Commands.Tpahereaccept.FinishPlayer")) {
											String msg = cfg2.getString("Commands.Tpahereaccept.FinishPlayer");
											msg = msg.replaceAll("&", "§");
											msg = msg.replaceAll("%prefix%", "" + prefix + "");
											msg = msg.replaceAll("%player%", "" + target.getName() + "");
											p.sendMessage(msg);
										} else {
											p.sendMessage("§cDu wurdest zu " + target.getName() + " teleportiert.");
										}

										sql.queryUpdate("DELETE FROM tphererequests WHERE requestname = '" + target.getName() + "'");
									}
								}
							} else {
								if (cfg2.contains("Commands.Tpahereaccept.NotOnline")) {
									String msg = cfg2.getString("Commands.Tpahereaccept.NotOnline");
									msg = msg.replaceAll("&", "§");
									msg = msg.replaceAll("%prefix%", "" + prefix + "");
									p.sendMessage(msg);
								} else {
									p.sendMessage(prefix + " §cSpieler ist nicht online.");
								}
							}
						} else {
							if (cfg2.contains("Commands.Tpahereaccept.NoRequest")) {
								String msg = cfg2.getString("Commands.Tpahereaccept.NoRequest");
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
