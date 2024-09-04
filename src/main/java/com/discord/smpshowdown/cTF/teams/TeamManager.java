package com.discord.smpshowdown.cTF.teams;

import com.discord.smpshowdown.cTF.CTF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class TeamManager {

    CtfTeam alphaTeam;
    CtfTeam deltaTeam;

    private final CTF main;
    public TeamManager(CTF main){
        this.main = main;
        alphaTeam = new CtfTeam("red", Material.RED_WOOL, ChatColor.RED);
        deltaTeam = new CtfTeam("blue", Material.BLUE_WOOL, ChatColor.BLUE);
        alphaTeam.setEnemyTeam(deltaTeam);
        deltaTeam.setEnemyTeam(alphaTeam);
    }

    public CtfTeam getAlphaTeam() {
        return alphaTeam;
    }

    public CtfTeam getDeltaTeam() {
        return deltaTeam;
    }

    public void createTeamsOnStartup(){
        main.getServer().getScheduler().scheduleSyncDelayedTask((Plugin) main, new Runnable(){
            public void run(){
                ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
                assert scoreboardManager != null;
                Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
                if (scoreboard.getTeam(alphaTeam.name) == null){
                    System.out.println("Alpha team missing! Creating it...");
                    Team newTeam = scoreboard.registerNewTeam(alphaTeam.name);
                    newTeam.setColor(alphaTeam.teamColor);
                    alphaTeam.setTeam(newTeam);
                }
                if (scoreboard.getTeam(deltaTeam.name) == null){
                    System.out.println("Delta team missing! Creating it...");
                    Team newTeam = scoreboard.registerNewTeam(deltaTeam.name);
                    newTeam.setColor(deltaTeam.teamColor);
                    deltaTeam.setTeam(newTeam);
                }
            }
        });


    }
}
