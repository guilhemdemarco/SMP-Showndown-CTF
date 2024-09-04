package com.discord.smpshowdown.cTF.listeners;

import com.discord.smpshowdown.cTF.CTF;
import com.discord.smpshowdown.cTF.players.PlayerData;
import com.discord.smpshowdown.cTF.teams.CtfTeam;
import com.discord.smpshowdown.cTF.teams.TeamManager;
import org.bukkit.Bukkit;
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

public class PlayerEvents implements Listener {

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event){
        TeamManager teamManager = CTF.teamManager;
        Player player = event.getPlayer();
        PlayerData playerData = CTF.playerData.get(player.getUniqueId());
        Block blockUnderPlayer = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        CtfTeam ctfTeam = playerData.getTeam();

        if (ctfTeam == null) return;

        if (blockUnderPlayer.getType() == ctfTeam.getEnemyTeam().getCaptureBlock() && !ctfTeam.getEnemyTeam().isFlagTaken()){
            Bukkit.broadcastMessage(String.format("%s has captured %s's flag!", player.getDisplayName(), ctfTeam.getEnemyTeam().getName()));
            ctfTeam.getEnemyTeam().setFlagTaken(true);
            playerData.setHasEnemyFlag(true);
            player.getInventory().setHelmet(new ItemStack(ctfTeam.getEnemyTeam().getBanner()));
        }

        if (blockUnderPlayer.getType() == ctfTeam.getCaptureBlock() && playerData.hasEnemyFlag()){
            Bukkit.broadcastMessage(String.format("%s secured %s's flag!", player.getDisplayName(), ctfTeam.getEnemyTeam().getName()));
            ctfTeam.getEnemyTeam().setFlagTaken(false);
            playerData.setHasEnemyFlag(false);
            player.getInventory().setHelmet(new ItemStack(Material.AIR));
        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player deadPlayer = event.getEntity();
        PlayerData playerData = CTF.playerData.get(deadPlayer.getUniqueId());
        if (playerData.hasEnemyFlag()){
            Bukkit.broadcastMessage(String.format("%s has dropped %s's flag!", deadPlayer.getDisplayName(), playerData.getTeam().getEnemyTeam().getName()));
            playerData.setHasEnemyFlag(false);
            playerData.getTeam().getEnemyTeam().setFlagTaken(false);
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
