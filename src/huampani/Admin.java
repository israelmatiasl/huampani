package huampani;


public class Admin extends Usuario {
    private String username;
    private String password;

    public Admin(int id, String name, String username, String password) {
        super(id, name, "Admin");
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}