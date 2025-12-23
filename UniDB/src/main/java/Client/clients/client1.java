package Client.clients;

import Client.UniDBClient;

import java.io.IOException;

public class client1 {
    UniDBClient client;
    public client1(UniDBClient client) {
        this.client = client;
    }
    public static void main(String[] args) throws Exception {
        int port = 5000;
        UniDBClient client = new UniDBClient();
        client1 c1 = new client1(client);
        c1.client.connect("localhost", port);
        c1.client.start();
    }
}
