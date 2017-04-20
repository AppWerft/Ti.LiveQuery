package de.appwerft.parselivequery;

import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;

// This proxy can be created by calling Parselivequery.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = ParselivequeryModule.class)
public class ObjectProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = "PLQ";
	private ParseQuery<GenericClass> query = ParseQuery
			.getQuery(GenericClass.class);
	static final String USER_ID_KEY = "userId";

	static final String BODY_KEY = "body";
	static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
	private String CLASSNAME;

	// Constructor
	public ObjectProxy() {
		super();
	}

	@Override
	public void handleCreationDict(KrollDict opts) {
		if (opts.containsKeyAndNotNull("CLASSNAME"))
			CLASSNAME = opts.getString("CLASSNAME");
		super.handleCreationDict(opts);
	}

	@Kroll.method
	public void save(KrollDict opts) {
		KrollCallbacks kcb = new KrollCallbacks(opts);
		KrollDict data = opts.getKrollDict("data");
		ParseObject message = ParseObject.create("Message");
		message.put(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
		message.put(BODY_KEY, data);
		message.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					kcb.onSuccess.call(getKrollObject(), new KrollDict());
				} else {
					kcb.onError.call(getKrollObject(), new KrollDict());
				}
			}
		});
	}

	@Kroll.method
	void load(KrollDict opts) {
		KrollCallbacks kcb = new KrollCallbacks(opts);
		ParseQuery<GenericClass> query = ParseQuery
				.getQuery(GenericClass.class);
		query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
		// get the latest 50 messages, order will show up newest to oldest of
		// this group
		query.orderByDescending("createdAt");
		query.whereEqualTo("CLASSNAME", CLASSNAME);
		// Execute query to fetch all messages from Parse asynchronously
		// This is equivalent to a SELECT query with SQL
		query.findInBackground(new FindCallback<GenericClass>() {
			public void done(List<GenericClass> messages, ParseException e) {
				if (e == null) {
					KrollDict res = new KrollDict();
					res.put("messages", messages.toArray());
					if (kcb.onSuccess != null)
						kcb.onSuccess.call(getKrollObject(), res);
				} else {

					Log.e("message", "Error Loading Messages" + e);
				}
			}
		});
	}
}