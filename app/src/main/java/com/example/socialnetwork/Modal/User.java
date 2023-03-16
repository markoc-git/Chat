package com.example.socialnetwork.Modal;

public class User {
    String username;
    String email;
    String id;
    String imgUrl;
    String status;
    public User(String username, String id,String imgUrl,String email,String status) {
        this.username = username;
        this.id = id;
        this.imgUrl = imgUrl;
        this.email = email;
        this.status = status;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getImgUrl() {
        return imgUrl;
    }


    public String getUsername() {
        return username;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
