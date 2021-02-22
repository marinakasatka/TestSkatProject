package com.in28minutes.erst.webservises.restfulwebservices;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SkatService{
	private static String token;
	private static String sum;
	private static String urlString = "http://13.74.31.101/api/points";
	protected SkatService() {}
	public static void main(String[] args){
		try {		
			getRequest();
			if(token!= null && sum != null) {
			    postRequest();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	private static void getRequest() throws IOException{
		token = null;
		sum = null;
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		if(responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			reader.close();
		
			System.out.println( "GET RESULT: " + builder.toString());
			if(!builder.isEmpty()) {
				unpackGetReplay(builder);
			}else {
				System.out.println("Replay is emty!");
			}
		}else {
			System.out.println("DON't work");
		}
	}


	private static void unpackGetReplay(StringBuilder buffer) {
		JSONParser parser = new JSONParser();  
		try {
			JSONObject json = (JSONObject) parser.parse(buffer.toString());
			String s = (String) json.get("token");
			if(s != null) { 
				token = s;
			}else {
				System.out.println("Missing token key!");
			}

			ArrayList<Long[]> list = new ArrayList<Long[]>();			
			JSONArray jsonArray = (JSONArray) json.get("points");
			Iterator<JSONArray> iterator = jsonArray.iterator();
			while(iterator.hasNext()) {
				JSONArray array = iterator.next();
				Iterator<?> it = array.iterator();
				Long[] longArray  =  new Long[2];
				int index = 0;
				while(it.hasNext()) {
					longArray[index] = (long) it.next();
					index++;
				}

				list.add(longArray);
			}
			sum = SumCalculater.getInstance().getCalculateSum(list);

		} catch (ParseException e) {
			System.out.println("Invalid replay!");
			e.printStackTrace();
		}  
	}


	@SuppressWarnings("unchecked")
	private static void postRequest() throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setDoOutput(true);
		con.setDoInput(true);
		
		JSONObject details = new JSONObject();
		details.put("token", token);
		details.put("points", sum);

		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(details.toString());
		wr.flush();

		if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			reader.close();
			
			if(!builder.isEmpty()) {
				JSONParser parser = new JSONParser();  
				try {
					JSONObject json = (JSONObject) parser.parse(builder.toString());
					boolean isSuccess = (boolean) json.get("success");
					System.out.println("Post RESULT: " + builder.toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			}

		}
	}
}
