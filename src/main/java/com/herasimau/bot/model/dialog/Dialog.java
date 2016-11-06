package com.herasimau.bot.model.dialog;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author herasimau on 05.11.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dialog {

    @JsonProperty("count")
    private Long count;

    @JsonProperty("items")
    private List<Message> messages = new ArrayList<Message>();

    @JsonProperty("unread")
    private Long unread;

    @JsonProperty("first_name")
    private String name;

    @JsonProperty("last_name")
    private String surname;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Long getUnread() {
        return unread;
    }

    public void setUnread(Long unread) {
        this.unread = unread;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }


    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }


}

