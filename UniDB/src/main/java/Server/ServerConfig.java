package Server;

import Main.ConsoleUI;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class ServerConfig {
    public static final int SOCKET_TIMEOUT_MS = 300000;
    static String Pass = null;
    public static ServerSocket createServerSocket(int port, String pass) throws Exception {
        Pass = pass;
        ConsoleUI ui = new ConsoleUI();
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
        InetAddress bound = serverSocket.getInetAddress();
        System.out.println(ui.printlnInfo("Server listening on: " + (bound.isAnyLocalAddress() ? "0.0.0.0" : bound.getHostAddress()) + ":" + serverSocket.getLocalPort()));
        return serverSocket;
    }
    public static ServerSocket createServerSocket(int port) throws Exception {
        ConsoleUI ui = new ConsoleUI();
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
        InetAddress bound = serverSocket.getInetAddress();
        System.out.println(ui.printlnInfo("Server listening on: " + (bound.isAnyLocalAddress() ? "0.0.0.0" : bound.getHostAddress()) + ":" + serverSocket.getLocalPort()));
        return serverSocket;
    }
}
