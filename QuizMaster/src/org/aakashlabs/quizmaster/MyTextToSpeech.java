package org.aakashlabs.quizmaster;

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MyTextToSpeech implements
		TextToSpeech.OnInitListener {
	/** Called when the activity is first created. */

	private TextToSpeech tts;
	String text;
	

	

	public  MyTextToSpeech(Context c){

		tts = new TextToSpeech(c, this);

		
		// button on click event
		
	}

	public void onDestroytts() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		//super.onDestroy();
	}

	@Override
	public void onInit(int status) {

		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				
				speakOut();
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}

	}

	private void speakOut() 
	{
			tts.speak(text, TextToSpeech.QUEUE_ADD, null);
	}
	public void speakNow(String sp)
	{
		text=sp;
		speakOut();
		
	}
	public void interruptSpeak()
	{
		if(tts.isSpeaking())
		{
			tts.stop();
		}
	}
}

