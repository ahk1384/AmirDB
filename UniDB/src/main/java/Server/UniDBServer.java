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
            System.out.print(ui.printInfo("do you want to enabel the login with password [Y/N]:"));
            String status = sc.nextLine();
            if (status.toLowerCase().equals("y")){
                System.out.print(ui.prompt("Enter Your Password : "));
                pass = sc.nextLine();
                System.out.println(ui.printlnSuccess("Your Password is set !"));
                serverSocket = new ServerConfig().createServerSocket(PORT,pass);
                break;
            }
            else{
                System.out.println(ui.printlnSuccess("Without Password Set !"));
                serverSocket = new ServerConfig().createServerSocket(PORT);
                break;
            }
        }
        while (true) {
            Socket client = serverSocket.accept();
            System.out.println(ui.printlnSuccess("Client connected: " + client.getInetAddress() + ":" + client.getPort()));
            new Thread(new ClientHandler(client, engine)).start();

        }
    }

    public static void main(String[] args) throws Exception {
        new UniDBServer().start();
    }
}