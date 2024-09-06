package com.discord.smpshowdown.cTF;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Bossbar {
    private BukkitTask task;
    private final CTF main;
    private BossBar bossbar;
    private double progress;
    public static String defaultTitle = "SMP Showndown - Capture the Flag";

    public Bossbar(CTF main) {
        this.main = main;
        this.progress = 1.0;
        createBar();
    }

    public void createBar(){
        bossbar = Bukkit.createBossBar(
                defaultTitle,
                BarColor.YELLOW,
                BarStyle.SEGMENTED_10
                );
        bossbar.setVisible(true);
    }

    public void updateBossbar(String newTitle, double progress){
        this.progress = progress;
        bossbar.setTitle(newTitle);
        bossbar.setProgress(progress);
    }

    public void addPlayer(Player player){
        bossbar.addPlayer(player);
    }

    public BossBar getBossbar() {
        return bossbar;
    }
}
