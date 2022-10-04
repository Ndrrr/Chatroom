package chatroom.server;

import chatroom.ANSI;
import chatroom.Message;
import chatroom.Node;

import javax.crypto.KeyAgreement;
import java.io.*;
import java.net.ServerSocket;
import java.security.PublicKey;
import java.util.*;

public class TcpServer extends Node {
    private int colorIndex = 0;
    private final int port = 9876;
    private final List<Message> messageList = new ArrayList<>();
    private volatile static int messageCounter = 0;
    private Set<SocketWrapper> sockets = new HashSet<>();
    public void run() throws IOException{

        ServerSocket server = new ServerSocket(port);
        System.out.println("server.Server is listening on port " + port);

        while (true) {
            try {
                SocketWrapper socket = new SocketWrapper(server.accept());
                sockets.add(socket);

                System.out.println("New client connected");
                // creating new thread for each client
                Thread clientThread = new Thread(()->{
                    try {
                        ObjectOutputStream oos = socket.getOos();
                        ObjectInputStream ois = socket.getOis();

                        // creating shared secret for client
                        KeyAgreement keyAgreement = this.makeKeyExchangeParams();
                        oos.writeObject(this.getPublicKey());
                        socket.setSharedSecret(this.getSharedSecret((PublicKey)ois.readObject(), keyAgreement));

                        handleSocket(socket, colorIndex++);
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
                clientThread.start();

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }


        System.out.println("Shutting down Socket server!!");
        server.close();
    }

    private void handleSocket(SocketWrapper socket, int colorIndex) throws IOException, ClassNotFoundException {
        while (socket.getSocket().isConnected()){
            ObjectInputStream ois = socket.getOis();

            try {
                Message message = this.decryptMessage((Message) ois.readObject(), socket.getSharedSecret());
                message.setColor(colorIndex);
                message.setId(messageCounter++);
                if (message.getMessage().equalsIgnoreCase(".exit")) {
                    message.setMessage(ANSI.BOLD.getValue() +"LEFT CHAT" + ANSI.RESET_FORMAT.getValue());
                    socket.closeSocket();
                }
                messageList.add(message);
                System.out.println(message.formatted());
                sendMessages();
            }catch(Exception e){
                // e.printStackTrace();
                System.out.println("Connection lost with one client");
                socket.closeSocket();
                break;
            }

        }


    }
    private void sendMessages(){
        // remove closed sockets
        sockets.removeIf(socketWrapper -> socketWrapper.getSocket().isClosed());

        sockets.forEach((socketWrapper) ->{
        ObjectOutputStream oos = socketWrapper.getOos();
        try {
            messageList.forEach((message -> {
                try {
                    // socket may be deleted in other thread until getting here from previous check
                    // so checking again
                    if(socketWrapper.getSocket().isConnected()) {
                        oos.writeObject(this.encryptMessage(message, socketWrapper.getSharedSecret()));
                    }
                } catch (IOException e) {
                    socketWrapper.closeSocket();
                    e.printStackTrace();
                }
            }));
            if(socketWrapper.getSocket().isConnected()) {
                oos.writeObject(this.encryptMessage(Message.END, socketWrapper.getSharedSecret()));
            }
        } catch (IOException e) {
            socketWrapper.closeSocket();
            e.printStackTrace();
        }
    });
    }


}
