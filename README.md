# Ti.ParseLiveQuery

This is the Titanium module for [ParseLiveQuery](https://github.com/parse-community/ParseLiveQuery-Android)

`ParseQuery` is one of the key concepts for Parse. It allows you to retrieve `ParseObject`s by specifying some conditions, making it easy to build apps such as a dashboard, a todo list or even some strategy games. However, `ParseQuery` is based on a pull model, which is not suitable for apps that need real-time support.

Suppose you are building an app that allows multiple users to edit the same file at the same time. `ParseQuery` would not be an ideal tool since you can not know when to query from the server to get the updates.

To solve this problem, we introduce Parse LiveQuery. This tool allows you to subscribe to a `ParseQuery` you are interested in. Once subscribed, the server will notify clients whenever a `ParseObject` that matches the `ParseQuery` is created or updated, in real-time.

The module is heavy WIP and not ready for production.

## Use the module

```javascript
var Parse = require("de.appwerft.parselivequery");

Parse.setEndpoint({
	uri :"wss://myparseinstance.com"), 
	applicationId : APPLICATION_ID
	clientKey : CLIENT_KEY
});	
Parse.loginAnonymous({
	onsuccess : WorkingWithParse,
	onerror : function(){}
});
Parse.login({
	email: "putin@gulag.org",
	password : "KGB",
	onsuccess : WorkingWithParse,
	onerror : function(){}
});
Parse.logout();

function WorkingWithParse() {
	var bird = Parse.createObject("Bird");
	bird.save({
		data : {
			age : 2,
			color : "red",
			name : "Rotkehlchen"
		},
		onsuccess : function() {},
		onerror : function(){}
	});
	bird.save({
		data : {
			age : 5,
			color : "blue",
			name : "Blaumeise"
		}
		onsuccess : function() {},
		onerror : function(){}
	});
	
	var query = Parse.createQuery({
		name : "Bird",
		query : "age>1,color==brown,orderby desc age"
	});
	
	// pull request:
	query.find({
		onload : function() {},
		onerror : function(){}
	});
	// live request:
	query.register({
		onchange : function() {},
		onerror : function(){}
	});
	query.unregister();
}

```
