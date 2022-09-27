package ch.hearc.arcattorney.player.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;

import ch.hearc.arcattorney.player.Role;
import ch.hearc.arcattorney.player.network.listener.MessageListener;
import ch.hearc.arcattorney.player.network.message.Message;
import ch.hearc.arcattorney.player.network.message.MessageType;
import ch.hearc.arcattorney.player.network.message.SubMessageType;

public class Player {
	// Communication and socket
	private Socket socket = null;
	private PlayerThread clientThread = null;

	// Output
	private PrintWriter output = null;

	// Attributes
	private String pseudo;
	private Role role;

	// Listeners
	private List<MessageListener> listMessageListener;

	public Player(String serverAddress, int serverPort, String pseudo, Role role) throws IOException, UnknownHostException {
		// Attributes
		this.pseudo = pseudo;
		this.role = role;

		// Listeners
		this.listMessageListener = new ArrayList<MessageListener>();

		// Communication
		System.out.println("Établissement de la connexion, veuillez patienter ...");
		try {
			// Connect to the server
			this.socket = new Socket(serverAddress, serverPort);
			System.out.println("Connecté : " + socket);
			// Open the Output to the server and create the listening server
			open();
		} catch (UnknownHostException uhe) {
			System.out.println("Hôte inconnu ou inatteignable : " + uhe.getMessage());
			throw(uhe);
		} catch (IOException ioe) {
			System.out.println("Problème de connexion : " + ioe.getMessage());
			throw(ioe);
		}
	}

	public void open() throws IOException {
		// Initialize the clientThread - which listen data from the server
		this.clientThread = new PlayerThread(socket, this);
		// Initialize the input output of the client
		this.output = new PrintWriter(this.socket.getOutputStream(), true);
	}

	// Stop and kill the threads, the sockets and all the inputs/outputs
	public void stop() {
		try {
			if (this.output != null) {
				this.output.close();
			}
			if (this.socket != null) {
				this.socket.close();
			}
		} catch (IOException ioe) {
			System.out.println("Erreur à la fermeture : " + ioe.getMessage());
		}
		if (this.clientThread != null) {
			this.clientThread.close();
		}
	}

	// Receive the data from the server
	public void handle(String msg) {
		// Deserialize the data received
		Gson gson = new Gson();
		Message data = gson.fromJson(msg, Message.class);

		if (data.getsType() == SubMessageType.QUITTING) {
			System.out.println("Déconnexion du client : " + this.pseudo);
			stop();
		} else {
			// Send the data to all the "listeners"
			triggerUiChanges(data);
		}
	}

	public void send(Message message) {
		Gson gson = new Gson();
		String data = gson.toJson(message);
		// Send the data to the server
		output.println(data);
	}

	public String getPseudo() {
		return this.pseudo;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	// Add a listener to the subscribed list
	public void addMessageListener(MessageListener msgListener) {
		this.listMessageListener.add(msgListener);
	}

	// Removed a listener from the subscribed list
	public void removeMessageListener(MessageListener msgListener) {
		this.listMessageListener.remove(msgListener);
	}

	// When a message is received, the ui changes accordingly
	private synchronized void triggerUiChanges(Message data) {
		for (MessageListener msgListener : this.listMessageListener) {
			msgListener.messageReceived(data);
		}
	}
}
