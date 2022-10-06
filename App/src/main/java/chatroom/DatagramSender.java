package chatroom;

import java.io.*;
import java.net.DatagramPacket;
import java.util.Optional;

public interface DatagramSender {
    default <T> Optional<DatagramPacket> constructDatagramPacketFromMessage(T message){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)){
            objectOutputStream.writeObject(message);
            byte[] data = byteArrayOutputStream.toByteArray();
            return Optional.of(new DatagramPacket(data, data.length));
        }catch (IOException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    default <T> Optional<T> constructMessageFromDatagramPacket(DatagramPacket packet){
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());
        try(ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)){
            T msg = (T) objectInputStream.readObject();
            return Optional.of(msg);
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
