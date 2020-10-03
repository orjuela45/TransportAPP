package com.example.transportapp.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class User  implements Serializable {

    String id;
    String email;
    String password;
    Date created_at;
    Date update_at;
    Integer status_id;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt() {this.created_at = new Date();}

    public Date getUpdatedAt() {return update_at;}

    public void setUpdateAt() {
        this.update_at = new Date();
    }

    public Integer getStatusID() {
        return status_id;
    }

    public void setStatusID(Integer status_id) {
        this.status_id = status_id;
    }
}
