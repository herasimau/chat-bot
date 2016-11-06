package com.herasimau.bot.service;

import com.herasimau.bot.commands.BotCommands;
import com.herasimau.bot.model.dialog.Dialog;
import com.herasimau.bot.model.user.User;
import com.herasimau.bot.model.user.UserResponse;
import com.herasimau.bot.repository.MessageRepository;
import com.herasimau.bot.model.dialog.Message;
import com.herasimau.bot.model.dialog.DialogResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author herasimau on 05.11.2016.
 */

@Transactional
@Service
public class VkServiceImpl implements VkService {


    @PostConstruct
    public void init(){
        dumpAllMessages();
    }

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    StatisticsService statisticsService;

    private static final Logger log = LoggerFactory.getLogger(VkServiceImpl.class);

    @Value("${vk.access.token}")
    private String ACCESS_TOKEN;
    @Value("${vk.peer.id}")
    private String PEER_ID;

    private Long offset = 0L;
    private Long count = 0L;
    private Long lastMessageId = 0L;

    private Integer rev = 0;


    @Scheduled(fixedRate = 700)
    @Override
    public void getMessagesUpdate() {

        this.rev=0;
        this.count = 50L;

        Dialog response = new RestTemplate().getForObject(getReqUrl(),DialogResponse.class).getResponse();

        if(response.getUnread()!=null){
            List<Message> messages = response.getMessages()
                    .stream()
                    .filter(message -> message.getReadState()!=1).collect(Collectors.toList());
            Long unread = response.getUnread();
            log.info("Unread messages = " + unread);
            for (int i = 0; i < unread; i++) {
                Message receivedMsg = messages.get(i);
                System.out.println(receivedMsg.getBody());
                System.out.println(receivedMsg.getBody().equalsIgnoreCase("!stats"));
                if(BotCommands.getBotCommands().contains(receivedMsg.getBody().toLowerCase())){
                    log.info("Bot command found.");

                    sendMessage("Обращение к боту от "+getNameAndSurname(receivedMsg.getUserId()));
                    switch (receivedMsg.getBody()){
                        case "!stats": sendMessage(statisticsService.getStats());
                            break;
                        case "!mystat": sendMessage(statisticsService.getUserStats(receivedMsg.getUserId()));
                            break;
                    }

                    log.info("Bot successfully respond to user.");
                }
                messageRepository.save(receivedMsg);
                this.lastMessageId=receivedMsg.getId();
                markAsRead(receivedMsg.getId());
                unread--;
            }
        }






    }

    @Override
    public void dumpAllMessages() {

        log.info("Start dump messages from VK to Database");
        log.info(this.ACCESS_TOKEN);
        this.count = 200L;
        this.rev = 1;
        log.info("Get for URL: "+getReqUrl());
        AtomicLong count = new AtomicLong(0);
        Long totalMessages = new RestTemplate().getForObject(getReqUrl(),DialogResponse.class).getResponse().getCount();
        log.info("Total messages to dump: "+totalMessages);

        while(count.get()<totalMessages){

            new RestTemplate().getForObject(getReqUrl(),DialogResponse.class)
                    .getResponse().getMessages()
                    .forEach(message ->{

                        if(message!=null){
                            messageRepository.save(message);
                            count.incrementAndGet();
                            this.lastMessageId = message.getId();
                        }

                    });
            offset+=200L;

            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("Dump finished, last messageID : "+this.lastMessageId);
        offset = 0L;
    }

    public void sendMessage(String message){

        new RestTemplate().getForEntity(getSendUrl(message),String.class);

    }

    public String getNameAndSurname(Long id){
        User user = new RestTemplate().getForObject(getUserUrl(id),UserResponse.class).getResponse().get(0);
        return user.getFirstName()+" "+user.getLastName();


    }

    public void markAsRead(Long messageId){
        new RestTemplate().getForEntity(getMarkAsReadUrl(messageId),String.class);

    }
    public String getSendUrl(String message){
        return "https://api.vk.com/method/messages.send?" +
                "random_id="+new Random().nextInt(10000) +
                "&peer_id=200000000"+PEER_ID +
                "&message=" +message+
                "&notification=0" +
                "&v=5.60"+
                "&access_token=" +ACCESS_TOKEN;

    }

    public String getReqUrl(){
        return  "https://api.vk.com/method/messages.getHistory?" +
                "offset=" +this.offset+
                "&count=" + this.count+
                "&peer_id=200000000"+PEER_ID +
                "&rev=" +this.rev+
                "&v=5.60" +
                "&access_token=" +this.ACCESS_TOKEN;
    }

    public String getUserUrl(Long id){

        return  "https://api.vk.com/method/users.get?" +
                "user_ids=" +id+
                "&name_case=gen" +
                "&v=5.60"+
                "&access_token=" +this.ACCESS_TOKEN;

    }

    public String getMarkAsReadUrl(Long messageId){
        return  "https://api.vk.com/method/messages.markAsRead?" +
                "message_ids=" +messageId+
                "&peer_id=200000000"+PEER_ID +
                "&v=5.60"+
                "&access_token=" +this.ACCESS_TOKEN;
    }




}
