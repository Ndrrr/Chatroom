import chatroom.server.TcpServer;

import java.io.IOException;

public class ServerRunner {
    public static void main(String[] args) throws IOException {
        TcpServer server = new TcpServer();
        server.run();
    }
}
