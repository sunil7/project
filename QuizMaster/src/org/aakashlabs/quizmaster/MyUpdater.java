package org.aakashlabs.quizmaster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class MyUpdater{

	private Context context;
	
	public void setContext(Context contextf){
	    context = contextf;
	}
	public void Update(String apkurl){
	      try {
	            URL url = new URL(apkurl);
	            HttpURLConnection c = (HttpURLConnection) url.openConnection();
	            c.setRequestMethod("GET");
	            c.setDoOutput(true);
	            c.connect();

	            String PATH = Environment.getExternalStorageDirectory() + "/download/";
	            File file = new File(PATH);
	            file.mkdirs();
	            File outputFile = new File(file, "QuizMaster.apk");
	            FileOutputStream fos = new FileOutputStream(outputFile);

	            InputStream is = c.getInputStream();

	            byte[] buffer = new byte[1024];
	            int len1 = 0;
	            while ((len1 = is.read(buffer)) != -1) {
	                fos.write(buffer, 0, len1);
	            }
	            fos.close();
	            is.close();//till here, it works fine - .apk is download to my sdcard in download file

	            Intent intent = new Intent(Intent.ACTION_VIEW);
	            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "QuizMaster.apk")), "application/vnd.android.package-archive");
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            context.startActivity(intent);  

	        } catch (IOException e) {
	            Toast.makeText(context.getApplicationContext(), "Update error!"+e.getMessage(), Toast.LENGTH_LONG).show();
	        }
	  }  
}
