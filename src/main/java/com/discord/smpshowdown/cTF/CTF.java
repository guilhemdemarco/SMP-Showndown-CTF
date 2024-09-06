package com.discord.smpshowdown.cTF;

import com.discord.smpshowdown.cTF.listeners.PlayerEvents;
import com.discord.smpshowdown.cTF.players.PlayerData;
import com.discord.smpshowdown.cTF.teams.CtfTeam;
import com.discord.smpshowdown.cTF.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.UUID;

public final class CTF extends JavaPlugin {

    public static HashMap<UUID, PlayerData> playerData = new HashMap<UUID, PlayerData>();
    public static TeamManager teamManager;
    public static GameManager gameManager;
    public static Bossbar bossbar;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        teamManager = new TeamManager(this);
        teamManager.createTeamsOnStartup();
        gameManager = new GameManager(this);
        bossbar = new Bossbar(this);


        //DEBUG: this is for when we reload the plugin, the playerdata hashmap gets reset
        // so when testing commands, you need to rejoin every reload, otherwise you get an error
        //TODO: data persistence

        Bukkit.getOnlinePlayers().forEach(player -> {
            playerData.putIfAbsent(player.getUniqueId(), new PlayerData(player));
            System.out.println(String.format("DEBUG: created playerdata of %s", player.getDisplayName()));
            bossbar.addPlayer(player);
        });

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        bossbar.getBossbar().removeAll();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //TODO: this is ugly
        if (label.equalsIgnoreCase("ctf")){
            if (!sender.isOp()){
                sender.sendMessage(ChatColor.RED + "You are not allowed to use this command!");
                return false;
            }
            if (args[0].equalsIgnoreCase("team")){
                if (args[1].equalsIgnoreCase("join")){
                    Player player = Bukkit.getPlayer(args[2]);
                    if (player == null) {
                        sender.sendMessage(String.format("Player '%s' not found!", args[2]));
                        return false;
                    }
                    if (args[3].equals(teamManager.getAlphaTeam().getName())){
                        System.out.println("Joining team alpha: " + player.getDisplayName());
                        sender.sendMessage(String.format("Switching %s's team to %s", player.getDisplayName(), teamManager.getAlphaTeam().getName()));
                        player.sendMessage("You've joined team " + teamManager.getAlphaTeam().getName());
                        playerData.get(player.getUniqueId()).setTeam(teamManager.getAlphaTeam());
                        teamManager.getAlphaTeam().addPlayer(player);
                        teamManager.getDeltaTeam().removePlayer(player);
                        return true;
                    }
                    else if (args[3].equals(teamManager.getDeltaTeam().getName())){
                        System.out.println("Joining team delta: " + player.getDisplayName());
                        sender.sendMessage(String.format("Switching %s's team to %s", player.getDisplayName(), teamManager.getDeltaTeam().getName()));
                        player.sendMessage("You've joined team " + teamManager.getDeltaTeam().getName());
                        playerData.get(player.getUniqueId()).setTeam(teamManager.getDeltaTeam());
                        teamManager.getDeltaTeam().addPlayer(player);
                        teamManager.getAlphaTeam().removePlayer(player);
                        return true;
                    }
                    else {
                        sender.sendMessage(String.format("Team '%s' not found!", args[3]));
                        return true;
                    }
                } else if (args[1].equalsIgnoreCase("spawn")) {
                    if (!(sender instanceof Player)){
                        sender.sendMessage("Cannot set spawn position from the console");
                        return false;
                    }
                    Player player = (Player) sender;

                    if (args[2].equals(teamManager.getAlphaTeam().getName())){
                        teamManager.getAlphaTeam().setSpawnLocation(player.getLocation());
                        player.sendMessage(String.format("Set team %s spawn to %d, %d, %d",
                                teamManager.getAlphaTeam().getName(),
                                player.getLocation().getBlockX(),player.getLocation().getBlockY(),player.getLocation().getBlockZ()));
                        return true;
                    }
                    else if (args[2].equals(teamManager.getDeltaTeam().getName())){
                        teamManager.getDeltaTeam().setSpawnLocation(player.getLocation());
                        player.sendMessage(String.format("Set team %s spawn to %d, %d, %d",
                                teamManager.getDeltaTeam().getName(),
                                player.getLocation().getBlockX(),player.getLocation().getBlockY(),player.getLocation().getBlockZ()));
                        return true;
                    }
                    else {
                        sender.sendMessage(String.format("Team '%s' not found!", args[3]));
                        return true;
                    }
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                return gameManager.startGame(sender);
            }
            else if (args[0].equalsIgnoreCase("stop")) {
                Bukkit.broadcastMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Game ended by operator");
                gameManager.stopGame(null);
                return true;
            }
        }

        sender.sendMessage("Incorrect command");
        return false;
    }

}
