package com.kittycatmedias.destrilite.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kittycatmedias.destrilite.event.EventManager;
import com.kittycatmedias.destrilite.network.DestriliteClient;
import com.kittycatmedias.destrilite.network.DestriliteServer;
import com.kittycatmedias.destrilite.network.packet.PacketManager;

public class DestriliteGame extends Game {
	private static DestriliteGame instance;

	private SpriteBatch batch;
	private EventManager eventManager;
	private PacketManager packetManager;
	private DestriliteScreen nextScreen;
	private BitmapFont font;

	private DestriliteServer server;
	private DestriliteClient client;

	@Override
	public void create () {
		instance = this;
		batch = new SpriteBatch();
		eventManager = new EventManager();
		packetManager = new PacketManager();
		font = new BitmapFont();
		changeScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();

		if(nextScreen != null && nextScreen.getManager().update(17)){
			setScreen(nextScreen);
			nextScreen = null;
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		if(nextScreen != null)nextScreen.dispose();
		font.dispose();
		if(server != null)server.stop();
		if(client != null)client.stop();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public static DestriliteGame getInstance() {
		return instance;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public BitmapFont getFont() {
		return font;
	}

	public DestriliteScreen getNextScreen() {
		return nextScreen;
	}

	public void changeScreen(DestriliteScreen screen){
		nextScreen = screen;
	}

	public float getTextOffset(String text){
		return getTextOffset(font, text);
	}

	public float getTextOffset(BitmapFont font, String text){
		GlyphLayout layout = new GlyphLayout();
		layout.setText(font, text);
		return -layout.width / 2;
	}

	public PacketManager getPacketManager() {
		return packetManager;
	}

	public boolean isNeitherClientNorServer(){
		return !isClient() && !isServer();
	}

	public boolean isClient(){
		return client != null;
	}

	public boolean isServer(){
		return server != null;
	}

	public void createServer(){
		createServer(DestriliteServer.DEFAULT_PORT, DestriliteServer.DEFAULT_PORT + 1);
	}

	public void createServer(int udp, int tcp){
		if(server == null && client == null) {
			server = new DestriliteServer(this, udp, tcp);
			server.start();
		}
	}

	public void createClient(String ip){
		createClient(ip, DestriliteServer.DEFAULT_PORT, DestriliteServer.DEFAULT_PORT + 1);
	}

	public void createClient(String ip, int udp, int tcp){
		if(server == null && client == null) {
			client = new DestriliteClient(this, ip, udp, tcp);
			client.start();
		}
	}

	public void stopServer(){
		if(server != null){
			server.stop();
			server = null;
		}
	}

	public void stopClient(){
		if(client != null){
			client.stop();
			client = null;
		}
	}

	public DestriliteServer getServer() {
		return server;
	}
}
