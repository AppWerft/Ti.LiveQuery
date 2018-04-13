package de.appwerft.parselivequery;

import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;

import de.appwerft.parselivequery.utils.GenericClass;

// This proxy can be created by calling Parselivequery.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = ParselivequeryModule.class)
public class ObjectProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = "PLQ";

	static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
	private String CLASSNAME;

	// empty Constructor
	public ObjectProxy() {
		super();
	}

	// constructor parameter import:
	@Override
	public void handleCreationArgs(KrollModule createdInModule, Object[] args) {
		if (args.length == 1 && args[0] instanceof String) {
			CLASSNAME = (String) args[0];
			Log.d(LCAT, "class " + (String) args[0] + " created");
		}
	}

	// save to parse
	@Kroll.method
	public void save(KrollDict opts) {
		final KrollCallbacks kcb = new KrollCallbacks(opts);
		KrollDict data = opts.getKrollDict("data");
		ParseObject parseObject = ParseObject.create(CLASSNAME);
		parseObject.put("userId", ParseUser.getCurrentUser().getObjectId());
		parseObject.put("body", opts);
		parseObject.saveInBackground(new SaveCallback() {
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

	// querying of parse:
	@Kroll.method
	public void find(KrollDict opts) {
		ParseQuery<ParseObject> query;
		// importing of callbacks:
		final KrollCallbacks kcb = new KrollCallbacks(opts);
		// importingof query proxy
		if (opts.containsKeyAndNotNull(ParselivequeryModule.QUERY)) {
			Object o = opts.get(ParselivequeryModule.QUERY);
			if (o instanceof QueryProxy) {
				query = ((QueryProxy) o).query;
				findHandler(query, kcb);
			}
		}
	}

	@Kroll.method
	public void register(KrollDict opts) {
		ParseQuery<ParseObject> query;
		// importing of callbacks:
		final KrollCallbacks kcb = new KrollCallbacks(opts);
		// importingof query proxy
		if (opts.containsKeyAndNotNull(ParselivequeryModule.QUERY)) {
			Object o = opts.get(ParselivequeryModule.QUERY);
			if (o instanceof QueryProxy) {
				query = ((QueryProxy) o).query;
				registerHandler(query, kcb);
			}
		}
	}

	@Kroll.method
	public void unregister(KrollDict opts) {
		// importing of callbacks:
		final KrollCallbacks kcb = new KrollCallbacks(opts);

	}

	private void findHandler(ParseQuery<ParseObject> query,
			final KrollCallbacks kcb) {
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					KrollDict res = new KrollDict();
					res.put("data", objects.toArray());
					if (kcb.onSuccess != null)
						kcb.onSuccess.call(getKrollObject(), res);
				} else {

					Log.e("message", "Error Loading Messages" + e);
				}
			}
		});
	}

	private void registerHandler(ParseQuery<ParseObject> query,
			final KrollCallbacks kcb) {
		ParseLiveQueryClient client = ParseLiveQueryClient.Factory.getClient();
		SubscriptionHandling<ParseObject> handling = client.subscribe(query);

		handling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
			@Override
			public void onEvents(ParseQuery<ParseObject> query,
					SubscriptionHandling.Event event, ParseObject object) {

				KrollDict kd = new KrollDict();
				kd.put("event", event); // int
				kd.put("data", parseObj2KrollDict(object)); // int
				if (kcb.onEvent != null) {
					kcb.onEvent.call(getKrollObject(), kd);
				}

				// Handling all events
			}
		});
	}

	private KrollDict parseObj2KrollDict(ParseObject object) {
		KrollDict kd = new KrollDict();
		kd.put("data", object.getJSONObject("data"));
		return kd;
	}
}