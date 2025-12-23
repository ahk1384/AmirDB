package Shared;

import java.io.Serializable;

public class Response implements Serializable {
    public boolean status;
    public String message;

    public Response(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public boolean isSuccess() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
