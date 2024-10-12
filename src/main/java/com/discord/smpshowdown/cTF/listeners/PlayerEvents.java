package com.discord.smpshowdown.cTF.listeners;

import com.discord.smpshowdown.cTF.CTF;
import com.discord.smpshowdown.cTF.GameManager;
import com.discord.smpshowdown.cTF.players.PlayerData;
import com.discord.smpshowdown.cTF.teams.CtfTeam;
import com.discord.smpshowdown.cTF.teams.TeamManager;
import org.bukkit.*;
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

import java.util.HashMap;
import java.util.Map;

public class PlayerEvents implements Listener {

    private static Map<CtfTeam, Location> lastOnGround = new HashMap<>();
    private static Map<Player, Long> sneakTime = new HashMap<>();

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
        Block blockPlayerBottom = blockUnderPlayer.getLocation().add(0,1,0).getBlock();
        Block blockPlayerTop = blockPlayerBottom.getLocation().add(0,1,0).getBlock();

        if (ctfTeam == null) return;
        if (!(CTF.gameManager.getGameState() == GameManager.GameState.STARTED)) return;

        if (blockUnderPlayer.getType() == ctfTeam.getEnemyTeam().getCaptureBlock() && !ctfTeam.getEnemyTeam().isFlagTaken()){
            String message = ctfTeam.getTeamColor() + ChatColor.BOLD.toString() +
                    String.format("%s has captured %s's flag!",
                            player.getDisplayName(),
                            ctfTeam.getEnemyTeam().getName());
            CTF.broadcastActionBarMessage(message);
            Bukkit.broadcastMessage(message);
            ctfTeam.getEnemyTeam().setFlagTaken(true);
            playerData.setHasEnemyFlag(true);
            player.getInventory().setHelmet(new ItemStack(ctfTeam.getEnemyTeam().getBanner()));
            CTF.sendActionBarMessage(player, ChatColor.WHITE + ChatColor.BOLD.toString() +
                    "YOU HAVE THE ENEMY FLAG!!! CARRY IT BACK TO YOUR BASE");
            CTF.broadcastSound(Sound.BLOCK_NOTE_BLOCK_BELL, 2);
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

        // check where the player was last on solid ground if they have the enemy flag
        // this is so that when they die, the flag will be placed on solid ground, and not on an unreachable place
        if (playerData.hasEnemyFlag() && !blockUnderPlayer.isEmpty() && blockPlayerBottom.isEmpty() && blockPlayerTop.isEmpty()){
            lastOnGround.put(playerData.getTeam().getEnemyTeam(), blockUnderPlayer.getLocation());
        }

        // this is ugly as shit but it works
        if (player.isSneaking()){
            if (playerData.getTeam().isFlagTaken()
                    && isPlayerInRadius(
                            player,
                            lastOnGround.get(playerData.getTeam()),
                            CTF.configManager.getFlagRetakeArea()
                    )) {
                if (!sneakTime.containsKey(player)) {
                    sneakTime.put(player, System.currentTimeMillis());
                    CTF.broadcastSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2);
                    CTF.sendActionBarMessage(player, "Retaking flag");
                }
                if (System.currentTimeMillis() - sneakTime.get(player) >= CTF.configManager.getFlagRetakeTimeMillis()){
                    Bukkit.broadcastMessage(playerData.getTeam().getTeamColor() +
                            String.format("Team %s's flag has been returned!",
                                    playerData.getTeam().getName()));
                    playerData.getTeam().setFlagTaken(false);
                    lastOnGround.get(playerData.getTeam()).getBlock().setType(Material.AIR);
                } else{
//                    double retakeRatio = (double)(System.currentTimeMillis() - sneakTime.get(player)) / CTF.configManager.getFlagRetakeTimeMillis();
//                    player.sendMessage(String.valueOf(retakeRatio));
//                    double retakePercentage = retakeRatio * 100;
                    CTF.sendActionBarMessage(player, "Retaking flag");
                }
            }
        } else {
            if (sneakTime.containsKey(player)) sneakTime.remove(player);
        }


    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if(!CTF.gameManager.getGameState().equals(GameManager.GameState.STARTED)) return;
        Player deadPlayer = event.getEntity();
        PlayerData playerData = CTF.playerData.get(deadPlayer.getUniqueId());
        if (playerData.hasEnemyFlag()){
            Bukkit.broadcastMessage(playerData.getTeam().getEnemyTeam().getTeamColor() +
                    String.format("%s has dropped %s's flag!",
                            deadPlayer.getDisplayName(),
                            playerData.getTeam().getEnemyTeam().getName()));
            Location lastOnGroundDeathLocation = lastOnGround.get(playerData.getTeam().getEnemyTeam());
            //lastOnGroundDeathLocation.getBlock().setType(playerData.getTeam().getEnemyTeam().getCaptureBlock());
            lastOnGroundDeathLocation.add(0, 1, 0).getBlock().setType(playerData.getTeam().getEnemyTeam().getBanner().getType());
            playerData.setHasEnemyFlag(false);
            CTF.broadcastSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 1);
        }

        if (deadPlayer.getKiller() instanceof Player){
            Player killerPlayer = deadPlayer.getKiller();
            PlayerData killerPlayerData = CTF.playerData.get(killerPlayer.getUniqueId());

            CTF.broadcastTitleMessage("", String.format("%s ⚔ %s",
                    killerPlayerData.getTeam().getTeamColor() + killerPlayer.getDisplayName(),
                    playerData.getTeam().getTeamColor() + deadPlayer.getDisplayName()
            ));
            CTF.broadcastSound(Sound.ENTITY_PLAYER_LEVELUP, 2);
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

    private static boolean isPlayerInRadius(Player player, Location location, int maxRadius){
        return player.getLocation().distance(location) <= maxRadius;
    }


}
