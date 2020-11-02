package server;

import server.messages.Message;

import java.io.IOException;

public class MessageHandler {

    private final ChatService service;

    public MessageHandler(){
        this.service = new ChatService();
    }

    public void processMessage(Message msg, ServerThread thread) throws IOException {
        switch (msg.getType()) {
            case GROUP:
                service.sendGroupMessage(msg);
                break;
            case PRIVATE:
                service.sendMessageToSender(msg);
                break;
            case BROADCAST:
                service.sendBroadcast(msg);
                break;
            case CONNECT:
                service.connectUser(msg.getSender(), thread);
                break;
            case DISCONNECT:
                service.disconnectUser(msg.getSender(), thread);
                break;
        }
    }
}
