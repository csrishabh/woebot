package com.rishabh.woebot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.rishabh.woebot.domain.BotMessage;
import com.rishabh.woebot.repo.MessageRepo;

@Service
public class MessageService {

	@Autowired
	private MessageRepo repo;

	public List<BotMessage> getInitialResponse() {

		return repo.getInitialResponse();
	}
	
	public BotMessage addMessage(BotMessage message) {

		return repo.save(message);
	}

	public List<BotMessage> getResponseByAnsId(String ansId) {

		return repo.getMessageByAndId(ansId,Sort.by(Sort.Direction.ASC,"orderNumber"));
	}
	
	public BotMessage getMessageById(String msgId) {

		Optional<BotMessage> optional = repo.findById(msgId);
		if(optional.isPresent()) {
			return optional.get();
		}
		else {
			return null;
		}
	}
}
