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
		if (opts.containsKeyAndNotNull("name"))
			name = opts.getString("name");
		super.handleCreationDict(opts);
		this.query = new ParseQuery(name);
	}

	@Kroll.method
	public QueryProxy add(String condition) {
		return new QueryProxy(query.whereEqualTo("key", "value"));
	}
}