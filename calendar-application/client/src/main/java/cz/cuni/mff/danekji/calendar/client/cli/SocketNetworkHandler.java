package cz.cuni.mff.danekji.calendar.client.cli;

import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.responses.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketNetworkHandler implements NetworkHandler {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    @Override
    public int connect(String host, int port) throws IOException, ClassNotFoundException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        Object obj = in.readObject();
        if (obj instanceof Integer sessionId) {
            return sessionId;
        } else {
            throw new IOException("Expected session ID but received: " + obj);
        }
    }

    @Override
    public Response sendCommand(Command command) throws IOException, ClassNotFoundException {
        out.writeObject(command);
        out.flush();
        return (Response) in.readObject();
    }

    @Override
    public void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}