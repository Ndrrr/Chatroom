import chatroom.client.Client;

import java.io.IOException;

public class ClientRunner {
    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
        client.run();
    }
}
