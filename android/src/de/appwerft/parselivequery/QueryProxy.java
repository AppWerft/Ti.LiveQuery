package de.appwerft.parselivequery;

import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
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
public class QueryProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = "PLQ";
	public ParseQuery<ParseObject> query;

	// Constructor
	public QueryProxy() {
		super();
	}

	public QueryProxy(ParseQuery<ParseObject> query) {
		super();
		this.query = query;
	}

	@Override
	public void handleCreationDict(KrollDict opts) {
		String className = null;
		String queryString;
		if (opts.containsKeyAndNotNull("name"))
			className = opts.getString("name");
		else {
			Log.w(LCAT, "name of object is missing");
			return;
		}
		query = new ParseQuery(className);
		if (opts.containsKeyAndNotNull("query")) {
			queryString = opts.getString("query");
			String[] conditions = queryString.split("/,/");
			for (int i = 0; i < conditions.length; i++) {
				String condition = conditions[i];
				if (condition.contains("==")) {
					String[] parts = condition.split("\\s*==\\s*");
					query = query.whereEqualTo(parts[0], parts[1]);
				}
				if (condition.contains("!=")) {
					String[] parts = condition.split("\\s*!=\\s*");
					query = query.whereNotEqualTo(parts[0], parts[1]);
				}
				if (condition.contains(">")) {
					String[] parts = condition.split("\\s*>\\s*");
					query = query.whereGreaterThan(parts[0], parts[1]);
				}
				if (condition.contains("<")) {
					String[] parts = condition.split("\\s*<\\s*");
					query = query.whereLessThan(parts[0], parts[1]);
				}
				if (condition.contains(">=")) {
					String[] parts = condition.split("\\s*>=\\s*");
					query = query.whereGreaterThanOrEqualTo(parts[0], parts[1]);
				}
				if (condition.contains("<=")) {
					String[] parts = condition.split("\\s*<=\\s*");
					query = query.whereLessThanOrEqualTo(parts[0], parts[1]);
				}
				if (condition.matches("limit\\s+[0-9]+")) {
					String[] parts = condition.split("\\s+");
					query = query.setLimit(Integer.parseInt(parts[1]));
				}
				if (condition.matches("exists\\s+(.*?)")) {
					String value = condition.replace("exists\\s+", "");
					query = query.whereExists(value);
				}
				if (condition.matches("notexists\\s+(.*?)")) {
					String value = condition.replace("notexists\\s+", "");
					query = query.whereDoesNotExist(value);
				}
				if (condition.contains("orderby ")) {
					String[] parts = condition.split(" ");
					if (parts[1].equals("asc"))
						query = query.orderByAscending(parts[2]);
					if (parts[1].equals("desc"))
						query = query.orderByDescending(parts[2]);
				}
			}
		}
		super.handleCreationDict(opts);
	}

	@Kroll.method
	public void registerQuery(KrollDict opts) {
		final ParseQuery<ParseObject> parseObject;
		// importing of callbacks:
		final KrollCallbacks krollCallbacks = new KrollCallbacks(opts);
		// importing of query proxy
		if (opts.containsKeyAndNotNull(ParselivequeryModule.QUERY)) {
			Object o = opts.get(ParselivequeryModule.QUERY);
			if (o instanceof QueryProxy) {
				parseObject = ((QueryProxy) o).query;
				registerHandler(parseObject, krollCallbacks);
			}
		}
	}

	@Kroll.method
	public void unregisterQuery(KrollDict opts) {
		// importing of callbacks:
		final KrollCallbacks krollCallbacks = new KrollCallbacks(opts);
		// unregisterHandler();
		// TODO
	}

	private void unregisterHandler(ParseQuery<ParseObject> parseqQuery,
			final KrollCallbacks krollCallbacks) {
		ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory
				.getClient();
		parseLiveQueryClient.unsubscribe(parseqQuery);

	}

	private void registerHandler(ParseQuery<ParseObject> query,
			final KrollCallbacks krollCallbacks) {
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

				// Handling all events
			}
		});
	}

	private KrollDict parseObj2KrollDict(ParseObject object) {
		KrollDict kd = new KrollDict();
		kd.put("data", object.getJSONObject("data"));
		return kd;
	}

	private String[] splitString(String foo) {
		String s = "This is a sample sentence.";
		String[] bar = s.split("\\s+");
		for (int i = 0; i < bar.length; i++) {
			bar[i] = bar[i].replaceAll("[^\\w]", "");
		}
		return bar;
	}
}