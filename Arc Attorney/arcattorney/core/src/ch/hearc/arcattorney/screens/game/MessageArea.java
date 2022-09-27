package ch.hearc.arcattorney.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import ch.hearc.arcattorney.utils.Constants;

public class MessageArea extends Label {

	private int compteur = 0;
	private String textToAdd = "";
	private Sound maleBlip;
	private Timer time;

	public MessageArea(CharSequence text, Skin skin) {
		super(text, skin);
		maleBlip = Gdx.audio.newSound(Gdx.files.internal(Constants.MALEBLIP));
		this.time = new Timer();
		// TODO Auto-generated constructor stub
	}
	@Override
	public void setText(CharSequence newText) {
		// TODO Auto-generated method stub
		super.setText(newText);
	}

	public void changeText(CharSequence newText, int delaySeconds) {
		compteur = 0;
		setText("");
		textToAdd = newText.toString();
		time.clear();
		time.scheduleTask(new Task() {
			public void run() {
				maleBlip.play();
				setText(getText() + "" + textToAdd.charAt(compteur));
				compteur++;
			}
		}, delaySeconds, (float) 0.1, textToAdd.length() - 1);
	}

}
