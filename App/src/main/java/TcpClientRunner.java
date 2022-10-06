import chatroom.client.TcpClient;

import java.io.IOException;

public class TcpClientRunner {
    public static void main(String[] args) throws IOException, InterruptedException {
        TcpClient client = new TcpClient();
        client.run();
    }
}
