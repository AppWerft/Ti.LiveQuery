package ti.livequery;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

// This proxy can be created by calling Parselivequery.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = ParselivequeryModule.class)
public class ParseUserProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = "PLQ";

	static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

	ParseUser user = null;

	// empty Constructor
	public ParseUserProxy() {
		super();
	}

	public ParseUserProxy(ParseUser user) {
		super();
		this.user = user;
	}

	// constructor parameter import:
	@Override
	public void handleCreationArgs(KrollModule createdInModule, Object[] args) {
		if (args.length == 1 && args[0] instanceof KrollDict) {
			KrollDict opts = (KrollDict) args[0];
			if (opts.containsKeyAndNotNull("username"))
				user.setUsername(opts.getString("username"));
			if (opts.containsKeyAndNotNull("password"))
				user.setPassword(opts.getString("password"));
			if (opts.containsKeyAndNotNull("email"))
				user.setEmail(opts.getString("email"));

		}
	}

	public ParseUserProxy setUser(ParseUser user) {
		this.user = user;
		return this;
	}

	@Kroll.method
	public void set(String key, String value) {
		user.put(key, value);
	}

	@Kroll.method
	public KrollDict getUser() {
		KrollDict res = new KrollDict();
		user.put("createdAt", user.getCreatedAt().toString());
		user.put("updatedAt", user.getUpdatedAt().toString());
		user.put("email", user.getEmail());
		user.put("username", user.getUsername());
		return res;
	}

	// save to parse
	@Kroll.method
	public void signUpInBackground(
			@Kroll.argument(optional = true) final KrollFunction callback) {
		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				KrollDict res = new KrollDict();
				if (e == null) {
					res.put("success", true);
					res.put("user", getUser());
				} else {
					res.put(ParselivequeryModule.ERROR, e.getMessage());
					res.put(ParselivequeryModule.CODE, e.getCode());
					res.put(ParselivequeryModule.SUCCESS, true);
				}
				if (callback != null)
					callback.call(getKrollObject(), res);
			}
		});
	}
}