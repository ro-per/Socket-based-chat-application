package server;

import server.messages.Message;

import java.io.IOException;

public class MessageHandler {

    private final ChatService service;

    public MessageHandler() {
        this.service = new ChatService();
    }

    public void processMessage(Message msg, ServerThread thread) throws IOException {
        switch (msg.getType()) {
            case PRIVATE:
            case REQUEST_PRIVATE:
                service.sendPrivateMessage(msg);
                break;
            case BROADCAST:
                service.sendPublicMessage(msg);
                break;
            case REQUEST_CONNECT:
                service.connectUser(msg.getSender(), thread);
                break;
            case REQUEST_DISCONNECT:
                service.disconnectUser(msg.getSender(), thread);
                break;
        }
    }
}
