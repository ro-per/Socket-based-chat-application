package server;

public interface ChatServerInterface {

    void broadcast(String msg);

    void addUser(String name);

    void removeUser(String name, ServerThread serverThread);

    boolean isDuplicateUserName(String name);

}
