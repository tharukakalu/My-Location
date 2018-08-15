package firebase;

public class User {

    public String email;
    public String password;
   // public String email;
    public String repassword;
//
    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String password) {
        this.email = name;
        this.password = password;
    }
}
