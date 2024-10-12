package com.discord.smpshowdown.cTF.config;

import com.discord.smpshowdown.cTF.CTF;
import com.discord.smpshowdown.cTF.teams.CtfTeam;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Arrays;

public class ConfigManager {

    private CTF main;

    private final CtfTeam[] teams;
    private final long flagRetakeTimeMillis;
    private final int flagRetakeArea;
    private final int gameDuration;

    public ConfigManager(CTF main) {
        this.main = main;
        this.teams = loadTeams();
        this.flagRetakeTimeMillis = main.getConfig().getLong("flag_retake_time_millis");
        this.flagRetakeArea = main.getConfig().getInt("flag_retake_area");
        this.gameDuration = main.getConfig().getInt("game_duration_seconds");
    }

    public CtfTeam[] getTeams() {
        return teams;
    }

    public long getFlagRetakeTimeMillis() {
        return flagRetakeTimeMillis;
    }

    public int getFlagRetakeArea() {
        return flagRetakeArea;
    }

    public int getGameDuration() {
        return gameDuration;
    }

    private CtfTeam[] loadTeams(){
        CtfTeam alphaTeam = loadTeam("alpha");
        CtfTeam deltaTeam = loadTeam("delta");

        // by default, if you create a location, the rotation will be pointed towards the north, which is not ideal
        // this will make it so, on spawn, they will point towards each other's spawn
        float[] alphaYawPitch = getYawPitch(alphaTeam.getSpawnLocation(), deltaTeam.getSpawnLocation());
        float[] deltaYawPitch = getYawPitch(deltaTeam.getSpawnLocation(), alphaTeam.getSpawnLocation());

        Location newAlphaLocation = alphaTeam.getSpawnLocation();
        newAlphaLocation.setYaw(alphaYawPitch[0]);
        newAlphaLocation.setPitch(alphaYawPitch[1]);
        alphaTeam.setSpawnLocation(newAlphaLocation);

        Location newDeltaLocation = deltaTeam.getSpawnLocation();
        newDeltaLocation.setYaw(deltaYawPitch[0]);
        newDeltaLocation.setPitch(deltaYawPitch[1]);
        deltaTeam.setSpawnLocation(newDeltaLocation);

        return new CtfTeam[]{alphaTeam, deltaTeam};
    }

    private CtfTeam loadTeam(String team){
        String name = main.getConfig().getString("teams." + team + ".name");
        Material captureBlock = Material.valueOf(main.getConfig().getString("teams." + team + ".captureBlock"));;
        ChatColor teamColor = ChatColor.valueOf(main.getConfig().getString("teams." + team + ".teamColor"));;;
        Material bannerBlock = Material.valueOf(main.getConfig().getString("teams." + team + ".bannerBlock"));;
        String[] spawnCoordinates = main.getConfig().getStringList("teams." + team + ".spawn").toArray(new String[0]);
        System.out.println("teams." + team + ".spawn : " + Arrays.toString(spawnCoordinates));
        Location spawnLocation = new Location(main.getServer().getWorld("world"),
                Double.parseDouble(spawnCoordinates[0]), Double.parseDouble(spawnCoordinates[1]), Double.parseDouble(spawnCoordinates[2]) );
        return new CtfTeam(name, captureBlock, teamColor, bannerBlock, spawnLocation);
    }

    public static float[] getYawPitch(Location from, Location to) {
        // Calculate differences
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();

        // Calculate yaw
        double yaw = Math.atan2(dz, dx) * 180.0 / Math.PI - 90.0;

        // Calculate pitch
        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        double pitch = -Math.atan2(dy, distanceXZ) * 180.0 / Math.PI;

        // Return yaw and pitch as a float array
        return new float[] {(float) yaw, (float) pitch};
    }
}
