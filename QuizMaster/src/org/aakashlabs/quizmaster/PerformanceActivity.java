package org.aakashlabs.quizmaster;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import org.aakashlabs.quizmaster.GetInActivity;;

public class PerformanceActivity extends Activity {

	String OpenTable;
	 Intent data;
	 Cursor cp;
	 private SQLiteDatabase mydb4Res;
		Spinner selName,selQuiz;
		private String TABLE_OPEN;
		public List<String> lsName;
		public List<String> lsQname;
		public List<String> lsTemp;
		private String DATABASE_PATH = "/data/data/org.aakashlabs.quizmaster/databases/";
	    public static final String DATABASE_NAME = "myQuiz.sqlite";
	    ArrayAdapter<String> adpt;
	    ArrayAdapter<String> adptQ;
	    
	ImageView imgObj,uphoto;
	TextView advise,bestscore,Qname;
	String uname,bestResult;
	int perc;
	static int uid;
	private int perIndex;
	private int count;
	private int[] scores=new int[20];
	private static String username,QzName;
	private int uidIndex,QuizNameIndex;
	private int unameIndex;
	public static Integer[] mThumbIds = {

		   R.drawable.images3,R.drawable.imggotit,R.drawable.think,R.drawable.sad

		};
	
	File imgFile;
	String FILENAME;
	String id;
	String filePath;
	private int phIndex;
	private int QnameIndex;
	private String Qselected;
	private int pos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_performance);
		openDatabase();
		scores[0]=0;
		selName=(Spinner)findViewById(R.id.Pspinner1);
		selQuiz=(Spinner)findViewById(R.id.PSpinner2);
		lsName = new ArrayList<String>();
		lsQname = new ArrayList<String>();
		lsTemp = new ArrayList<String>();
		adpt =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,lsName);
		adptQ =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,lsQname);
		adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adptQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		try
		{
			loadSpinner();
			loadQSpinner();
		}
		catch(Exception e)
		{

			Toast.makeText(this,"load spinner fail:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
		}
		
		
		selName.setAdapter(adpt);
		selQuiz.setAdapter(adptQ);
		//init img array
		imgObj = (ImageView) findViewById(R.id.PimageView1);
		advise=(TextView)findViewById(R.id.PTextView03);
		bestscore=(TextView)findViewById(R.id.PTextView04);

		uphoto = (ImageView) findViewById(R.id.PimageView2);
		//item change
		
		selName.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
		public void onNothingSelected(AdapterView<?> arg0) { }
		
		public void onItemSelected(AdapterView<?> parent, View v,int position, long id) {
		// Code that does something when the Spinner value changes
			String item = parent.getItemAtPosition(position).toString();
			 username=item;
			 fetchData();
			 loadPhoto();
		//	 loadQSpinner();
		}
		});
		
		//if sel quiz from spinner
		selQuiz.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
		public void onNothingSelected(AdapterView<?> arg0) { }
		
		public void onItemSelected(AdapterView<?> parent, View v,int position, long id) {
		// Code that does something when the Spinner value changes
			String item = parent.getItemAtPosition(position).toString();
			 Qselected=item;
		//	 fetchData();
		//	 loadPhoto();
			 pos=position;
			 disp();
			 loadPhoto();
		}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_performance, menu);
		return true;
	}
	private void openDatabase() {
		// TODO Auto-generated method stub
		
		try
		{
			mydb4Res=SQLiteDatabase.openDatabase(DATABASE_PATH+DATABASE_NAME,null, SQLiteDatabase.CREATE_IF_NECESSARY );
			//Toast.makeText(this, "Database opening..", Toast.LENGTH_SHORT).show();
			//checkDB();
		}
		catch(Exception e)
		{
			Toast.makeText(this,"open db failed:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	//close db
	private void closeDatabase() {
		// TODO Auto-generated method stub
		mydb4Res.close();
		
	}
private void openResultTable() {
	 int i = 0;
	 int qzi;
	 if(!lsQname.isEmpty())
	 {
		 lsQname.clear();
	 }
	 
		//hard-coded SQL-select command with no arguments
		String mySQL="select * from Result where UID ="+ uid ; //+" AND QuizName='"+Qselected+"'";
		cp = mydb4Res.rawQuery(mySQL, null);
		 perIndex = cp.getColumnIndex("per");
		 qzi = cp.getColumnIndex("QuizName");
//		QuizNameIndex= cp.getColumnIndex("QuizName");
		int countRecords=cp.getCount();
		// scores=new int[count];
		 cp.moveToFirst();
		 i=0;
		 if(countRecords==0)
		 {
			 bestscore.setText("You have not given this Quiz.");

			 imgObj.setImageResource(mThumbIds[2]);
			advise.setText("Please give quiz and then see Result!");
		 }
		 else
		 {
			 while(i<countRecords)
			 {
				 if(cp.moveToPosition(i))
				 {
					 try
					 {
						scores[i] = Integer.parseInt(cp.getString(perIndex));
						lsQname.add(i,cp.getString(qzi));
	//					QuizName=cp.getString(QuizNameIndex);
					 }
					 catch(Exception e)
					 {
						e.printStackTrace();
					 }
				 }
				 i++;
				//advance to the next record (first rec. if necessary)
				 if(cp.isLast()){cp.moveToFirst();}
			 }
			 
//			 sort_scores(scores,countRecords);
/*			 int j=0,k=0,totalsize=lsQname.size();
			 String strtemp;
		while(k<totalsize)
		{
			strtemp=lsQname.get(k);
			if(strtemp.compareToIgnoreCase(Qselected)==0)
			 {
				 lsTemp.add(j, strtemp);
				 j++;
			 }
			k++;
		}
			 */
//			 int index4score=lsQname.indexOf(Qselected);
			 perc=scores[pos];
			 loadImg();
			 bestscore.setText(""+perc);
			 
			 selQuiz.setAdapter(adptQ);
			 adptQ.notifyDataSetChanged();
		 }
//		Toast.makeText(this,""+countRecords+"data reading..", Toast.LENGTH_SHORT).show();
			}

private void openUserInfoTable() {
	 int i = 0,count;
		//hard-coded SQL-select command with no arguments
		String mySQL="select * from UserInfo where name = '"+ username +"'";
		cp = mydb4Res.rawQuery(mySQL, null);
		 uidIndex = cp.getColumnIndex("UID");
		 phIndex=cp.getColumnIndex("urphoto");
		 count=cp.getCount();
		
		 if(cp.moveToFirst())
			{
			 try
			 {
				 uid=Integer.parseInt(cp.getString(uidIndex)); 
				 filePath=cp.getString(phIndex);
			 }
			 catch(NullPointerException npe)
			 {
				 Toast.makeText(this,"Couldn't find your data..", Toast.LENGTH_SHORT).show();
				// finish();
			 }
			 
			}
			
		//advance to the next record (first rec. if necessary)
		
//		 Toast.makeText(this,"file:>>"+filePath, Toast.LENGTH_SHORT).show();
//		Toast.makeText(this,""+count+"uid reading..", Toast.LENGTH_SHORT).show();
		
		}

	private void loadSpinner()
	{
		int i=0;
		String mySQL="select * from UserInfo";
		cp = mydb4Res.rawQuery(mySQL, null);
		 unameIndex = cp.getColumnIndex("name");
		
		 if(cp.moveToFirst()){}
		 while(i<cp.getCount())
		 {
			 if(cp.moveToPosition(i))
			 {
				 lsName.add(i,cp.getString(unameIndex));
			 }
			i++;
		 }
		 selName.setAdapter(adpt);
		 adpt.notifyDataSetChanged();
		 
	}
	private void loadImg()
	{
		if(perc>=80)
		{
			imgObj.setImageResource(mThumbIds[0]);
			advise.setText("Excellent...!!!");
			
		}
		else 
			{
			if(perc>=60)
			{
				imgObj.setImageResource(mThumbIds[1]);
				advise.setText("Very Good...!!");
				
			}
			else if(perc>=45)
			{
				imgObj.setImageResource(mThumbIds[2]);
				advise.setText("Need to improved...");
			}
			else
			{
				imgObj.setImageResource(mThumbIds[3]);
				advise.setText("Serious exercise required.!");
			}
			}
	}
	void sort_scores(int a[],int n)
	{
		int i,j,temp;
		
		for(i=0;i<n;i++)
		{
			for(j=i+1;j<n;j++)
			{
			if(a[i]<a[j])
			{
				temp=a[i];
				a[i]=a[j];
				a[j]=temp;
			}
			}
		}
	}
	
	
	@Override
    public void onDestroy()
    {
        super.onDestroy();
        closeDatabase();
    	
            super.onDestroy();
            finish();
          //  System.exit(0);
       
       
    }
	private void fetchData()
	{
		 try
		 {
		 openUserInfoTable();
		 openResultTable();
		 
		 }
		 catch(Exception e)
		 {
			 Toast.makeText(getBaseContext(),"reading result table problem:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
		 }
		
	}
	private void loadPhoto()
	{
		
		imgFile  = getApplicationContext().getFileStreamPath(filePath);
		if(imgFile.exists())
		{
			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			uphoto.setImageBitmap(myBitmap);
	//		Toast.makeText(this,"Image path:"+imgFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
		}
		else
		{
			uphoto.setImageResource(R.drawable.cross);
		}
		
		//uphoto.setImageDrawable(Drawable.createFromPath(filePath.toString()));
//		Toast.makeText(this,"Image:"+filePath.toString(), Toast.LENGTH_SHORT).show();
		
	
		
	}
	private void givenQuiz()
	{
		int i = 0,count;
		//hard-coded SQL-select command with no arguments
		String mySQL="select QuizName from Result where name = '"+ username +"'";
		cp = mydb4Res.rawQuery(mySQL, null);
		 QnameIndex = cp.getColumnIndex("QuizName");
		
		 count=cp.getCount();
		lsQname.clear();
		 if(cp.moveToFirst()){}
		 while(i<cp.getCount())
		 {
			 if(cp.moveToPosition(i))
			 {
				
/*					if(!lsQname.contains(cp.getString(QnameIndex)))
					{
						lsQname.add(i,cp.getString(QnameIndex));
						
					}*/
				 lsQname.add(i,cp.getString(QnameIndex));
			 }
			i++;
		 }		
		 //Toast.makeText(this,count+"person gives quiz", Toast.LENGTH_SHORT).show();
	}
	@SuppressLint("ShowToast")
	private void loadQSpinner()
	{
		try
		{
			//givenQuiz();
		}
		catch(Exception e)
		{
			Toast.makeText(getBaseContext(), ""+e.getMessage(),Toast.LENGTH_SHORT);
		}
		selQuiz.setAdapter(adptQ);
		 adptQ.notifyDataSetChanged();
	}
	private void disp()
	{
		 perc=scores[pos];
		 loadImg();
		 bestscore.setText(""+perc);
	}
	
	@Override
	protected void onPause() {
	 // TODO Auto-generated method stub
	 super.onPause();

	 overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	}
}
