package server;

import server.exceptions.DuplicateUsernameException;
import server.exceptions.UserNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class ChatServerController {
    private Map<String, User> users;

    public ChatServerController() {
        users = new HashMap<>();

    }

    public void connectUser(String username) throws DuplicateUsernameException {
        if (users.containsKey(username)) {
            throw new DuplicateUsernameException(username);
        } else {
            User user = new User(username);
            users.put(username, user);
        }
    }

    public void disconnectUser(String username) throws UserNotFoundException {
        User u = findUserByName(username);
        if (u != null) {
            users.remove(u);
        } else throw new UserNotFoundException(username);
    }

    public User findUserByName(String username) throws UserNotFoundException {
        User u = users.get(username);
        if (u != null) {
            return u;
        } else throw new UserNotFoundException(username);
    }
}
