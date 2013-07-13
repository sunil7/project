package org.aakashlabs.quizmaster;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity {

	
	MyTextToSpeech mytts;
	private String uname;
	int trueans;
	int falseans;
	int skipans;
	private int perc;
	TextView txtnm,txtc,txti,txtskp,txtscr;
	private Button btnGoBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		mytts=new MyTextToSpeech(getApplicationContext());
		mytts.speakNow("Quiz is over");
		
		txtnm=(TextView)findViewById(R.id.RTextView06);
		txtc=(TextView)findViewById(R.id.RTextView07);
		txti=(TextView)findViewById(R.id.RTextView08);
		txtskp=(TextView)findViewById(R.id.RTextView09);
		txtscr=(TextView)findViewById(R.id.RTextView10);
		btnGoBack=(Button)findViewById(R.id.Rbutton1);
		
		
		if(getIntent().getExtras().getString("user")!=null )
		{
			
			uname=getIntent().getExtras().getString("user");
			trueans=getIntent().getExtras().getInt("anstrue");
			falseans=getIntent().getExtras().getInt("ansfalse");
			skipans=getIntent().getExtras().getInt("skip");
			perc=getIntent().getExtras().getInt("perc");
		}
		else
		{
			Toast.makeText(this,"intent nulll...",Toast.LENGTH_LONG).show();
		}
		txtnm.setText(""+uname);
		txtc.setText(""+trueans);
		txti.setText(""+falseans);
		txtskp.setText(""+skipans);
		txtscr.setText(""+perc);
		
		
		
btnGoBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent Gintent = new Intent(getApplicationContext(),MainActivity.class);
				startActivity(Gintent);
	}

		});

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_result, menu);
		return true;
	}

	@Override
	 protected void onPause() {
	  // TODO Auto-generated method stub
	  super.onPause();
	 
	  overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	 }
	@Override
    public void onDestroy()
    {
        super.onDestroy();
        finish();
       // System.exit(0);
    }
}
