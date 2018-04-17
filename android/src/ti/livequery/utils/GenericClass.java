package ti.livequery.utils;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("GenericClass")
public class GenericClass extends ParseObject {
	public static final String USER_ID_KEY = "userId";
	public static final String BODY_KEY = "body";

	public String getCLASSNAME() {
		return getString("CLASSNAME");
	}

	public void setCLASSNAME(String CLASSNAME) {
		put("CLASSNAME", CLASSNAME);
	}

	public String getUserId() {
		return getString(USER_ID_KEY);
	}

	public String getBody() {

		return getString(BODY_KEY);
	}

	public void setUserId(String userId) {

		put(USER_ID_KEY, userId);
	}

	public void setBody(String body) {
		put(BODY_KEY, body);

	}
}