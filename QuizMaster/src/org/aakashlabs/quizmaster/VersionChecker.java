package org.aakashlabs.quizmaster;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.*;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class VersionChecker {
	 
   
    //public static final String INFO_FILE = "https://www.dropbox.com/s/a0x51a60gka1vjg/autoupdate_info.txt";
    
  //  public static final String VER_INFO_URL= "https://www.dropbox.com/s/55tvwemmvrvsi8u/version_info.txt";
    public String dlink="https://dl.dropboxusercontent.com/s/a0x51a60gka1vjg/autoupdate_info.txt?token_hash=AAEHcHKVt2AAs9gqJ5IzRTXLaOxiBn9e_uQVvMSwpDXp3w&dl=1";
    public String VersionInfo="https://dl.dropboxusercontent.com/s/a0x51a60gka1vjg/autoupdate_info.txt?token_hash=AAEHcHKVt2AAs9gqJ5IzRTXLaOxiBn9e_uQVvMSwpDXp3w&dl=1";
    private int currentVersionCode;
   
    private String currentVersionName;
 
    private int latestVersionCode;
  
    private String latestVersionName;
 
    private String downloadURL;
    
  public  String data=null;
  JSONObject jsonObj=null;
 
   private Context appcontext;
   public VersionChecker(Context context)
   {
	   appcontext=context;
   }
   public void checkNewVersion()
   {
	   try
       {
       	//String data = downloadJsonFile(VersionInfo);//downloadHttp(newApkUrl);
       //	new DownloadFileFromURL().execute(VersionInfo);;
       }
       catch(Exception e)
       {
       	Log.e("VersionCheck", "download json file fail.."+e.getMessage());
       }
   }
    public void getData(Context context) {
    	appcontext=context;
        try{
           
            // Download package info
            URL newApkUrl=new URL(VersionInfo);
            
           
            
            try
            {
            	/*JSONTokener jtok=new JSONTokener(data);            	
            	JSONObject jsonObject = (JSONObject) jtok.nextValue();

            	
                 jsonObj= jsonObject.getJSONObject("apkVersionCheck");*/
            	jsonObj=getJSONfromURL(VersionInfo);
            }
            catch(Exception je)
            {
            	Log.e("VersionUpdate", "unable to create json object"+je.getMessage());
            	return;
            }
            
           /* if((data==null) || data.length()==0)
            {
            	return;
            }*/
           // Log.d("Data download", data.toString());
           
            
            if(jsonObj==null)
            {
            	Log.d("Data download", "json null");
            	return;
            	
            }
            
            latestVersionCode = jsonObj.getInt("versionCode");
            latestVersionName = jsonObj.getString("versionName");
            downloadURL = jsonObj.getString("downloadURL");
            
            Log.d("AutoUpdate", "upto this work");
        }catch(JSONException e){
            Log.e("AutoUpdate", "error in JSON", e);
        }catch(IOException e){
            Log.e("AutoUpdate", "Io exception", e);
        }
    }
    public void getInstalledVerInfo(Context context)
    {
    	appcontext=context;
    	try
    	{
    	 PackageInfo pckginfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
         currentVersionCode = pckginfo.versionCode;
         currentVersionName = pckginfo.versionName;
         
        
    	}
    	catch(Exception e)
    	{
    		Log.e("Install version info",""+e.getMessage());
    	}

    }
   
    public boolean isNewVersionAvailable() {
        return getLatestVersionCode() > getCurrentVersionCode();
    }
 
   
    public int getCurrentVersionCode() {
        return currentVersionCode;
    }
 
    
    public String getCurrentVersionName() {
        return currentVersionName;
    }
 
    
    public int getLatestVersionCode() {
        return latestVersionCode;
    }
 
    
    public String getLatestVersionName() {
        return latestVersionName;
    }
 
    
    public String getDownloadURL() {
        return downloadURL;
    }
 
    
    private static String downloadHttp(URL url) throws IOException {
        HttpURLConnection c = (HttpURLConnection)url.openConnection();
        c.setRequestMethod("GET");
        c.setReadTimeout(15 * 1000);
        c.setUseCaches(false);
        c.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while((line = reader.readLine()) != null){
            stringBuilder.append(line + "\n");
        }
        return stringBuilder.toString();
    }
    
    public String downloadJsonFile(String VersionInfo) throws IOException
    {
    	 StringBuilder builder = new StringBuilder();
    	    HttpClient client = new DefaultHttpClient();
    	    HttpGet httpGet = new HttpGet(VersionInfo);
    	    try {
    	      HttpResponse response = client.execute(httpGet);
    	      StatusLine statusLine = response.getStatusLine();
    	      int statusCode = statusLine.getStatusCode();
    	      if (statusCode == 200) {
    	        HttpEntity entity = response.getEntity();
    	        InputStream content = entity.getContent();
    	        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
    	        String line;
    	        while ((line = reader.readLine()) != null) {
    	          builder.append(line);
    	        }
    	      } else {
    	        Log.e(ParseException.class.toString(), "Failed to download file");
    	      }
    	    } catch (ClientProtocolException e) {
    	      e.printStackTrace();
    	    } catch (IOException e) {
    	      e.printStackTrace();
    	    }
    	    return builder.toString();
    	  }
	
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

    	ProgressDialog progressDialog;
    	
    	 @Override
         protected void onPreExecute() {
             super.onPreExecute();
           /*  progressDialog = new ProgressDialog(appcontext);
             progressDialog.setMessage("Checking Version");
             progressDialog.setCancelable(false);
             progressDialog.setIndeterminate(true);
             progressDialog.show();*/
         }
    	
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.e("VersionCheck", "download json inprocess");
			StringBuilder builder = new StringBuilder();
    	    HttpClient client = new DefaultHttpClient();
    	    HttpGet httpGet = new HttpGet(VersionInfo);
    	    
    	    try {
    	      HttpResponse response = client.execute(httpGet);
    	      Log.e("VersionCheck", "download start");
    	      StatusLine statusLine = response.getStatusLine();
    	      int statusCode = statusLine.getStatusCode();
    	      
    	      if (statusCode == 200) {
    	        HttpEntity entity = response.getEntity();
    	        InputStream content = entity.getContent();
    	        BufferedReader reader = new BufferedReader(new InputStreamReader(content,"utf-8"), 8);
    	        String line;
    	        while ((line = reader.readLine()) != null) {
    	          builder.append(line);
    	        }
    	        data=builder.toString();
    	        Log.e("VersionCheck", "download json complete");
    	        Log.d("Data download", data.toString());
    	        return builder.toString();
    	      } else {
    	        Log.e(ParseException.class.toString(), "Failed to download file");
    	      }
    	    } catch (ClientProtocolException e) {
    	      e.printStackTrace();
    	    } catch (IOException e) {
    	      e.printStackTrace();
    	    }
    	    
			return null;
		}
		 @Override
		    protected void onPostExecute(String result) {
		        super.onPostExecute(result);
		      //  progressDialog.dismiss();
		    }
    }
    
    
    public static JSONObject getJSONfromURL(String url){

    	//initialize
    	InputStream is = null;
    	String result = "";
    	JSONObject jArray = null;
    	JSONObject jobj=null;
    	//http post
    	/*try{
    		HttpClient httpclient = new DefaultHttpClient();
    		
    		HttpPost httppost = new HttpPost(url);
    		httppost.setHeader("Content-type", "application/json");
    		httppost.setHeader("Accept", "application/json");
    		HttpResponse response = httpclient.execute(httppost);
    		HttpEntity entity = response.getEntity();
    		is = entity.getContent();

    	}catch(Exception e){
    		Log.e("log_tag", "Error in http connection "+e.toString());
    	}

    	//convert response to string
    	try{
    		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
    		StringBuilder sb = new StringBuilder();
    		String line = null;
    		while ((line = reader.readLine()) != null) {
    			sb.append(line + "\n");
    		}
    		is.close();
    		result=sb.toString();
    	}catch(Exception e){
    		Log.e("log_tag", "Error converting result "+e.toString());
    	}

    	//try parse the string to a JSON object
    	try{
            	jArray = new JSONObject(result);
    	}catch(JSONException e){
    		Log.e("log_tag", "Error parsing data "+e.toString());
    	}

    	return jArray;
    } */
    	DefaultHttpClient httpClient = new DefaultHttpClient();
    	HttpGet httpGet = new HttpGet(url);
    	httpGet.setHeader("Content-type", "application/json");
		httpGet.setHeader("Accept", "application/json");

    	try {
    	    HttpResponse httpResponse = httpClient.execute(httpGet);
    	    HttpEntity httpEntity = httpResponse.getEntity();

    	    int status = httpResponse.getStatusLine().getStatusCode();

    	    if (status == HttpStatus.SC_OK) {
    	        String jsonString = EntityUtils.toString(httpEntity);
    	        try {
    	            JSONObject jsonObject = new JSONObject(jsonString.substring(jsonString.indexOf("{"), jsonString.lastIndexOf("}") + 1));
    	            jobj=jsonObject;
    	        } catch (JSONException e) {
    	            e.printStackTrace();
    	        }
    	    }
    	} catch (ClientProtocolException e) {
    	    e.printStackTrace();
    	    Log.e("log_tag", "Error parsing data "+e.toString());
    	} catch (IOException e) {
    	    e.printStackTrace();
    	    Log.e("log_tag", "Error parsing data "+e.toString());
    	}
    	 return jobj;
    }
   
}
    
    
