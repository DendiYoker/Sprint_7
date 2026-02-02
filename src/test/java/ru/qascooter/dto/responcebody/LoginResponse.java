package ru.qascooter.dto.responcebody;


public class LoginResponse {
    private String id;

    public LoginResponse() {}

    public LoginResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}