package de.appwerft.parselivequery;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;

// This proxy can be created by calling Parselivequery.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = ParselivequeryModule.class)
public class QueryProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = "PLQ";
	private ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
	static final String USER_ID_KEY = "userId";

	static final String BODY_KEY = "body";

	// Constructor
	public QueryProxy() {
		super();
	}

	@Override
	public void handleCreationDict(KrollDict opts) {

		super.handleCreationDict(opts);
	}

	@Kroll.method
	public void post(KrollDict opts) {
		KrollDict data = opts.getKrollDict("data");
		KrollFunction onSuccess = (KrollFunction) opts.get("onsuccess");
		KrollFunction onError = (KrollFunction) opts.get("onerror");

		ParseObject message = ParseObject.create("Message");
		message.put(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
		message.put(BODY_KEY, data);
		message.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					onSuccess.call(getKrollObject(), new KrollDict());
				} else {
					onError.call(getKrollObject(), new KrollDict());
				}
			}
		});
	}
}