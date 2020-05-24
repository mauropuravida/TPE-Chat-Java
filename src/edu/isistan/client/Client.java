package edu.isistan.client;

import edu.isistan.chat.ChatGUI;
import edu.isistan.chat.gui.MainWindows;
import edu.isistan.common.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket s = new Socket(args[0], 6663);
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            ChatGUI gui = MainWindows.launchOrGet(new Callback(dos),args[1]);
            new Thread(()-> {
                try {
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    while (true) {
                        byte type = dis.readByte();
                        switch (type) {
                            case (Protocol.ADD_USER):
                                String user = dis.readUTF();
                                gui.addUser(user);
                                break;
                            case (Protocol.REMOVE_USER):
                                user = dis.readUTF();
                                gui.removeUser(user);
                                break;

                            case (Protocol.GENERAL_MSG):
                                user = dis.readUTF();
                                String text = dis.readUTF();
                                gui.addNewGeneralMsg(user, text);
                                break;
                                
                            case (Protocol.PRIVATE_MSG):
                            	String userFrom = dis.readUTF();
                            	String userTo = dis.readUTF();
                                String textP = dis.readUTF();
                                gui.addNewMsg(userFrom, userTo, textP);
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }).start();
            dos.writeByte(Protocol.HANDSHAKE);
            dos.writeUTF(args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
