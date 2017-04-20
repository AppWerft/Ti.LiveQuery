package de.appwerft.parselivequery;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

import com.parse.ParseQuery;
import com.parse.SubscriptionHandling;

// This proxy can be created by calling Parselivequery.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = ParselivequeryModule.class)
public class QueryProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = "PLQ";
	private ParseQuery<Message> query;

	// Constructor
	public QueryProxy() {
		super();
	}

	@Override
	public void handleCreationDict(KrollDict options) {
		super.handleCreationDict(options);
		query = ParseQuery.getQuery(Message.class);
	}
}