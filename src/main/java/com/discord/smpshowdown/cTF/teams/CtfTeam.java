package com.discord.smpshowdown.cTF.teams;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.scoreboard.Team;

public class CtfTeam {
    String name;
    Material captureBlock;
    ChatColor teamColor;
    boolean isFlagTaken;
    Team team;
    CtfTeam enemyTeam;

    public CtfTeam(String name, Material captureBlock, ChatColor teamColor){
        this.name = name;
        this.captureBlock = captureBlock;
        this.teamColor = teamColor;
        this.isFlagTaken = false;
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
}
