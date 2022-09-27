package ch.hearc.arcattorney.screens.connection;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.io.IOException;
import java.net.UnknownHostException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ch.hearc.arcattorney.ArcAttorney;
import ch.hearc.arcattorney.player.Role;
import ch.hearc.arcattorney.player.network.Player;
import ch.hearc.arcattorney.player.network.Server;
import ch.hearc.arcattorney.player.network.message.Message;
import ch.hearc.arcattorney.player.network.message.MessageType;
import ch.hearc.arcattorney.player.network.message.SubMessageType;
import ch.hearc.arcattorney.utils.Constants;

public class ConnectionScreen implements Screen {

	private ArcAttorney game;
	private Stage stage;

	private Label labelPSeudo;
	private Label labelIp;
	private Label labelError;
	private TextField textFieldPseudo;
	private TextField textFieldIp;
	private TextButton btnMain;
	private TextButton btnQuitter;
	private TextButton btnAbout;
	private CheckBox createGame;
	private Image imageLogo;

	public ConnectionScreen(ArcAttorney game) {
		this.game = game;
		this.stage = new Stage();
		Table table = new Table();

		table.setDebug(Constants.SCREEN_DEBUG);
		table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		this.imageLogo = new Image(new Texture(Gdx.files.internal(Constants.HAMMER)));
		this.labelPSeudo = new Label("Pseudo : ", game.skin);
		this.textFieldPseudo = new TextField("", game.skin);
		this.labelIp = new Label("Adresse IP : ", game.skin);
		this.textFieldIp = new TextField("127.0.0.1", game.skin);
		this.labelError = new Label("", game.skin);
		this.labelError.setColor(Color.RED);
		this.createGame = new CheckBox("  Souhaitez-vous creer une partie ?", game.skin);
		this.btnMain = new TextButton("Connexion", game.skin);
		this.btnQuitter = new TextButton("Quitter", game.skin);
		this.btnAbout = new TextButton("A propos", game.skin);

		setTable(table);

		this.stage.addActor(table);

		control();
	}

	/**
	 * Position element in to the table
	 * 
	 * @param table
	 */
	private void setTable(Table table) {
		table.add(this.imageLogo).colspan(2).padBottom(30).width(100).height(100);
		table.row();
		table.add(this.labelPSeudo).padBottom(30);
		table.add(this.textFieldPseudo).padBottom(30);
		table.row();
		table.add(this.labelIp).padBottom(30);
		table.add(this.textFieldIp).padBottom(30);
		table.row();
		table.add(this.labelError).colspan(2).padBottom(30);
		table.row();
		table.add(this.createGame).colspan(2).padBottom(30);
		table.row();
		table.add(this.btnMain).colspan(2).width(200).height(50).padBottom(20);
		table.row();
		table.add(this.btnAbout).colspan(2).width(200).height(50).padBottom(20);
		table.row();
		table.add(this.btnQuitter).colspan(2).width(200).height(50);
	}

	public void control() {
		// Change the functionality of the main button
		this.createGame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (createGame.isChecked()) {
					btnMain.setText("Creer une partie");
					labelIp.setVisible(false);
					textFieldIp.setVisible(false);
				} else {
					btnMain.setText("Connexion");
					labelIp.setVisible(true);
					textFieldIp.setVisible(true);
				}
			}
		});
		// Add a listener to the main button
		this.btnMain.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Check if the pseudo field is empty or not (pseudo required to connect
				if (!textFieldPseudo.getText().isEmpty()) {
					// If the client want to create a game
					if (createGame.isChecked()) {
						game.server = new Server(5000);
						try {
							game.client = new Player("127.0.0.1", 5000, textFieldPseudo.getText(), Role.UNROLLED);
							game.lobbyScreen.setHostRole(true);
							game.lobbyScreen.listenerControl();
							game.client.send(new Message(game.client.getPseudo(), null, null, game.client.getRole(), MessageType.LOBBY, SubMessageType.ENTERING_LOBBY));
							// Go to the lobby
							game.setScreen(game.lobbyScreen);
						} catch (UnknownHostException ioe) {
							labelError.setText("L'hote est introuvable.");
						} catch (IOException ioe) {
							labelError.setText("Un probleme de connexion est apparu.");
						}
					} else {
						// If the client want to join a game
						// Create the client
						try {
							game.server = null;
							game.client = new Player(textFieldIp.getText(), 5000, textFieldPseudo.getText(), Role.UNROLLED);
							game.lobbyScreen.setHostRole(false);
							game.lobbyScreen.listenerControl();
							game.client.send(new Message(game.client.getPseudo(), null, null, game.client.getRole(), MessageType.LOBBY, SubMessageType.ENTERING_LOBBY));
							// Go to the lobby
							game.setScreen(game.lobbyScreen);
						} catch (UnknownHostException ioe) {
							labelError.setText("L'hote est introuvable.");
						} catch (IOException ioe) {
							labelError.setText("Un probleme de connexion est apparu.");
						}
					}
				} else {
					labelError.setText("Il faut entrer un pseudo afin de se connecter.");
				}
			}
		});

		this.btnQuitter.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
				
		this.btnAbout.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Dialog dia = new Dialog("A propos", game.skin) {
					@Override
					protected void result(Object object) {
						// rien
					}
				};
				Image schoolLogo = new Image(new Texture(Gdx.files.internal(Constants.SCHOOL)));
				dia.getContentTable().add(schoolLogo).padBottom(100);
				dia.getContentTable().row();
				dia.getContentTable().add(new Label("Bruno Costa, Diogo Lopes Da Silva et Valentino Izzo", game.skin));
		        dia.getContentTable().row();
		        dia.getContentTable().add(new Label("Tous droits reserves", game.skin));
				dia.button("Retour", true);
				dia.setPosition(500, 100);
				dia.setWidth(700);
				dia.setHeight(500);
				dia.setMovable(true);
				stage.addActor(dia);
			}
		});
	}

	@Override
	public void show() {
		System.out.println("Screen : " + ConnectionScreen.class.getSimpleName());
		Gdx.input.setInputProcessor(this.stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(207 / 255f, 216 / 255f, 220 / 255f, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.stage.act(delta);
		this.stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		System.out.println(width + ":" + height);
		stage.getViewport().update(width, height);
	}

	@Override
	public void pause() {
		// rien
	}

	@Override
	public void resume() {
		game.camera.position.set(game.camera.position.x, game.camera.position.y, 0);
		game.camera.update();
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		this.stage.dispose();
	}
}
