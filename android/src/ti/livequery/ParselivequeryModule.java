/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package ti.livequery;

import java.net.URI;


import java.net.URISyntaxException;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

import android.content.Context;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;

import static okhttp3.internal.platform.Platform.INFO;


@Kroll.module(name = "Parselivequery", id = "ti.livequery")
public class ParselivequeryModule extends KrollModule {

	@Kroll.constant
	public static final int LOG_LEVEL_DEBUG = Parse.LOG_LEVEL_DEBUG;
	@Kroll.constant
	public static final int LOG_LEVEL_ERROR = Parse.LOG_LEVEL_ERROR;
	@Kroll.constant
	public static final int LOG_LEVEL_VERBOSE = Parse.LOG_LEVEL_VERBOSE;
	@Kroll.constant
	public static final int LOG_LEVEL_NONE = Parse.LOG_LEVEL_NONE;
	@Kroll.constant
	public static final int LOG_LEVEL_INFO = Parse.LOG_LEVEL_INFO;
	@Kroll.constant
	public static final int LOG_LEVEL_WARNING = Parse.LOG_LEVEL_WARNING;
	
	@Kroll.constant
	public static final int INTERCEPTOR_LEVEL_BODY = HttpLoggingInterceptor.Level.BODY

	// Standard Debugging variables
	private static final String LCAT = "PLQ";
	public static final String QUERY = "query";
	ParseLiveQueryClient client;
	Context ctx;

	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;

	public ParselivequeryModule() {
		super();
		ctx = TiApplication.getInstance().getBaseContext();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {
		Log.d(LCAT, "inside onAppCreate");
		// put module init code that needs to run when the application is
		// created
	}

	@Kroll.method
	public void setLogLevel(int level) {

	}

	@Kroll.method
	public void initialize(KrollDict args) {
		String applicationId = (String) args.get("applicationId");
		String clientKey = (String) args.get("clientKey");
		String server = (String) args.get("server");
		Boolean localDatastoreEnabled = TiConvert.toBoolean(
				args.get("localDatastoreEnabled"), false);
		Parse.Configuration.Builder builder = new Parse.Configuration.Builder(
				TiApplication.getInstance().getCurrentActivity())
				.applicationId(applicationId).clientKey(clientKey)
				.server(server);

		if (localDatastoreEnabled) {
			builder = builder.enableLocalDataStore();
		}
		Parse.initialize(builder.build());
	}

	@Kroll.method
	public void createClient(@Kroll.argument(optional = true) KrollDict opts) {
		setEndpoint(opts);
	}

	@Kroll.method
	public boolean setEndpoint(@Kroll.argument(optional = true) KrollDict opts) {
		String applicationId = null;
		String clientKey = null;
		if (opts != null) {
			if (opts.containsKeyAndNotNull("applicationId")) {
				applicationId = opts.getString("applicationId");
			}
			if (opts.containsKeyAndNotNull("clientKey")) {
				clientKey = opts.getString("clientKey");
			}
			if (opts.containsKeyAndNotNull(TiC.PROPERTY_URI)) {
				try {
					@SuppressWarnings("unused")
					URI dummy = new URI(opts.getString(TiC.PROPERTY_URI));
					// first we need Parse client
					Parse.initialize(new Parse.Configuration.Builder(ctx)
							.applicationId(applicationId).clientKey(clientKey)
							.server(opts.getString(TiC.PROPERTY_URI)).build());
					// and a parseLiveQueryClient
					client = ParseLiveQueryClient.Factory.getClient();
					return true;
				} catch (URISyntaxException e) {
					e.printStackTrace();
					return false;
				}
			}

		} else
			client = ParseLiveQueryClient.Factory.getClient();
		return true;
	}

	@Kroll.method
	public Boolean isConnected() {
		if (client == null)
			return false;
		else
			return true; // not really
	}

	@Kroll.method
	public void reconnect() {
		if (client != null) {
			client.reconnect();
		}
	}

	@Kroll.method
	public void disconnect() {
		if (client != null) {
			client.disconnect();
		}
	}

	@Kroll.method
	public void connectIfNeeded() {
		if (client != null) {
			client.connectIfNeeded();
		}
	}

	@Kroll.method
	public void saveObject(KrollDict object) {
		String className = (String) object.get("className");
		KrollDict parameters = (KrollDict) object.get("parameters");
		final KrollFunction callback = (KrollFunction) object.get("callback");
		ParseObject newObject = new ParseObject(className);
		for (String key : parameters.keySet()) {
			newObject.put(key, parameters.get(key));
		}
		newObject.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				KrollDict dict = new KrollDict();
				if (e == null) {
					dict.put("success", true);

				} else {
					dict.put("success", false);
				}
				callback.call(getKrollObject(), dict);
			}
		});
	}

	@Kroll.method
	void logout() {
		ParseUser.logOut();
	}

	@Kroll.method
	void login(KrollDict opts) {
		if (client == null)
			return;
		final KrollCallbacks kcb = new KrollCallbacks(opts);
		String email = null, password = null;
		if (opts.containsKeyAndNotNull("email")) {
			email = opts.getString("email");
		}
		if (opts.containsKeyAndNotNull("password")) {
			password = opts.getString("password");
		}
		ParseUser.logInInBackground(email, password, new LogInCallback() {
			@Override
			public void done(ParseUser parseUser, com.parse.ParseException e) {
				KrollDict kd = new KrollDict();
				if (parseUser != null)
					if (kcb.onSuccess != null) {
						kcb.onSuccess.call(getKrollObject(), kd);
					} else {
						if (kcb.onError != null) {
							kcb.onError.call(getKrollObject(), kd);
						}
					}
			}
		});
	}

	@Kroll.method
	void loginAnonymous(KrollDict opts) {
		if (client == null)
			return;
		final KrollCallbacks kcb = new KrollCallbacks(opts);
		ParseAnonymousUtils.logIn(new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				KrollDict res = new KrollDict();
				if (e != null) {
					res.put("user", user.toString());
					kcb.onSuccess.call(getKrollObject(), res);
				} else {
					kcb.onError.call(getKrollObject(), res);
				}
			}
		});
	}

	@Kroll.method
	public void clearAllCachedResults() {
		ParseQuery.clearAllCachedResults();
	}
}
