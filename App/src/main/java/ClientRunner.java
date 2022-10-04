import chatroom.client.TcpClient;

import java.io.IOException;

public class ClientRunner {
    public static void main(String[] args) throws IOException, InterruptedException {
        TcpClient client = new TcpClient();
        client.run();
    }
}
