package com.discord.smpshowdown.cTF.teams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class CtfTeam {
    String name;
    Material captureBlock;
    ChatColor teamColor;
    ItemStack banner;
    boolean isFlagTaken;
    Team team;
    CtfTeam enemyTeam;
    List<Player> players;
    Location spawnLocation;

    public CtfTeam(String name, Material captureBlock, ChatColor teamColor, Material bannerBlock, Location spawnLocation){
        this.name = name;
        this.captureBlock = captureBlock;
        this.teamColor = teamColor;
        ItemStack _tempBanner = new ItemStack(bannerBlock);
        ItemMeta meta = _tempBanner.getItemMeta();
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        _tempBanner.setItemMeta(meta);
        this.banner = _tempBanner;
        this.spawnLocation = spawnLocation;
        this.isFlagTaken = false;
        this.players = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Material getCaptureBlock() {
        return captureBlock;
    }

    public ChatColor getTeamColor() {
        return teamColor;
    }

    public ItemStack getBanner(){
        return banner;
    }

    public boolean isFlagTaken() {
        return isFlagTaken;
    }

    public void setFlagTaken(boolean flagTaken) {
        isFlagTaken = flagTaken;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public CtfTeam getEnemyTeam() {
        return enemyTeam;
    }

    public void setEnemyTeam(CtfTeam enemyTeam) {
        this.enemyTeam = enemyTeam;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam(this.name).addPlayer(player);
        players.add(player);
    }

    public void removePlayer(Player player){
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam(this.name).removePlayer(player);
        players.remove(player);
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
}
