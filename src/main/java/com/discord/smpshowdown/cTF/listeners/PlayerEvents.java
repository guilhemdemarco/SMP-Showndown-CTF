package com.discord.smpshowdown.cTF.listeners;

import com.discord.smpshowdown.cTF.CTF;
import com.discord.smpshowdown.cTF.players.PlayerData;
import com.discord.smpshowdown.cTF.teams.CtfTeam;
import com.discord.smpshowdown.cTF.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event){
        TeamManager teamManager = CTF.teamManager;
        Player player = event.getPlayer();
        PlayerData playerData = CTF.playerData.get(player.getUniqueId());
        Block blockUnderPlayer = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        CtfTeam ctfTeam = playerData.getTeam();

//        System.out.println(String.format(
//                "%s: Block under is %s, team %s, flag captured %s",
//                blockUnderPlayer.getType(),
//                player.getDisplayName(),
//                ctfTeam.getName(),
//                playerData.hasEnemyFlag()
//        ));

        if (ctfTeam == null) return;

        if (blockUnderPlayer.getType() == ctfTeam.getEnemyTeam().getCaptureBlock() && !ctfTeam.getEnemyTeam().isFlagTaken()){
            Bukkit.broadcastMessage(String.format("%s has captured %s's flag!", player.getDisplayName(), ctfTeam.getEnemyTeam().getName()));
            ctfTeam.getEnemyTeam().setFlagTaken(true);
            playerData.setHasEnemyFlag(true);
        }

        if (blockUnderPlayer.getType() == ctfTeam.getCaptureBlock() && playerData.hasEnemyFlag()){
            Bukkit.broadcastMessage(String.format("%s secured %s's flag!", player.getDisplayName(), ctfTeam.getEnemyTeam().getName()));
            ctfTeam.getEnemyTeam().setFlagTaken(false);
            playerData.setHasEnemyFlag(false);
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        PlayerData data = new PlayerData(player);
        CTF.playerData.putIfAbsent(player.getUniqueId(), data);
        System.out.println("Added player data");
    }
}
