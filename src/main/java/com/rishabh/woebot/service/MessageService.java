package com.rishabh.woebot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rishabh.woebot.domain.BotMessage;
import com.rishabh.woebot.repo.MessageRepo;

@Service
public class MessageService {

	@Autowired
	private MessageRepo repo;
	
	
	public List<BotMessage> getInitialResponse(){
		
		return repo.getInitialResponse();
	}
}
