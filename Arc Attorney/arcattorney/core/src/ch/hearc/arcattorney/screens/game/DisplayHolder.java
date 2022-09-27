package ch.hearc.arcattorney.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import ch.hearc.arcattorney.ArcAttorney;
import ch.hearc.arcattorney.player.Role;
import ch.hearc.arcattorney.utils.Constants;

public class DisplayHolder {

	private ArcAttorney game;

	private TextureAtlas textureAtlasDA;
	private TextureAtlas textureAtlasProcureur;
	private TextureAtlas textureAtlasJudge;
	private TextureAtlas textureAtlasObjection;
	public Animation<TextureRegion> animation;
	public float elapsedTime = 0f;
	public int x;
	public int y;
	public Role currentRole;
	public DisplayHolder(ArcAttorney game) {
		this.game = game;
		x = 260;
		y = 120;
		textureAtlasDA = new TextureAtlas(Gdx.files.internal(Constants.DAANIMATION));
		textureAtlasProcureur = new TextureAtlas(Gdx.files.internal(Constants.PROCUREURANIMATION));
		textureAtlasJudge = new TextureAtlas(Gdx.files.internal(Constants.JUDGEANIMATION));
		textureAtlasObjection = new TextureAtlas(Gdx.files.internal(Constants.OBJECTIONANIMATION));
		animation = new Animation<TextureRegion>(1f / 6, textureAtlasJudge.getRegions());
	}

	public void changeAnimation(Role role) {
		// Temporaire car je sais pas c'que je vais récupérer
		if (role == Role.DEFENCE_ATTORNEY) {
			animation = new Animation<TextureRegion>(1f / 6f, textureAtlasDA.getRegions());
			x = 150;
			y = 205;
		} else if (role == Role.PROSECUTOR) {
			animation = new Animation<TextureRegion>(1f / 5f, textureAtlasProcureur.getRegions());
			x = 350;
			y = 205;
		} else if (role == Role.JUDGE) {
			animation = new Animation<TextureRegion>(1f / 6f, textureAtlasJudge.getRegions());
			x = 260;
			y = 120;
		} else if (role == Role.OBJECTION) {
			animation = new Animation<TextureRegion>(1f / 6f, textureAtlasObjection.getRegions());
			x = 260;
			y = 120;
		} else if (role == Role.OBJECTION) {
			animation = new Animation<TextureRegion>(1f / 6f, textureAtlasObjection.getRegions());
			x = 260;
			y = 120;
		}
	}

	public void changeToWhenFinished(Role role) {
		currentRole = role;
		Timer time = new Timer();
		time.scheduleTask(new Task() {
			public void run() {
				changeAnimation(currentRole);
			}
		}, 1);
		time.start();
	}
}
