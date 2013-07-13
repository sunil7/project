package org.aakashlabs.quizmaster;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class QuizStartActivity extends Activity {

	//String OpenTable;
	MyTextToSpeech mytts4Q;
	 
	 int min,max,qlimit;
	 boolean Qover=false;
	 String name,strDbOpen;
		private SQLiteDatabase mydb;
		Spinner selQuiz;
		String TABLE_OPEN;
		
		private String DATABASE_PATH = "/data/data/org.aakashlabs.quizmaster/databases/";
	    public static final String DATABASE_NAME = "myQuiz.sqlite";
	    
	   private Cursor cQ;
	 int per=0,Uid;
	 int Qidindex,Questionindex,Answerindex,op1index,op2index,op3index,op4index,count;
	 String Qid,Question,Answer,op1,op2,op3,op4;
	 List<Integer> generated = new ArrayList<Integer>();
	 Random rnd;
	 int[] arr=new int[10];
	 
	 AlertDialog.Builder alert;
	 
	 static int qnum;
	 
	 TextView txtqno,txtqun,txttimer;
	 
	 Button btnspeak,btnop1,btnop2,btnop3,btnop4,btnsumm,btnnext,btnskip;
	private Object[] strarr;
	 
	 static int anstrue,ansfalse,skip;
	 CountDownTimer ct;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_start);
		
		Qover=false;
		qnum=1;
		anstrue=0;
		ansfalse=0;
		skip=0;
		min=0;
		max=1;
		qlimit=1;
		
		LinearLayout ll = (LinearLayout)findViewById(R.id.SlinearLayout1);
			txttimer=(TextView)findViewById(R.id.STextView01);
			txtqno=(TextView)findViewById(R.id.STextView010);
			txtqun=(TextView)ll.findViewById(R.id.StextView3);
			
			btnop1=(Button)findViewById(R.id.Sbutton1);
			btnop2=(Button)findViewById(R.id.Sbutton2);
			btnop3=(Button)findViewById(R.id.Sbutton3);
			btnop4=(Button)findViewById(R.id.Sbutton4);
			btnspeak=(Button)findViewById(R.id.Sbutton00);
			btnsumm=(Button)findViewById(R.id.SButton01);
			btnnext=(Button)findViewById(R.id.SButton03);
			btnskip=(Button)findViewById(R.id.SButton02);
			btnnext.setEnabled(false);
			mytts4Q=new MyTextToSpeech(getBaseContext());
			
			alert = new AlertDialog.Builder(this);
			
			for(int i=0;i<10;i++)
			{
				arr[i]=(i+1);
			}
			
			rnd=new Random();
			timer();
			
			
		if(getIntent().getExtras().getString("OpenDb")!=null && getIntent().getExtras().getString("uname")!=null && getIntent().getExtras().getString("uid")!=null)
		{
			TABLE_OPEN=getIntent().getExtras().getString("OpenDb");
			name=getIntent().getExtras().getString("uname");
			Uid=Integer.parseInt((getIntent().getExtras().getString("uid")));
		}
		else
		{
			
			Intent myintentback = new Intent(getBaseContext(),GetInActivity.class);
			myintentback.putExtra("error", "10");
			startActivity(myintentback); 
			Toast.makeText(this,"please enter name",Toast.LENGTH_LONG).show();
		}
		
		openDatabase();
		
		try {
			openQuestionTable();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Toast.makeText(this,e1.getMessage(),Toast.LENGTH_LONG).show();
			e1.printStackTrace();
		}
		
		genRandomNum();
		
		try
		{
			loadQuestion();	
		}
		catch(Exception e)
		{
			Toast.makeText(this,"database open error "+e.getMessage(),Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
		
		
		//now button click event handler
		btnspeak.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View arg0) 
			{
				mytts4Q.speakNow("Question is"+Question);
				mytts4Q.speakNow("Option 1 :"+op1);
				mytts4Q.speakNow("Option 2 :"+op2);
				mytts4Q.speakNow("Option 3 :"+op3);
				mytts4Q.speakNow("Option 4 :"+op4); 
			}
		});
		
		btnop1.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View arg1) 
			{
				if(Answer.compareToIgnoreCase(btnop1.getText().toString())==0)
				{
					anstrue=anstrue+1;
				}
				else
				{
					ansfalse=ansfalse+1;
					
				}
				btnop1.setClickable(false);
				btnop2.setClickable(false);
				btnop3.setClickable(false);
				btnop4.setClickable(false);
				btnnext.setClickable(true);
				btnnext.setEnabled(true);
				btnskip.setClickable(false);
				btnskip.setEnabled(false);
				btnsumm.setEnabled(false);
			}
		});
		
		btnop2.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View arg2) 
			{
				
				try
				{
					if(Answer.compareToIgnoreCase(btnop2.getText().toString())==0)
					{
						anstrue=anstrue+1;
					}
					else
					{
						ansfalse=ansfalse+1;
						
					}
					btnop1.setClickable(false);
					btnop2.setClickable(false);
					btnop3.setClickable(false);
					btnop4.setClickable(false);
					btnnext.setClickable(true);
					btnnext.setEnabled(true);
					btnskip.setClickable(false);
					btnskip.setEnabled(false);
					btnsumm.setEnabled(false);
				}
				catch(Exception e)
				{
					Toast.makeText(getBaseContext(), "Answer:"+e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				
			}
			});
			
		
		
		btnop3.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View arg3) 
			{
			
				if(Answer.compareToIgnoreCase(btnop3.getText().toString())==0)
				{
					anstrue=anstrue+1;
				}
				else
				{
					ansfalse=ansfalse+1;
					
				}
				btnop1.setClickable(false);
				btnop2.setClickable(false);
				btnop3.setClickable(false);
				btnop4.setClickable(false);
				btnnext.setClickable(true);
				btnnext.setEnabled(true);
				btnskip.setClickable(false);
				btnskip.setEnabled(false);
				btnsumm.setEnabled(false);
			}
		});
		
		btnop4.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View arg4) 
			{
				
				if(Answer.compareToIgnoreCase(btnop4.getText().toString())==0)
				{
					anstrue=anstrue+1;
				}
				else
				{
					ansfalse=ansfalse+1;
					
				}
				btnop1.setClickable(false);
				btnop2.setClickable(false);
				btnop3.setClickable(false);
				btnop4.setClickable(false);
				btnnext.setClickable(true);
				btnnext.setEnabled(true);
				btnskip.setClickable(false);
				btnskip.setEnabled(false);
				btnsumm.setEnabled(false);
			}
		});
		
		btnskip.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View arg5) 
			{
				if((qnum<=10) && skip<10){
					qnum++;
					skip++;
					try
					{
					loadQuestion();
					}
					catch(Exception e)
					{
						Toast.makeText(getBaseContext(),"question reading failed...:"+ e.getMessage(), Toast.LENGTH_SHORT).show();
					}
					mytts4Q.interruptSpeak();
					btnop1.setClickable(true);
					btnop2.setClickable(true);
					btnop3.setClickable(true);
					btnop4.setClickable(true);
					btnop1.setEnabled(true);
					btnop2.setEnabled(true);
					btnop3.setEnabled(true);
					btnop4.setEnabled(true);
					btnnext.setClickable(false);
					btnnext.setEnabled(false);
					btnskip.setClickable(true);
					btnskip.setEnabled(true);
					btnsumm.setEnabled(true);
				}
				else
				{
					ct.cancel();
					 txttimer.setText("done!");
					btnop1.setClickable(false);
					btnop2.setClickable(false);
					btnop3.setClickable(false);
					btnop4.setClickable(false);
					btnop1.setEnabled(false);
					btnop2.setEnabled(false);
					btnop3.setEnabled(false);
					btnop4.setEnabled(false);
					btnnext.setText("show");
					btnnext.setClickable(true);
					btnnext.setEnabled(true);
					btnskip.setClickable(false);
					btnskip.setEnabled(false);
				}
			}
		});
		
		btnsumm.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View arg6) 
			{
				if((!Qover)&& qnum<=10)
				{
					Toast.makeText(getBaseContext(), "True Answer:"+Answer, Toast.LENGTH_SHORT).show();
					btnop1.setEnabled(false);
					btnop2.setEnabled(false);
					btnop3.setEnabled(false);
					btnop4.setEnabled(false);
					btnnext.setEnabled(false);
					btnnext.setClickable(false);
				//	btnskip.setClickable(true);
					btnskip.setEnabled(true);
				}
				if(Qover)
				{
					
					btnnext.setText("show");
					ct.cancel();
					 txttimer.setText("done!");
					btnnext.setClickable(true);
					btnnext.setEnabled(true);
				}
			}
		});
		
		btnnext.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View arg7) 
			{
				qnum++;
				loadQuestion();
				mytts4Q.interruptSpeak();
				if((btnnext.getText().toString().compareToIgnoreCase("show")==0) && Qover)
				{
					ct.cancel();
					
					per=(anstrue*10);
					insertResult();
					
					Intent myintent = new Intent(getApplicationContext(),ResultActivity.class);
					myintent.putExtra("user",name);
					myintent.putExtra("perc", per);
					myintent.putExtra("anstrue", anstrue);
					myintent.putExtra("ansfalse", ansfalse);
					myintent.putExtra("skip", skip);
					startActivity(myintent); 
				}
				if(btnnext.isClickable())
				{
					btnnext.setClickable(false);
				}
				btnsumm.setEnabled(true);
			}
		});
	}

	public void loadQuestion() {
		// TODO Auto-generated method stub
		btnop1.setClickable(true);
		btnop2.setClickable(true);
		btnop3.setClickable(true);
		btnop4.setClickable(true);
		btnnext.setClickable(true);
		btnskip.setClickable(true);
		btnskip.setEnabled(true);
		if((qnum)<=10)
		{
			int in=qnum-1;
			int n=arr[in];
			try {
					getQuestion(n);
				} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(this,"getQuestion fail to read:"+n+e.getMessage(),Toast.LENGTH_LONG).show();
				e.printStackTrace();
				
			}
		}
		else
		{
			if((qnum)>10)
			{
				btnop1.setClickable(false);
				btnop2.setClickable(false);
				btnop3.setClickable(false);
				btnop4.setClickable(false);
				btnop1.setEnabled(false);
				btnop2.setEnabled(false);
				btnop3.setEnabled(false);
				btnop4.setEnabled(false);
				btnnext.setClickable(false);
				btnskip.setClickable(false);
				btnsumm.setEnabled(true);
				btnsumm.setClickable(true);
				txttimer.setText("Quiz is finished..");
				btnnext.setText("show");
				Qover=true;
				btnsumm.setFocusable(true);
				btnspeak.setEnabled(false);
			}
		}
		
		
	}

	private void genRandomNum() {
		// TODO Auto-generated method stub
		
		int i=0,j=0;
		int c=10,num=0;
		max=qlimit;
		
		// Ideally just create one instance globally
		//>>>>>>>>>>>>>>>>>Toast.makeText(this,"total count in tabel:"+max,Toast.LENGTH_LONG).show();
		for (i = 0; i < c; i++)
		{
		    while(true)
		    {
		        Integer next = rnd.nextInt(max - (min+1));
		        if (!generated.contains(next))
		        {
		            // Done for this iteration
		            generated.add(next);
		            break;
		        }
		    }
		}
		strarr=generated.toArray();
		Integer [] arrInt=generated.toArray(new Integer[generated.size()]);
		for(int k=0;k<generated.size();k++)
		{
			arr[k]=arrInt[k];
		}
		
//>>>>>		Toast.makeText(getBaseContext(),+arr[0]+":"+arr[1]+":"+arr[2]+":"+arr[3]+":"+arr[4]+":"+arr[5]+":"+arr[6]+":"+arr[7]+":"+arr[8]+":"+arr[9], Toast.LENGTH_LONG).show();

	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_quiz_start, menu);
		return true;
	}
	
	private void openQuestionTable() throws Exception {
		
		//hard-coded SQL-select command with no arguments
		String mySQL="select * from " + TABLE_OPEN;
		cQ = mydb.rawQuery(mySQL, null);
		 Qidindex = cQ.getColumnIndex("Qid");
		 Questionindex = cQ.getColumnIndex("Question");
		 Answerindex = cQ.getColumnIndex("Answer");
		 op1index = cQ.getColumnIndex("op1");
		 op2index = cQ.getColumnIndex("op2");
		 op3index = cQ.getColumnIndex("op3");
		 op4index = cQ.getColumnIndex("op4");
		 count=cQ.getCount();
		 qlimit=cQ.getCount();
		//advance to the next record (first rec. if necessary)
		
		
//		Toast.makeText(this,"question reading..", Toast.LENGTH_SHORT).show();
		
		}
	private void getQuestion(int p) throws Exception
	{
			cQ.moveToFirst();
			//Toast.makeText(getBaseContext(), "position:"+p, Toast.LENGTH_SHORT).show();
			if(cQ.moveToPosition(p))
			{
				 Qid = cQ.getString(Qidindex);
				 Question = cQ.getString(Questionindex);
				 Answer = cQ.getString(Answerindex);
				 op1 = cQ.getString(op1index);
				 op2 = cQ.getString(op2index);
				 op3 = cQ.getString(op3index);
				 op4 = cQ.getString(op4index);
				
				txtqno.setText(qnum+"/"+"10");
				txtqun.setText(""+Question);
				txtqun.scrollTo(0,0);
				txtqun.setMovementMethod(new ScrollingMovementMethod());
				txttimer.setText("");
				btnop1.setText(op1);
				btnop2.setText(op2);
				btnop3.setText(op3);
				btnop4.setText(op4);
				Toast.makeText(this, "true:"+anstrue+"FALSE:"+ansfalse, Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(getBaseContext(), "position:"+p, Toast.LENGTH_SHORT).show();
			}
			
			if(cQ.isLast())
			{
				cQ.moveToFirst();
			}	
		}
		
	//to open database
	private void openDatabase() {
		// TODO Auto-generated method stub
		
		try
		{
			mydb=SQLiteDatabase.openDatabase(DATABASE_PATH+DATABASE_NAME,null, SQLiteDatabase.CREATE_IF_NECESSARY );
			
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
		mydb.close();
		
	}
	public void insertResult()
	{
		mydb.beginTransaction();
		try
		{
			mydb.execSQL( "insert into Result( QuizName ,UID , name , per) values ('"+ TABLE_OPEN +"',"+ Uid +",'"+ name + "',"+ per +");" );
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
	
	private void timer()
	{
		
		long ticker=1000;
		long time=180000;
		ct=new CountDownTimer(time, ticker) {

		     public void onTick(long millisUntilFinished) {
		   
		    //     txttimer.setText("Seconds Left: " +millisUntilFinished / 1000);
		         final long  millseconds=millisUntilFinished;//Long.parseLong(txttimer.getText().toString());
		       //  final long minute=(seconds/1000)/60;
		        final long seconds = (int) (millseconds / 1000) % 60 ;
		         final long minutes = (int) ((millseconds / (1000*60)) % 60);
		         txttimer.setText("Time Left: " +minutes+":"+seconds);
		       
		     }

		     public void onFinish() {
		         txttimer.setText("done!");
		         showCompleteDialog();
		     }
		  }.start();
		  
		  

	}
	
	private void showCompleteDialog()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
          alert.setTitle("Quiz Master"); //Set Alert dialog title here
          alert.setMessage("Times up... Touch Finish Button to see Result"); //Message here
          alert.setIcon(R.drawable.ic_launcher);
          
          alert.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
           // String value = input.getText().toString();
            // Do something with value!
              //You will get input data in this variable.
        	  skip=skip +(10-(qnum-1));
        	  quizFinish();
              QuizStartActivity.this.finish();

            }
          });

          alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              // Canceled.
            	
                dialog.cancel();
                QuizStartActivity.this.finish();
            }
          });
          alert.setNeutralButton("Just Save Result", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                  // Canceled.
            	  skip=skip +(10-(qnum-1));
            	  per=(anstrue*10);
          		insertResult();
                    QuizStartActivity.this.finish();
                }
              });
          
          AlertDialog alert3 = alert.create();
          alert3.setCanceledOnTouchOutside(false);
          alert3.setCancelable(false);
          try
          {
        	  alert3.show();
          }
          catch(Exception e)
          {
        	  e.printStackTrace();
        	  
          }
         
        /*  LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	View dialoglayout = inflater.inflate(R.layout.dialog, null);
			
          
          AlertDialog alertDialog = alert.create();
          alertDialog.setCanceledOnTouchOutside(false);
          alertDialog.setCancelable(false);
          alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
          alertDialog.setView(dialoglayout);
          
          
          try
          {
        	  alertDialog.show();
          }
          catch(Exception e)
          {
        	  e.printStackTrace();
        	  
          }*/
     /* Alert Dialog Code End*/  
	}
	
	private void quizFinish()
	{
		per=(anstrue*10);
		insertResult();
	
	Intent myintent = new Intent(getBaseContext(),ResultActivity.class);
	myintent.putExtra("user",name);
	myintent.putExtra("perc", per);
	myintent.putExtra("anstrue", anstrue);
	myintent.putExtra("ansfalse", ansfalse);
	myintent.putExtra("skip", skip);
	startActivity(myintent); 
	}
	
	
	@Override
	 protected void onPause() {
	  // TODO Auto-generated method stub
	  super.onPause();
	 
	  overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	  mytts4Q.interruptSpeak();
	 }
	@Override
    public void onDestroy()
    {
        super.onDestroy();
        closeDatabase();
       	mytts4Q.onDestroytts();
       	this.finish();
       
    //    System.exit(0);
		
    }

}
