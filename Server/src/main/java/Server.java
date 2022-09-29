import chat.Message;

import java.io.*;
import java.net.ServerSocket;

public class Server {
    public static final String[] colors = {"\u001B[31m", "\u001B[32m","\u001B[33m","\u001B[34m","\u001B[35m","\u001B[36m"};
    private static int colorIndex = 0;
    private static final int port = 9876;


    public static void main(String[] args) throws IOException{

        ServerSocket server = new ServerSocket(port);
        System.out.println("Server is listening on port " + port);

        while (true) {
            try {
                SocketWrapper socket = new SocketWrapper(server.accept(), colors[colorIndex++%colors.length]);
                System.out.println("New client connected");
                Thread clientThread = new Thread(()->{
                    try {
                        handleSocket(socket);
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

    static boolean handleSocket(SocketWrapper socket) throws IOException, ClassNotFoundException {
        while (true){
            ObjectInputStream ois = socket.getOis();
            Message message = (Message) ois.readObject();
            System.out.println(socket.getColor() + message.formatted());

            if (message.getMessage().equalsIgnoreCase("exit")) socket.getSocket().close();
        }

    }


}
