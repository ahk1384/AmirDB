package Server;

import Engine.ExecutionEngine;
import Main.ConsoleUI;

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
        ConsoleUI ui = new ConsoleUI();
        Scanner sc = new Scanner(System.in);
        ServerSocket serverSocket;
        String pass;
        while (true){
            ui.printInfo("do you want to enabel the login with password [Y/N]:");
            String status = sc.nextLine();
            if (status.toLowerCase().equals("y")){
                ui.prompt("Enter Your Password : ");
                pass = sc.nextLine();
                ui.printlnSuccess("Your Password is set !");
                serverSocket = new ServerConfig().createServerSocket(PORT,pass);
                break;
            }
            else{
                ui.printlnSuccess("Without Password Set !");
                serverSocket = new ServerConfig().createServerSocket(PORT);
                break;
            }
        }
        while (true) {
            Socket client = serverSocket.accept();
            ui.printlnSuccess("Client connected: " + client.getInetAddress());
            new Thread(new ClientHandler(client, engine)).start();
        }
    }

    public static void main(String[] args) throws Exception {
        new UniDBServer().start();
    }
}