package firebase;

public class Regester {

    public String name;
    public String password;
    public String email;
    public String repassword;
    //
    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Regester() {
    }

    public Regester(String name,String email, String password, String repassword) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.repassword = repassword;
    }
}
