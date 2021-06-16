package rahulstech.javafx.example;

public class User {

    private String userId;
    private String username;
    private String password;
    private String givenName;
    private String familyName;

    public User() {}

    User(String userId, String username, String password, String givenName, String familyName) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.givenName = givenName;
        this.familyName = familyName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
}
