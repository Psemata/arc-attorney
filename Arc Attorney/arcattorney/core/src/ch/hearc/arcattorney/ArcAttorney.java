package ch.hearc.arcattorney;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import ch.hearc.arcattorney.affair.Case;
import ch.hearc.arcattorney.player.network.Player;
import ch.hearc.arcattorney.player.network.Server;
import ch.hearc.arcattorney.player.network.message.Message;
import ch.hearc.arcattorney.player.network.message.MessageType;
import ch.hearc.arcattorney.player.network.message.SubMessageType;
import ch.hearc.arcattorney.screens.connection.ConnectionScreen;
import ch.hearc.arcattorney.screens.game.DisplayHolder;
import ch.hearc.arcattorney.screens.game.GameUIAP;
import ch.hearc.arcattorney.screens.game.GameUIJudge;
import ch.hearc.arcattorney.screens.lobby.LobbyScreen;
import ch.hearc.arcattorney.screens.menu.EndScreen;
import ch.hearc.arcattorney.utils.Constants;

public class ArcAttorney extends Game {
	// Manage assets (images, logo, elements)
	public AssetManager assetManager;
	// Draw elements on the frame
	public SpriteBatch batch;
	// What the user sees
	public OrthographicCamera camera;
	// Coordinates axes used on the application
	public Viewport viewport;
	// Design of all the elements
	public Skin skin;

	// Screens
	public ConnectionScreen connectionScreen;
	public LobbyScreen lobbyScreen;
	public GameUIAP gameUIAP;
	public GameUIJudge gameUIJudge;
	public EndScreen endScreen;

	// Main elements
	public Player client;
	public Server server;
	public Case currentCase;

	@Override
	public void create() {
		// Initialize main elements of the application's frame
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		// Create the camera
		camera = new OrthographicCamera(Constants.GAME_WORLD_WIDTH, Constants.GAME_WORLD_HEIGHT);
		// Set the position of the Camera at the center
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		// float aspectRatio = (float) Gdx.graphics.getHeight() / (float)
		// Gdx.graphics.getWidth();
		viewport = new StretchViewport(Constants.GAME_WORLD_WIDTH, Constants.GAME_WORLD_HEIGHT, camera);
		skin = new Skin(Gdx.files.internal(Constants.SKIN), new TextureAtlas(Gdx.files.internal(Constants.ATLAS)));

		// Set the network data to null
		this.client = null;
		this.server = null;
		this.currentCase = null;

		// Initialize the screens
		this.connectionScreen = new ConnectionScreen(this);
		this.lobbyScreen = new LobbyScreen(this);
		this.gameUIAP = new GameUIAP(this);
		this.gameUIJudge = new GameUIJudge(this);
		this.endScreen = new EndScreen(this);
		setScreen(this.connectionScreen);
	}

	public void restart() {
		// Set the network data to null
		this.client = null;
		this.server = null;
		this.currentCase = null;

		// Initialize the screens
		this.connectionScreen = new ConnectionScreen(this);
		this.lobbyScreen = new LobbyScreen(this);
		this.gameUIAP = new GameUIAP(this);
		this.gameUIJudge = new GameUIJudge(this);
		this.endScreen = new EndScreen(this);
	}

	public void killClient() {
		if (this.client != null) {
			this.client.stop();
		}
	}

	public void killServer() {
		if (this.server != null) {
			this.server.stop();
		}
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		if (client != null) {
			client.send(new Message(client.getPseudo(), null, null, client.getRole(), MessageType.LOBBY,
					SubMessageType.QUITTING));
		}
		batch.dispose();
	}
}
