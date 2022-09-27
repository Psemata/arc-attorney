package ch.hearc.arcattorney.player.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerThread extends Thread {
	private Socket socket = null;
	private Player client = null;
	private BufferedReader input = null;
	private AtomicBoolean communicationBoolean;

	// Thread which will listen data from the server and will redirect to the client
	public PlayerThread(Socket socket, Player client) {
		this.communicationBoolean = new AtomicBoolean(true);
		this.socket = socket;
		this.client = client;
		open();
		start();
	}

	// Open the input (data from the server)
	public void open() {
		try {
			this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException ioe) {
			System.out.println("Erreur lors de l'ouverture de l'input " + ioe.getMessage());
			client.stop();
		}
	}

	// Close the input
	public void close() {
		try {
			if (this.input != null) {
				this.input.close();
				this.communicationBoolean.set(false);
			}
		} catch (IOException ioe) {
			System.out.println("Erreur lors de la fermeture de l'input " + ioe.getMessage());
		}
	}

	@Override
	public void run() {
		while (this.communicationBoolean.get()) {
			try {
				// Give to the client the data listened from the server
				client.handle(this.input.readLine());
			} catch (IOException ioe) {
				System.out.println("Erreur d'écoute : " + ioe.getMessage());
				client.stop();
			}
		}
	}
}
