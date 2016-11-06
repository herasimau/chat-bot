package com.herasimau.bot.repository;


import com.herasimau.bot.model.dialog.Message;
import org.springframework.data.repository.CrudRepository;

/**
 * @author herasimau on 05.11.2016.
 */
public interface MessageRepository extends CrudRepository<Message,Long> {


}
