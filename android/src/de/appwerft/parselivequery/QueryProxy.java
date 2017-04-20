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
public class QueryProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = "PLQ";
	private ParseQuery<ParseObject> query;

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
		String name = null;
		String queryString;
		if (opts.containsKeyAndNotNull("name"))
			name = opts.getString("name");
		if (opts.containsKeyAndNotNull("name")) {
			queryString = opts.getString("query");
			String[] conditions = queryString.split("/,/");
			for (int i = 0; i < conditions.length; i++) {
				String condition = conditions[i];
				if (condition.contains("==")) {
					String[] parts = condition.split("==");
					query = query.whereEqualTo(parts[0], parts[1]);
				}
				if (condition.contains("!=")) {
					String[] parts = condition.split("!=");
					query = query.whereNotEqualTo(parts[0], parts[1]);
				}
				if (condition.contains(">")) {
					String[] parts = condition.split(">");
					query = query.whereGreaterThan(parts[0], parts[1]);
				}
				if (condition.contains("<")) {
					String[] parts = condition.split("<");
					query = query.whereLessThan(parts[0], parts[1]);
				}
				if (condition.contains(">=")) {
					String[] parts = condition.split(">=");
					query = query.whereGreaterThanOrEqualTo(parts[0], parts[1]);
				}
				if (condition.contains("<=")) {
					String[] parts = condition.split("<=");
					query = query.whereLessThanOrEqualTo(parts[0], parts[1]);
				}
				if (condition.contains("exists ")) {
					String value = condition.replace("exists ", "");
					query = query.whereExists(value);
				}
				if (condition.contains("notexists ")) {
					String value = condition.replace("notexists ", "");
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
}