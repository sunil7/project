package org.aakashlabs.quizmaster;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;




public class GetInActivity extends Activity implements Callback {

	EditText etName;
	Button btnGo;
	//TextView t3;
	String name,strDbOpen,Uid;
	private SQLiteDatabase mydb;
	Spinner selQuiz;
	String TABLE_OPEN;
	public List<String> lsType;
	ArrayAdapter<String> aspnQ;
	private String DATABASE_PATH = "/data/data/org.aakashlabs.quizmaster/databases/";
	private SurfaceView sv;
	private SurfaceHolder sHolder;
	private Camera mCamera;
	private Parameters parameters;
	
	private Bitmap photo;
    public static final String DATABASE_NAME = "myQuiz.sqlite";
	private static final int CAMERA_REQUEST = 1;
	//-----------
	FileOutputStream fos;
	Camera.PictureCallback mCall;
	String FILENAME;
	String filePath;
	String timeStamp;
	boolean pgFlag;
	String photo_name;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_in);
		selQuiz=(Spinner)findViewById(R.id.Gspr1);
		etName=(EditText)findViewById(R.id.GeTextName);
		btnGo=(Button)findViewById(R.id.Gbutton1);
		//t3=(TextView)findViewById(R.id.textView2);
		strDbOpen="GK";
		//get file name for image
		filePath=getfilePath();
		//open database
		try
		{
			openDatabase();	
		}
		catch(Exception e)
		{
			//createIfNexist();
			Toast.makeText(this,"try again after re-install"+e.getMessage(),1).show();
		}
		// Set up the Spinner entries
		lsType = new ArrayList<String>();
		 aspnQ =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,lsType);
		aspnQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selQuiz.setAdapter(aspnQ);
		
		//load spinner to display quiz name
		loadQuizSpinner();
		// Set up a callback for the spinner
		selQuiz.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
		public void onNothingSelected(AdapterView<?> arg0) { }
		
		public void onItemSelected(AdapterView<?> parent, View v,int position, long id) {
		// Code that does something when the Spinner value changes
			String item = parent.getItemAtPosition(position).toString();
			 strDbOpen=item;
	Toast.makeText(parent.getContext(), "Quiz select: " + strDbOpen, Toast.LENGTH_SHORT).show();
		}
		});

		//------------------------------------------------------------camera activity--------------------------------------------------------------------
		boolean index = isFrontCamera();
		if (!index){
		    Toast.makeText(getApplicationContext(), "No front camera", Toast.LENGTH_LONG).show();
		}
		else
		{
		   

		    	
		     sv = (SurfaceView) findViewById(R.id.surfaceView1);  
		     sHolder = sv.getHolder();  
		     sHolder.addCallback(this);  
		     sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
		     
//		         Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//		       startActivityForResult(cameraIntent, CAMERA_REQUEST);

		}


		//-----------------------------------------------------------------------------------------------------------------------------------------------
btnGo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				name=etName.getText().toString();
				if(etName.getText().length()==0)
				{
					etName.setFocusable(true);
					etName.bringToFront();
					etName.setHint("enter your name");
					etName.setHeight(etName.getHeight()+20);
				}
				else
				{
							useRawQuery1();
							
							useRawQuery1();
							
							Toast.makeText(getBaseContext(),"Hello.."+name+" Lets start quiz...", Toast.LENGTH_SHORT).show();
							
							Intent Gintent = new Intent(getBaseContext(),QuizStartActivity.class);
							Gintent.putExtra("OpenDb",strDbOpen );
							Gintent.putExtra("uname", name);
							Gintent.putExtra("uid",Uid );
							startActivity(Gintent);
				}
				
	}

		});
	


	}
//------------------------------------------------------------------------------------------------------------------------------------------------	
/*	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAMERA_REQUEST) {
	        photo = (Bitmap) data.getExtras().get("data");
	        iv_image.setImageBitmap(photo);
	    }
	}
*/
	private void takePhoto()
	{
		//check for availability of front camera
		 
		 mCamera.takePicture(null, null, mCall);
	}
	private boolean isFrontCamera()
	{
		int numCameras= Camera.getNumberOfCameras();
		for(int i=0;i<numCameras;i++){
		    Camera.CameraInfo info = new CameraInfo();
		    Camera.getCameraInfo(i, info);
		    if(CameraInfo.CAMERA_FACING_FRONT == info.facing){
		        return true;
		    }
		}
		return false;
	}
	int getFrontCameraId() {
	    CameraInfo ci = new CameraInfo();
	    for (int i = 0 ; i < Camera.getNumberOfCameras(); i++) {
	        Camera.getCameraInfo(i, ci);
	        if (ci.facing == CameraInfo.CAMERA_FACING_FRONT) return i;
	        }
	return -1; // No front-facing camera found
	}
//----------------------------------------------------------------------------------------------------------------------------------------------------	
	public void closeDatabase() {
		// TODO Auto-generated method stub
		mydb.close();
		
	}
	//check if db exist
	private void createIfNexist() {
		// TODO Auto-generated method stub
		mydb=this.openOrCreateDatabase(DATABASE_NAME,MODE_WORLD_WRITEABLE ,null);
	}
	//insert name into userinfo table
	public void insertData() {
		
		//insert data
		mydb.beginTransaction();
		try
		{
			
			mydb.execSQL( "insert into UserInfo(name,urphoto) values ('"+ name + "','"+ filePath +"');" );
		//	 Toast.makeText(getBaseContext(), "Image name save in db: " + filePath, Toast.LENGTH_LONG).show();
			//Toast.makeText(this,"data inserted",Toast.LENGTH_LONG).show();
			mydb.setTransactionSuccessful();
			
		}
		catch(Exception e)
		{
			//Toast.makeText(this,"name insert problem:"+e.getMessage(),Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		finally
		{
			mydb.endTransaction();
		}
		
	}
	
	private void useRawQuery1() {
		
		
		//hard-coded SQL-select command with no arguments
		try
		{
			String sql = "SELECT * FROM UserInfo WHERE name = '"+ name +"'";
			Cursor c2 = mydb.rawQuery(sql, null);
			int index = c2.getColumnIndex("UID");
			int PhIndex=c2.getColumnIndexOrThrow("urphoto");
			int count=c2.getCount();
			if(count>0)
			{
				c2.moveToFirst();
				
				Uid=c2.getString(index);
				photo_name=c2.getString(PhIndex);
			}
			if(count<=0)
			{
				takePhoto();
				insertData();
			}
		}
		catch(Exception e)
		{
			Toast.makeText(getBaseContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
/*		String mySQL="select * from UserInfo";
		Cursor c1 = mydb.rawQuery(mySQL, null);
		int index = c1.getColumnIndex("UID");
		
		if(c1.moveToLast())	
		{
			Uid=c1.getString(index);
		}
		} 
		catch(Exception e) 
		{
			//Toast.makeText(this, e.getMessage(), 1).show();
			e.printStackTrace();
		}
		String sql = "SELECT * FROM UserInfo WHERE name = '"+ name +"'";
		Cursor c2 = mydb.rawQuery(sql, null);
		int count=c2.getCount();
		if(count>0)
		{
			
		}
*/		}
	

	private void openDatabase() {
		// TODO Auto-generated method stub
		
		try
		{
			mydb=SQLiteDatabase.openDatabase(DATABASE_PATH+DATABASE_NAME,null, SQLiteDatabase.CREATE_IF_NECESSARY );
		}
		catch(Exception e)
		{
			Toast.makeText(this,"open db failed:"+ e.getMessage(),Toast.LENGTH_LONG).show();
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_get_in, menu);
		return true;
	}
	
	
		
/*	void checkDB() throws Exception {
        try {
            mydb = SQLiteDatabase
                    .openDatabase(
                    		 DATABASE_PATH+DATABASE_NAME,
                            null, 0);
            Log.d("opendb", "EXIST");
            mydb.close();
            Toast.makeText(this,"inside check db:(Try)",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

            AssetManager am = getApplicationContext().getAssets();
            OutputStream os = new FileOutputStream(
            		 DATABASE_PATH+DATABASE_NAME);
            byte[] b = new byte[100];

            int r;
            InputStream is = am.open("QuizDb.sqlite");
            while ((r = is.read(b)) != -1) {
                os.write(b, 0, r);
            }
            Log.i("DATABASE_HELPER", "Copying the database ");
            is.close();
            os.close();
            Toast.makeText(this,"inside check db:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
	
	}
*/
	private void loadQuizSpinner()
	{
		int i=0;
		String sql_q = "SELECT * FROM QuizName";
		try
		{
		Cursor c2_q = mydb.rawQuery(sql_q, null);
		int index = c2_q.getColumnIndex("Qname");
		
		if(c2_q.moveToFirst()){}
		 while(i<c2_q.getCount())
		 {
			 if(c2_q.moveToPosition(i))
			 {
				 lsType.add(i,c2_q.getString(index));
			 }
			i++;
		 }
		 selQuiz.setAdapter(aspnQ);
		 aspnQ.notifyDataSetChanged();
		}
		catch(Exception e)
		{
			Log.e("QUIZ", ""+e.getMessage());
			lsType.add("GATE");
			lsType.add("GK");
			selQuiz.setAdapter(aspnQ);
			 aspnQ.notifyDataSetChanged();
		}
	}
	private String getfilePath()
	{
		FILENAME = "imgUser";
		timeStamp = 
		        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		filePath = FILENAME+timeStamp+ ".jpg" ;
		return filePath;
		
	}
	
	@Override
	 protected void onPause() {
	  // TODO Auto-generated method stub
	  super.onPause();
	  overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	 }
	
	@Override
    public void onDestroy()
    {
        super.onDestroy();
        closeDatabase();
 //       Toast.makeText(getBaseContext(),"Quit from GetIn", Toast.LENGTH_SHORT).show();
        this.finish();
    }
//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		// TODO Auto-generated method stub
		parameters = mCamera.getParameters();
	     mCamera.setParameters(parameters);  
	     mCamera.startPreview();  

	     mCall = new Camera.PictureCallback()  
	     {  
	         private Bitmap bmp;

			@SuppressLint("SimpleDateFormat")
			@Override  
	         public void onPictureTaken(byte[] data, Camera camera)  
	         {          
	          //  Uri uriTarget = getContentResolver().insert//(Media.EXTERNAL_CONTENT_URI, image);
	            //(Media.EXTERNAL_CONTENT_URI, new ContentValues());
				//filePath=getfilePath();
				try {
					fos = openFileOutput(filePath, Context.MODE_PRIVATE);
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					fos.write(data);
//					 Toast.makeText(getBaseContext(),"Image saved: " + filePath, Toast.LENGTH_LONG).show();
					fos.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
/*	             OutputStream imageFileOS;
	             try {
	                 imageFileOS = getContentResolver().openOutputStream(uriTarget);
	                 imageFileOS.write(data);
	                 imageFileOS.flush();
	                 imageFileOS.close();

	                 Toast.makeText(getBaseContext(),
	                         "Image saved: " + uriTarget.toString(), Toast.LENGTH_LONG).show();
	             }
	             catch (FileNotFoundException e) {
	                 e.printStackTrace();
	             }catch (IOException e) {
	                 e.printStackTrace();
	             }
	             //mCamera.startPreview();
*/
	             bmp = BitmapFactory.decodeByteArray(data, 0, data.length);  
	         
	         }  
	     };  

	    
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		int index = getFrontCameraId();
	    if (index==-1){
	        Toast.makeText(getApplicationContext(), "No front camera", Toast.LENGTH_LONG).show();
	    }
	    else
	    {
	        mCamera = Camera.open(index);
//	        Toast.makeText(getApplicationContext(), "With front camera", Toast.LENGTH_LONG).show();
	    }
	   //   mCamera = Camera.open(index);  
	      try {  
	         mCamera.setPreviewDisplay(holder);  

	      } catch (IOException exception) {  
	          mCamera.release();  
	          mCamera = null;  
	      }  

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		 mCamera.stopPreview();  
		    mCamera.release();  
		    mCamera = null; 
	}
		
	

}