package serverController;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

interface ClientRequestHandler {

    void handleRequest(JSONObject jsonData, PrintWriter socketWriter) throws IOException;
}
