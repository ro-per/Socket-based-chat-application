import server.User;
import server.exceptions.PrivacyException;
import server.messages.Message;
import server.messages.MessageType;

import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws PrivacyException, InterruptedException {
        User u1 = new User();
        User u2 = new User();
        User u3 = new User();
        User u4 = new User();
        User u5 = new User();
        User u6 = new User();
        User u7 = new User("romeo");
        User u8 = new User("romeo");

        System.out.println(u1);
        System.out.println(u2);
        System.out.println(u3);
        System.out.println(u4);
        System.out.println(u5);
        System.out.println(u6);
        System.out.println(u7);
        System.out.println(u8);

        final Set<User> group1 = new HashSet<>(); //u2
        group1.add(u2);
        final Set<User> group2 = new HashSet<>(); //u3,u4,u5
        group2.add(u3);
        group2.add(u4);
        group2.add(u5);


        Message m_private = new Message(u1, MessageType.PRIVATE, "Hi user 2", group1);

        Message m_group = new Message(u1, MessageType.GROUP, "Hi group 2", group2);
        m_group.addMember(u7);

//        group2.add(u6);

        Message m_broadcast = new Message(u1, MessageType.BROADCAST, "Hi everyone");


        System.out.println(m_private);
        System.out.println(m_group);
        System.out.println(m_broadcast);

        System.out.println(u7.equals(u8));

    }


}
