package clientcontroller;

import java.io.IOException;

import org.json.simple.JSONObject;

import clientservice.ConsoleInputValidator;
import clientservice.DiscardMenuItemService;
import requestresponse.ServerRequestSender;
import requestresponse.ServerResponseReader;

public class DiscardMenuItemController {

    private final ConsoleInputValidator inputValidator;
    private final DiscardMenuItemService discardMenuItemService;
    private final JSONObject menuItem;
    private final ServerRequestSender serverRequest;
    private final ServerResponseReader serverResponse;

    public DiscardMenuItemController(ConsoleInputValidator inputValidator, ServerRequestSender serverRequest, ServerResponseReader serverResponse) {
        this.menuItem = new JSONObject();
        this.inputValidator = inputValidator;
        this.serverRequest = serverRequest;
        this.serverResponse = serverResponse;
        this.discardMenuItemService = new DiscardMenuItemService(serverRequest, serverResponse);
    }

    @SuppressWarnings("unchecked")
    public void discardMenuItems() {
        try {
            menuItem.put("requestType", "discardMenuItem");
            serverRequest.sendRequest(menuItem);
            boolean result = discardMenuItemService.readDiscardItemResponse();
            if (result) {
                displayDiscardMenu();
            } else {
                System.out.println("You have already performed action for the month");
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void displayDiscardMenu() throws IOException {
        System.out.println("Enter operation to perform:\n1.Delete Menu Item\n2.Ask for Feedback\n3.Exit");
        int command = inputValidator.getValidatedOption(2);

        switch (command) {
            case 1 ->
                performMenuAction(3);
            case 2 ->
                performMenuAction(4);
            case 3 ->
                System.out.println("Exiting...");
            default ->
                System.out.println("Invalid request");
        }
    }

    @SuppressWarnings("unchecked")
    private void performMenuAction(int messageId) throws IOException {
        System.out.println("Enter Item Id: ");
        int itemId = inputValidator.getValidatedIntInput();
        if (itemId < 1) {
            System.out.println("Invalid Item Id");
            return;
        }
        menuItem.put("requestType", "storeDiscardedItem");
        menuItem.put("id", itemId);
        menuItem.put("messageId", messageId);
        serverRequest.sendRequest(menuItem);
        serverResponse.printResponse();
    }
}
