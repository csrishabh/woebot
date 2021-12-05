package com.rishabh.woebot.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.services.chat.v1.model.ActionParameter;
import com.google.api.services.chat.v1.model.Button;
import com.google.api.services.chat.v1.model.Card;
import com.google.api.services.chat.v1.model.CardHeader;
import com.google.api.services.chat.v1.model.FormAction;
import com.google.api.services.chat.v1.model.Image;
import com.google.api.services.chat.v1.model.ImageButton;
import com.google.api.services.chat.v1.model.Message;
import com.google.api.services.chat.v1.model.OnClick;
import com.google.api.services.chat.v1.model.OpenLink;
import com.google.api.services.chat.v1.model.Section;
import com.google.api.services.chat.v1.model.TextButton;
import com.google.api.services.chat.v1.model.TextParagraph;
import com.google.api.services.chat.v1.model.WidgetMarkup;
import com.rishabh.woebot.domain.BotMessage;
import com.rishabh.woebot.service.MessageService;

@RestController
public class WoeBotController {

	private static final String INTERACTIVE_TEXT_BUTTON_ACTION = "doTextButtonAction";
	private static final String INTERACTIVE_IMAGE_BUTTON_ACTION = "doImageButtonAction";
	private static final String INTERACTIVE_BUTTON_KEY = "originalMessage";
	private static final String HEADER_IMAGE = "https://rishwoebot.herokuapp.com/bot_icon.jpg";
	private static final String BOT_NAME = "Tsys Help desk";
	private static final String REDIRECT_URL = "https://goo.gl/kwhSNz";
	private static final String MESSAGE_ID = "messageId";

	@Autowired
	private MessageService msgService;

	
	@GetMapping("/test")
	@ResponseBody
	public List<BotMessage> Test() {
		
		return msgService.getInitialResponse();
		
	}
	
	/**
	 * Handles a GET request to the /bot endpoint.
	 *
	 * @param event Event from chat.
	 * @return Message
	 */
	@PostMapping("/")
	@ResponseBody
	public Message onEvent(@RequestBody JsonNode event) {
		Message reply = new Message();

		String eventType = event.get("type").asText();
		switch (eventType) {
		case "ADDED_TO_SPACE":
			String spaceType = event.at("/space/type").asText();
			if ("ROOM".equals(spaceType)) {
				String displayName = event.at("/space/displayName").asText();
				String replyText = String.format("Thanks for adding me to %s", displayName);
				reply.setText(replyText);
			} else {
				String displayName = event.at("/user/displayName").asText();
				String replyText = String.format("Thanks for adding me to a DM, %s!", displayName);
				reply.setText(replyText);
			}
			break;
		case "MESSAGE":
			List<BotMessage> msgList = msgService.getInitialResponse();
			Card card = createCardResponse(event.at("/message/text").asText(), msgList);
			reply.setCards(Collections.singletonList(card));
			break;
		case "CARD_CLICKED":
			// Get the custom action name and custom parameter value out of the event
			// object.
			String actionName = event.at("/action/actionMethodName").asText();
			String customParameterValue = event.at("/action/parameters/0/value").asText();
			card = respondToInteractiveCardClick(actionName, customParameterValue);
			reply.setCards(Collections.singletonList(card));
			break;
		case "REMOVED_FROM_SPACE":
			String name = event.at("/space/name").asText();
			System.out.println(String.format("Bot removed from %s", name));
			break;
		default:
			reply.setText("Cannot determine event type");
			break;
		}
		return reply;
	}
	
	/**
	 * Creates a card-formatted response based on the message sent in Hangouts Chat.
	 *
	 * @param message the event object sent from Hangouts Chat
	 * @return a card instance
	 */
	
	private Card createCardResponse(String message, List<BotMessage> msgList) {

		System.out.println("Message :->" + message);
		Card card = new Card();
		List<WidgetMarkup> widgets = new ArrayList<>();
		/*
		 * Stream.of(message.split(" ")).forEach((s -> { if (s.contains("header")) {
		 * CardHeader header = new
		 * CardHeader().setTitle(BOT_NAME).setSubtitle("Card header")
		 * .setImageUrl(HEADER_IMAGE).setImageStyle("IMAGE"); card.setHeader(header); }
		 * else if (s.contains("keyvalue")) { KeyValue widget = new
		 * KeyValue().setTopLabel("KeyValue widget").
		 * setContent("This is a KeyValue widget")
		 * .setBottomLabel("The bottom label").setIcon("STAR"); widgets.add(new
		 * WidgetMarkup().setKeyValue(widget)); } } }));
		 */
		CardHeader header = new CardHeader().setTitle(BOT_NAME).setSubtitle("How i can help you")
				.setImageUrl(HEADER_IMAGE).setImageStyle("IMAGE");
		card.setHeader(header);
		List<Button> buttonsList = new ArrayList<>();
		msgList.forEach(msg -> {

			switch (msg.getType()) {
			case BUTTON:
				List<ActionParameter> customParameters = Collections
						.singletonList(new ActionParameter().setKey(MESSAGE_ID).setValue(msg.getId()));
				FormAction action = new FormAction().setActionMethodName(INTERACTIVE_TEXT_BUTTON_ACTION)
						.setParameters(customParameters);
				OnClick onClick = new OnClick().setAction(action);
				TextButton button = new TextButton().setText(msg.getMsgTxt()).setOnClick(onClick);
				Button widget = new Button().setTextButton(button);
				buttonsList.add(widget);
				break;

			case LINK:
				OpenLink openLink = new OpenLink().setUrl(msg.getRedirectUrl());
				OnClick onClickLinkAction = new OnClick().setOpenLink(openLink);
				TextButton linkButton = new TextButton().setText(msg.getMsgTxt()).setOnClick(onClickLinkAction);
				Button linkWidget = new Button().setTextButton(linkButton);
				buttonsList.add(linkWidget);
				break;

			case IMAGEBUTTON:
				OpenLink link = new OpenLink().setUrl(msg.getRedirectUrl());
				OnClick onClickImage = new OnClick().setOpenLink(link);
				ImageButton imageButton = new ImageButton().setIcon(msg.getMsgTxt()).setOnClick(onClickImage);
				Button imageWidget = new Button().setImageButton(imageButton);
				buttonsList.add(imageWidget);

			case IMAGE:
				Image image = new Image().setImageUrl(msg.getImageUrl());
				widgets.add(new WidgetMarkup().setImage(image));

			case TEXT_PARAGRAPH:
				TextParagraph textWidget = new TextParagraph().setText(msg.getMsgTxt());
				widgets.add(new WidgetMarkup().setTextParagraph(textWidget));
			default:
				break;
			}

		});

		if (buttonsList.size() > 0) {
			widgets.add(new WidgetMarkup().setButtons(buttonsList));
		}
		Section section = new Section().setWidgets(widgets);
		card.setSections(Collections.singletonList(section));
		return card;
	}

	/**
	 * Handles the click for an interactive button.
	 *
	 * @param actionName           name of action invoked
	 * @param customParameterValue custom payload from event
	 * @return a response card
	 */
	private Card respondToInteractiveCardClick(String actionName, String customParameterValue) {
		// Determine which button the user clicked.
		String message = String.format("You clicked <u>%s</u>. <br> Your original message was \"%s\".",
				actionName.equals(INTERACTIVE_TEXT_BUTTON_ACTION) ? "a TextButton" : "an ImageButton",
				customParameterValue);

		Card card = new Card();
		CardHeader header = new CardHeader();
		header.setTitle(BOT_NAME).setSubtitle("Interactive card click").setImageUrl(HEADER_IMAGE)
				.setImageStyle("IMAGE");
		card.setHeader(header);
		TextParagraph text = new TextParagraph().setText(message);
		List<WidgetMarkup> widgets = Collections.singletonList(new WidgetMarkup().setTextParagraph(text));
		Section section = new Section().setWidgets(widgets);
		card.setSections(Collections.singletonList(section));
		return card;
	}
}
