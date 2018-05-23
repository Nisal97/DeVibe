package devibe.com.devibe;

public class User {

    private String username;
    private String email;
    private Object data;

    public User(String username, String email, Object data){
        this.username = username;
        this.email = email;
        this.data = data;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Object getData(){
        return data;
    }
}
