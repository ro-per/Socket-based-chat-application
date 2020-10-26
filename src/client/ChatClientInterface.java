package client;

import java.io.IOException;
import java.util.List;

public interface ChatClientInterface {

    void send(String message) throws IOException;

    List<String> getMessages();
}
