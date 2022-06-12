package com.kittycatmedias.destrilite.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.esotericsoftware.kryonet.Connection;
import com.kittycatmedias.destrilite.entity.type.player.Player;
import com.kittycatmedias.destrilite.entity.type.player.Race;
import com.kittycatmedias.destrilite.event.EventHandler;
import com.kittycatmedias.destrilite.event.EventListener;
import com.kittycatmedias.destrilite.event.events.network.ConnectionDisconnectedEvent;
import com.kittycatmedias.destrilite.network.packet.PacketHandler;
import com.kittycatmedias.destrilite.network.packet.PacketListener;
import com.kittycatmedias.destrilite.network.packet.packets.*;
import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.generator.WorldGenerator;
import com.sun.tools.jdi.Packet;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainMenuScreen extends DestriliteScreen implements PacketListener, EventListener {

    private VerticalGroup mainGroup, spGroup, mpGroup, optionsGroup, playersGroup;
    private Table spTable, mpTable, optionsTable, exitTable, setRaceTable, startSPTable, spBackTable,
            startServerTable, startMPTable,optionsBackTable,mpBackTable,connectMPTable,cancelMPTable,playerTable;
    private CheckBox clientOrServerCheck;
    private Image spRaceImage;
    private TextField ipField, portUDPField, portTCPField, nameField;
    private Label nameLabel, mpLabel, versionLabel, optionsLabel, spLabel;

    private TextButton.TextButtonStyle textButtonStyle;
    private Label.LabelStyle labelStyle;
    private CheckBox.CheckBoxStyle checkBoxStyle;

    private AtomicBoolean serverMode;

    private Race race;
    private long id;

    private ObjectMap<Connection, Player> connections;
    private Array<Player> players;
    private Player player;

    private TextureAtlas playerAtlas;

    public MainMenuScreen(DestriliteGame game){
        super(game, new ScalingViewport(Scaling.fit, 192*2, 108*2));
        race = Race.HUMAN;

        players = new Array<>();
        connections = new ObjectMap<>();

        assetManager.load("atlas/player.atlas", TextureAtlas.class);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mainGroup.setBounds(0, 0,getViewport().getWorldWidth(),getViewport().getWorldHeight());
        spGroup.setBounds(0,0,getViewport().getWorldWidth(),getViewport().getWorldHeight());
        mpGroup.setBounds(0,0,getViewport().getWorldWidth(),getViewport().getWorldHeight());
        optionsGroup.setBounds(0,0,getViewport().getWorldWidth(),getViewport().getWorldHeight());

        mainGroup.setOrigin(mainGroup.getWidth() / 2, mainGroup.getHeight() / 2);
        spGroup.setOrigin(spGroup.getWidth() / 2, spGroup.getHeight() / 2);
        mpGroup.setOrigin(mpGroup.getWidth() / 2, mpGroup.getHeight() / 2);
        optionsGroup.setOrigin(optionsGroup.getWidth() / 2, optionsGroup.getHeight() / 2);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        stage.draw();
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        playerAtlas = assetManager.get("atlas/player.atlas");
        for(Race r : Race.getRaces())r.generateSprites(playerAtlas);

        loadMenu();
    }

    private void loadMenu(){
        serverMode = new AtomicBoolean();

        mainGroup = new VerticalGroup();
        mainGroup.space(3);

        spGroup = new VerticalGroup();
        spGroup.space(3);
        spGroup.setVisible(false);

        mpGroup = new VerticalGroup();
        mpGroup.space(3);
        mpGroup.setVisible(false);

        optionsGroup = new VerticalGroup();
        optionsGroup.space(3);
        optionsGroup.setVisible(false);

        mainGroup.setBounds(0, 0,getViewport().getWorldWidth(),getViewport().getWorldHeight());
        spGroup.setBounds(0,0,getViewport().getWorldWidth(),getViewport().getWorldHeight());
        mpGroup.setBounds(0,0,getViewport().getWorldWidth(),getViewport().getWorldHeight());
        optionsGroup.setBounds(0,0,getViewport().getWorldWidth(),getViewport().getWorldHeight());

        mainGroup.setOrigin(mainGroup.getWidth() / 2, mainGroup.getHeight() / 2);
        spGroup.setOrigin(spGroup.getWidth() / 2, spGroup.getHeight() / 2);
        mpGroup.setOrigin(mpGroup.getWidth() / 2, mpGroup.getHeight() / 2);
        optionsGroup.setOrigin(optionsGroup.getWidth() / 2, optionsGroup.getHeight() / 2);


        float buttonPad = 5;
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = game.getUISkin().getDrawable("button_up");
        textButtonStyle.down = game.getUISkin().getDrawable("button_down");
        textButtonStyle.pressedOffsetX = 1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = game.getFont();

        spTable = new Table();
        Button spButton = new TextButton("Single Player", textButtonStyle);
        spButton.pad(buttonPad);
        spTable.add(spButton).width(100);

        mpTable = new Table();
        Button mpButton = new TextButton("Multiplayer", textButtonStyle);
        mpButton.pad(buttonPad);
        mpTable.add(mpButton).width(100);

        optionsTable = new Table();
        Button optionsButton = new TextButton("Options", textButtonStyle);
        optionsButton.pad(buttonPad);
        optionsTable.add(optionsButton).width(100);

        exitTable = new Table();
        Button exitButton = new TextButton("Exit", textButtonStyle);
        exitButton.pad(buttonPad);
        exitTable.add(exitButton).width(100);

        setRaceTable = new Table();
        Button setRaceButton = new TextButton("Change Race", textButtonStyle);
        setRaceButton.pad(buttonPad);
        setRaceTable.add(setRaceButton);

        spRaceImage = new Image(race.getHeadSprite());
        spRaceImage.setScaling(Scaling.fillX);
        setRaceTable.add(spRaceImage).width(19).expand().fill();
        setRaceTable.pack();

        startSPTable = new Table();
        Button startSPButton = new TextButton("Start", textButtonStyle);
        startSPButton.pad(buttonPad);
        startSPTable.add(startSPButton).width(100);

        spBackTable = new Table();
        Button spBackButton = new TextButton("Back", textButtonStyle);
        spBackButton.pad(buttonPad);
        spBackTable.add(spBackButton).width(100);

        mpBackTable = new Table();
        Button mpBackButton = new TextButton("Back", textButtonStyle);
        mpBackButton.pad(buttonPad);
        mpBackTable.add(mpBackButton).width(100);

        optionsBackTable = new Table();
        Button optionsBackButton = new TextButton("Back", textButtonStyle);
        optionsBackButton.pad(buttonPad);
        optionsBackTable.add(optionsBackButton).width(100);

        cancelMPTable = new Table();
        Button cancelMPButton = new TextButton("Cancel", textButtonStyle);
        cancelMPButton.pad(buttonPad);
        cancelMPTable.add(cancelMPButton).width(100);

        startMPTable = new Table();
        Button startMPButton = new TextButton("Start", textButtonStyle);
        startMPButton.pad(buttonPad);
        startMPTable.add(startMPButton).width(100);

        connectMPTable = new Table();
        Button connectMPButton = new TextButton("Connect", textButtonStyle);
        connectMPButton.pad(buttonPad);
        connectMPTable.add(connectMPButton).width(100);

        startServerTable = new Table();
        Button startServerButton = new TextButton("Start Server", textButtonStyle);
        startServerButton.pad(buttonPad);
        startServerTable.add(startServerButton).width(100);

        playerTable = new Table();
        playersGroup = new VerticalGroup();
        playersGroup.pad(buttonPad);
        playerTable.add(playersGroup);


        checkBoxStyle = new CheckBox.CheckBoxStyle(game.getUISkin().getDrawable("button_up"), game.getUISkin().getDrawable("button_down"), game.getFont(), Color.WHITE);

        clientOrServerCheck = new CheckBox("Server", checkBoxStyle);
        clientOrServerCheck.pad(buttonPad);

        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.background = game.getUISkin().getDrawable("button_up");
        style.font = game.getFont();
        style.fontColor = Color.WHITE;

        ipField = new TextField("IP", style);
        ipField.setWidth(100);
        ipField.setAlignment(Align.center);
        portUDPField = new TextField("37189", style);
        portUDPField.setAlignment(Align.center);
        portUDPField.setWidth(100);
        portTCPField = new TextField("37190", style);
        portTCPField.setAlignment(Align.center);
        portTCPField.setWidth(100);
        nameField = new TextField("Name", style);
        nameField.setAlignment(Align.center);
        nameField.setWidth(100);

        labelStyle = new Label.LabelStyle(game.getFont(), Color.CORAL);

        nameLabel = new Label(DestriliteGame.NAME.toUpperCase(), labelStyle);
        nameLabel.setFontScale(3f);

        versionLabel = new Label(DestriliteGame.VERSION, labelStyle);

        spLabel = new Label("Single Player", labelStyle);
        spLabel.setFontScale(2f);

        mpLabel = new Label("Multiplayer", labelStyle);
        mpLabel.setFontScale(2f);

        optionsLabel = new Label("Options", labelStyle);
        optionsLabel.setFontScale(2f);



        spButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainGroup.setVisible(false);
                spGroup.setVisible(true);
            }
        });
        mpButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainGroup.setVisible(false);
                mpGroup.setVisible(true);
            }
        });
        optionsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainGroup.setVisible(false);
                optionsGroup.setVisible(true);
            }
        });
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        setRaceButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                race = Race.getRace((race.getID()+1)%Race.getRaces().size);
                spRaceImage.setDrawable(new TextureRegionDrawable(race.getHeadSprite()));
            }
        });
        startSPButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Player player = new Player(race, -1, "");
                game.changeScreen(new GameScreen(game, null, player));
            }
        });
        spBackButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                spGroup.setVisible(false);
                mainGroup.setVisible(true);
            }
        });
        mpBackButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mpGroup.setVisible(false);
                mainGroup.setVisible(true);
            }
        });
        optionsBackButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                optionsGroup.setVisible(false);
                mainGroup.setVisible(true);
            }
        });
        cancelMPButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cancelButton();
            }
        });
        startMPButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getServer().setAllowing(false);
                World world = new World(WorldGenerator.GRASSLANDS, MathUtils.random.nextLong(), -1);
                SetWorldPacket packet = new SetWorldPacket();
                packet.world = world.getID();
                game.getServer().sendToAll(packet, true);
                GameScreen screen = new GameScreen(game, world, player);
                game.changeScreen(screen);
                for(Player player : players)screen.addPlayer(player);
            }
        });
        connectMPButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try{
                    game.createClient(ipField.getText().replaceAll(" ", ""), Integer.parseInt(portUDPField.getText().replaceAll(" ", "")), Integer.parseInt(portTCPField.getText().replaceAll(" ", "")));
                    PlayerConnectPacket packet = new PlayerConnectPacket();
                    for(Player p : players)p.dispose();
                    players = new Array<>();
                    id = MathUtils.random.nextLong();
                    packet.id = id;
                    packet.name = nameField.getText().trim();
                    packet.race = 0;
                    game.getClient().sendToServer(packet, true);
                    mpGroup.removeActor(mpBackTable);
                    mpGroup.removeActor(portTCPField);
                    mpGroup.removeActor(portUDPField);
                    mpGroup.removeActor(connectMPTable);
                    mpGroup.removeActor(ipField);
                    mpGroup.removeActor(clientOrServerCheck);
                    mpGroup.removeActor(nameField);
                    mpGroup.addActor(cancelMPTable);
                    mpGroup.addActor(playerTable);
                }catch(Exception e){e.printStackTrace();}
            }
        });
        startServerButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try{
                    game.createServer(Integer.parseInt(portUDPField.getText().replaceAll(" ", "")), Integer.parseInt(portTCPField.getText().replaceAll(" ", "")));
                    mpGroup.removeActor(mpBackTable);
                    mpGroup.removeActor(portTCPField);
                    mpGroup.removeActor(portUDPField);
                    mpGroup.removeActor(startServerTable);
                    mpGroup.removeActor(clientOrServerCheck);
                    mpGroup.removeActor(nameField);
                    mpGroup.addActor(startMPTable);
                    mpGroup.addActor(cancelMPTable);
                    mpGroup.addActor(playerTable);
                    connections = new ObjectMap<>();
                    for(Player p : players)p.dispose();
                    players = new Array<>();
                    player = new Player(Race.getRace(0), MathUtils.random.nextLong(), nameField.getText().trim());
                    players.add(player);
                }catch(Exception e){e.printStackTrace();}
            }
        });
        clientOrServerCheck.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                serverMode.set(!serverMode.get());
                if(serverMode.get()){
                    mpGroup.removeActor(ipField);
                    mpGroup.removeActor(connectMPTable);
                    mpGroup.addActorAfter(nameField, startServerTable);
                }else{
                    mpGroup.addActorAfter(clientOrServerCheck, ipField);
                    mpGroup.addActorAfter(nameField, connectMPTable);
                    mpGroup.removeActor(startServerTable);
                }
            }
        });



        mainGroup.addActor(nameLabel);
        mainGroup.addActor(versionLabel);
        mainGroup.addActor(spTable);
        mainGroup.addActor(mpTable);
        mainGroup.addActor(optionsTable);
        mainGroup.addActor(exitTable);

        spGroup.addActor(spLabel);
        spGroup.addActor(setRaceTable);
        spGroup.addActor(startSPTable);
        spGroup.addActor(spBackTable);

        mpGroup.addActor(mpLabel);
        mpGroup.addActor(clientOrServerCheck);
        mpGroup.addActor(ipField);
        mpGroup.addActor(portUDPField);
        mpGroup.addActor(portTCPField);
        mpGroup.addActor(nameField);
        mpGroup.addActor(connectMPTable);
        mpGroup.addActor(mpBackTable);

        optionsGroup.addActor(optionsLabel);
        optionsGroup.addActor(optionsBackTable);

        stage.addActor(spGroup);
        stage.addActor(mpGroup);
        stage.addActor(mainGroup);
        stage.addActor(optionsGroup);
    }

    @PacketHandler
    public void onWorldCreate(WorldCreatePacket packet, Connection connection){
        if(game.isClient()){
            World world = WorldCreatePacket.decode(packet);
            GameScreen screen = new GameScreen(game, world, player);
            game.changeScreen(screen);
            for(Player player : players)screen.addPlayer(player);
        }
    }

    @PacketHandler
    public void onPlayerConnect(PlayerConnectPacket packet, Connection connection){
        Player player = new Player(Race.getRace(packet.race), packet.id, packet.name);
        players.add(player);


        if(game.isServer()){
            connections.put(connection, player);
            game.getServer().sendToAll(packet, true);
            for(Player p : players)if(p != player){
                PlayerConnectPacket pa = new PlayerConnectPacket();
                pa.id = p.getID();
                pa.race = p.getRace().getID();
                pa.name = p.getName();
                connection.sendTCP(pa);
            }
        }
        if(player.getID() == id)this.player = player;
        else{
            Label playerLabel = new Label(packet.name, labelStyle);
            playerLabel.setName(packet.name);
            playerLabel.setFontScale(1f);
            playersGroup.addActor(playerLabel);
        }
    }

    @PacketHandler
    public void onPlayerDisconnect(PlayerDisconnectPacket packet, Connection connection){
        Player player = Player.getPlayer(packet.id);
        if(player != null){

            for(Actor actor : playersGroup.getChildren())if(actor instanceof Label){
                Label label = (Label) actor;
                if(label.getName().equals(player.getName())){
                    playersGroup.removeActor(actor);
                    break;
                }
            }

            player.dispose();
            players.removeValue(player, true);
        }
    }

    @PacketHandler
    public void onEntityCreate(EntityCreatePacket packet, Connection connection){
        EntityCreatePacket.decode(packet);
    }

    @EventHandler
    public void onConnectionDisconnected(ConnectionDisconnectedEvent event){
        if(connections.containsKey(event.getConncetion())){
            Player player = connections.get(event.getConncetion());
            connections.remove(event.getConncetion());
            PlayerDisconnectPacket packet = new PlayerDisconnectPacket();
            packet.id = player.getID();
            onPlayerDisconnect(packet, event.getConncetion());
            game.getServer().sendToAll(packet, true);
        }
    }

    private void cancelButton(){
        if(serverMode.get()){
            game.getServer().sendToAll(new ServerClosePacket(), true);
            game.stopServer();
        }
        else game.stopClient();
        mpGroup.addActorAfter(mpLabel, mpBackTable);
        if(serverMode.get())mpGroup.addActorAfter(mpLabel, startServerTable);
        else mpGroup.addActorAfter(mpLabel, connectMPTable);
        mpGroup.addActorAfter(mpLabel, portTCPField);
        mpGroup.addActorAfter(mpLabel, portUDPField);
        mpGroup.addActorAfter(portTCPField, nameField);
        if(!serverMode.get())mpGroup.addActorAfter(mpLabel, ipField);
        mpGroup.addActorAfter(mpLabel, clientOrServerCheck);
        mpGroup.removeActor(cancelMPTable);
        mpGroup.removeActor(startMPTable);
        mpGroup.removeActor(playerTable);
        playersGroup.clearChildren();
    }
}
