package ch.hearc.arcattorney.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import ch.hearc.arcattorney.ArcAttorney;
import ch.hearc.arcattorney.player.Role;
import ch.hearc.arcattorney.player.network.listener.MessageListener;
import ch.hearc.arcattorney.player.network.message.Message;
import ch.hearc.arcattorney.player.network.message.MessageType;
import ch.hearc.arcattorney.player.network.message.SubMessageType;
import ch.hearc.arcattorney.screens.lobby.LobbyScreen;
import ch.hearc.arcattorney.utils.Constants;

public class GameUIJudge implements Screen {

	private ArcAttorney game;
	private Stage stage;
	private Stage stageDialog;
	private DisplayHolder displayHolder;
	private SpriteBatch batch;
	private Image background;
	private MessageArea messageArea;
	private TextButton btnVerdict;
	private TextButton btnEvidence;
	private TextButton btnMessageHistory;
	private TextButton btnSend;
	private TextArea text;
	private boolean gagne;
	private Timer time;
	
	private History dialogHistorique;

	public GameUIJudge(ArcAttorney game) {
		this.game = game;
		this.displayHolder = new DisplayHolder(game);
		this.stage = new Stage();
		this.stageDialog = new Stage();
		this.batch = game.batch;
		this.background = new Image(new Texture(Gdx.files.internal(Constants.COURT)));
		this.messageArea = new MessageArea("", game.skin);
		this.btnVerdict = new TextButton("Verdict", game.skin);
		this.btnSend = new TextButton("Envoyer", game.skin);
		this.btnSend.setProgrammaticChangeEvents(true);
		this.btnEvidence = new TextButton("Preuves", game.skin);
		this.btnMessageHistory = new TextButton("Historique", game.skin);
		this.text = new TextArea("", game.skin);
		this.text.setMaxLength(100);
		this.time = new Timer();
		this.dialogHistorique = new History(game.skin);
		this.gagne = true;
		
		this.dialogHistorique.buttonClose(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.input.setInputProcessor(stage);
			};
		});
		
		stage.addListener(new InputListener() {
		    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		        if (!(event.getTarget() instanceof TextArea)) stage.setKeyboardFocus(null);
		        return false;
		    }
		    
		    @Override
		    public boolean keyUp(InputEvent event, int keycode) {
		    	// TODO Auto-generated method stub
		    	if(!(event.getTarget() instanceof TextArea) && keycode == Input.Keys.ENTER) {
		    		btnSend.toggle();
		    	}else if((event.getTarget() instanceof TextArea) && keycode == Input.Keys.ENTER) {
		    		text.setText(text.getText().substring(0, text.getText().length()-1));
		    		btnSend.toggle();
		    	}
		    	return false;
		    }
		});

		Table table = new Table();
		table.setDebug(Constants.SCREEN_DEBUG);
		table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		setTable(table);

		// this.stage.addActor(background);
		this.stage.addActor(table);
	}

	private void setTable(Table table) {
		table.add(this.background).colspan(6).padTop(10).center().row();

		table.add(this.messageArea).colspan(6).padTop(10).center().row();
		HorizontalGroup group = new HorizontalGroup();
		VerticalGroup vg = new VerticalGroup();
		HorizontalGroup hg = new HorizontalGroup();
		btnVerdict.padRight(20);

		group.addActor(btnVerdict);

		table.add(group).left().padBottom(10).padTop(20);
		table.row();
		table.add(text).left().width(1000).height(200).padBottom(10);
		vg.addActor(btnSend);
		hg.addActor(btnEvidence);
		hg.addActor(btnMessageHistory);
		vg.addActor(hg);
		table.add(vg);
	}

	public void control() {
		this.btnSend.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("Texte : " + text.getText());
				String s = text.getText();
				text.setText("");
				game.client.send(new Message(game.client.getPseudo(), s, null, game.client.getRole(),
						MessageType.CHAT, SubMessageType.NORMAL));
			}
		});
		this.btnVerdict.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Dialog dia = new Dialog("Verdict", game.skin) {
					@Override
					protected void result(Object object) {
						// TODO Auto-generated method stub
						gagne = (Boolean) object;
						String result = "" + gagne;
						game.client.send(new Message(game.client.getPseudo(), result, null, game.client.getRole(),
								MessageType.CHAT, SubMessageType.VERDICT));
					}
				};
				dia.text("l'accuse est ?");
				dia.button("Coupable", true);
				dia.button("Innocent", false);
				dia.setPosition(500, 100);
				dia.setWidth(300);
				dia.setMovable(false);
				stage.addActor(dia);
			}
		});
		
		this.btnMessageHistory.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.input.setInputProcessor(stageDialog);
				dialogHistorique.show(stageDialog);
			}
		});
	}

	@Override
	public void show() {
		System.out.println("Screen : " + LobbyScreen.class.getSimpleName());
		Gdx.input.setInputProcessor(this.stage);
	}

	@Override
	public void render(float delta) {
		displayHolder.elapsedTime += Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(207 / 255f, 216 / 255f, 220 / 255f, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.stage.act(delta);
		this.stage.draw();
		this.batch.begin();
		this.batch.draw(displayHolder.animation.getKeyFrame(displayHolder.elapsedTime, true), displayHolder.x,
				displayHolder.y, 750, 580);
		this.batch.end();
		this.stageDialog.act(delta);
		this.stageDialog.draw();
	}

	public void listenerControl() {

		btnSend.setTouchable(Touchable.disabled);

		time.scheduleTask(new Task() {
			public void run() {
				btnSend.setTouchable(Touchable.enabled);
			}
		}, (float) (0.1 * (game.currentCase.getRecap().length()) + 1));
		time.start();

		messageArea.changeText(game.currentCase.getRecap(), 0);

		control();
		this.game.client.addMessageListener(new MessageListener() {
			@Override
			public void messageReceived(Message message) {
				System.out.println(message.getContent());
				if (message.getmType() == MessageType.CHAT) {
					if (message.getsType() == SubMessageType.NORMAL) {
						displayHolder.changeAnimation(message.getRole());
						messageArea.changeText(message.getPseudo() + " : " + message.getContent(), 0);
						dialogHistorique.setNewMessage(message.getPseudo() + " :\n" + message.getContent());
					} else if (message.getsType() == SubMessageType.OBJECTION) {
						displayHolder.changeAnimation(Role.OBJECTION);
						displayHolder.changeToWhenFinished(message.getRole());
						messageArea.changeText(message.getPseudo() + " : " + message.getContent(), 1);
						dialogHistorique.setNewMessage(message.getPseudo() + " :\n" + message.getContent());
					} else if (message.getsType() == SubMessageType.VERDICT) {
						if (message.getContent().equals("true")) {
							// Procureur gagne
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									game.endScreen.setEndText("Le procureur gagne", "Le client a ete emprisonne");
									game.setScreen(game.endScreen);
								}
							});

						} else if (message.getContent().equals("false")) {
							// DA gagne
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									game.endScreen.setEndText("L'avocat de la defense gagne",
											"Le client a ete innocente");
									game.setScreen(game.endScreen);
								}
							});
						}
					}
				}
				if (message.getsType() == SubMessageType.SOMEONE_IS_QUITTING) {
					Gdx.app.exit();
				}
			}
		});
	}

	@Override
	public void resize(int width, int height) {
		System.out.println(width + ":" + height);
		stage.getViewport().update(width, height);
	}

	public void waitTimer() {

	}

	@Override
	public void pause() {
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
