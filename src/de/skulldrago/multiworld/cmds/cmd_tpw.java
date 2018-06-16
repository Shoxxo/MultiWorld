package de.skulldrago.multiworld.cmds;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;

public class cmd_tpw implements CommandExecutor {
	Multiworld service = Multiworld.getPlugin();
	String prefix = service.getPrefix();
	File lang = new File("plugins/MultiWorld", "lang_de.yml");
	YamlConfiguration cfg4 = YamlConfiguration.loadConfiguration(lang);

	MySQL sql = Multiworld.getPlugin().getMysql();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			if (p.hasPermission("Multiworld.tp")) {
				if (args.length == 0) {
					Connection conn = sql.getConnection();
					ResultSet rs = null;
					PreparedStatement st = null;

					try {
						st = conn.prepareStatement("SELECT * FROM worlds WHERE owner = '" + p.getName() + "'");
						rs = st.executeQuery();
						
						if (rs.next()) {
							World Playerworld = Bukkit.getWorld(rs.getString("worldname"));

							if (Playerworld != null) {
								Double x = rs.getDouble("spawnx");
								Double y = rs.getDouble("spawny");
								Double z = rs.getDouble("spawnz");

								Float yaw = rs.getFloat("spawnyaw");
								Float pitch = rs.getFloat("spawnpitch");

								Location loc = new Location(Playerworld, x, y, z, yaw, pitch);

								p.teleport(loc);
							} else if (rs.next()) {
								Bukkit.getServer().createWorld(new WorldCreator(rs.getString("worldname")));
								Double x = rs.getDouble("spawnx");
								Double y = rs.getDouble("spawny");
								Double z = rs.getDouble("spawnz");

								Float yaw = rs.getFloat("spawnyaw");
								Float pitch = rs.getFloat("spawnpitch");

								Location loc = new Location(Playerworld, x, y, z, yaw, pitch);

								p.teleport(loc);
							}
						} else {
							if (cfg4.contains("Commands.Tpworld.Error")) {
								String msg = cfg4.getString("Commands.Tpworld.Error");
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

				} else if (args.length == 1) {
					String target = args[0];

					Connection conn = sql.getConnection();
					ResultSet rs = null;
					PreparedStatement st = null;
					ResultSet rs2 = null;
					PreparedStatement st2 = null;

					try {
						st = conn.prepareStatement("SELECT * FROM worlds WHERE worldname = '" + target + "'");
						rs = st.executeQuery();

						if (rs.next()) {
							//rs.next();
							Boolean locked = rs.getBoolean("locked");
							String owner = rs.getString("owner");
							World Playerworld = Bukkit.getWorld(rs.getString("worldname"));

							if (Playerworld == null) {
								p.chat("/load " + target);
								Playerworld = Bukkit.getWorld(target);
							}
							if ((locked == true) && (!(p.hasPermission("Multiworld.admin")))) {

								st2 = conn.prepareStatement("SELECT * FROM worldresidents WHERE worldname = '" + target + "'");
								rs2 = st2.executeQuery();

								List<String> residents = new ArrayList<String>();
								if (rs2.next()) {
									while (rs2.next()) {
										residents.add(rs2.getString("resident"));
									}
								}
								if (owner.equals(p.getName()) || residents.contains(p.getName())) {

									if (Playerworld != null) {
										Double x = rs.getDouble("spawnx");
										Double y = rs.getDouble("spawny");
										Double z = rs.getDouble("spawnz");
										Float yaw = rs.getFloat("spawnyaw");
										Float pitch = rs.getFloat("spawnpitch");

										Location loc2 = new Location(Playerworld, x, y, z, yaw, pitch);
										p.teleport(loc2);

									}
								} else {
									if (cfg4.contains("System.NoPermission")) {
										String msg = cfg4.getString("System.NoPermission");
										msg = msg.replaceAll("&", "§");
										msg = msg.replaceAll("%prefix%", "" + prefix + "");
										p.sendMessage(msg);

									} else {
										p.sendMessage(prefix + " §cDu hast nicht die Permissions um diesen Befehl zu benutzen.");
									}
								}
							} else {

								if (owner != null && owner != " ") {

									if (Playerworld != null) {
										Double X = rs.getDouble("spawnx");
										Double Y = rs.getDouble("spawny");
										Double Z = rs.getDouble("spawnz");
										
										Float Yaw = rs.getFloat("spawnyaw");
										Float Pitch = rs.getFloat("spawnpitch");
										
										Location loc3 = new Location(Playerworld, X, Y, Z, Yaw, Pitch);
										p.teleport(loc3);
									} else {
										if (cfg4.contains("Commands.Tpworld.Error")) {
											String msg = cfg4.getString("Commands.Tpworld.Error");
											msg = msg.replaceAll("&", "§");
											msg = msg.replaceAll("%prefix%", "" + prefix + "");
											p.sendMessage(msg);
										} else {
											p.sendMessage(prefix + " §cFehler: Welt existiert nicht");
										}

									}

								} else if (target.equalsIgnoreCase("world") || target.equalsIgnoreCase("world_nether") || target.equalsIgnoreCase("world_the_end")) {
									World w = Bukkit.getWorld(target);
									Location loc = w.getSpawnLocation();
									p.teleport(loc);
								} else {
									if (cfg4.contains("Commands.Tpworld.NotExists")) {
										String msg = cfg4.getString("Commands.Tpworld.NotExists");
										msg = msg.replaceAll("&", "§");
										msg = msg.replaceAll("%prefix%", "" + prefix + "");
										p.sendMessage(msg);
									} else {
										p.sendMessage(prefix + " §cWelt existiert nicht oder du bist nicht berechtigt");
									}
								}
							}
						} else {
							if (cfg4.contains("Commands.Tpworld.Error")) {
								String msg = cfg4.getString("Commands.Tpworld.Error");
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
					if (cfg4.contains("Commands.Tpworld.WrongSyntax")) {
						String msg = cfg4.getString("Commands.Tpworld.WrongSyntax");
						msg = msg.replaceAll("&", "§");
						msg = msg.replaceAll("%prefix%", "" + prefix + "");
						p.sendMessage(msg);
					} else {
						p.sendMessage(prefix + " §cFalsche Syntax. Benutze bitte /tpworld oder /tpworld <Weltname>");
					}
				}
			} else {
				if (cfg4.contains("System.NoPermission")) {
					String msg = cfg4.getString("System.NoPermission");
					msg = msg.replaceAll("&", "§");
					msg = msg.replaceAll("%prefix%", "" + prefix + "");
					p.sendMessage(msg);

				} else {
					p.sendMessage(prefix + " §cDu hast nicht die Permissions um diesen Befehl zu benutzen.");
				}
			}
		} else {
			if (cfg4.contains("System.OnlyPlayers")) {
				String msg = cfg4.getString("System.OnlyPlayers");
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
