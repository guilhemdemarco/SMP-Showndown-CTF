package com.discord.smpshowdown.cTF.players;

import com.discord.smpshowdown.cTF.teams.CtfTeam;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class PlayerData {

    UUID uuid;
    Player player;
    boolean hasEnemyFlag;
    CtfTeam team;

    public PlayerData(Player player){
        this.uuid = player.getUniqueId();
        this.player = player;
        this.hasEnemyFlag = false;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer(){
        return player;
    }

    public void setHasEnemyFlag(boolean hasEnemyFlag) {
        this.hasEnemyFlag = hasEnemyFlag;
    }

    public boolean hasEnemyFlag() {
        return hasEnemyFlag;
    }

    public CtfTeam getTeam() {
        return team;
    }

    public void setTeam(CtfTeam team) {
        this.team = team;
    }
}
