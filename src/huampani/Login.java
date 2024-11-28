package huampani;


public class Login {
    private final String USERNAME = "admin";
    private final String PASSWORD = "admin";

    public boolean ingresar(String username, String password) {
        return USERNAME.equals(username) && PASSWORD.equals(password);
    }
}
