import chatroom.server.UdpServer;

import java.net.SocketException;

public class UdpServerRunner {
    public static void main(String[] args) throws SocketException {
        UdpServer udpServer = new UdpServer();
        udpServer.run();
    }
}
