package Client;

import Main.ConsoleUI;
import Shared.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class UniDBClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public UniDBClient() {
    }
    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket. getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("✅ Connected to server!");
    }

    public void start() throws Exception {
        ConsoleUI ui = new ConsoleUI();
        ConsoleUI.init();
        ui.printBanner("UniDB Client (Network Mode)");
        ui.printlnInfo("Type 'exit' to quit.");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            ui.prompt("UniDB>");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                out.writeObject(new Request(MessageType.EXIT, input));
                Response response = (Response) in.readObject();
                ui.printBanner(response.getMessage());
                break;
            }

            out.writeObject(new Request(MessageType.QUERY, input));
            Response response = (Response) in.readObject();
            System.out.println(response.getMessage());
        }

        socket.close();
        ui.close();
    }

    public static void main(String[] args) throws Exception {
        UniDBClient client = new UniDBClient();
        client.connect("localhost", 5000);
        client.start();
    }
}