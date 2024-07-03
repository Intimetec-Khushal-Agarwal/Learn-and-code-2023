package server;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

interface ClientRequestHandler {

    void handleRequest(JSONObject jsonData, PrintWriter out) throws IOException;
}
