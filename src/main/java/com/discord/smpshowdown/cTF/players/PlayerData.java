package com.discord.smpshowdown.cTF.players;

import com.discord.smpshowdown.cTF.teams.CtfTeam;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class PlayerData {

    UUID uuid;
    boolean hasEnemyFlag;
    CtfTeam team;

    public UUID getUuid() {
        return uuid;
    }

    public boolean isHasEnemyFlag() {
        return hasEnemyFlag;
    }

    public CtfTeam getTeam() {
        return team;
    }

    public void setTeam(CtfTeam team) {
        this.team = team;
    }

    public PlayerData(Player player){
        this.uuid = player.getUniqueId();
        this.hasEnemyFlag = false;
    }
}
