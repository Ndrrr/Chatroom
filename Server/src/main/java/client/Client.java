package client;

import chat.ANSI;
import chat.Message;
import chat.Security;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;

public class Client {

    public void run() throws IOException, InterruptedException {
        System.out.println(Security.secretKey);
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = sc.nextLine();
//        System.out.print("Enter server ip: ");
//        String ip = sc.nextLine();

        InetAddress host = InetAddress.getLocalHost();
        Socket socket = new Socket(host.getHostName(), 9876);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        String userInput;

        Thread getMessageThread = new Thread(()->{
            while(!socket.isClosed()) {
                try {
                    Message msg;
                    List <Message> messages = new ArrayList<>();
                    while(!(msg =(Message) ois.readObject()).equals(Message.END)){
                        messages.add(msg);
                    }
                    System.out.print(ANSI.RESET_CONSOLE.getValue());
                    System.out.flush();
                    System.out.println(ANSI.BOLD.getValue() + username + ANSI.RESET_FORMAT.getValue());
                    messages.forEach(message -> System.out.println(message.formatted()));
                    System.out.print("Enter your message: ");
                } catch (IOException | ClassNotFoundException e) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        System.out.println("Connection lost");
                    }
                    // e.printStackTrace();
                    System.out.println("Connection lost");
                }
            }
        });
        getMessageThread.start();

        System.out.print("Enter your message: ");
        do{
            userInput = sc.nextLine();
            Message message = new Message();
            message.setMessage(userInput);
            message.setSender(username);
            message.setDateTime(LocalDateTime.now());


            try {
                //message.setEncryptedMessage(Security.encryptObject("AES/CBC/PKCS5Padding", message.getMessage(), Security.secretKey, Security.ivParameterSpec));
                oos.writeObject(message);
            }catch(Exception e){
                e.printStackTrace();
            }
            Thread.sleep(100);
        }while(!userInput.equalsIgnoreCase("exit"));
        socket.close();
    }
}
