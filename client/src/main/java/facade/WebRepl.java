package facade;

import websocket.messages.Notification;
import java.util.Scanner;
import principal.Client;
import ui.EscapeSequences.*;

public class WebRepl implements NotificationHandler {

    private Client client;

    public WebRepl(String serverUrl, Client client) {
        this.client = client;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(Notification notification) {
        System.out.println(notification.message());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n >>> ");
    }

}
