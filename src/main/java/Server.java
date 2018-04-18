import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {

        private static UsersList list = new UsersList();
        private static ChatHistory chatHistory = new ChatHistory();

        public static void main(String[] args) {
            try {
                ServerSocket socketListener = new ServerSocket(1234);
                while (true) {
                    Socket client = null;
                    while (client == null) {
                        client = socketListener.accept();
                        System.out.println("ClientSide was connected");
                    }
                    new ClientThread(client);
                }
            } catch (SocketException e){
                System.out.println("Socket exception");
                e.printStackTrace();
            } catch (IOException e){
                System.out.println("I/O exception");
                e.printStackTrace();
            }
        }

        public synchronized static UsersList getUserList() {
            return list;
        }

        public synchronized static ChatHistory getChatHistory() {
            return chatHistory;
        }
    }
