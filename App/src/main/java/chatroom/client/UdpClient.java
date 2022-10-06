package chatroom.client;

import chatroom.*;

import javax.crypto.KeyAgreement;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class UdpClient extends Node implements DatagramSender {
    private int port;
    private byte[] buffer = new byte[1024];
    private byte[] sharedSecret;
    private InetAddress serverIpAddress;
    private int serverPort;
    public void run(){
        String messageStr;
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter server port: ");
        serverPort = ConsoleUtils.getCorrectInt(val -> val >= 1024 && val <= 49151, "Port numbers must be between 1024 and 49151");
        System.out.print("Enter your username: ");
        String username = sc.nextLine();


        try(DatagramSocket socket = new DatagramSocket()){
            boolean[] finished = {false};
            // serverIpAddress = InetAddress.getLocalHost();
            System.out.print("Enter server ip: ");
            serverIpAddress = InetAddress.getByName(sc.nextLine());
            Thread getMessagesThread = new Thread(() ->{
                DatagramPacket received = new DatagramPacket(buffer, buffer.length);
                while(!finished[0]) {
                    try {
                        socket.receive(received);
                    } catch (IOException e) {
                        System.out.println("Connection ended");
                    }
                    Optional<List<Message>> optionalMessage = constructMessageFromDatagramPacket(received);
                    List<Message> receivedMsg = optionalMessage.get();
                    receivedMsg = receivedMsg.stream().map(message -> decryptMessage(message, sharedSecret)).collect(Collectors.toList());
                    System.out.print(ANSI.RESET_CONSOLE.getValue());
                    System.out.flush();

                    System.out.println("\n" + ANSI.BOLD.getValue() + username + ANSI.RESET_FORMAT.getValue());
                    receivedMsg.forEach(message -> System.out.println(message.formatted()));
                    System.out.print("Your message: ");
                }
            });
            exchangeKeys(socket);
            getMessagesThread.start();


            while(!finished[0]) {
                System.out.print("Your message: ");
                messageStr = sc.nextLine();
                if(messageStr.equals(".exit")){
                    finished[0] = true;
                    break;
                }

                Message msg = new Message(1, messageStr, username, LocalDateTime.now());
                Message encryptedMessage = encryptMessage(msg, sharedSecret);
                Optional<DatagramPacket> packetOptional = constructDatagramPacketFromMessage(encryptedMessage);
                if (packetOptional.isEmpty()) {
                    System.out.println("Improper message format");
                }
                DatagramPacket packet = packetOptional.get();
                packet.setAddress(serverIpAddress);
                packet.setPort(serverPort);
                socket.send(packet);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void exchangeKeys(DatagramSocket socket) throws IOException {
        KeyAgreement keyAgreement = this.makeKeyExchangeParams();
        PublicKey clientPublicKey = this.getPublicKey();
        DatagramPacket keyPacket = constructDatagramPacketFromMessage(clientPublicKey).get();
        keyPacket.setPort(serverPort);
        keyPacket.setAddress(serverIpAddress);
        socket.send(keyPacket);
        socket.receive(keyPacket);
        Optional<PublicKey> serverKeyOptional = constructMessageFromDatagramPacket(keyPacket);
        if(serverKeyOptional.isEmpty()){
            System.out.println("Malformed Key");
            //socket.close();
            return;
        }
        PublicKey serverKey = serverKeyOptional.get();
        sharedSecret = getSharedSecret(serverKey, keyAgreement);
    }
}
