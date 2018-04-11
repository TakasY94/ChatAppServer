import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;

public class ClientThread extends Thread {
    private Socket socket;
    private Message c;
    private String login;
    private Timer timer;

    public ClientThread(Socket socket){
        this.socket = socket;
        this.start();
    }

    public void run(){
        try {
            final ObjectInputStream inputStream = new ObjectInputStream(this.socket.getInputStream());
            final ObjectOutputStream outputStream = new ObjectOutputStream(this.socket.getOutputStream());

            this.c = (Message) inputStream.readObject();
            this.login = this.c.getLogin();

            if (! this.c.getMessage().equals(Config.HELLO_MESSAGE)){
                System.out.println("[" + this.c.getLogin() + "]: " + this.c.getMessage());
                getChatHistory().addMessage(this.c);
            } else {
                outputStream.writeObject(getChatHistory());
                this.broadcast(getUserList().getClientList(), new Message("Server-Bot", "The user " + login + " has been connect"));
            }
            getUserList().addUser(login, socket, outputStream, inputStream);

            this.c.setUsers(getUserList.getUsers());
            this.broadcast(getUserList.getClientList, this.c);

            this.timer = new Timer(DELAY, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try { //Если количество входящих пакетов от клиента рано исходящему, значит клиент еще не в ауте
                        if (inPacks == outPacks) {
                            outputStream.writeObject(new Ping());
                            outPacks++;
                            System.out.println(outPacks + " out");
                        } else { //Иначе, в ауте
                            throw new SocketException();
                        }
                    } catch (SocketException ex1) {
                        System.out.println("packages not clash");
                        System.out.println(login + " disconnected!");
                        //Удаляем клиента из списка доступных и информируем всех
                        getUserList().deleteUser(login);
                        broadcast(getUserList().getClientsList(), new Message("Server-Bot", "The user " + login + " has been disconnect", getUserList().getUsers()));
                        flag = true;
                        timer.stop();
                    }  catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {

        } catch (ClassNotFoundException e){

        }
    }

        private void broadcast(ArrayList<Client> clientsArrayList, Message message){
            try {
                for (Client client : clientsArrayList) {
                    client.getThisObjectOutputStream().writeObject(message);
                }
            } catch (SocketException e) {
                System.out.println("in broadcast: " + login + " disconnected!");
                getUserList().deleteUser(login);
                this.broadcast(getUserList().getClientsList(), new Message("Server-Bot", "The user " + login + " has been disconnected", getUserList().getUsers()));

                timer.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
