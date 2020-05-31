package edu.isistan.server.message;

import java.io.DataOutputStream;
import java.io.IOException;

public class UTFMessage extends Message {
	private String message;
	
	public UTFMessage(String message) {
		this.message = message;
	}

	@Override
	public void sendMessage(DataOutputStream dos) {
		try {
			dos.writeUTF(this.message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
