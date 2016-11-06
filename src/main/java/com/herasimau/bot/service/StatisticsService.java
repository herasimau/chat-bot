package com.herasimau.bot.service;

import com.herasimau.bot.model.dialog.DialogResponse;
import com.herasimau.bot.model.dialog.Message;
import com.herasimau.bot.model.user.User;
import com.herasimau.bot.model.user.UserResponse;
import com.herasimau.bot.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;


/**
 * Created by herasimau on 06/11/16.
 */
@Service
public class StatisticsService {

    @Value("${vk.access.token}")
    private  String ACCESS_TOKEN ;
    @Autowired
    MessageRepository messageRepository;
    @Value("${vk.peer.id}")
    private String PEER_ID;
    private Long totalMessages;
    private Long sexM =0L;
    private Long sexF=0L;
    private Long topUser;
    private Long topUserMessages = 0L;
    private HashMap<Long,Long> stats = new HashMap<>();

    @PostConstruct
    public void init(){
        updateStatistics();
    }

    public  void updateStatistics(){
        this.totalMessages = new RestTemplate().getForObject(getReqUrl(),DialogResponse.class).getResponse().getCount();
        updateUsers();
        updateTopUser();

    }

    public String getUserStats(Long userId){
        return "Пользователь "+getNameAndSurname(userId)+", "+
                "написал "+this.stats.get(userId)+ " сообщений.";
    }

    public String getStats(){
        return "Всего написано сообщений: "+this.totalMessages+", "+
                "Всего мальчиков: "+this.sexM+", "+
                "Всего девочек: "+this.sexF+", "+
                "Мистер болтун: " + getNameAndSurname(this.topUser)+", "+
                "написал "+topUserMessages+" сообщений.";
    }


    public void updateTopUser(){
        Iterable<Message> messages = messageRepository.findAll();

        messages.forEach(message -> {
            if(this.stats.get(message.getUserId())!=null){
                Long count = this.stats.get(message.getUserId());
                count++;
                this.stats.put(message.getUserId(),count);
            }
            else {
                this.stats.put(message.getUserId(),0L);
            }
        });

        this.topUser = this.stats.entrySet()
                .stream()
                .max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1)
                .get().getKey();
        this.topUserMessages = this.stats.get(topUser);
    }

    public  void updateUsers(){
        UserResponse userResponse = new RestTemplate().getForObject(getChatUser(),UserResponse.class);
        userResponse.getResponse().forEach(user -> {
            if(user.getSex()!=null){
                if(user.getSex()==2L){
                    this.sexM++;
                }
                else if (user.getSex()==1L){
                    this.sexF++;
                }
            }

        });
    }
    public  String getChatUser(){
        return "https://api.vk.com/method/messages.getChatUsers?" +
                "chat_id="+PEER_ID +
                "&fields=sex" +
                "&v=5.60"+
                "&access_token=" +ACCESS_TOKEN;

    }

    public String getNameAndSurname(Long id){
        User user = new RestTemplate().getForObject(getUserUrl(id),UserResponse.class).getResponse().get(0);
        return user.getFirstName()+" "+user.getLastName();


    }
    public String getUserUrl(Long id){

        return  "https://api.vk.com/method/users.get?" +
                "user_ids=" +id+
                "&name_case=nom" +
                "&v=5.60"+
                "&access_token=" +this.ACCESS_TOKEN;

    }
    public  String getReqUrl(){
        return  "https://api.vk.com/method/messages.getHistory?" +
                "offset=0" +
                "&count=0"  +
                "&peer_id=200000000" +PEER_ID +
                "&rev=0" +
                "&v=5.60" +
                "&access_token=" +this.ACCESS_TOKEN;
    }


}
