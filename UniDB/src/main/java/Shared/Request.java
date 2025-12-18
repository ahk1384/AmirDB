package Shared;

import java.io.Serializable;

public class Request implements Serializable {
    public MessageType messageType;
    public String query;
    public Request (MessageType messageType, String query) {
        this.messageType = messageType;
        this.query = query;
    }
    public MessageType getType() {
        return messageType;
    }
    public String getQuery() {
        return query;
    }
}
