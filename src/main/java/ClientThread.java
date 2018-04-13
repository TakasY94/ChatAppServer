import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientThread extends Thread {
    private final static int DELAY = 30000;

    private Socket socket;
    private Message c;
    private String login;
    private Timer timer;
    private int outPacks = 0;
    private boolean flag = false;
    private int inPacks = 0;

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

            this.timer.start();

            outputStream.writeObject(new Ping());
            this.outPacks++;
            System.out.println(outPacks + " out");

            while(true) {
                if(this.flag){
                    this.flag = false;
                    break;
                }
                this.c = (Message) inputStream.readObject();

                if (this.c instanceof Ping) {
                    this.inPacks++;
                    System.out.println(this.inPacks + " in");
                } else if (! c.getMessage().equals(Config.HELLO_MESSAGE)) {
                    System.out.println("[" + login + "]: " + c.getMessage());
                    getChatHistory().addMessage(this.c);
                } else {
                    outputStream.writeObject(getChatHistory());
                    this.broadcast(getUserList().getClientList(), new Message("Server-Bot", "The user " + login + " has been connect"));
                }
                this.c.setOnlineUsers(getUserList().getUsers());

                if (! (c instanceof Ping) && ! c.getMessage().equals(Config.HELLO_MESSAGE)) {
                    System.out.println("Send broadcast Message: " + c.getMessage() + "");
                    this.broadcast(getUserList().getClientsList(), this.c);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.println(login + " disconnected!");
            this.broadcast(getUserList().getClientsList(), new Message("Server-Bot", "The user " + login + " has been disconnect", getUserList().getUsers()));
            this.timer.stop();
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
