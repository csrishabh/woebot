package com.rishabh.woebot.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document("message")
public class BotMessage {
	
	@Id
	private String id;
	@Getter
	private String msgTxt;
	private MessageType type;
	private String ansId;
	private ResponseType responseType;
	private String redirectUrl;
	private String imageUrl;
	
}
