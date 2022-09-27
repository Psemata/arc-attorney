package ch.hearc.arcattorney.player.network.listener;

import ch.hearc.arcattorney.player.network.message.Message;

public interface MessageListener {
	public void messageReceived(Message message);
}
