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

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("🚀 UniDB Server running on port " + PORT);

        while (true) {
            Socket client = serverSocket.accept();
            System.out.println("✅ Client connected:  " + client.getInetAddress());
            new Thread(new ClientHandler(client, engine)).start();
        }
    }

    public static void main(String[] args) throws IOException {
        new UniDBServer().start();
    }
}