package ti.livequery;

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

// This proxy can be created by calling Parselivequery.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = ParselivequeryModule.class)
public class ParseObjectProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = "PLQ";

	static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
	private String CLASSNAME;

	// empty Constructor
	public ParseObjectProxy() {
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
	public void saveInBackground(Object[] args) {
		if (args.length < 1) {
			Log.e(LCAT,
					"saveInBackground() needs one or two parameters: JSobject with payload and optional calback");
			return;
		}
		KrollDict payload = (KrollDict) args[0];

		ParseObject parseObject = ParseObject.create(CLASSNAME);
		if (ParseUser.getCurrentUser() != null) {
			parseObject.put("userId", ParseUser.getCurrentUser().getObjectId());
		}
		for (String key : payload.keySet()) {
			parseObject.put(key, payload.get(key));
		}
		parseObject.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				KrollFunction callback = null;
				if (args.length > 1) {
					callback = (KrollFunction) args[1];
				}
				KrollDict res = new KrollDict();
				if (e == null) {
					res.put("success", true);
				} else {
					res.put("error", e.getMessage());
					res.put("code", e.getCode());
					res.put("success", false);
				}
				if (callback != null)
					callback.call(getKrollObject(), res);
			}
		});
	}

	// querying of parse:
	@Kroll.method
	public void findObject(KrollDict opts) {
		ParseQuery<ParseObject> query;
		// importing of callbacks:
		final KrollCallbacks krollCallbacks = new KrollCallbacks(opts);
		// importingof query proxy
		if (opts.containsKeyAndNotNull(ParselivequeryModule.QUERY)) {
			Object o = opts.get(ParselivequeryModule.QUERY);
			if (o instanceof QueryProxy) {
				query = ((QueryProxy) o).query;
				findHandler(query, krollCallbacks);
			}
		}
	}

	private void findHandler(ParseQuery<ParseObject> query,
			final KrollCallbacks krollCallbacks) {
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					KrollDict res = new KrollDict();
					res.put("data", objects.toArray());
					if (krollCallbacks.onSuccess != null)
						krollCallbacks.onSuccess.call(getKrollObject(), res);
				} else {

					Log.e("message", "Error Loading Messages" + e);
				}
			}
		});
	}

	@Kroll.method
	public void registerObject(QueryProxy query, KrollDict opts) {
		// final ParseQuery<ParseObject> parseObject;

		// importing of callbacks:
		final KrollCallbacks krollCallbacks = new KrollCallbacks(opts);
		// importing of query proxy
		if (opts.containsKeyAndNotNull(ParselivequeryModule.QUERY)) {
			Object o = opts.get(ParselivequeryModule.QUERY);
			if (o instanceof QueryProxy) {
				ParseQuery<ParseObject> parseQuery = ((QueryProxy) o)
						.getQuery();
				registerHandler(parseQuery, krollCallbacks);
			}
		}
	}

	private void registerHandler(ParseQuery<ParseObject> query,
			final KrollCallbacks krollCallbacks) {
		ParseObject parseObject = ParseObject.create(CLASSNAME);
		ParseLiveQueryClient client = ParseLiveQueryClient.Factory.getClient();
		SubscriptionHandling<ParseObject> handling = client.subscribe(query);

		handling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
			@Override
			public void onEvents(ParseQuery<ParseObject> query,
					SubscriptionHandling.Event event, ParseObject object) {
				KrollDict kd = new KrollDict();
				kd.put("event", event); // int
				kd.put("data", parseObj2KrollDict(object)); // int
				if (krollCallbacks.onEvent != null) {
					krollCallbacks.onEvent.call(getKrollObject(), kd);
				}
			}
		});
	}

	@Kroll.method
	public void unregisterObject(QueryProxy query, KrollDict opts) {
		ParseQuery<ParseObject> parseQuery = ((QueryProxy) query).getQuery();

		ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory
				.getClient();
		parseLiveQueryClient.unsubscribe(parseQuery);
	}

	private KrollDict parseObj2KrollDict(ParseObject object) {
		KrollDict kd = new KrollDict();
		kd.put("data", object.getJSONObject("data"));
		return kd;
	}
}