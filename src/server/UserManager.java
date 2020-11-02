package server;

import server.exceptions.DuplicateUsernameException;
import server.exceptions.UserNotFoundException;

import javax.jws.soap.SOAPBinding;
import java.awt.*;
import java.util.*;

public class UserManager {

    private Map<User, ServerThread> users;

    public UserManager() {
        users = new HashMap<>();
    }

    public void connectUser(User user, ServerThread thread) throws DuplicateUsernameException {
        if (users.containsKey(user)) {
            throw new DuplicateUsernameException(user.getName());
        } else {
            users.put(user, thread);
        }
    }

    public void disconnectUser(User user) throws UserNotFoundException {
        if (users.containsKey(user)) {
            users.remove(user);
        } else throw new UserNotFoundException(user.getName());
    }

    public ServerThread getServerThread(User user){
        return users.get(user);
    }

    public Collection<ServerThread> getAllServerThreads(){
        return users.values();
    }

    public Set<ServerThread> getServerThreadByUsers(Set<User> userSet){
        Set<ServerThread> threads = new HashSet<>();
        for (User user : users.keySet()){
            if (userSet.contains(user)){
                threads.add(users.get(user));
            }
        }
        return threads;
    }
}
