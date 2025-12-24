package Client;

import Main.ConsoleUI;
import Shared.*;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class UniDBClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean Authorize;

    public UniDBClient() {
    }

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("✅ Connected to server!");
    }

    public void start() throws Exception {
        ConsoleUI ui = new ConsoleUI();
        ConsoleUI.init();
        System.out.print(ui.printBanner("UniDB Client (Network Mode)"));
        System.out.println(ui.printlnInfo("Type 'exit' to quit."));
        Scanner scanner = new Scanner(System.in);
        Response response2 = (Response) in.readObject();
        if (response2.isSuccess() && Objects.equals(response2.getMessage(), "Accepted")) {
            Authorize = true;
        }
        while (true) {
            while (!Authorize) {
                System.out.println(ui.prompt("Enter the Password : "));
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) {
                    out.writeObject(new Request(MessageType.EXIT, input));
                    Response response = (Response) in.readObject();
                    System.out.println(ui.printBanner(response.getMessage()));
                    break;
                }
                out.writeObject(new Request(MessageType.AUTH, input));
                Response response = (Response) in.readObject();
                if (response.isSuccess() && Objects.equals(response.getMessage(), "Accepted")) {
                    System.out.println(ui.printlnSuccess("Correct Password you now logged in !"));
                    Authorize = true;
                    break;
                } else {
                    System.out.println(ui.printlnError("Wrong Passwrod"));
                }
            }
            if (Authorize) {
                System.out.print(ui.prompt("UniDB>"));
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) {
                    out.writeObject(new Request(MessageType.EXIT, input));
                    Response response = (Response) in.readObject();
                    System.out.println(ui.printBanner(response.getMessage()));
                    break;
                }
                out.writeObject(new Request(MessageType.QUERY, input));
                Response response = (Response) in.readObject();
                System.out.println(response.getMessage());
            } else {
                break;
            }
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