import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class UsersList {
    private Map<String, Client> onlineUsers = new HashMap<String, Client>();

    public void addUser(String login, Socket socket, ObjectOutputStream oos, ObjectInputStream ois){
        System.out.println(login + " connected");

    }
}
