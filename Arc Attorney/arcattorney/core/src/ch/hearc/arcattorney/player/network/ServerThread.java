package ch.hearc.arcattorney.player.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerThread extends Thread {

	// Attributes
	private Server mainServer = null;
	private Socket socket = null;
	private int id;
	private AtomicBoolean communicationBoolean;

	// Input and output
	private BufferedReader input = null;
	private PrintWriter output = null;

	/**
	 * A ServerThread is a thread which will help the client to communicate with the
	 * server
	 */
	public ServerThread(Socket socket, Server mainServer, int id) {
		this.socket = socket;
		this.mainServer = mainServer;
		this.id = id;
		this.communicationBoolean = new AtomicBoolean(true);
	}

	// Send data to the client(s)
	public void send(String msg) {
		try {
			output.println(msg);
		} catch (Exception ioe) {
			System.out.println(this.id + " n'arrive pas à envoyer de message " + ioe.getMessage());
			mainServer.remove(this.id);
		}
	}

	// Return the client id
	public int getClientID() {
		return this.id;
	}

	public void setClientID(int id) {
		this.id = id;
	}

	@Override
	public void run() {
		System.out.println("Thread server " + this.id + " est lancé");
		while (this.communicationBoolean.get()) {
			try {
				// Listen data from the client and give it to the server so it can broadcast it
				mainServer.handle(this.id, input.readLine());
			} catch (IOException ioe) {
				System.out.println(this.id + " n'arrive pas à lire le message " + ioe.getMessage());
				mainServer.remove(this.id);
				stopThread();
			}
		}
	}

	// Open the inputs/outputs
	public void open() throws IOException {
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.output = new PrintWriter(socket.getOutputStream(), true);
	}

	public void stopThread() {
		this.communicationBoolean.set(false);
	}

	public void close() throws IOException {
		if (this.socket != null) {
			this.socket.close();
		}
		if (this.input != null) {
			this.input.close();
		}
		if (this.output != null) {
			this.output.close();
		}
		stopThread();
	}
}
