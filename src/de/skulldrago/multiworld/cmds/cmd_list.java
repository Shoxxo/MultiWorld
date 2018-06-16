package de.skulldrago.multiworld.cmds;

import java.io.File;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.skulldrago.multiworld.main.Multiworld;
import de.skulldrago.multiworld.mysql.MySQL;

public class cmd_list implements CommandExecutor {
    Multiworld service = Multiworld.getPlugin();
    String prefix = service.getPrefix();
    File lang = new File("plugins/MultiWorld", "lang_de.yml");
    YamlConfiguration cfg2 = YamlConfiguration.loadConfiguration(lang);

    MySQL sql = Multiworld.getPlugin().getMysql();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            String worlds = Bukkit.getServer().getWorlds().stream().map(World::getName)
                    .collect(Collectors.joining(", "));
            if (cfg2.contains("Commands.List.Message")) {
                String msg = cfg2.getString("Commands.List.Message");
                msg = msg.replaceAll("&", "§");
                msg = msg.replaceAll("%prefix%", "" + prefix + "");
                msg = msg.replaceAll("%worlds%", "" + worlds + "");
                p.sendMessage(msg);
            } else {
                p.sendMessage(prefix + " §cVorhandene Welten: §6" + worlds + ".");
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
