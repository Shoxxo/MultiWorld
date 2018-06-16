package de.skulldrago.multiworld.cmds;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;

public class cmd_createserver implements CommandExecutor {
    Multiworld service = Multiworld.getPlugin();
    String prefix = service.getPrefix();
    File lang = new File("plugins/MultiWorld", "lang_de.yml");
    YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);

    MySQL sql = Multiworld.getPlugin().getMysql();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("Multiworld.serverworld")) {
                if (args.length == 1) {
                    WorldCreator c = WorldCreator.name(args[0]);
                    Bukkit.createWorld(c);
                } else if (args.length == 3) {
                    String worldName = args[0];
                    String worldEnv = args[1];
                    String worldType = args[2];

                    Connection conn = sql.getConnection();
                    ResultSet rs = null;
                    PreparedStatement st = null;

                    try {
                        st = conn.prepareStatement("SELECT worldname FROM worlds WHERE worldname= '" + worldName + "'");
                        rs = st.executeQuery();

                        if (!(rs.next())) {
                            if (cfg2.contains("Commands.Createworld.Create")) {
                                String msg = cfg2.getString("Commands.Createworld.Create");
                                msg = msg.replaceAll("&", "§");
                                msg = msg.replaceAll("%world%", "" + worldName + "");
                                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                p.sendMessage(msg);
                            } else {
                                p.sendMessage(prefix + " §4Welt §e" + worldName + " §4wird erstellt...");
                            }

                            if (worldEnv.equalsIgnoreCase("normal")) {
                                if (worldType.equalsIgnoreCase("normal")) {
                                    WorldCreator c = (WorldCreator) WorldCreator.name(worldName).environment(Environment.NORMAL).type(WorldType.NORMAL);
                                    Bukkit.createWorld(c);
                                } else if (worldType.equalsIgnoreCase("flat")) {
                                    WorldCreator c = (WorldCreator) WorldCreator.name(worldName).environment(Environment.NORMAL).type(WorldType.FLAT);
                                    Bukkit.createWorld(c);
                                } else if (worldType.equalsIgnoreCase("amplified")) {
                                    WorldCreator c = (WorldCreator) WorldCreator.name(worldName).environment(Environment.NORMAL).type(WorldType.AMPLIFIED);
                                    Bukkit.createWorld(c);
                                } else if (worldType.equalsIgnoreCase("bigbiome")) {
                                    WorldCreator c = (WorldCreator) WorldCreator.name(worldName).environment(Environment.NORMAL).type(WorldType.LARGE_BIOMES);
                                    Bukkit.createWorld(c);
                                } else {
                                    if (cfg2.contains("Commands.Createserver.WrongSyntaxEnvironment")) {
                                        String msg = cfg2.getString("Commands.Createworld.WrongSyntaxEnvironment");
                                        msg = msg.replaceAll("&", "§");
                                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                        p.sendMessage(msg);
                                    } else {
                                        p.sendMessage(prefix + " §c/createserverworld <Name> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                                    }
                                }
                            } else if (worldEnv.equalsIgnoreCase("nether")) {
                                if (worldType.equalsIgnoreCase("normal")) {
                                    WorldCreator c = (WorldCreator) WorldCreator.name(worldName).environment(Environment.NETHER).type(WorldType.NORMAL);
                                    Bukkit.createWorld(c);
                                } else if (worldType.equalsIgnoreCase("flat")) {
                                    WorldCreator c = (WorldCreator) WorldCreator.name(worldName).environment(Environment.NETHER).type(WorldType.FLAT);
                                    Bukkit.createWorld(c);
                                } else if (worldType.equalsIgnoreCase("amplified")) {
                                    WorldCreator c = (WorldCreator) WorldCreator.name(worldName).environment(Environment.NETHER).type(WorldType.AMPLIFIED);
                                    Bukkit.createWorld(c);
                                } else if (worldType.equalsIgnoreCase("bigbiome")) {
                                    WorldCreator c = (WorldCreator) WorldCreator.name(worldName).environment(Environment.NETHER).type(WorldType.LARGE_BIOMES);
                                    Bukkit.createWorld(c);
                                } else {
                                    if (cfg2.contains("Commands.Createserver.WrongSyntaxEnvironment")) {
                                        String msg = cfg2.getString("Commands.Createworld.WrongSyntaxEnvironment");
                                        msg = msg.replaceAll("&", "§");
                                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                        p.sendMessage(msg);
                                    } else {
                                        p.sendMessage(prefix + " §c/createserverworld <Name> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                                    }
                                }
                            } else if (worldEnv.equalsIgnoreCase("end")) {
                                if (worldType.equalsIgnoreCase("normal")) {
                                    WorldCreator c = (WorldCreator) WorldCreator.name(worldName).environment(Environment.THE_END).type(WorldType.NORMAL);
                                    Bukkit.createWorld(c);
                                } else if (worldType.equalsIgnoreCase("flat")) {
                                    WorldCreator c = (WorldCreator) WorldCreator.name(worldName).environment(Environment.THE_END).type(WorldType.FLAT);
                                    Bukkit.createWorld(c);
                                } else if (worldType.equalsIgnoreCase("amplified")) {
                                    WorldCreator c = (WorldCreator) WorldCreator.name(worldName).environment(Environment.THE_END).type(WorldType.AMPLIFIED);
                                    Bukkit.createWorld(c);
                                } else if (worldType.equalsIgnoreCase("bigbiome")) {
                                    WorldCreator c = (WorldCreator) WorldCreator.name(worldName).environment(Environment.THE_END).type(WorldType.LARGE_BIOMES);
                                    Bukkit.createWorld(c);
                                } else {
                                    if (cfg2.contains("Commands.Createserver.WrongSyntaxEnvironment")) {
                                        String msg = cfg2.getString("Commands.Createworld.WrongSyntaxEnvironment");
                                        msg = msg.replaceAll("&", "§");
                                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                        p.sendMessage(msg);
                                    } else {
                                        p.sendMessage(prefix + " §c/createserverworld <Name> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                                    }
                                }
                            } else {
                                if (cfg2.contains("Commands.Createserver.WrongSyntaxEnvironment")) {
                                    String msg = cfg2.getString("Commands.Createworld.WrongSyntaxEnvironment");
                                    msg = msg.replaceAll("&", "§");
                                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                    p.sendMessage(msg);
                                } else {
                                    p.sendMessage(prefix + " §c/createserverworld <Name> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                                }
                            }

                            String owner = "Server";
                            String locked = "false";

                            Location loc = Bukkit.getWorld(worldName).getSpawnLocation();

                            Double x = loc.getX();
                            Double y = loc.getY();
                            Double z = loc.getZ();

                            Float yaw = loc.getYaw();
                            Float pitch = loc.getPitch();

                            String resident = "Nobody";

                            sql.queryUpdate("INSERT INTO worlds (worldname, owner, locked, type, spawnx, spawny, spawnz, spawnyaw, spawnpitch) VALUES ('" + worldName + "', '" + owner + "', '" + locked + "', '" + worldType + "', '" + x + "', '" + y + "', '" + z + "', '" + yaw + "', '" + pitch + "')");
                            sql.queryUpdate("INSERT INTO worldresidents (worldname, type, resident) VALUES ('" + worldName + "', '" + worldType + "', '" + resident + "')");

                            if (cfg2.contains("Commands.Createserver.Finish")) {
                                String msg = cfg2.getString("Commands.Createworld.Finish");
                                msg = msg.replaceAll("&", "§");
                                msg = msg.replaceAll("%world%", "" + worldName + "");
                                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                p.sendMessage(msg);
                            } else {
                                p.sendMessage(prefix + " §cWelt §e" + worldName + " §cerfolgreich erstellt.");
                            }
                            p.teleport(Bukkit.getWorld(worldName).getSpawnLocation());
                        } else {
                            if (cfg2.contains("Commands.Createserver.IsWorld")) {
                                String msg = cfg2.getString("Commands.Createworld.IsWorld");
                                msg = msg.replaceAll("&", "§");
                                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                p.sendMessage(msg);
                            } else {
                                p.sendMessage(prefix + " §cError Welt existiert schon");
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (cfg2.contains("Commands.Createserver.WrongSyntax")) {
                        String msg = cfg2.getString("Commands.Createworld.WrongSyntax");
                        msg = msg.replaceAll("&", "§");
                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                        p.sendMessage(msg);
                    } else {
                        p.sendMessage(prefix + " §cFalsche Syntax. Bitte benutze /createserverworld <Weltname> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
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
