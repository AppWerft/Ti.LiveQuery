package de.appwerft.parselivequery;

import java.util.Date;

import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {

	public Message() {
	}

	public String getId() {
		return getObjectId();
	}

	public Date getDate() {
		return getCreatedAt();
	}

	public ParseUser getSender() {
		return getParseUser("sender");
	}

	public void setSender(ParseUser user) {
		put("sender", user);
	}

	public ParseUser getReceiver() {
		return getParseUser("receiver");
	}

	public void setReceiver(ParseUser user) {
		put("receiver", user);
	}

	public String getContent() {
		return getString("content");
	}

	public void setContent(String content) {
		put("content", content);
	}
}