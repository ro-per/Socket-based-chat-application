package user;

import exceptions.DuplicateUsernameException;
import exceptions.UserNotFoundException;
import main.ServerThread;

import java.util.*;
import java.util.logging.Logger;

public class UserManager {

    private static final Logger logger = Logger.getLogger(UserManager.class.getName());
    private Map<User, ServerThread> userServerThreadMap;

    public UserManager() {
        userServerThreadMap = new HashMap<>();
    }

    public void connectUser(User user1, ServerThread thread) throws DuplicateUsernameException {
        boolean duplicate = existsUser(user1);
        if (duplicate || user1.compareWithString("Server")) {

            //TODO send ERROR_LOGIN

            throw new DuplicateUsernameException(user1.getName());
        } else {
            userServerThreadMap.put(user1, thread);
        }
    }

    public void disconnectUser(User user) throws UserNotFoundException {
        if (userServerThreadMap.containsKey(user)) {
            userServerThreadMap.remove(user);
        } else throw new UserNotFoundException(user.getName());
    }

    public ServerThread getServerThread(User user) {
        return userServerThreadMap.get(user);
    }

    public Set<String> getUsernames() {
        Set<String> usernames = new HashSet<>();
        for (User u : userServerThreadMap.keySet()) {
            usernames.add(u.getName());
        }
        return usernames;
    }

    public ServerThread getServerThreadByUsername(String username) {
        User u = null;
        for (User user : userServerThreadMap.keySet()) {
            if (user.compareWithString(username)) {
                u = user;
            }
        }
        return userServerThreadMap.get(u);
    }

    public Collection<ServerThread> getServerThreads() {
        return userServerThreadMap.values();
    }

    public Set<ServerThread> getServerThreadByUsers(Set<User> userSet) {
        Set<ServerThread> threads = new HashSet<>();
        for (User user : userServerThreadMap.keySet()) {
            if (userSet.contains(user)) {
                threads.add(userServerThreadMap.get(user));
            }
        }
        return threads;
    }

    public boolean existsUser(User user1) {
        for (User user2 : userServerThreadMap.keySet()) {
            String u2 = user2.getName().toLowerCase();
            String u1 = user1.getName().toLowerCase();
            if (u2.contains(u1)) {
                return true;
            }
        }
        return false;
    }
}
