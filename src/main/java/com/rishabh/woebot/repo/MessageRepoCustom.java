package com.rishabh.woebot.repo;

import java.util.List;

import com.rishabh.woebot.domain.BotMessage;

public interface MessageRepoCustom {

	public List<BotMessage> getInitialResponse();
}
