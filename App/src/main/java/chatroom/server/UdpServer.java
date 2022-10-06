package chatroom.server;

import chatroom.DatagramSender;
import chatroom.Message;
import chatroom.Node;

import javax.crypto.KeyAgreement;
import javax.xml.crypto.Data;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

public class UdpServer extends Node implements DatagramSender {
    private final int port = 6789;
    private final byte[] buffer = new byte[1024];
    private final List<ClientData> clients = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();
    private static int messageCounter = 0;

    public void run() throws SocketException {
        try(DatagramSocket server = new DatagramSocket(port)){
            while(true){
                getMessage(server);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void getMessage(DatagramSocket server) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        server.receive(packet);
        InetAddress address = packet.getAddress();
        int senderPort = packet.getPort();
        ClientData sender = new ClientData(address, senderPort);

        // getting and sending public keys with Diffie hellman
        if(!clients.contains(sender)){
            Optional<PublicKey> publicKeyOptional = constructMessageFromDatagramPacket(packet);
            if(publicKeyOptional.isEmpty()){
                System.out.println("Malformed packet");
                return;
            }
            PublicKey publicKey = publicKeyOptional.get();

            sender.color = messageCounter++;

            KeyAgreement keyAgreement = this.makeKeyExchangeParams();
            sender.sharedSecret = this.getSharedSecret(publicKey, keyAgreement);
            DatagramPacket serverPublicKey = constructDatagramPacketFromMessage(this.getPublicKey()).get();
            serverPublicKey.setPort(senderPort);
            serverPublicKey.setAddress(address);
            server.send(serverPublicKey);
            clients.add(sender);
            return;
        }

        Optional<Message> msg = constructMessageFromDatagramPacket(packet);
        if(msg.isEmpty()){
            System.out.println("Malformed message");
            return;
        }
        sender = clients.stream().filter(sender::equals).findAny().get();

        Message message = decryptMessage(msg.get(), sender.sharedSecret);
        message.setColor(sender.color);
        System.out.println(message.formatted());
        messages.add(message);
        deliverMessage(server, sender);
    }

    private void deliverMessage(DatagramSocket server, ClientData sender){

        clients.forEach(client ->{
            List <Message> encryptedMessages = messages.stream().map(message -> encryptMessage(message, client.sharedSecret)).collect(Collectors.toList());

            Optional<DatagramPacket> packetOptional = constructDatagramPacketFromMessage(encryptedMessages);
            if(packetOptional.isEmpty()){
                System.out.println("Malformed packet");
                return;
            }
            DatagramPacket packet = packetOptional.get();

            packet.setPort(client.port);
            packet.setAddress(client.address);
            try {
                server.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private static class ClientData{
        int port;
        InetAddress address;
        int color;
        byte[] sharedSecret;
        ClientData(InetAddress address, int port){
            this.address = address;
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientData that = (ClientData) o;
            return port == that.port && address.equals(that.address);
        }

        @Override
        public int hashCode() {
            return Objects.hash(port, address);
        }

    }
}
