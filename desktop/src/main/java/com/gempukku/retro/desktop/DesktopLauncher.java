package com.gempukku.retro.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gempukku.secsy.gaming.SecsyGameApplication;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Set<String> activeProfiles = new HashSet<String>();
        // Basic setup - scanning all the things from ClassPath
        activeProfiles.add("reflectionsEntityComponentFieldHandler");
        activeProfiles.add("reflectionsNameComponentManager");
        activeProfiles.add("reflectionsComponentFieldConverter");
        activeProfiles.add("reflectionsPrefabSource");
        // Basic entity related staff
        activeProfiles.add("simpleEntityManager");
        activeProfiles.add("prefabManager");
        activeProfiles.add("nameConventionComponents");
        activeProfiles.add("annotationEventDispatcher");
        activeProfiles.add("simpleEntityIndexManager");
        // Rest of the generic stuff
        activeProfiles.add("gameLoop");
        activeProfiles.add("audioManager");
        activeProfiles.add("pipelineRenderer");
        activeProfiles.add("easing");
        activeProfiles.add("time");
        activeProfiles.add("textureAtlas");
        activeProfiles.add("2dCamera");
        activeProfiles.add("platformer2dMovement");
        activeProfiles.add("basic2dPhysics");
        activeProfiles.add("backgroundMusic");
        activeProfiles.add("ai");
        activeProfiles.add("aiMovement");
        activeProfiles.add("aiWait");
        activeProfiles.add("delayActions");
        activeProfiles.add("sprites");
        activeProfiles.add("bobbingSprites");
        activeProfiles.add("fadingSprites");
        activeProfiles.add("genesisSimulation");
        activeProfiles.add("colorTint");
        activeProfiles.add("actions");
        activeProfiles.add("activateWithSensor");
        activeProfiles.add("faction");
        activeProfiles.add("spawn");
        activeProfiles.add("combat");
        activeProfiles.add("weapon");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.fullscreen = Boolean.parseBoolean(arg[0]);
        config.resizable = false;
        config.width = 640;
        config.height = 480;

        config.foregroundFPS = 0;
        config.vSyncEnabled = true;
        config.title = "Ludum Dare 43";
        new LwjglApplication(new SecsyGameApplication(0, 0, activeProfiles), config);
    }

    private static DisplayMode getLargestDisplayMode() {
        int width = 0;
        DisplayMode result = null;
        GraphicsEnvironment ge = GraphicsEnvironment.
                getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (DisplayMode displayMode : gs[0].getDisplayModes()) {
            if (displayMode.getWidth() > width) {
                width = displayMode.getWidth();
                result = displayMode;
            }
        }
        return result;
    }
}
