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
import com.parse.SubscriptionHandling;

@Kroll.proxy(creatableInModule = ParselivequeryModule.class)
public class ParseQueryProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = ParselivequeryModule.LCAT;
	public ParseQuery<ParseObject> query;
	String CLASSNAME = null;

	// Constructor
	public ParseQueryProxy() {
		super();
	}

	public ParseQueryProxy(ParseQuery<ParseObject> query) {
		super();
		this.query = query;
	}

	public ParseQuery<ParseObject> getQuery() {
		return query;
	}

	@Override
	public void handleCreationArgs(KrollModule createdInModule, Object[] args) {

		String queryString;
		if (args[0] instanceof KrollDict) {
			KrollDict opts = (KrollDict) args[0];

			if (opts.containsKeyAndNotNull(ParselivequeryModule.NAME))
				CLASSNAME = opts.getString(ParselivequeryModule.NAME);
			else {
				Log.w(LCAT, "name of object is missing");
				return;
			}
			query = new ParseQuery<ParseObject>(CLASSNAME);
			if (opts.containsKeyAndNotNull(ParselivequeryModule.QUERY)) {
				queryString = opts.getString(ParselivequeryModule.QUERY);
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
						query = query.whereGreaterThanOrEqualTo(parts[0],
								parts[1]);
					}
					if (condition.contains("<=")) {
						String[] parts = condition.split("\\s*<=\\s*");
						query = query
								.whereLessThanOrEqualTo(parts[0], parts[1]);
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
		} else if (args[0] instanceof String) {
			query = new ParseQuery<ParseObject>((String) args[0]);
		}
		super.handleCreationArgs(createdInModule, args);
	}

	@Kroll.method
	public void getInBackground() {

	}

	@Kroll.method
	public void findInBackground() {

		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				KrollDict res = new KrollDict();
				if (e == null) {
					res.put(ParselivequeryModule.SUCCESS, true);
					res.put("data", objects.toArray());
				} else {
					res.put(ParselivequeryModule.SUCCESS, false);
					res.put(ParselivequeryModule.ERROR, e.getMessage());
					res.put(ParselivequeryModule.CODE, e.getCode());
					Log.e("message", "Error Loading Messages" + e);
				}
			}
		});

	}

	@Kroll.method
	public void subscribe(
			@Kroll.argument(optional = true) final KrollFunction callback) {
		ParseLiveQueryClient client = ParseLiveQueryClient.Factory.getClient();
		SubscriptionHandling<ParseObject> handling = client.subscribe(query);
		handling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
			@Override
			public void onEvents(ParseQuery<ParseObject> query,
					SubscriptionHandling.Event event, ParseObject object) {
				KrollDict kd = new KrollDict();
				kd.put("event", event.ordinal()); // int
				kd.put("eventName", event.name());
				kd.put("data", parseObj2KrollDict(object)); // int
				if (callback != null) {
					callback.call(getKrollObject(), kd);
				}
			}
		});
	}

	@Kroll.method
	public void unsubscribe() {
		ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory
				.getClient();
		parseLiveQueryClient.unsubscribe(query);
	}

	private String[] splitString(String foo) {
		String s = "This is a sample sentence.";
		String[] bar = s.split("\\s+");
		for (int i = 0; i < bar.length; i++) {
			bar[i] = bar[i].replaceAll("[^\\w]", "");
		}
		return bar;
	}

	private KrollDict parseObj2KrollDict(ParseObject object) {
		KrollDict kd = new KrollDict();
		kd.put("data", object.getJSONObject("data"));
		return kd;
	}
}