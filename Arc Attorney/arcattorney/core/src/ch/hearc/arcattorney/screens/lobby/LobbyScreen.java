package ch.hearc.arcattorney.screens.lobby;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.google.gson.Gson;
import ch.hearc.arcattorney.ArcAttorney;
import ch.hearc.arcattorney.affair.Case;
import ch.hearc.arcattorney.player.Role;
import ch.hearc.arcattorney.player.network.listener.MessageListener;
import ch.hearc.arcattorney.player.network.message.Message;
import ch.hearc.arcattorney.player.network.message.MessageType;
import ch.hearc.arcattorney.player.network.message.SubMessageType;
import ch.hearc.arcattorney.utils.Constants;

public class LobbyScreen implements Screen {

	// Main elements
	private ArcAttorney game;
	private Stage stage;

	// Roles
	private Map<Role, Integer> indexes;
	private String[] roles;

	// Container
	private CheckBox[] listCheckBoxs;
	private SelectBox<String>[] listSelectBoxs;
	private Label[] listPseudos;

	// Number of player in the lobby
	private int clientCount;

	// User id
	private int id;

	// Is the interface the host's
	private boolean isHost;

	// UI elements
	// Case elements
	private Texture[] images;
	private Case cases[];
	
	// Current case index
	private int currentCase;
	
	private Image skipLeft;
	private Image logoCase;
	private Image skipRight;
	private Label titleCase;
	private TextArea descriptionCase;

	// Player 1 part
	private Label pseudo1;
	private CheckBox ready1;
	private SelectBox<String> selectBox1;

	// Player 2 part
	private Label pseudo2;
	private CheckBox ready2;
	private SelectBox<String> selectBox2;

	// Player 3 part
	private Label pseudo3;
	private CheckBox ready3;
	private SelectBox<String> selectBox3;

	// Error label
	private Label labelError;

	// Launch button
	private TextButton btnReady;

	public LobbyScreen(ArcAttorney game) {
		this.game = game;
		this.stage = new Stage();

		Table table = new Table();
		table.setDebug(Constants.SCREEN_DEBUG);
		table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Index array used to link number to roles
		this.indexes = new HashMap<Role, Integer>();
		this.indexes.put(Role.UNROLLED, 0);
		this.indexes.put(Role.JUDGE, 1);
		this.indexes.put(Role.DEFENCE_ATTORNEY, 2);
		this.indexes.put(Role.PROSECUTOR, 3);
		this.roles = new String[] { "Sans role", "Juge", "Avocat de la defense", "Procureur" };

		// Nb of client connected to the lobby
		this.clientCount = 0;

		// Put the isHost at false at default
		this.isHost = false;
		// User id
		this.id = -1;
		// Case elements
		this.cases = new Case[2];
		this.cases[0] = new Case(Constants.ACTOR,
				"Un acteur tres connu aux Etats-unis est accuse d'avoir tue son frere pour une histoire d'adultere.",
				"Voici l'accuse Mr. Thomson, c'est un mechant",
				1);
		this.cases[1] = new Case(Constants.CHEF,
				"Ce chef peu repute aurait empoisonne le plat offert au critique culinaire qui devait mettre un terme a sa carriere",
				"Voici l'accuse Mr. Bouilla Blaise, c'est un mechant",
				2);

		this.images = new Texture[2];
		this.images[0] = new Texture(Gdx.files.internal(this.cases[0].getImagePath()));
		this.images[1] = new Texture(Gdx.files.internal(this.cases[1].getImagePath()));

		this.currentCase = 0;
		game.currentCase = cases[0];
		
		this.skipLeft = new Image(new Texture(Gdx.files.internal(Constants.SKIPLEFT)));
		this.logoCase = new Image(this.images[0]);
		this.skipRight = new Image(new Texture(Gdx.files.internal(Constants.SKIPRIGHT)));
		this.titleCase = new Label("Affaire numero : " + cases[0].getIndex(), game.skin);
		this.descriptionCase = new TextArea(cases[0].getDescription(), game.skin);
		this.descriptionCase.setTouchable(Touchable.disabled);

		// Element for player 1
		this.pseudo1 = new Label("-", game.skin);
		this.ready1 = new CheckBox("", game.skin);
		this.ready1.setDisabled(true);
		this.ready1.setChecked(false);
		this.selectBox1 = new SelectBox<String>(game.skin);
		this.selectBox1.setDisabled(true);
		this.selectBox1.setItems(this.roles);

		// Element for player 2
		this.pseudo2 = new Label("-", game.skin);
		this.ready2 = new CheckBox("", game.skin);
		this.ready2.setDisabled(true);
		this.ready2.setChecked(false);
		this.selectBox2 = new SelectBox<String>(game.skin);
		this.selectBox2.setDisabled(true);
		this.selectBox2.setItems(this.roles);

		// Element for player 3
		this.pseudo3 = new Label("-", game.skin);
		this.ready3 = new CheckBox("", game.skin);
		this.ready3.setDisabled(true);
		this.ready3.setChecked(false);
		this.selectBox3 = new SelectBox<String>(game.skin);
		this.selectBox3.setDisabled(true);
		this.selectBox3.setItems(this.roles);

		// Error label
		this.labelError = new Label("", game.skin);
		this.labelError.setColor(Color.RED);

		// Ready button
		this.btnReady = new TextButton("Pret !", game.skin);
		this.btnReady.setDisabled(true);
		this.btnReady.setTouchable(Touchable.disabled);

		// Array for pseudos
		this.listPseudos = new Label[3];
		this.listPseudos[0] = this.pseudo1;
		this.listPseudos[1] = this.pseudo2;
		this.listPseudos[2] = this.pseudo3;

		// Array for check boxes
		this.listCheckBoxs = new CheckBox[3];
		this.listCheckBoxs[0] = this.ready1;
		this.listCheckBoxs[1] = this.ready2;
		this.listCheckBoxs[2] = this.ready3;

		// Array for select boxes
		this.listSelectBoxs = new SelectBox[3];
		this.listSelectBoxs[0] = this.selectBox1;
		this.listSelectBoxs[1] = this.selectBox2;
		this.listSelectBoxs[2] = this.selectBox3;

		setTable(table);
		this.stage.addActor(table);
	}

	// Set the libgdx table
	private void setTable(Table table) {
		table.add(this.skipLeft).padBottom(30).left();
		table.add(this.logoCase).width(100).height(100).padBottom(30).padLeft(80);
		table.add(this.skipRight).padBottom(30).right();
		table.row();
		table.add(this.titleCase).colspan(3).center().padBottom(30);
		table.row();
		table.add(this.descriptionCase).colspan(3).width(350).height(75).padBottom(50);
		table.row();
		table.add(this.pseudo1).width(100).padBottom(30);
		table.add(this.ready1).width(100).padBottom(30);
		table.add(this.selectBox1).padBottom(30);
		table.row();
		table.add(this.pseudo2).width(100).padBottom(30);
		table.add(this.ready2).width(100).padBottom(30);
		table.add(this.selectBox2).padBottom(30);
		table.row();
		table.add(this.pseudo3).width(100).padBottom(50);
		table.add(this.ready3).width(100).padBottom(50);
		table.add(this.selectBox3).padBottom(50);
		table.row();
		table.add(this.labelError).colspan(3).padBottom(50);
		table.row();
		table.add(this.btnReady).colspan(3).width(200).height(50);
	}

	public void control() {
		// When the client change his role
		this.listSelectBoxs[this.id].addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.client.setRole(getKey(indexes, listSelectBoxs[id].getSelectedIndex()));
				game.client.send(new Message(game.client.getPseudo(), null, null,
						getKey(indexes, listSelectBoxs[id].getSelectedIndex()), MessageType.LOBBY,
						SubMessageType.ROLE_CHANGE));
			}
		});
		// When the client set himself as ready
		this.listCheckBoxs[this.id].addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (listCheckBoxs[id].isChecked()) {
					game.client.send(new Message(game.client.getPseudo(), null, null,
							getKey(indexes, listSelectBoxs[id].getSelectedIndex()), MessageType.LOBBY,
							SubMessageType.READY));
				} else {
					game.client.send(new Message(game.client.getPseudo(), null, null,
							getKey(indexes, listSelectBoxs[id].getSelectedIndex()), MessageType.LOBBY,
							SubMessageType.NOT_READY));
				}
			}
		});
		// If the client is the host, the client can launch the game if everyone is
		// ready and have a different role
		if (isHost) {
			this.btnReady.setDisabled(false);
			this.btnReady.setTouchable(Touchable.enabled);
			this.btnReady.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					// Launch to game
					if (allChecked() && differentRoles()) {
						game.client.send(new Message(game.client.getPseudo(), null, null, game.client.getRole(), MessageType.LOBBY, SubMessageType.LAUNCHING_GAME));
					} else {
						labelError.setText("Les joueurs doivent se mettre pret et chacun doit choisir un role different");
					}
				}
			});
			this.skipLeft.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if(currentCase - 1 < 0) {
						currentCase = images.length -1;						
					} else {
						currentCase--;
					}
					String message = Integer.toString(currentCase);
					game.currentCase = cases[currentCase];
					game.client.send(new Message(game.client.getPseudo(), message, null, game.client.getRole(), MessageType.LOBBY, SubMessageType.AFFAIR_CHANGE));
				}
			});
			this.skipRight.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if(currentCase + 1 > images.length - 1) {
						currentCase = 0;						
					} else {
						currentCase++;
					}
					String message = Integer.toString(currentCase);
					game.currentCase = cases[currentCase];
					game.client.send(new Message(game.client.getPseudo(), message, null, game.client.getRole(), MessageType.LOBBY, SubMessageType.AFFAIR_CHANGE));
				}
			});
		}
	}

	public void listenerControl() {
		this.game.client.addMessageListener(new MessageListener() {

			@Override
			public void messageReceived(Message message) {
				if (message.getsType() == SubMessageType.ENTERING_LOBBY) {

					// Get the data from the other clients
					Gson gson = new Gson();
					Message[] data = gson.fromJson(message.getContent(), Message[].class);
					// Start up to 0 to get the count ready
					clientCount = 0;
					for (int i = 0; i < data.length; i++) {
						if (data[i] != null) {
							listPseudos[i].setText(data[i].getPseudo());
							listCheckBoxs[i].setChecked(data[i].getsType() == SubMessageType.READY);
							listSelectBoxs[i].setSelected(roles[indexes.get(data[i].getRole())]);
							clientCount++;
						}
					}

					// if this is the first connection of the client
					if (id == -1) {
						id = message.getPositionId();
						listCheckBoxs[message.getPositionId()].setDisabled(false);
						listSelectBoxs[message.getPositionId()].setDisabled(false);
						control();
					}

					listPseudos[message.getPositionId()].setText(message.getPseudo());
				} else {
					// If the sub-type of the message received is
					if (message.getsType() == SubMessageType.ROLE_CHANGE) {
						// Add the role change to the respective client
						listSelectBoxs[message.getPositionId()].setSelected(roles[indexes.get(message.getRole())]);
					} else if (message.getsType() == SubMessageType.AFFAIR_CHANGE) {
						// Change the case
						Texture newTexture = images[Integer.parseInt(message.getContent())];
						String description = cases[Integer.parseInt(message.getContent())].getDescription();
						
						logoCase.setDrawable(new SpriteDrawable(new Sprite(newTexture)));
						descriptionCase.setText(description);
						titleCase.setText("Affaire numero : " + cases[Integer.parseInt(message.getContent())].getIndex());
					} else if (message.getsType() == SubMessageType.READY) {
						// Add the ready case to the respective client
						listCheckBoxs[message.getPositionId()].setChecked(true);
					} else if (message.getsType() == SubMessageType.NOT_READY) {
						listCheckBoxs[message.getPositionId()].setChecked(false);
					} else if (message.getsType() == SubMessageType.LAUNCHING_GAME) {
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								if (game.client.getRole() == Role.JUDGE) {
									game.gameUIJudge.listenerControl();
									game.setScreen(game.gameUIJudge);
								} else {
									game.gameUIAP.listenerControl();
									game.setScreen(game.gameUIAP);
								}
							}
						});

					} else if (message.getsType() == SubMessageType.SOMEONE_IS_QUITTING) {
						// Rearrange the place of the clients
						moveClients(message.getPositionId());
					}
				}
			}
		});
	}

	public void setHostRole(boolean isHost) {
		this.isHost = isHost;
	}

	public Role getKey(Map<Role, Integer> map, Integer value) {
		for (Entry<Role, Integer> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void moveClients(int quittingId) {
		if (quittingId > 0) {
			if (quittingId < clientCount - 1) {
				for (int i = quittingId + 1; i < clientCount; i++) {
					listPseudos[i - 1].setText(listPseudos[i].getText());
					listCheckBoxs[i - 1].setChecked(listCheckBoxs[i].isChecked());
					listSelectBoxs[i - 1].setSelectedIndex(listSelectBoxs[i].getSelectedIndex());
				}
			}
			listPseudos[clientCount - 1].setText("-");
			listCheckBoxs[clientCount - 1].setChecked(false);
			listSelectBoxs[clientCount - 1].setSelectedIndex(indexes.get(Role.UNROLLED));
			clientCount--;
		}
	}

	private boolean allChecked() {
		for (int i = 0; i < listCheckBoxs.length; i++) {
			if (!listCheckBoxs[i].isChecked()) {
				return false;
			}
		}

		return true;
	}

	private boolean differentRoles() {
		Set<String> roles = new HashSet();

		for (int i = 0; i < listSelectBoxs.length; i++) {
			roles.add(listSelectBoxs[i].getSelected());
		}

		return (roles.size() == 3);
	}

	/*
	 * private boolean roleDifferent() {
	 * 
	 * }
	 */

	@Override
	public void show() {
		System.out.println("Screen : " + LobbyScreen.class.getSimpleName());
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
