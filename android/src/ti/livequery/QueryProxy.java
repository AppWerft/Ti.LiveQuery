package ti.livequery;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;

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

	public ParseQuery<ParseObject> getQuery() {
		return query;
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
		query = new ParseQuery<ParseObject>(className);
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

	private String[] splitString(String foo) {
		String s = "This is a sample sentence.";
		String[] bar = s.split("\\s+");
		for (int i = 0; i < bar.length; i++) {
			bar[i] = bar[i].replaceAll("[^\\w]", "");
		}
		return bar;
	}
}