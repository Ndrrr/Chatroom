import chat.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int port = 9876;
    public static void main(String[] args) throws IOException, ClassNotFoundException{
        //create the socket server object
        //static ServerSocket variable
        ServerSocket server = new ServerSocket(port);
        //keep listens indefinitely until receives 'exit' call or program terminates
        while(true){
            System.out.println("Waiting for the client request");
            //creating socket and waiting for client connection
            Socket socket = server.accept();
            //read from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to String
            Message message = (Message) ois.readObject();
            System.out.println(message);
            //create ObjectOutputStream object
            // ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //write object to Socket
//          // oos.writeObject("Hi Client "+message);
            //close resources
            ois.close();
            //oos.close();
            socket.close();
            //terminate the server if client sends exit request
            if(message.getMessage().equalsIgnoreCase("exit")) break;
        }
        System.out.println("Shutting down Socket server!!");
        //close the ServerSocket object
        server.close();
    }


}
