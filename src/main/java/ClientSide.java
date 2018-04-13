import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientSide {
    public static void main(String[] args) throws Exception {
        int port = 1234;
        try {
            Socket socket = new Socket("localhost", port);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
            DataInputStream ois = new DataInputStream(socket.getInputStream());
            {
                System.out.printf("ClientSide connected to socket.");
                System.out.println();
                System.out.println("ClientSide writing channel = oos & reading channel = ois initialized.");

                while (!socket.isOutputShutdown()) ;
                if (br.ready()) {
                    System.out.println("ClientSide start writing in channel...");
                    Thread.sleep(200);
                    String message = br.readLine();
                    socket.getOutputStream().write((message).getBytes());
                }
            }
        } catch (Exception e) {

        }
    }
}