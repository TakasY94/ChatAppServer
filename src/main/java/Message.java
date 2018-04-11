import java.util.Calendar;
import java.util.Date;

public class Message {
    private String login;
    private String message;
    private String[] users;
    private Date time;

    public Message(String login, String message){
        this.login = login;
        this.message = message;
        this.time = Calendar.getInstance().getTime();
    }

    public Message(String login, String message,String[] users){
        this.login = login;
        this.message = message;
        this.time = Calendar.getInstance().getTime();
        this.users = users;
    }

    public void setUsers(String[] users) {
        this.users = users;
    }

    public String getLogin() {
        return login;
    }

    public String getMessage() {
        return message;
    }

    public String[] getUsers() {
        return users;
    }

    public String getTime() {

        return Calendar.getInstance().getTime().toString();
    }
}
