package edu.isistan.server.message;

import java.io.DataOutputStream;
import java.io.IOException;

public class ByteMessage extends Message {
	private byte message;
	
	public ByteMessage(byte message) {
		this.message = message;
	}

	@Override
	public void sendMessage(DataOutputStream dos) {
		try {
			dos.writeByte(this.message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
