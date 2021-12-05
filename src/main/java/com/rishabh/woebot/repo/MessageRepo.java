package com.rishabh.woebot.repo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.rishabh.woebot.domain.BotMessage;

public interface MessageRepo extends MongoRepository<BotMessage, String> , MessageRepoCustom{
	
	@Query("{'ansId' : ?0}")
	public List<BotMessage> getMessageByAndId(String ansId, Sort sort);

}
