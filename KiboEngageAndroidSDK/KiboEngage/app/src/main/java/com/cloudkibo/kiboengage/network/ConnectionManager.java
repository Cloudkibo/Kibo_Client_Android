package com.cloudkibo.kiboengage.network;


import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class ConnectionManager {
	
	
    /////////////////////////////////////////////////////////////////////
    // VARIABLES                                                       //
    /////////////////////////////////////////////////////////////////////

    static InputStream is = null;
    static JSONObject jObj = null;
    static JSONArray jArr = null;
    static String json = "";
    
    
    
	/////////////////////////////////////////////////////////////////////
	// Constructor                                                     //
	/////////////////////////////////////////////////////////////////////

    public ConnectionManager() {

    }
    
    
    
    
    
    
	/////////////////////////////////////////////////////////////////////
	// Sending HTTP POST Request to URL                                //
	/////////////////////////////////////////////////////////////////////

    public JSONObject getDataFromServer(String userDataURL, String appId, String clientId, String appSecret) {
		
    	// Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(userDataURL);
            httpGet.addHeader("kibo-app-id", appId);
            httpGet.addHeader("kibo-client-id", clientId);
            httpGet.addHeader("kibo-app-secret", appSecret);

            HttpResponse httpResponse = httpClient.execute(httpGet);

            if(httpResponse.getStatusLine().getStatusCode() == 401){
                return null;
            }

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("JSON", json);
        } catch (Exception e) {
            Log.d("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.d("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;
	}
    
    
    
    
    
    
    public JSONArray getArrayFromServer(String userDataURL, String appId, String clientId, String appSecret) {
		
    	// Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(userDataURL);
            httpGet.addHeader("kibo-app-id", appId);
            httpGet.addHeader("kibo-client-id", clientId);
            httpGet.addHeader("kibo-app-secret", appSecret);

            HttpResponse httpResponse = httpClient.execute(httpGet);

            if(httpResponse.getStatusLine().getStatusCode() == 401){
                return null;
            }

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("JSON", json);
        } catch (Exception e) {
            Log.d("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jArr = new JSONArray(json);
        } catch (JSONException e) {
            Log.d("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jArr;
	}
    
    
    
    
    
    
    
    public JSONArray sendArrayToServer(String userDataURL, String appId, String clientId, String appSecret, List<NameValuePair> params) {
		
    	// Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(userDataURL);
            httpPost.addHeader("kibo-app-id", appId);
            httpPost.addHeader("kibo-client-id", clientId);
            httpPost.addHeader("kibo-app-secret", appSecret);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);

            if(httpResponse.getStatusLine().getStatusCode() == 401){
                return null;
            }

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("JSON", json);
        } catch (Exception e) {
            Log.d("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jArr = new JSONArray(json);
        } catch (JSONException e) {
            Log.d("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jArr;
	}
    
    
    
    
    
    
    public JSONObject sendObjectToServer(String userDataURL, String appId, String clientId, String appSecret, List<NameValuePair> params) {
		
    	// Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(userDataURL);
            httpPost.addHeader("kibo-app-id", appId);
            httpPost.addHeader("kibo-client-id", clientId);
            httpPost.addHeader("kibo-app-secret", appSecret);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);

            if(httpResponse.getStatusLine().getStatusCode() == 401){
                return null;
            }

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            try {
				return new JSONObject().put("Error", "No Internet");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("JSON", json);
        } catch (Exception e) {
            Log.d("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.d("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;
	}




    public JSONObject sendJSONObjectToServer(String userDataURL, String appId, String clientId, String appSecret, JSONObject jsonD) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(userDataURL);
            httpPost.addHeader("kibo-app-id", appId);
            httpPost.addHeader("kibo-client-id", clientId);
            httpPost.addHeader("kibo-app-secret", appSecret);
            httpPost.setHeader("Content-type", "application/json");

            StringEntity params = new StringEntity(jsonD.toString());
            httpPost.setEntity(params);

            HttpResponse httpResponse = httpClient.execute(httpPost);

            if(httpResponse.getStatusLine().getStatusCode() == 401){
                return null;
            }

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                return new JSONObject().put("Error", "No Internet");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("JSON", json);
        } catch (Exception e) {
            Log.d("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.d("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;
    }





    public JSONObject sendObjectToServerNoAuth(String userDataURL, List<NameValuePair> params) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(userDataURL);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);

            if(httpResponse.getStatusLine().getStatusCode() == 401){
                return null;
            }

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                return new JSONObject().put("Error", "No Internet");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("JSON", json);
        } catch (Exception e) {
            Log.d("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.d("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;
    }
    

    
    private class ParseComError implements Serializable {
        int code;
        String error;
    }


}
