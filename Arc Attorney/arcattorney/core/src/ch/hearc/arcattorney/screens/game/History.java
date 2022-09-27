package ch.hearc.arcattorney.screens.game;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Queue;

public class History extends Dialog {

	private final int SIZEQUEUE = 10;

	private final float DIALOG_WIDHT = (float)700;
	private final float DIALOG_HEIGHT = (float)350;
	private final float DIALOG_PADING = (float)20;
	private final float BUTTON_HEIGHT = (float)40;
	private final float BUTTON_WIDHT = (float)200;
	private final float BUTTON_PAD_HEIGHT = (float)15;
	private final float WIDHT_SPLIT_MESSAGE = (float)DIALOG_WIDHT - 100;
	private final float PADING_CELL = (float)10;
	
	private Skin skin;
	private Queue<Label> queueMessage;
	private Table scrollTable;
	private ScrollPane scroller;

	public History(Skin skin) {
		super("Historique des message", skin);
		this.skin = skin;
		//List des label
		this.queueMessage = new Queue<Label>();
		this.scrollTable = new Table();
		this.scroller = new ScrollPane(this.scrollTable);
		setup();
	}

	/**
	 * Configure les paramètes de la dialog
	 */
	private void setup() {
		padLeft(DIALOG_PADING);
		padRight(DIALOG_PADING);
		padBottom(DIALOG_PADING);
		getButtonTable().defaults().height(BUTTON_HEIGHT);
		getTableDebug();

		setModal(false);
		setMovable(true);
		setResizable(false);
	}

	/**
	 * Créée un nouveau label avec le message
	 * 
	 * @param message
	 * @param align
	 */
	public void setNewMessage(String message) {
		Label label = new Label(message, this.skin);
		label.setAlignment(Align.left);
		label.setWrap(true);
		label.setWidth(10);

		addToList(label);
	}

	/**
	 * Ajoute dans la liste le message si la liste est pleine, supprime le dernier
	 * élément et ajoute le nouveau label au début
	 * 
	 * @param newLabel
	 */
	private void addToList(Label newLabel) {
		queueMessage.addLast(newLabel);

		getContentTable().clear();
		for (Label label : queueMessage) {
			this.scrollTable.add(label).width(WIDHT_SPLIT_MESSAGE).padTop(PADING_CELL).padBottom(PADING_CELL);
			this.scrollTable.row();
		}
		//Met la scrollbar tout en bas
		this.scroller.scrollTo(0, 0, 0, 0);
		//Fait prendre toute la place au scrollpane
		getContentTable().setFillParent(true);
		getContentTable().add(this.scroller).fill().expand();
	}

	/**
	 * Détection du bouton close
	 * 
	 * @param listener
	 */
	public void buttonClose(InputListener listener) {
		TextButton button = new TextButton("Close", this.skin);
		button.setSize(BUTTON_WIDHT, 44);
		button.addListener(listener);
		button.padLeft(BUTTON_PAD_HEIGHT);
		button.padRight(BUTTON_PAD_HEIGHT);
		button(button);
	}

	@Override
	public float getPrefWidth() {
		return DIALOG_WIDHT;
	}

	@Override
	public float getPrefHeight() {
		return DIALOG_HEIGHT;
	}
}
