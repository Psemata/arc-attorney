package ch.hearc.arcattorney.screens.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ch.hearc.arcattorney.ArcAttorney;
import ch.hearc.arcattorney.player.network.message.Message;
import ch.hearc.arcattorney.player.network.message.MessageType;
import ch.hearc.arcattorney.player.network.message.SubMessageType;
import ch.hearc.arcattorney.screens.connection.ConnectionScreen;
import ch.hearc.arcattorney.screens.lobby.LobbyScreen;
import ch.hearc.arcattorney.utils.Constants;

public class EndScreen implements Screen {

	// Main elements
	private ArcAttorney game;
	private Stage stage;

	// Image
	private Image imageLogo;

	// Labels
	private Label labelTitre;
	private Label labelDescription;

	// Buttons
	private TextButton btnQuitter;
	private TextButton btnRecommencer;

	public EndScreen(ArcAttorney game) {
		this.game = game;
		this.stage = new Stage();

		Table table = new Table();
		table.setDebug(Constants.SCREEN_DEBUG);
		table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		;

		this.imageLogo = new Image(new Texture(Gdx.files.internal(Constants.HAMMER)));

		this.labelTitre = new Label("Fin de la partie", game.skin);
		this.labelDescription = new Label("Le juge a rendu son verdict : ", game.skin);

		this.btnQuitter = new TextButton("Quitter", game.skin);
		this.btnRecommencer = new TextButton("Recommencer", game.skin);

		setTable(table);
		this.stage.addActor(table);

		control();
	}

	private void setTable(Table table) {
		table.add(this.imageLogo).colspan(2).padBottom(50).width(100).height(100);
		table.row();
		table.add(this.labelTitre).colspan(2).padBottom(30);
		table.row();
		table.add(this.labelDescription).colspan(2).padBottom(20);
		table.row();
		table.add(this.btnQuitter);
		table.add(this.btnRecommencer);
	}

	private void control() {
		this.btnQuitter.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.killClient();
				game.killServer();
				Gdx.app.exit();
			}
		});

		this.btnRecommencer.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.killClient();
				game.killServer();
				game.restart();
				game.setScreen(game.connectionScreen);
			}
		});
	}

	public void setEndText(String title, String description) {
		this.labelTitre.setText(title);
		this.labelDescription.setText(description);
	}

	@Override
	public void show() {
		System.out.println("Screen : " + EndScreen.class.getSimpleName());
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
		stage.getViewport().update(width, height);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

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
