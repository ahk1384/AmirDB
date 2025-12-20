package Server;

import Engine.ExecutionEngine;
import Parser.QueryParser;
import Shared.*;
import java.io.*;
import java.net.Socket;

import static Server.ServerConfig.Pass;


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
            if (Pass == null){
                out.writeObject(new Response(true,"Accepted"));
            }else{
                out.writeObject(new Response(false,"NullPassword"));
            }
            while (true) {
                Request request = (Request) in.readObject();
                if (request.getType() == MessageType.AUTH){
                    if (request.getQuery().equals(Pass) || Pass == null){
                        out.writeObject(new Response(true,"Accepted"));
                    }
                    else{
                        out.writeObject(new Response(false,"Wrong Password"));
                    }
                }
                else if (request.getType() == MessageType.EXIT) {
                    QueryParser.parseAndExecute("db.s.save()", engine);
                    out.writeObject(new Response(true, "Goodbye!"));
                    break;
                }else {
                    String result = QueryParser.parseAndExecute(request.getQuery(), engine);
                    out.writeObject(new Response(true, result));
                }

            }

            socket.close();
        } catch (Exception e) {
            System.err.println("❌ Client disconnected");
        }
    }
}