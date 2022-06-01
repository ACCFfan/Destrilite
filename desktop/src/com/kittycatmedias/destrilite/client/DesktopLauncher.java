package com.kittycatmedias.destrilite.client;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.kittycatmedias.destrilite.client.DestriliteGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Destrilite");
		config.useVsync(true);
		config.setWindowedMode(1080,720);
		new Lwjgl3Application(new DestriliteGame(), config);
	}
}
