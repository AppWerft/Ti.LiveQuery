# Ti.LiveQuery

This is the Titanium module for [ParseLiveQuery](https://github.com/parse-community/ParseLiveQuery-Android)

`ParseQuery` is one of the key concepts for Parse. It allows you to retrieve `ParseObject`s by specifying some conditions, making it easy to build apps such as a dashboard, a todo list or even some strategy games. However, `ParseQuery` is based on a pull model, which is not suitable for apps that need real-time support.

Suppose you are building an app that allows multiple users to edit the same file at the same time. `ParseQuery` would not be an ideal tool since you can not know when to query from the server to get the updates.

To solve this problem, we introduce Parse LiveQuery. This tool allows you to subscribe to a `ParseQuery` you are interested in. Once subscribed, the server will notify clients whenever a `ParseObject` that matches the `ParseQuery` is created or updated, in real-time.

The module is heavy WIP and not ready for production.

# Module

## Constants

* LOG\_LEVEL_NONE
* LOG\_LEVEL_INFO
* LOG\_LEVEL_WARNING
* LOG\_LEVEL_ERROR
* LOG\_LEVEL_VERBOSE
* LOG\_LEVEL_DEBUG	
* INTERCEPTOR\_LEVEL_BODY
* INTERCEPTOR\_LEVEL_BASIC
* INTERCEPTOR\_LEVEL_HEADERS
* INTERCEPTOR\_LEVEL_NONE
* ACCOUNT\_ALREADY_LINKED
* CONNECTION\_FAILED
* EMAIL\_MISSING
* EMAIL\_NOT_FOUND
* EMAIL\_TAKEN
* INVALID\_EMAIL_ADDRESS
* USERNAME\_TAKEN
* PASSWORD\_MISSING
* PASSWORD\_TAKEN
 


## Initializing the module

```javascript
var Parse = require("ti.livequery");

// Use for troubleshooting -- remove this line for production
Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

// Use for monitoring Parse  traffic        
// Can be INTERCEPTOR_LEVEL_BODY, INTERCEPTOR_LEVEL_HEADERS (see constants)
Parse.setHttpLoggingInterceptorLevel(Parse.INTERCEPTOR_LEVEL_BODY)
 
// set applicationId, and server server based on the values in the sever (i.e. Heroku) settings.
// clientKey is not needed unless explicitly configured
// any network interceptors must be added with the Configuration Builder given this syntax
Parse.initialize({
	applicationId : "myAppId", // should correspond to APP_ID env variable
    clientKey : null,  // set explicitly unless clientKey is explicitly configured on Parse server
    uri : "https://my-parse-app-url.herokuapp.com/parse/"
    });
```
We also need to make sure to set the application instance above as the android:name for the application within the AndroidManifest.xml. This change in the manifest determines which application class is instantiated when the app is launched and also adding the application ID metadata tag:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.parsetododemo"
    android:versionCode="1"
    android:versionName="1.0" >
    <application
        <!-- other attributes here -->
        android:name=".ParseApplication">
    </application>
</manifest>
```
### Setup network permissions

We also need to add a few important network permissions to the AndroidManifest.xml:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.parsetododemo"
    android:versionCode="1"
    android:versionName="1.0" >
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
</manifest>
```
### Saving ParseObjects

```javascript
Parse.initialize(...);
var Cat = Parse.createParseObject("Cat");
Cat.saveInBackground({
		tail : "black",
		legs : 4
	},
	function(event){
		console.log(event);
	})
});	
```

### Querying ParseObjects

### Objects By Id

If you have the objectId, you can retrieve the whole ParseObject using a ParseQuery:

```javascript
LQ.createParseQuery("Cat").getInBackground("aFuEsvjoHt",function(e){
	console.log(e)
})
```
### Objects By Query Conditions

```javascript
var query = LQ.createParseQuery({
	name : "Cat",
	query : "tail==black,age>3,orderby age asc"
});
query.findInBackground(function(e){
	console.log(e)
})
```
### Objects Created by a particular user

```javascript
var query = LQ.createParseQuery({
	name : "Cat",
	query : "owner==" + LQ.getCurrentUser()
});
query.findInBackground(function(e){
	console.log(e)
})

```

### Working with Users

```javascript
// Create the ParseUser
var user = LQ.createParseUser({
	username : "joestevens",
	password : "verysecret",
	email : "x@y.com"
});
// Set custom properties
user.set("phone", "+49 40 60812460");
// Invoke signUpInBackground
user.signUpInBackground(function(e) {
	if (e.success) {
	   
	   // Hooray! Let them use the app now.
    } else {
      // Sign up didn't succeed. Look at the ParseException
      // to figure out what went wrong
    }

});
```
This call will asynchronously create a new user in your Parse App. Before it does this, it checks to make sure that both the username and email are unique.

```javascript
LQ.loginInBackground("joestevens", "secret123", function(e){
	console.log(e);
	// returns success, error, user
});
```
If the credentials are correct, the ParseUser will be passed back accordingly. You can now access the cached current user for your application at any time in order to determine the session status:

```javascript
var user = LQ.getCurrentUser();
if (user != null) {
  // do stuff with the user
  user.set("phone","+494060812460");
} else {
	
  // show the signup or login screen
}
```
Alternativly:

```javascript
LQ.loginAnonymous({
	onsuccess : WorkingWithParse,
	onerror : function(){}
});
```
and in the end:

```javascript
LQ.logout();
```

### Live Queries

```javascript
var query = LQ.createParseQuery({
	name : "Cat",
	query : "color==yellow,age>1"
});
query.subscribe(function(e){
	console.log(e)
})

// maybe later:
query.unsubscribe();
```



## Methods 

### initialize(dictionary);
* applicationId (String)
* clientKey (String)
* uri (URL/Endpoint)
* localDatastoreEnabled (true/false)
 
### setHttpLoggingInterceptorLevel(int)
 
see constants above

### setLoggingLevel(int)
see constants above
 
### createParseObject(classmame)
Returns an ParseObject  
 

### reconnect()

### disconnect()

### connectIfNeeded()



### loginAnonymous(CALLBACKFUNCTION)

### logInInBackground(USERNAME,PASSWORD,CALLBACKFUNCTION)

## Methods of ParseObject