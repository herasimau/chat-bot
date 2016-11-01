package com.herasimau.bot.vk;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author herasimau on 01.11.2016.
 */
public class VkApi {


    private static final String API_VERSION = "5.59";
    public static final String AUTH_URL = "https://oauth.vk.com/authorize"
            + "?client_id=5703060"
            + "&scope=messages"
            + "&redirect_uri=https://oauth.vk.com/blank.html"
            + "&display=page"
            + "&v="+API_VERSION
            + "&response_type=token";


    public void auth() throws IOException {
        try {
            Desktop.getDesktop().browse(new URL(AUTH_URL).toURI());
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
    }



}
