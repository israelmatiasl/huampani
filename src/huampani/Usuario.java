package huampani;

import java.io.Serializable;

public class Usuario implements Serializable {
    protected int id;
    protected String name;
    protected String userType; // "Admin" 

    public Usuario(int id, String name, String userType) {
        this.id = id;
        this.name = name;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserType() {
        return userType;
    }
}