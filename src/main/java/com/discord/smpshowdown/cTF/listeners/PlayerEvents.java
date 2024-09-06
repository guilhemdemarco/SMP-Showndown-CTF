package com.discord.smpshowdown.cTF.listeners;

import com.discord.smpshowdown.cTF.CTF;
import com.discord.smpshowdown.cTF.GameManager;
import com.discord.smpshowdown.cTF.players.PlayerData;
import com.discord.smpshowdown.cTF.teams.CtfTeam;
import com.discord.smpshowdown.cTF.teams.TeamManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = CTF.playerData.get(player.getUniqueId());
        CtfTeam ctfTeam = playerData.getTeam();

        if (CTF.gameManager.getGameState() == GameManager.GameState.STARTING && ctfTeam != null){
            event.setCancelled(true);
            return;
        }

        TeamManager teamManager = CTF.teamManager;
        Block blockUnderPlayer = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

        if (ctfTeam == null) return;
        if (!(CTF.gameManager.getGameState() == GameManager.GameState.STARTED)) return;

        if (blockUnderPlayer.getType() == ctfTeam.getEnemyTeam().getCaptureBlock() && !ctfTeam.getEnemyTeam().isFlagTaken()){
            Bukkit.broadcastMessage(ctfTeam.getTeamColor() + ChatColor.BOLD.toString() +
                    String.format("%s has captured %s's flag!",
                            player.getDisplayName(),
                            ctfTeam.getEnemyTeam().getName()));
            ctfTeam.getEnemyTeam().setFlagTaken(true);
            playerData.setHasEnemyFlag(true);
            player.getInventory().setHelmet(new ItemStack(ctfTeam.getEnemyTeam().getBanner()));
            sendActionBarMessage(player, ChatColor.WHITE + ChatColor.BOLD.toString() +
                    "YOU HAVE THE ENEMY FLAG!!! CARRY IT BACK TO YOUR BASE");
        }

        if (blockUnderPlayer.getType() == ctfTeam.getCaptureBlock() && playerData.hasEnemyFlag()){
            Bukkit.broadcastMessage(ctfTeam.getTeamColor() + ChatColor.BOLD.toString() +
                    String.format("%s secured %s's flag!",
                            player.getDisplayName(),
                            ctfTeam.getEnemyTeam().getName()));
            ctfTeam.getEnemyTeam().setFlagTaken(false);
            playerData.setHasEnemyFlag(false);
            player.getInventory().setHelmet(new ItemStack(Material.AIR));

            CTF.gameManager.stopGame(ctfTeam);
        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player deadPlayer = event.getEntity();
        PlayerData playerData = CTF.playerData.get(deadPlayer.getUniqueId());
        if (playerData.hasEnemyFlag()){
            Bukkit.broadcastMessage(playerData.getTeam().getEnemyTeam().getTeamColor() +
                    String.format("%s has dropped %s's flag!",
                            deadPlayer.getDisplayName(),
                            playerData.getTeam().getEnemyTeam().getName()));
            playerData.setHasEnemyFlag(false);
            playerData.getTeam().getEnemyTeam().setFlagTaken(false);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        PlayerData data = new PlayerData(player);
        CTF.playerData.putIfAbsent(player.getUniqueId(), data);
        player.sendMessage(ChatColor.DARK_RED + "REMEMBER TO RELOAD!!!!");
        System.out.println("Added player data");
        CTF.bossbar.addPlayer(player);


    }

    public void createBoard(Player player){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getMainScoreboard();
        Objective objective = scoreboard.registerNewObjective("CTF", "dummy",
                ChatColor.BOLD + ChatColor.BLUE.toString() + "SMP Showdown - CTF");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    }

    public void sendActionBarMessage(Player player, String message){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}
