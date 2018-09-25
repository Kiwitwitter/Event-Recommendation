package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; //No restrictions
	private static final String API_KEY = "EDvvl0i4NDRrMsYxdJbhA6PnTAxhGsNs";
	
	public JSONArray search(double lat, double lon, String keyword) {
		if(keyword==null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = URLEncoder.encode(keyword,"UTF-8");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		String geoHash = GeoHash.encodeGeohash(lat, lon, 9);
		
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=50", API_KEY, geoHash, keyword);
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL(URL+"?"+query).openConnection();
			connection.setRequestMethod("GET");
			
			int responseCode = connection.getResponseCode();
			System.out.println("Sending 'GET' request to URL:"+URL);
			System.out.println("Response Code:"+responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			
			String inputLine;
			while((inputLine = in.readLine())!=null) {
				response.append(inputLine);
			}
			in.close();
			
			JSONObject obj = new JSONObject(response.toString());
			if(!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				return embedded.getJSONArray("events");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();

	}
	
	private void queryAPI(double lat, double lon) {
		JSONArray events = search(lat, lon, null);
		
		try {
			for(int i = 0; i< events.length();i++) {
				JSONObject event = events.getJSONObject(i);
				System.out.println(event.toString(2));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) {
		TicketMasterAPI tmapi = new TicketMasterAPI();
		tmapi.queryAPI(29.682684, -95.295410);
	}
}
