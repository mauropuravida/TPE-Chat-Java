package edu.isistan.server;

import edu.isistan.common.Protocol;
import edu.isistan.server.message.ByteMessage;
import edu.isistan.server.message.Message;
import edu.isistan.server.message.UTFMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class Client implements Runnable {
	private Socket s;
	private Server server;
	private DataOutputStream dos;
	private String userName;
	private Queue<Message> queueOfMessages = new ConcurrentLinkedQueue<>();
	private Semaphore pendingMessages = new Semaphore(0);

	public Client(Socket s, Server server) {
		this.s = s;
		this.server = server;
	}

	private void dispatch() {
		while (true) {
			try {
				pendingMessages.acquire();
			} catch (InterruptedException e) {
			}
			Message m = queueOfMessages.poll();
			m.sendMessage(dos);
		}
	}

	@Override
	public void run() {
		try {
			DataInputStream dis = new DataInputStream(this.s.getInputStream());
			dos = new DataOutputStream(this.s.getOutputStream());
			byte type = dis.readByte();
			if (type == Protocol.HANDSHAKE) {
				userName = dis.readUTF();
				if (!this.server.addClient(userName, this)) {
					userName = null;
					s.close();
					return;
				}
			}
			new Thread(() -> dispatch()).start();
			while (true) {
				type = dis.readByte();
				switch (type) {
				case (Protocol.GENERAL_MSG):
					String text = dis.readUTF();
					this.server.sendGeneralMsg(userName, text);
					break;

				case (Protocol.PRIVATE_MSG):
					String to = dis.readUTF();
					String textP = dis.readUTF();
					this.server.sendMsg(userName, to, textP);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (userName != null) {
				this.server.removeUser(userName);
			}
		}
	}

	private void addByteMessage(byte message) {
		queueOfMessages.add(new ByteMessage(message));
		pendingMessages.release();
	}

	private void addUtfMessage(String message) {
		queueOfMessages.add(new UTFMessage(message));
		pendingMessages.release();
	}

	public void removeUser(String userName) {
		addByteMessage(Protocol.REMOVE_USER);
		addUtfMessage(userName);
	}

	public void addUser(String userName) {
		addByteMessage(Protocol.ADD_USER);
		addUtfMessage(userName);
	}

	public void sendGeneralMsg(String userName, String text) {
		addByteMessage(Protocol.GENERAL_MSG);
		addUtfMessage(userName);
		addUtfMessage(text);
	}

	public void sendMsg(String userFrom, String userTo, String text) {
		addByteMessage(Protocol.PRIVATE_MSG);
		addUtfMessage(userFrom);
		addUtfMessage(userTo);
		addUtfMessage(text);
	}
}
