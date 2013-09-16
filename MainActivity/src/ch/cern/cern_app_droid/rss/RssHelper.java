package ch.cern.cern_app_droid.rss;

public class RssHelper {
	
	public static String URL = "url";
	public static String TITLE = "title";

	private RssHelper() {} // make it static class
	
	public static String getShortDescription(String longDesc) {
		return longDesc.replaceAll("\\<.*?\\>", "");
	}
	
}
