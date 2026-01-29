package Client.clients;

import Client.UniDBClient;

import java.io.Console;
import java.io.IOException;

public class client2 {
    UniDBClient client;
    public client2(UniDBClient client) {
        this.client = client;
    }
    public static void main(String[] args) throws Exception {
        int port = 5000;
        UniDBClient client = new UniDBClient();
        client2 c2 = new client2(client);
        c2.client.connect("localhost", port);
        c2.client.start();
    }
}
