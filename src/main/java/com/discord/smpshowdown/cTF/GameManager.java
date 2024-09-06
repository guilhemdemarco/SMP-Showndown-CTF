package com.discord.smpshowdown.cTF;

import com.discord.smpshowdown.cTF.teams.CtfTeam;
import com.discord.smpshowdown.cTF.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class GameManager {

    GameState gameState;

    private CTF main;
    int count;
    BukkitTask countdownTask;
    BukkitTask timerTask;
    BukkitTask task;

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

//        for (Player player : alphaTeam.getPlayers()){
//            player.setRespawnLocation(alphaTeam.getSpawnLocation(),true);
//            player.teleport(alphaTeam.getSpawnLocation());
//        }
//        for (Player player : deltaTeam.getPlayers()){
//            player.setRespawnLocation(deltaTeam.getSpawnLocation(),true);
//            player.teleport(deltaTeam.getSpawnLocation());
//        }

        gameState = GameState.STARTING;

        startCountdownTimer();
        return true;
    }

    public void stopGame(CtfTeam winner){
        setGameState(GameState.FINISHED);
        Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.GOLD.toString() +
                "GAME'S OVER!");

        // if there isnt a winning team, that means time ran out, or was stopped by a command, and it's a draw!
        if (winner == null){
            String message = ChatColor.BOLD.toString() + ChatColor.GOLD.toString() +
                    "It's a draw! Nobody wins!";
            CTF.bossbar.updateBossbar(message, 1.0);
            Bukkit.broadcastMessage(message);
        }
        else {
            timerTask.cancel();
            String message = ChatColor.BOLD.toString() + winner.getTeamColor() +
                    String.format("Team %s wins!", winner.getName());
            CTF.bossbar.updateBossbar(message, 1.0);
            Bukkit.broadcastMessage(message);
        }

        task = Bukkit.getScheduler().runTaskLater(main, () ->{
            setGameState(GameState.STOPPED);
            Bukkit.broadcastMessage("Game can start again");
            CTF.bossbar.updateBossbar(Bossbar.defaultTitle, 1.0);
        }, 20*5);
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

    public void startCountdownTimer() {
        count = 3;
        countdownTask = Bukkit.getScheduler().runTaskTimer(main, () -> {
            if (count == 0) {
                setGameState(GameState.STARTED);
                Bukkit.broadcastMessage(ChatColor.AQUA + "GO!");
                countdownTask.cancel();
                startGameTimer();
            }else {
                Bukkit.broadcastMessage(ChatColor.AQUA + String.valueOf(count));
            }
            count--;
        }, 0, 20);
    }

    public void startGameTimer() {
        int limit = 10;
        count = 10;
        Bossbar bossbar = CTF.bossbar;
        timerTask = Bukkit.getScheduler().runTaskTimer(main, () -> {
            if (count == 0) {
                Bukkit.broadcastMessage("Game ended!");
                stopGame(null);
                timerTask.cancel();
            } else {
                Bukkit.broadcastMessage(ChatColor.AQUA + String.valueOf(count));
                int newcount = count;
                String barMessage = "";
                barMessage += " - Time remaining: " + String.format("%02d:%02d", (newcount % 3600) / 60, newcount % 60) + " - ";
                if (CTF.teamManager.getAlphaTeam().isFlagTaken()) barMessage += ChatColor.BOLD.toString() + CTF.teamManager.getAlphaTeam().getTeamColor() +
                        String.format("%s FLAG TAKEN", CTF.teamManager.getAlphaTeam().getName()).toUpperCase();
                if (CTF.teamManager.getDeltaTeam().isFlagTaken()) barMessage += ChatColor.BOLD.toString() + CTF.teamManager.getDeltaTeam().getTeamColor() +
                        String.format("%s FLAG TAKEN", CTF.teamManager.getDeltaTeam().getName()).toUpperCase();
                bossbar.updateBossbar(barMessage, (double) newcount / limit);
            }
            count--;
        }, 0, 20);
    }

}


