package com.rishabh.woebot.repo;

import org.springframework.data.mongodb.repository.MongoRepository;


import com.rishabh.woebot.domain.BotMessage;

public interface MessageRepo extends MongoRepository<BotMessage, String> , MessageRepoCustom{
	

}
