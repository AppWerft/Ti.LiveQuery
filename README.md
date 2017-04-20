# Ti.ParseLiveQuery

This is the Titanium module for [ParseLiveQuery](https://github.com/parse-community/ParseLiveQuery-Android)

`ParseQuery` is one of the key concepts for Parse. It allows you to retrieve `ParseObject`s by specifying some conditions, making it easy to build apps such as a dashboard, a todo list or even some strategy games. However, `ParseQuery` is based on a pull model, which is not suitable for apps that need real-time support.

Suppose you are building an app that allows multiple users to edit the same file at the same time. `ParseQuery` would not be an ideal tool since you can not know when to query from the server to get the updates.

To solve this problem, we introduce Parse LiveQuery. This tool allows you to subscribe to a `ParseQuery` you are interested in. Once subscribed, the server will notify clients whenever a `ParseObject` that matches the `ParseQuery` is created or updated, in real-time.

The module is heavy WIP and not ready for production.

## Use the module

```
var PaLiQ = require("de.appwerft.parselivequery");
PaLiQ.setEndpoint("wss://myparseinstance.com");

PaLiQ.createQuery({
	onevent : function() {
	}
});
```
Alternativly to `setEnpoint()` you can set a property in tiapp.xml with key `PARSE_ENDPOINT`.
