package requestresponse;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerResponseReader {

    private final BufferedReader socketReader;

    public ServerResponseReader(BufferedReader socketReader) {
        this.socketReader = socketReader;
    }

    public void printResponse() throws IOException {
        String response = readResponseUntilEnd();
        System.out.println(response);
    }

    public String readResponseUntilEnd() throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        String responseLine;
        while ((responseLine = socketReader.readLine()) != null) {
            if (responseLine.equals("END_OF_RESPONSE")) {
                break;
            }
            responseBuilder.append(responseLine).append("\n");
        }
        return responseBuilder.toString();
    }

    public String readJsonResponse() throws IOException {
        return socketReader.readLine();
    }
}
