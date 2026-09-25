package ap.exercises.finalproject;

import java.io.Serializable;

public class PersonParent implements Serializable {

    private final String name;
    private final String userId;
    private String username;
    private String password;

    public PersonParent(String name, String userId, String username, String password) {
        this.name = name;
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
