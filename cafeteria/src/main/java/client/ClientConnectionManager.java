package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import clientservice.ConsoleInputValidator;
import requestresponse.ServerRequestSender;
import requestresponse.ServerResponseReader;

public class ClientConnectionManager {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2709;
    private Socket socket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private BufferedReader consoleReader;
    private ConsoleInputValidator inputValidations;
    private ServerRequestSender serverRequest;
    private ServerResponseReader serverResponse;

    public void initializeConnection() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        consoleReader = new BufferedReader(new InputStreamReader(System.in));
        socketWriter = new PrintWriter(socket.getOutputStream(), true);
        inputValidations = new ConsoleInputValidator(consoleReader);
        serverRequest = new ServerRequestSender(socketWriter);
        serverResponse = new ServerResponseReader(socketReader);
    }

    public BufferedReader getSocketReader() {
        return socketReader;
    }

    public PrintWriter getSocketWriter() {
        return socketWriter;
    }

    public BufferedReader getConsoleReader() {
        return consoleReader;
    }

    public ConsoleInputValidator getInputValidations() {
        return inputValidations;
    }

    public ServerRequestSender getServerRequest() {
        return serverRequest;
    }

    public ServerResponseReader getServerResponse() {
        return serverResponse;
    }

    public void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (socketReader != null) {
                socketReader.close();
            }
            if (socketWriter != null) {
                socketWriter.close();
            }
            if (consoleReader != null) {
                consoleReader.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
}
