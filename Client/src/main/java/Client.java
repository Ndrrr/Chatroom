import chat.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        Scanner sc = new Scanner(System.in);
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        String userInput;
        do{
            System.out.print("Enter the message to be sent to the server: ");
            userInput = sc.nextLine();
            Message message = new Message(userInput);
            message.setSender("Client");
            message.setDateTime(LocalDateTime.now());
            //establish socket connection to server
            socket = new Socket(host.getHostName(), 9876);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sending request to Socket Server");
            oos.writeObject(message);
            //read the server response message

            //close resources
            oos.close();
            Thread.sleep(100);
        }while(!userInput.equalsIgnoreCase("exit"));
    }
}
