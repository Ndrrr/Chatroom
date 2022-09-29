import chat.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException{
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = sc.nextLine();
//        System.out.print("Enter server ip: ");
//        String ip = sc.nextLine();

        InetAddress host = InetAddress.getLocalHost();
        Socket socket = new Socket(host.getHostName(), 9876);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        String userInput;

        do{
            System.out.print("Enter the message to be sent to the server: ");
            userInput = sc.nextLine();
            Message message = new Message(userInput);
            message.setSender(username);
            message.setDateTime(LocalDateTime.now());


            System.out.println("Sending request to Socket Server");
            oos.writeObject(message);
            Thread.sleep(100);
        }while(!userInput.equalsIgnoreCase("exit"));
    }
}
