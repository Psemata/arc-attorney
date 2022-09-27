package ch.hearc.arcattorney.player.network.message;

public enum SubMessageType {
	// Chat Subtype - Defendant and Prosecutor
	NORMAL, OBJECTION,
	// Chat Subtype - Judge
	VERDICT, REFUSE_OBJECTION, ACCEPT_OBJECTION,
	// Lobby Subtype
	ROLE_CHANGE, READY, NOT_READY, ENTERING_LOBBY, AFFAIR_CHANGE, LAUNCHING_GAME,
	// General Subtype
	QUITTING, SOMEONE_IS_QUITTING
}
