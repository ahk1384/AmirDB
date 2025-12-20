package Server;

import Engine.ExecutionEngine;

import java.io.Console;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class UniDBServer {
    private static final int PORT = 5000;
    private ExecutionEngine engine;

    public UniDBServer() throws IOException {
        engine = new ExecutionEngine();
    }

    public void start() throws Exception {
        Scanner sc = new Scanner(System.in);
        ServerSocket serverSocket;
        String pass;
        while (true){
            System.out.print("do you want to enabel the login with password [Y/N]:");
            String status = sc.nextLine();
            if (status.toLowerCase().equals("y")){
                System.out.print("Enter Your Password : ");
                pass = sc.nextLine();
                serverSocket = new ServerConfig().createServerSocket(PORT,pass);
                System.out.println("Your Password is set !");
                break;
            }
            else{
                System.out.println("Without Password Set !");
                serverSocket = new ServerConfig().createServerSocket(PORT);
                break;
            }
        }
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