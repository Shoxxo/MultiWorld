package de.theneotv.multiworld.cmds;

import de.theneotv.multiworld.main.Multiworld;
import de.theneotv.multiworld.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
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

public class cmd_create implements CommandExecutor {

    Multiworld service = Multiworld.getPlugin();
    String prefix = service.getPrefix();
    File lang = new File("plugins/MultiWorld", "lang_de.yml");
    YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);

    MySQL sql = Multiworld.getPlugin().getMysql();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("Multiworld.create")) {
                if (args.length == 3) {

                    String worldName = args[0];
                    String worldEnv = args[1];
                    String worldType = args[2];

                    Connection conn = sql.getConnection();
                    ResultSet rs = null;
                    PreparedStatement st = null;
                    ResultSet rs2 = null;
                    PreparedStatement st2 = null;

                    try {
                        st = conn.prepareStatement("SELECT worldname FROM worlds WHERE worldname = '" + worldName + "'");
                        rs = st.executeQuery();

                        st2 = conn.prepareStatement("SELECT numbers, max FROM worldplayers WHERE name = '" + p.getName() + "'");
                        rs2 = st2.executeQuery();

                        if (rs2.next()) {
                            int welten = rs2.getInt("numbers");
                            int maxwelten = rs2.getInt("max");

                            if (!(welten == maxwelten) || p.hasPermission("Multiworld.admin")) {

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
                                            WorldCreator c = WorldCreator.name(worldName).environment(Environment.NORMAL).type(WorldType.NORMAL);
                                            Bukkit.createWorld(c);
                                        } else if (worldType.equalsIgnoreCase("flat")) {
                                            WorldCreator c = WorldCreator.name(worldName).environment(Environment.NORMAL).type(WorldType.FLAT);
                                            Bukkit.createWorld(c);
                                        } else if (worldType.equalsIgnoreCase("amplified")) {
                                            WorldCreator c = WorldCreator.name(worldName).environment(Environment.NORMAL).type(WorldType.AMPLIFIED);
                                            Bukkit.createWorld(c);
                                        } else if (worldType.equalsIgnoreCase("bigbiome")) {
                                            WorldCreator c = WorldCreator.name(worldName).environment(Environment.NORMAL).type(WorldType.LARGE_BIOMES);
                                            Bukkit.createWorld(c);
                                        } else {
                                            if (cfg2.contains("Commands.Createworld.WrongSyntaxEnvironment")) {
                                                String msg = cfg2.getString("Commands.Createworld.WrongSyntaxEnvironment");
                                                msg = msg.replaceAll("&", "§");
                                                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                                p.sendMessage(msg);
                                            } else {
                                                p.sendMessage(prefix + " §c/createworld <Name> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                                            }
                                        }
                                    } else if (worldEnv.equalsIgnoreCase("nether")) {
                                        if (worldType.equalsIgnoreCase("normal")) {
                                            WorldCreator c = WorldCreator.name(worldName).environment(Environment.NETHER).type(WorldType.NORMAL);
                                            Bukkit.createWorld(c);
                                        } else if (worldType.equalsIgnoreCase("flat")) {
                                            WorldCreator c = WorldCreator.name(worldName).environment(Environment.NETHER).type(WorldType.FLAT);
                                            Bukkit.createWorld(c);
                                        } else if (worldType.equalsIgnoreCase("amplified")) {
                                            WorldCreator c = WorldCreator.name(worldName).environment(Environment.NETHER).type(WorldType.AMPLIFIED);
                                            Bukkit.createWorld(c);
                                        } else if (worldType.equalsIgnoreCase("bigbiome")) {
                                            WorldCreator c = WorldCreator.name(worldName).environment(Environment.NETHER).type(WorldType.LARGE_BIOMES);
                                            Bukkit.createWorld(c);
                                        } else {
                                            if (cfg2.contains("Commands.Createworld.WrongSyntaxEnvironment")) {
                                                String msg = cfg2.getString("Commands.Createworld.WrongSyntaxEnvironment");
                                                msg = msg.replaceAll("&", "§");
                                                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                                p.sendMessage(msg);
                                            } else {
                                                p.sendMessage(prefix + " §c/createworld <Name> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                                            }
                                        }
                                    } else if (worldEnv.equalsIgnoreCase("end")) {
                                        if (worldType.equalsIgnoreCase("normal")) {
                                            WorldCreator c = WorldCreator.name(worldName).environment(Environment.THE_END).type(WorldType.NORMAL);
                                            Bukkit.createWorld(c);
                                        } else if (worldType.equalsIgnoreCase("flat")) {
                                            WorldCreator c = WorldCreator.name(worldName).environment(Environment.THE_END).type(WorldType.FLAT);
                                            Bukkit.createWorld(c);
                                        } else if (worldType.equalsIgnoreCase("amplified")) {
                                            WorldCreator c = WorldCreator.name(worldName).environment(Environment.THE_END).type(WorldType.AMPLIFIED);
                                            Bukkit.createWorld(c);
                                        } else if (worldType.equalsIgnoreCase("bigbiome")) {
                                            WorldCreator c = WorldCreator.name(worldName).environment(Environment.THE_END).type(WorldType.LARGE_BIOMES);
                                            Bukkit.createWorld(c);
                                        } else {
                                            if (cfg2.contains("Commands.Createworld.WrongSyntaxEnvironment")) {
                                                String msg = cfg2.getString("Commands.Createworld.WrongSyntaxEnvironment");
                                                msg = msg.replaceAll("&", "§");
                                                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                                p.sendMessage(msg);
                                            } else {
                                                p.sendMessage(prefix + " §c/createworld <Name> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                                            }
                                        }
                                    } else {
                                        if (cfg2.contains("Commands.Createworld.WrongSyntaxEnvironment")) {
                                            String msg = cfg2.getString("Commands.Createworld.WrongSyntaxEnvironment");
                                            msg = msg.replaceAll("&", "§");
                                            msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                            p.sendMessage(msg);
                                        } else {
                                            p.sendMessage(prefix + " §c/createworld <Name> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                                        }
                                    }

                                    Location loc = Bukkit.getWorld(worldName).getSpawnLocation();

                                    double x = loc.getX();
                                    double y = loc.getY();
                                    double z = loc.getZ();

                                    float yaw = loc.getYaw();
                                    float pitch = loc.getPitch();

                                    String locked = "true";
                                    String resident = "Nobody";
                                    String owner = p.getName();
                                    welten = welten + 1;

                                    sql.queryUpdate("INSERT INTO worlds (worldname, owner, locked, type, spawnx, spawny, spawnz, spawnyaw, spawnpitch) VALUES ('" + worldName + "', '" + owner + "', '" + locked + "', '" + worldType + "', '" + x + "', '" + y + "', '" + z + "', '" + yaw + "', '" + pitch + "')");
                                    sql.queryUpdate("INSERT INTO worldresidents (worldname, type, resident) VALUES ('" + worldName + "', '" + worldType + "', '" + resident + "')");
                                    sql.queryUpdate("UPDATE worldplayers SET numbers = '" + welten + "' WHERE name = '" + p.getName() + "'");

                                    if (cfg2.contains("Commands.Createworld.Finish")) {
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
                                    if (cfg2.contains("Commands.Createworld.IsWorld")) {
                                        String msg = cfg2.getString("Commands.Createworld.IsWorld");
                                        msg = msg.replaceAll("&", "§");
                                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                        p.sendMessage(msg);
                                    } else {
                                        p.sendMessage(prefix + " §cError Welt existiert schon");
                                    }
                                }
                            } else {
                                if (cfg2.contains("Commands.Createworld.MaxError")) {
                                    String msg = cfg2.getString("Commands.Createworld.MaxError");
                                    msg = msg.replaceAll("&", "§");
                                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                                    p.sendMessage(msg);
                                } else {
                                    p.sendMessage(prefix + "§cDu hast die maximale Anzahl an Welten ereicht. Wende dich an einem Moderator!");
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (cfg2.contains("Commands.Createworld.WrongSyntax")) {
                        String msg = cfg2.getString("Commands.Createworld.WrongSyntax");
                        msg = msg.replaceAll("&", "§");
                        msg = msg.replaceAll("%prefix%", "" + prefix + "");
                        p.sendMessage(msg);
                    } else {
                        p.sendMessage(prefix + " §cFalsche Syntax. Bitte benutze /createworld oder /createworld <Weltname> <Environment (normal | nether | end)> <Type (normal | flat | amplified | bigbiome)>");
                    }
                }
            } else {
                if (cfg2.contains("System.NoPermission")) {
                    String msg = cfg2.getString("System.NoPermission");
                    msg = msg.replaceAll("&", "§");
                    msg = msg.replaceAll("%prefix%", "" + prefix + "");
                    p.sendMessage(msg);
                } else {
                    p.sendMessage(prefix + " §cDazu hast du keine Berechtigung!");
                }
            }
        } else {
            if (cfg2.contains("System.OnlyPlayers")) {
                String msg = cfg2.getString("System.OnlyPlayers");
                msg = msg.replaceAll("&", "§");
                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                sender.sendMessage(msg);
            } else {
                sender.sendMessage(prefix + " §cNur Spieler dürfen diesen Befehl benutzen!");
            }
        }
        return true;
    }
}
