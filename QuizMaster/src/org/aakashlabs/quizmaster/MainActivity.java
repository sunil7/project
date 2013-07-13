package org.aakashlabs.quizmaster;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.aakashlabs.quizmaster.DataBaseHelper;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button start,result,exit,update;
	SQLiteDatabase mydbcreate;
	private String DATABASE_PATH = "/data/data/org.aakashlabs.quizmaster/databases/";
    public static final String DATABASE_NAME = "myQuiz.sqlite";
	public DataBaseHelper dbh;
	TextView updateLabel;
	 public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	 private ProgressDialog mProgressDialog;
	 DownloadManager manager;
	private MyUpdater mup;
	private VersionChecker vcheck;
	public String apkurl="https://dl.dropboxusercontent.com/s/s8w534lnsv8ac02/QuizMaster.apk?token_hash=AAHN0oMiCATVT49gn4U2oVvBLjuHW8m1YY0iTF9AQBaaXw&dl=1";
	private BroadcastReceiver receiver_complete;
	static boolean  flag_download=false;
	private boolean downloadStarts=false;
	private  static boolean downloadRunning=false;
	private boolean downloadComplete=false;
	
	private DownloadManager downloadManager;
	 private long downloadReference=0;
	private String downloadFileName;
	final String Download_ID = "DOWNLOAD_ID";
	Notification notification;
	NotificationManager notificationMgr;
	 
	 SharedPreferences preferenceManager;
	 List<Long> mul_download_req_id= new ArrayList<Long>();
	 private int count_ids=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 if( Build.VERSION.SDK_INT >= 9){
	            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	            StrictMode.setThreadPolicy(policy); 
	     }
		
		start=(Button)findViewById(R.id.Mbutton2);
		result=(Button)findViewById(R.id.Mbutton3);
		exit=(Button)findViewById(R.id.Mbutton4);
		update=(Button)findViewById(R.id.Mbutton1);
		updateLabel=(TextView)findViewById(R.id.MtextView1);
		
		if(mul_download_req_id.size()>0)mul_download_req_id.clear();
		// declare the dialog as a member field of your activity
		ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
		
		vcheck=new VersionChecker(getApplicationContext());
//---------------------------------------------------------------------------------------------------------------------------------------
		//set filter to only when download notification click and register broadcast receiver
		  IntentFilter filter_click = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
		  registerReceiver(downloadReceiver, filter_click);
		   
		  IntentFilter filter_complete = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		  registerReceiver(downloadCompleteReceiver, filter_complete);
		  
		  preferenceManager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//------------------------------------------------------------------------------------------------------------------------------------
		 
//-----------------------------------------------------------------------------------------------------------------------------
		try {
				dbh=new DataBaseHelper(this.getApplicationContext());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		try
		{
			loadAssetDB();
			
		}
		catch(Exception e)
		{
			Toast.makeText(this,"Asset empty"+e.getMessage(),Toast.LENGTH_LONG).show();
			
		}
		
		start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent myintent = new Intent(getApplicationContext(),GetInActivity.class);
				myintent.putExtra("sid", "1");
				startActivity(myintent); 
				
			}

		});
		
		result.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent myintent = new Intent(getApplicationContext(),PerformanceActivity.class);
				myintent.putExtra("sid", "2");
				startActivity(myintent); 
				
			}

		});
		
		exit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showConfirmExit();
				//	finish();
		        // System.exit(0);
				
			}

		});
		
		update.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isNetworkConnectionAvailable())
				{
					
					/*updateLabel.setText("Update available.");
					myDownloadManager();*/
					try
					{
					vcheck.getInstalledVerInfo(getApplicationContext());
					Log.e("Current version", "installed ver:"+vcheck.getCurrentVersionName());
					vcheck.checkNewVersion();
					vcheck.getData(getApplicationContext());
					//Log.e("data:",vcheck.data.toString());
					Log.e("Latest version", "Latest ver:"+vcheck.getLatestVersionName());
					
					if(vcheck.isNewVersionAvailable())
					{
						apkurl=vcheck.getDownloadURL();
						
					updateLabel.setText("Latest version: "+vcheck.getLatestVersionName()+" available");
					prompt2download();
					
					}
					else
					{
						updateLabel.setText("App already upto date...");
					}
					}
					catch(Exception e)
					{
						Log.e("Update","something going wrong"+e.getMessage());
					}
					
				}
				else
				{
					updateLabel.setText("Network not available.");
					Toast.makeText(getBaseContext(),"Check internet connection and then try again", Toast.LENGTH_LONG).show();
				}
					
				
			}

		});

	}
	 
	public void loadAssetDB()
	{
		// TODO Auto-generated method stub
		dbh.initAll();
			}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
 
	 //check network connection
	 boolean isNetworkConnectionAvailable() {  
		    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo info = cm.getActiveNetworkInfo();     
		    if (info == null) return false;
		    State network = info.getState();
		    return (network == NetworkInfo.State.CONNECTED );//|| network == NetworkInfo.State.CONNECTING);
		}
//-----------------------------------------------------------------------------------------------------------------------------------------------
	private void startsNewDownload()
	{
	
	 downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
	   Uri Download_Uri = Uri.parse(apkurl);
	   DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
	    
	   //Restrict the types of networks over which this download may proceed.
	   request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
	   //Set whether this download may proceed over a roaming connection.
	   request.setAllowedOverRoaming(false);
	   //Set the title of this download, to be displayed in notifications (if enabled).
	   request.setTitle("QuizMaster");
	   //Set a description of this download, to be displayed in notifications (if enabled)
	   request.setDescription("New Version of App is downloading..");
	   //Set the local destination for the downloaded file to a path within the application's external files directory
	   request.setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS,"QuizMaster.apk");
	 
	   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		     request.allowScanningByMediaScanner();
		     request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		 }
	   //Enqueue a new download and same the referenceId
	   Log.e("Start download","(outside if)download Running:"+downloadRunning);
	   if(downloadRunning==false)
	   {
		   setDownloadRunningFlag(true);
	   downloadReference = downloadManager.enqueue(request);
	   mul_download_req_id.add(count_ids++,downloadReference);
	   SharedPreferences.Editor prefEdit = preferenceManager.edit();
	   prefEdit.putLong(Download_ID, downloadReference);
	   prefEdit.commit();
	   Log.e("Start download","(inside if)download Running:"+downloadRunning);
	   }
	   else
	   {
		   setDownloadRunningFlag(true);
		   downloadRunning=true;
		   
	   }

	}
	
	private void dispDownload()
	{
		 Intent intent = new Intent();
		   intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
		   startActivity(intent);
	}
	
	private void checkStatus()
	{
		Query myDownloadQuery = new Query();
		   //set the query filter to our previously Enqueued download
		   myDownloadQuery.setFilterById(preferenceManager.getLong(Download_ID, 0));//downloadReference);
		 
		   //Query the download manager about downloads that have been requested.
		   Cursor cursor = downloadManager.query(myDownloadQuery);
		   if(cursor.moveToFirst()){
		   
			   //column for status
			   int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
			   int status = cursor.getInt(columnIndex);
			   //column for reason code if the download failed or paused
			   int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
			   int reason = cursor.getInt(columnReason);
			   //get the download filename
			   int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
			   String filename = cursor.getString(filenameIndex);
			   downloadFileName=filename;
			   String statusText = "";
			   String reasonText = "";
			   
			   switch(status){
			   case DownloadManager.STATUS_FAILED:
			    statusText = "STATUS_FAILED";
			    
			    
			   case DownloadManager.STATUS_PENDING:
				    statusText = "STATUS_FAILED";
			    switch(reason){
			    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
			        reasonText = "ERROR_FILE_ALREADY_EXISTS";
			        break;
			    
			    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
			        reasonText = "PAUSED_WAITING_FOR_NETWORK";
			        break;
			        
			    case DownloadManager.PAUSED_UNKNOWN:
			        reasonText = "PAUSED Unknown";
			        break;  
			   
			    }
			  
			   
			   case DownloadManager.STATUS_RUNNING:
			    	   statusText = "STATUS_RUNNING";
			    	   downloadRunning=true;
			    	   break;
			   case DownloadManager.STATUS_SUCCESSFUL:
			    	   statusText = "STATUS_SUCCESSFUL";
			    	 //set filter to only when download successfully complete and register broadcast receiver
			 		  IntentFilter filter_success = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
			 		  registerReceiver(downloadCompleteReceiver, filter_success);
			 		 // downloadedFile();
			    	  // dispDownload();
			 		 // cancelDownload();
			    	   reasonText = "Filename:\n" + filename;
			    	   break;
			    default:statusText=""+status;
			    		reasonText=""+reason;
			   }
			   Toast toast = Toast.makeText(this,statusText + "\n" +reasonText,Toast.LENGTH_LONG);
			   toast.setGravity(Gravity.TOP, 25, 400);
			   toast.show();
			 
				
		   }
		  
	}
	private void cancelpandingDownload()
	{
		
		if(downloadReference==0)
		{
			return;
		}
		for(int i=(int) (downloadReference-5);i<(downloadReference+5);i++)
		{
			try
			{
				
				DownloadManager dm =(DownloadManager)getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);             
			    dm.remove(i);
			    downloadRunning=false;
			}
			catch(Exception e)
			{
				Log.e("Brodcast_QuizMaster", "download not found with id:"+i+""+e.getMessage());
			}
		}
		
		
	}
	private void cancelDownload()
	{
		
		long downloadID = preferenceManager.getLong(Download_ID, 0);
		downloadManager.remove(downloadReference);
		downloadManager.remove(downloadID);
	}
	private void cancelAllDownload()
	{
		int i=0;
		for(i=0;i< mul_download_req_id.size();i++)
		{
			long downloadID =  mul_download_req_id.get(i);
			Log.e("Brodcast_QuizMaster", "download remove id:"+downloadID);
			downloadManager.remove(downloadID);
		}
		
		
		
		
	}
//----------------------------------------------------------------------------------------------------------------------------------------------------
		 BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
			 
			  @Override
			  public void onReceive(Context context, Intent intent) {
/*
				  for(int i=0;i<mul_download_req_id.size();i++)
				  {
					  //Toast.makeText(getBaseContext(), "List download id:"+mul_download_req_id.get(i), Toast.LENGTH_SHORT);
					  Log.e("Brodcast_QuizMaster", "List contain id:"+mul_download_req_id.get(i));
				  }*/
				  
				  String action1 = intent.getAction();
				  if(downloadReference==0)return;
				  Log.e("Brodcast_QuizMaster", "outside nt click & inside rec"+downloadReference);
				  if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action1)) {
					  long downloadID = preferenceManager.getLong(Download_ID, 0);
					    Log.e("Brodcast_QuizMaster", "Notification clicked"+downloadID);
					    dispDownload();
					    //DownloadManager dm =(DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
					    
					   
					}
				  
				  
			    
			
			  }
			  };
			  
			  //when download complete
			  BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
					 
				  @Override
				  public void onReceive(Context context, Intent intent) 
				  {
					  String action1 = intent.getAction();
					  if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action1)) {
						  long downloadID = preferenceManager.getLong(Download_ID, 0);
						    Log.e("Brodcast_QuizMaster", "Notification clicked"+downloadID);
						
						    DownloadManager mydm =(DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
						   dispDownload();
						   installApp(context);
						}
					  				  
					  checkStatus();
					  
				  }
				  
				  };
//------------------------------------------------------------------------------------------------------------------------------------------------------
	private void myDownloadManager()
	{
		if(isDownloadManagerAvailable(getApplicationContext()))
		{
			
			startsNewDownload();
			//checkStatus();
		}
		else
		{
			updateLabel.setText("download is in process..please wait...");
			mup=new MyUpdater();
			mup.setContext(getApplicationContext());
			mup.Update(apkurl);
		}
	}
//-----------------------------------------------------------------------------------------------------------------------------------------------
	private void downloadedFile()
	{
		 DownloadManager.Query query = new DownloadManager.Query();
		   query.setFilterById(preferenceManager.getLong(Download_ID, 0));
		   Cursor cursor = downloadManager.query(query);
		     
		   if(cursor.moveToFirst()){
		    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
		    int status = cursor.getInt(columnIndex);
		    int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
		    int reason = cursor.getInt(columnReason);
		  if(status == DownloadManager.STATUS_SUCCESSFUL){
			     //Retrieve the saved download id
			     long downloadID = preferenceManager.getLong(Download_ID, 0);
			       
			     ParcelFileDescriptor file;
			     try {
			      file = downloadManager.openDownloadedFile(downloadID);
			      Toast.makeText(MainActivity.this,
			        "File Downloaded: " + file.toString(),
			        Toast.LENGTH_LONG).show();
			     } catch (FileNotFoundException e) {
			      // TODO Auto-generated catch block
			      e.printStackTrace();
			      Toast.makeText(MainActivity.this,
			        e.toString(),
			        Toast.LENGTH_LONG).show();
			     }
			       
			    }
		   }
	}
//--------------------------------------------------------------------------------------------------------------------------------------------
	private void installApp(Context context)
	{
		 Intent intent2install = new Intent(Intent.ACTION_VIEW);
         intent2install.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "QuizMaster.apk")), "application/vnd.android.package-archive");
         intent2install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         context.startActivity(intent2install);  
	}
	
protected void showDialogCancel() {
	 //Ask the user if they want to quit
	AlertDialog.Builder builder3= new AlertDialog.Builder(this);
    builder3.setIcon(android.R.drawable.ic_dialog_alert)
    .setTitle("Quit")
    .setMessage("What to do with pending download??")
    .setPositiveButton("Cancel all pending download", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            //Stop the activity
        	cancelpandingDownload();
        	setDownloadRunningFlag(false);
        	//mul_download_req_id.clear();
            finish();    
        }
    })
    .setNegativeButton("No, leave them in background", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            //Stop the activity
        	 setDownloadRunningFlag(true);
        	 finish();
        	 System.exit(0);
            dialog.dismiss();    
        }
    });
    AlertDialog alert = builder3.create();
    alert.show();
	}

//--------------------------------------------------------------------------------------------------------------------------------------------
	 
	 
	 public static boolean isDownloadManagerAvailable(Context context) {
		    try {
		        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
		            return false;
		        }
		        Intent intent = new Intent(Intent.ACTION_MAIN);
		        intent.addCategory(Intent.CATEGORY_LAUNCHER);
		        intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
		        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
		                PackageManager.MATCH_DEFAULT_ONLY);
		        return list.size() > 0;
		    } catch (Exception e) {
		        return false;
		    }
		}
	protected void setdownloadComplete(boolean b) {
	// TODO Auto-generated method stub
	downloadComplete=b;
	}

	protected void setDownloadRunningFlag(boolean b) {
	// TODO Auto-generated method stub
	downloadRunning=b;
	}

	private void setDownloadstartedFlag(boolean bl)
	{
		downloadStarts=bl;
	}
	private boolean isDownloadstarted()
	{
		return flag_download;		
	}
	private void showConfirmExit()
	{
		 //Ask the user if they want to quit
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Quit")
        .setMessage("Are you sure want to Exit??")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Stop the activity
            	if(downloadRunning)
            	{
            		showDialogCancel();
            	}
            	moveTaskToBack(true);
            	/*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            	startActivity(intent);
            	
            	moveTaskToBack(true);
            	
            		android.os.Process.killProcess(android.os.Process.myPid());
            		MainActivity.this.finish(); 
            		System.exit(0);*/
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Stop the activity
                dialog.dismiss();    
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

	}
//------------------------------------------------------------------------------------------------------------------------------------------------
	private void prompt2download()
	{
		 //Ask the user if they want to quit
    	AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
        builder3.setIcon(R.drawable.ic_launcher)
        .setTitle("QuizMaster")
        .setMessage("Newer version is available. Do you want to download it??")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Stop the activity
            	Toast.makeText(getBaseContext(),"Downloading starts..", Toast.LENGTH_LONG);
				myDownloadManager();
                 
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Stop the activity
                dialog.dismiss();    
            }
        });
        AlertDialog alert3 = builder3.create();
        alert3.show();
	}
//-----------------------------------------------------------------------------------------------------------------------------------------------------
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	   
		//Handle the back button
	    if(keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
	    	
	       showConfirmExit();
	        return true;
	    }
	    else {
	        return super.onKeyDown(keyCode, event);
	    }
	}
	 @Override
	 protected void onResume() {
	  // TODO Auto-generated method stub
	  super.onResume();
	    
	  IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
	  registerReceiver(downloadReceiver, intentFilter);
	  
	  IntentFilter filter_complete = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
	  registerReceiver(downloadCompleteReceiver, filter_complete);
	 }

	 @Override
	 protected void onPause() {
	  // TODO Auto-generated method stub
	  super.onPause();
	 
	  overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	 }
	 
	 @Override
	 protected void onStop()
	 {
		 super.onStop();
	     unregisterReceiver(downloadReceiver);
	     unregisterReceiver(downloadCompleteReceiver);
	     
	 }
	@Override
    public void onDestroy()
    {
        super.onDestroy();
       
        finish();
       
      //  System.exit(0);
       // cancelAllDownload();
        
    }
//-----------------------------------------------------------------------------------------------------------------------------------------------------
	
	 
}
