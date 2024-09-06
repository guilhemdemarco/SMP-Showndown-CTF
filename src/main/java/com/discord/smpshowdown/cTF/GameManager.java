package com.discord.smpshowdown.cTF;

import com.discord.smpshowdown.cTF.players.PlayerData;
import com.discord.smpshowdown.cTF.teams.CtfTeam;
import com.discord.smpshowdown.cTF.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class GameManager {

    GameState gameState;

    private CTF main;

    public GameManager(CTF main) {
        this.main = main;
        this.gameState = GameState.STOPPED;
    }

    //TODO: make it so it doesnt depend on a commandsender
    public boolean startGame(CommandSender sender){
        if (!gameState.equals(GameState.STOPPED)) {
            sender.sendMessage("Cannot start the game yet!",
                    "Current status: "+ gameState);
            return false;
        };

        TeamManager teamManager = CTF.teamManager;
        CtfTeam alphaTeam = teamManager.getAlphaTeam();
        CtfTeam deltaTeam = teamManager.getDeltaTeam();

        boolean error = false;
        if (alphaTeam.getSpawnLocation() == null) {
            sender.sendMessage(String.format("Team %s's location is not defined", alphaTeam.getName()));
            error = true;
        }
        if (deltaTeam.getSpawnLocation() == null) {
            sender.sendMessage(String.format("Team %s's location is not defined", deltaTeam.getName()));
            error = true;
        }
        if (alphaTeam.getPlayers().isEmpty()) {
            sender.sendMessage(String.format("No players on team %s", alphaTeam.getName()));
            error = true;
        }
        if (deltaTeam.getPlayers().isEmpty()) {
            sender.sendMessage(String.format("No players on team %s", deltaTeam.getName()));
            error = true;
        }
        if (error) {
            sender.sendMessage("Cannot start game!");
            sender.sendMessage("[DEBUG] starting game anyways");
            //setGameState(GameState.STOPPED);
            //return false;
        }

        for (Player player : alphaTeam.getPlayers()){
            player.setRespawnLocation(alphaTeam.getSpawnLocation(),true);
            player.teleport(alphaTeam.getSpawnLocation());
        }
        for (Player player : deltaTeam.getPlayers()){
            player.setRespawnLocation(deltaTeam.getSpawnLocation(),true);
            player.teleport(deltaTeam.getSpawnLocation());
        }

        gameState = GameState.STARTING;

        startTimer();
        return true;
    }

    public enum GameState{
        STOPPED, STARTING, STARTED, FINISHED
    }

    public GameState getGameState() {
        return gameState;
    }

    //TODO: remove this; you shouldnt be allowed to change the state outside of the state manager
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    int count;
    BukkitTask task;
    public void startTimer() {
        count = 3;
        task = Bukkit.getScheduler().runTaskTimer(main, () -> {
            if (count == 0) {
                setGameState(GameState.STARTED);
                Bukkit.broadcastMessage(ChatColor.AQUA + "GO!");
                task.cancel();
            }else {
                Bukkit.broadcastMessage(ChatColor.AQUA + String.valueOf(count));
            }
            count--;
        }, 0, 20);
    }
}


