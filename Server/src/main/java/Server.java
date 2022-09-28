import chat.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int port = 9876;
    public static void main(String[] args) throws IOException, ClassNotFoundException{
        //create the socket server object
        //static ServerSocket variable
        ServerSocket server = new ServerSocket(port);
        //keep listens indefinitely until receives 'exit' call or program terminates
        System.out.println("Server is listening on port " + port);
        List<SocketWrapper> sockets = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        SocketWrapper socket = new SocketWrapper(server.accept());
                        sockets.add(socket);
                        System.out.println("New client connected");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        List<SocketWrapper> socketsForRemove = new ArrayList<>();
        while(true){
            Boolean[] cont = {true};
            sockets.forEach((socket)->{
                if(socket.getSocket().isClosed()) socketsForRemove.add(socket);
                else{
                    try {
                        cont[0] = handleSocket(socket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            sockets.removeAll(socketsForRemove);
            if(!cont[0]) break;
         }
        System.out.println("Shutting down Socket server!!");
        //close the ServerSocket object
        server.close();
    }

    static boolean handleSocket(SocketWrapper socket) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = socket.getOis();
        Message message = (Message) ois.readObject();
        System.out.println(message);

        if(message.getMessage().equalsIgnoreCase("exit")) socket.getSocket().close();
        if(message.getMessage().equalsIgnoreCase("terminate")) return false;
        return true;
    }


}
