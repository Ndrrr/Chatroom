package chatroom.client;

import chatroom.ANSI;
import chatroom.Message;
import chatroom.Node;

import javax.crypto.KeyAgreement;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.*;

public class Client extends Node {

    public void run() throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = sc.nextLine();

        // initializing server data
        //  System.out.print("Enter server ip: ");
        //  String ip = sc.nextLine();

        InetAddress host = InetAddress.getLocalHost();
        Socket socket = new Socket(host.getHostName(), 9876);


        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        byte[] sharedSecret = null;
        try {
            // creating shared secret for connection
            KeyAgreement keyAgreement = this.makeKeyExchangeParams();
            sharedSecret = this.getSharedSecret((PublicKey) ois.readObject(), keyAgreement);

            oos.writeObject(this.getPublicKey());
        }catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }

        // create thread for getting message from server
        Thread messageThread = getMessageThread(username, socket, oos, ois, sharedSecret);
        messageThread.start();

        String userInput;
        System.out.print("Enter your message: ");
        do{
            // get user input from scanner
            userInput = sc.nextLine();
            // construct message
            Message message = new Message();
            message.setMessage(userInput);
            message.setSender(username);
            message.setDateTime(LocalDateTime.now());

            try {
                oos.writeObject(encryptMessage(message, sharedSecret));
            }catch(Exception e){
                e.printStackTrace();
            }
            Thread.sleep(100);
        }while(!userInput.equalsIgnoreCase(".exit"));
        socket.close();
    }

    private Thread getMessageThread(String username, Socket socket, ObjectOutputStream oos, ObjectInputStream ois, byte[] sharedSecret){
        return new Thread(()->{
            while(!socket.isClosed()) {
                try {
                    Message msg;
                    List <Message> messages = new ArrayList<>();
                    // get and decrypt messages from server
                    while(!(msg =decryptMessage((Message) ois.readObject(), sharedSecret)).equals(Message.END)){
                        messages.add(msg);
                    }

                    // reset console view (does not work in some consoles)
                    System.out.print(ANSI.RESET_CONSOLE.getValue());
                    System.out.flush();

                    System.out.println("\n" + ANSI.BOLD.getValue() + username + ANSI.RESET_FORMAT.getValue());
                    messages.forEach(message -> System.out.println(message.formatted()));
                    System.out.print("Enter your message: ");
                } catch (IOException | ClassNotFoundException e) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        System.out.println("Cannot close socket");
                    }
                    // e.printStackTrace();
                    System.out.println("Connection lost");
                }
            }
        });
    }
}
