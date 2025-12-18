package Server;

import java.net.ServerSocket;

public class ServerConfig {
    public static final int SERVER_PORT = 5555;
    public static final String SERVER_HOST = "localhost";
    public static final int MAX_CLIENTS = 50;
    public static final int SOCKET_TIMEOUT_MS = 30000;

    public static ServerSocket createServerSocket(int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
        return serverSocket;
    }

}
