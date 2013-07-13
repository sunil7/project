package org.aakashlabs.quizmaster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	

	 //The Android's default system path of your application database.
	 private static String DB_PATH = "/data/data/org.aakashlabs.quizmaster/databases/";

	 private static String DB_NAME = "myQuiz.sqlite";
	 
	 private final String DB_Internal = "myQuiz";

	 private SQLiteDatabase myDataBase; 

	 private final Context myContext;

	 /**
	  * Constructor
	  * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	  * @param context
	 * 
	  */
	 public DataBaseHelper(final Context context) throws IOException {
	  super(context, DB_NAME, null, 1);
	  this.myContext = context;
	 
	 
	 } 
	 
	 //initilize all
	 public void initAll()
	 {
		 try {
			createDataBase();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			openDataBase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 close();
		 
	 }

	 /**
	  * Creates a empty database on the system and rewrites it with your own database.
	  * */
	 public final void createDataBase() throws IOException {

	  final boolean dbExist = checkDataBase();
	  SQLiteDatabase db_Read = null;
	  if (dbExist) {
	   //do nothing - database already exist
	  } else {
	  
	   //By calling this method and empty database will be created into the default system path 
	   //of your application so we are gonna be able to overwrite that database with our database. 
	   db_Read = this.getReadableDatabase(); 
	   db_Read.close();
	   try {
	    copyDataBase();
	   } catch (IOException e) {
	    throw new Error("Error copying database");
	   }
	  }
	 }

	 /**
	  * Check if the database already exist to avoid re-copying the file each time you open the application.
	  * @return true if it exists, false if it doesn't
	  */
	 private boolean checkDataBase() {
	  final File dbFile = new File(DB_PATH + DB_NAME);
	  return dbFile.exists();
	 }

	 /**
	  * Copies your database from your local assets-folder to the just created empty database in the
	  * system folder, from where it can be accessed and handled.
	  * This is done by transfering bytestream.
	  * */
	 private void copyDataBase() throws IOException {

	  //Open your local db as the input stream
	  final InputStream myInput = myContext.getAssets().open(DB_NAME);

	  // Path to the just created empty db
	  final String outFileName = DB_PATH + DB_NAME;

	  //Open the empty db as the output stream
	  final OutputStream myOutput = new FileOutputStream(outFileName);

	  //transfer bytes from the inputfile to the outputfile
	  final byte[] buffer = new byte[1024];
	  int length;
	  while ((length = myInput.read(buffer)) > 0) {
	   myOutput.write(buffer, 0, length);
	  }

	  //Close the streams
	  myOutput.flush();
	  myOutput.close();
	  myInput.close();

	 }

	 public final void openDataBase() throws Exception {
	  //Open the database
	  final String myPath = DB_PATH + DB_NAME;
	     myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	 }

	 @Override
	 public final synchronized void close() {
	  if (myDataBase != null)
	   myDataBase.close();
	  super.close();
	 }

	 @Override
	 public void onCreate(final SQLiteDatabase arg0) {
	 }

	 @Override
	 public void onUpgrade(final SQLiteDatabase arg0, final int arg1, final int arg2) {
	 }
	


}
