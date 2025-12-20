package Server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class ServerConfig {
    public static final int SERVER_PORT = 5000;
    public static final String SERVER_HOST = "192.168.133.247"; // Server's IP address
    public static final int MAX_CLIENTS = 50;
    public static final int SOCKET_TIMEOUT_MS = 300000;
    static String Pass = null;
    public static ServerSocket createServerSocket(int port, String pass) throws Exception {
        Pass = pass;
        ServerSocket serverSocket = new ServerSocket(port); // binds to all interfaces
        serverSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
        InetAddress bound = serverSocket.getInetAddress();
        System.out.println("Server listening on: " + (bound.isAnyLocalAddress() ? "0.0.0.0" : bound.getHostAddress()) + ":" + serverSocket.getLocalPort());
        return serverSocket;
    }
    public static ServerSocket createServerSocket(int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port); // binds to all interfaces
        serverSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
        InetAddress bound = serverSocket.getInetAddress();
        System.out.println("Server listening on: " + (bound.isAnyLocalAddress() ? "0.0.0.0" : bound.getHostAddress()) + ":" + serverSocket.getLocalPort());
        return serverSocket;
    }
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = createServerSocket(SERVER_PORT,"@Amir22111");
            while (true) {
                // Accept client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle communication with the client
                // (Example: simple echo server)
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    System.out.println("Received from client: " + clientMessage);
                    out.println("Echo: " + clientMessage);
                }

                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
