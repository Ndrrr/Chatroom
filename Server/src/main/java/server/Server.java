package server;

import chat.Message;
import chat.Security;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server {
    private int colorIndex = 0;
    private final int port = 9876;
    private final List<Message> messageList = new ArrayList<>();
    private volatile static int messageCounter = 0;

    private Set<SocketWrapper> sockets = new HashSet<>();
    public void run() throws IOException{
        System.out.println(Security.secretKey);
        ServerSocket server = new ServerSocket(port);
        System.out.println("server.Server is listening on port " + port);

        while (true) {
            try {
                SocketWrapper socket = new SocketWrapper(server.accept());
                sockets.add(socket);
                System.out.println("New client connected");
                Thread clientThread = new Thread(()->{
                    try {
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
                Message message = (Message) ois.readObject();
                message.setColor(colorIndex);
                if (message.getMessage().equalsIgnoreCase(".exit")) {
                    socket.getSocket().close();
                    break;
                }
                message.setId(messageCounter++);
                messageList.add(message);
                sendMessages();
                System.out.println(message.formatted());
            }catch(Exception e){
                // e.printStackTrace();
                System.out.println("Connection lost with one client");
                socket.closeSocket();
                break;
            }

        }


    }
    private void sendMessages(){
       sockets.removeIf(socketWrapper -> socketWrapper.getSocket().isClosed());
       sockets.forEach((socketWrapper) ->{

        ObjectOutputStream oos = socketWrapper.getOos();
        try {
            messageList.forEach((message -> {
                try {
                    if(socketWrapper.getSocket().isConnected())
                        oos.writeObject(message);
                } catch (IOException e) {
                    socketWrapper.closeSocket();
                    e.printStackTrace();
                }
            }));
            if(socketWrapper.getSocket().isConnected())
                oos.writeObject(Message.END);
        } catch (IOException e) {
            socketWrapper.closeSocket();
            e.printStackTrace();
        }
    });
    }


}
