package ch.hearc.arcattorney.player.network.message;

import ch.hearc.arcattorney.player.Role;

public class Message {

	// Attributes
	private String pseudo;
	private String content;
	private String proof; // TODO : Changer le type de String à la classe voulue

	// Which role the user in the lobby choose
	private Role role;

	// Message type
	private MessageType mType;
	private SubMessageType sType;

	// Position id - for the lobby
	private int positionId;

	public Message(String pseudo, String content, String proof, Role role, MessageType mType, SubMessageType sType) {
		this.pseudo = pseudo;
		this.content = content;
		this.proof = proof;
		this.role = role;
		this.mType = mType;
		this.sType = sType;
	}

	// Constructor with special content add - for the lobby
	public Message(String content, int positionId, Message message) {
		this(message.getPseudo(), content, message.getProof(), message.getRole(), message.getmType(), message.getsType());
		this.positionId = positionId;
	}

	// Getter
	public String getPseudo() {
		return this.pseudo;
	}

	public String getContent() {
		return this.content;
	}

	public String getProof() {
		return this.proof;
	}

	public Role getRole() {
		return this.role;
	}

	public MessageType getmType() {
		return this.mType;
	}

	public SubMessageType getsType() {
		return this.sType;
	}
	
	public void setType(SubMessageType sType) {
		this.sType = sType;
	}

	public int getPositionId() {
		return this.positionId;
	}
}
