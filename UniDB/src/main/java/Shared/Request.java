package Shared;

import java.io.Serializable;

public class Request implements Serializable {
    public MessageType messageType;
    public String query;
    public Request (MessageType messageType, String query) {
        this.messageType = messageType;
        this.query = query;
    }
    public MessageType getMessageType() {
        return messageType;
    }
    public String getquery() {
        return query;
    }
}
