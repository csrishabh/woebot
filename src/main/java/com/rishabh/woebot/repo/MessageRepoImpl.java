package com.rishabh.woebot.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.rishabh.woebot.domain.BotMessage;

public class MessageRepoImpl implements MessageRepoCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Override
	public List<BotMessage> getInitialResponse() {
		
		
			Criteria regex = Criteria.where("ansId").isNull();
			return mongoTemplate.find(new Query().addCriteria(regex), BotMessage.class);		
	}

}
