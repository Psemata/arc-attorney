package ch.hearc.arcattorney.player.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;

import ch.hearc.arcattorney.player.network.message.Message;
import ch.hearc.arcattorney.player.network.message.MessageType;
import ch.hearc.arcattorney.player.network.message.SubMessageType;

public class Server {
	// Attributes
	private ServerThread clientsTab[] = new ServerThread[3];
	private int clientCount;
	private ServerSocket serverSocket = null;
	private Thread connectionThread = null;

	// Array possessing the lobby informations if someone enters the lobby late
	private Message lobbyData[] = new Message[3];

	// Boolean used to stop threads
	private AtomicBoolean connectionBoolean;

	// Server creation
	public Server(int port) {
		this.clientCount = 0;
		this.connectionBoolean = new AtomicBoolean(true);
		this.lobbyData = new Message[3];
		try {
			System.out.println("Liaison au port " + port + ", veuillez patienter ...");
			// Launch the server to the specified port
			this.serverSocket = new ServerSocket(port);
			System.out.println("Serveur lancé : " + serverSocket);
			// Start the connection thread
			start();
		} catch (IOException ioe) {
			System.out.println("Ne peut pas se lier au port " + port + " : " + ioe.getMessage());
		}
	}

	public void start() {
		if (this.connectionThread == null) {
			this.connectionThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (connectionThread != null && connectionBoolean.get()) {
						try {
							System.out.println("En attente d'un client ... ");
							// Wait for connections
							addThread(serverSocket.accept());
						} catch (IOException ioe) {
							System.out.println("Le serveur n'a pas pu accepter la connexion : " + ioe);
							stop();
						}
					}
				}
			});
			this.connectionThread.start();
		}
	}

	public void stop() {
		if (this.connectionThread != null) {
			this.connectionBoolean.set(false);
			this.connectionThread = null;
			try {
				this.serverSocket.close();
			} catch (IOException e) {
				System.out.println("Un problème est apparu lors de la fermeture du serveur" + e);
			}
		}
	}

	// Broadcast the received data to all the clients
	public synchronized void handle(int id, String input) {
		// Deserializing the data to check infos
		Gson gson = new Gson();
		Message data = gson.fromJson(input, Message.class);
		
		if(data != null) {
			if(data.getsType() == SubMessageType.QUITTING) {
				clientsTab[findClient(id)].send(gson.toJson(data)); // If the client want to disconnect, send the quitting data to him
				remove(id);
				// Set the message data as -> someone is quitting
				data = new Message(null, id, data);
				data.setType(SubMessageType.SOMEONE_IS_QUITTING);
			} else if (data.getmType() == MessageType.LOBBY) {
				// Add informations to the array so the next client to connect can retrieve it
				lobbyData[id] = data;
				if (data.getsType() == SubMessageType.ENTERING_LOBBY) {
					// If the client is entering the lobby, it's collecting the data in the serverto refresh its UI
					data = new Message(gson.toJson(this.lobbyData), id, data);
				} else if(data.getsType() != SubMessageType.AFFAIR_CHANGE) {
					// Send the changes to the other clients
					data = new Message(null, id, data);
				}
			}
					
			// Serialize the data received and send it to the clients	
			for (int i = 0; i < clientCount; i++) {
				clientsTab[i].send(gson.toJson(data));
			}
		}
	}

	// Remove a client and disconnect him/her from the list
	public synchronized void remove(int id) {
		int pos = findClient(id);
		if (pos >= 0) {
			ServerThread terminate = clientsTab[pos];
			System.out.println("Déconnexion du client : " + id);
			if (pos < clientCount - 1) {
				for (int i = pos + 1; i < clientCount; i++) {					
					clientsTab[i - 1] = clientsTab[i];
					lobbyData[i - 1] = lobbyData[i];
				}
				//lobbyData[pos] = null; // Quit the data put into the lobby array
				//clientsTab[pos].setClientID(id); // Set the next client's id		
			}
			clientCount--;
			try {
				terminate.close();
			} catch (IOException ioe) {
				System.out.println("Erreur lors de la fermeture du thread : " + ioe);
				terminate.stopThread();
			}
		}
	}

	// Get the index of the client in the tab
	private int findClient(int id) {
		for (int i = 0; i < clientCount; i++) {
			if (clientsTab[i].getClientID() == id) {
				return i;
			}
		}
		return -1;
	}

	// A client connects to the server and create a thread used for communication
	private void addThread(Socket socket) {
		if (clientCount < clientsTab.length) {
			System.out.println("Client accepté : " + socket);
			clientsTab[clientCount] = new ServerThread(socket, this, clientCount);
			try {
				clientsTab[clientCount].open();
				clientsTab[clientCount].start();
				clientCount++;
			} catch (IOException ioe) {
				System.out.println("Erreur lors de l'ouverture du thread");
			}
		} else {
			System.out.println("Client refusé : taille maximum atteinte - " + clientCount);
		}
	}
}