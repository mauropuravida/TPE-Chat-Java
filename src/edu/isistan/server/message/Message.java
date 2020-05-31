package edu.isistan.server.message;

import java.io.DataOutputStream;

public abstract class Message {
	public abstract void sendMessage(DataOutputStream dos);
}
