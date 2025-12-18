package Server;

import Engine.ExecutionEngine;
import Parser.QueryParser;
import Shared.*;
import java.io.*;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;
    private final ExecutionEngine engine;

    public ClientHandler(Socket socket, ExecutionEngine engine) {
        this.socket = socket;
        this.engine = engine;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            while (true) {
                Request request = (Request) in.readObject();
                if (request.getType() == MessageType.EXIT) {
                    QueryParser.parseAndExecute("db.s.save()", engine);
                    out.writeObject(new Response(true, "Goodbye!"));
                    break;
                }
                String result = QueryParser.parseAndExecute(request.getQuery(), engine);
                out.writeObject(new Response(true, result));


            }

            socket.close();
        } catch (Exception e) {
            System.err.println("❌ Client disconnected");
        }
    }
}