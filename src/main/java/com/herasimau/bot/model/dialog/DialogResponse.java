package com.herasimau.bot.model.dialog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author herasimau on 05.11.2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "response"
})
public class DialogResponse {

    @JsonProperty("response")
    private Dialog response;

    public Dialog getResponse() {
        return response;
    }

    public void setResponse(Dialog response) {
        this.response = response;
    }
}