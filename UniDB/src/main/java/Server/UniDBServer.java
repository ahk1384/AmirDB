package Server;

import Engine.ExecutionEngine;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UniDBServer {
    private static final int PORT = 5000;
    private ExecutionEngine engine;

    public UniDBServer() throws IOException {
        engine = new ExecutionEngine();
    }

    public void start() throws Exception {
        ServerSocket serverSocket = new ServerConfig().createServerSocket(5000);
        System.out.println("🚀 UniDB Server running on port " + PORT);

        while (true) {
            Socket client = serverSocket.accept();
            System.out.println("✅ Client connected:  " + client.getInetAddress());
            new Thread(new ClientHandler(client, engine)).start();
        }
    }

    public static void main(String[] args) throws Exception {
        new UniDBServer().start();
    }
}