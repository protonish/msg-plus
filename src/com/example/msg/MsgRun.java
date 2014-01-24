package com.example.msg;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MsgRun extends Activity implements OnItemClickListener {
	
	Button compose;
	ListView listview;
	int password;
	int def_pass = 1234;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_run);
		refresh();
		//try{
			//InputStream instream = openFileInput("password.txt");//open the text file for reading
            // if file the available for reading
            //if (instream != null) {
            	showDialog();
            //}
            //else
            //{
            //	setPassword();
            //}
		//}
		//catch (IOException e) {
			//e.printStackTrace();
		//}
		
		
		compose = (Button)findViewById(R.id.button1);
		//listview = (ListView)findViewById(R.id.listView1);
		
		//final Context context = this;
		
		
		compose.setOnClickListener(new OnClickListener() {		
			
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClassName("com.example.msg", "com.example.msg.Compose");
				startActivity(i);
				
			}
		});
	}
	
	
	public void showDialog()
	{

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final AlertDialog.Builder realert = new AlertDialog.Builder(this);

        alert.setTitle("Enter Pin to open."); 
        alert.setMessage("Pin:"); 

        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(input);
        alert.setCancelable(false);
        alert.setPositiveButton("Go!", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            try {
                password = Integer.parseInt(input.getText().toString());
                if(password == def_pass)
                {
                	//code
                	dialog.dismiss();
                }
                else
                {
                    String message = "The pin you have entered is incorrect." + " \n \n" + "Please try again!";
                    
                    realert.setTitle("Error");
                    realert.setMessage(message);
                    realert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        	  finish();
                          }
                        });
                    realert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                       public void onClick(DialogInterface dialog, int id) {
                            showDialog();
                       }
                   });
                    realert.create().show();

                }
                
            } catch(Exception e) {
               System.out.println("Exception: " + e);
            } 

          // Do something with value!
        }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            // Canceled.
        	  finish();
          }
        });
        alert.show();
        

	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Show your Alert Box here
        	finish();
        }
        return false;
    }
	
	//starting array list
    ArrayList<String> smsList = new ArrayList<String>();
    
	public void onItemClick( AdapterView<?> parent, View view, int pos, long id ) 
	{
		try 
		{
		    String[] splitted = smsList.get( pos ).split("\n"); 
			String sender = splitted[0];
			String decMsg = "";
			for ( int i = 1; i < splitted.length; ++i )
			{
			    decMsg += splitted[i];
			}
			
			Toast.makeText( this, sender+"\n"+decMsg, Toast.LENGTH_LONG ).show();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void refresh()
	{
		ContentResolver contentResolver = getContentResolver();
		
		String[] colList = {SMSReciever.ADDRESS, SMSReciever.BODY};
		String[] argList = {"cryptsms%"};
		Cursor cursor = contentResolver.query( Uri.parse( "content://sms/inbox" ), colList, "body LIKE ?", argList, "DATE");

		int indexBody = cursor.getColumnIndex( SMSReciever.BODY);
		int indexAddr = cursor.getColumnIndex( SMSReciever.ADDRESS );
		//int indexDate = cursor.getColumnIndex(SMSReciever.DATE );
		
		if ( indexBody < 0 || !cursor.moveToFirst() ) return;
		smsList.clear();
		
		
		do
		{			
			//code to link crypt class
			//String str = cursor.getString( indexAddr ) + "\n" + cursor.getString( indexBody );
			String encMsg = cursor.getString( indexBody ).substring(8);
			//decrypting
			RSA rsa = new RSA(1024);
			String decMsg = rsa.decrypt(encMsg);
			
			/*BigInteger cipherText = new BigInteger(encMsg.getBytes());
			 * 
			RSA rsa = new RSA(1024);
			BigInteger plaintext = rsa.decrypt(cipherText);
        	String decMsg = new String(plaintext.toByteArray());
        	//adding to listview*/
			String str = cursor.getString( indexAddr ) + "\n" + decMsg;
			//smsList.setTextColour();
			smsList.add( str );			
		}
		while( cursor.moveToNext());		
		
		ListView listview = (ListView) findViewById( R.id.listView1);
		listview.setAdapter( new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, smsList) );
		listview.setOnItemClickListener( this );
	}
	
	public void about()
	{
		AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
	    builder2.setTitle("About Msg+ V 1.0");
	    builder2.setMessage("Msg+ is a [concept] secured messaging app which ensures security over the network pass-through.\n\n# Nishant Kambhatla\n(VIT University Chennai)");
	    builder2.setCancelable(true);
	    builder2.setNeutralButton(android.R.string.ok,
	            new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	            dialog.cancel();
	        }
	    });

	    AlertDialog alert12 = builder2.create();
	    alert12.show();
	}

	
	//set password
	/*public void setPassword()
	{
		AlertDialog.Builder alert3 = new AlertDialog.Builder(this);
		        
        alert3.setTitle("Choose Pin."); 
        alert3.setMessage("Choose a Pin:"); 

        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        //input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert3.setView(input);
        //alert3.setCancelable(false);
        alert3.setNeutralButton("Confirm", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
        	try {
            	OutputStreamWriter out=new OutputStreamWriter(openFileOutput("password.txt",MODE_PRIVATE));
            	String pwd = input.getText().toString();
            	out.write(pwd);
            	out.close();
            	input.setText("");
                //Toast.makeText(this,"Password Created !",Toast.LENGTH_LONG).show();
                }
            catch (java.io.IOException e) {
            //do something if an IOException occurs.
            //Toast.makeText(this,"Sorry! Password could't be added",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            }
        }
        });
        
       }
    
	
	public String getPassword()
	{
		String text = new String();
	    
	    
	     try {
	            // open the file for reading we have to surround it with a try
	        
	            InputStream instream = openFileInput("password.txt");//open the text file for reading
	            
	            // if file the available for reading
	            if (instream != null) {                
	                
	              // prepare the file for reading
	              InputStreamReader inputreader = new InputStreamReader(instream);
	              BufferedReader buffreader = new BufferedReader(inputreader);
	               
	              String pwd=null;
	              //We initialize a string "line" 
	              
	            pwd = buffreader.readLine();
	            text = pwd;
	            }
	            }    
	                
	             //now we have to surround it with a catch statement for exceptions
	            catch (IOException e) {
	                e.printStackTrace();
	            }
	            return text;
	}
	
	
	public void settings()
	{
		setPassword();
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_msg_run, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.item1:
	            refresh();
	            return true;
	        case R.id.item2:
	            about();
	            return true;
	        /*case R.id.item3:
	        	settings();
	        	return true;*/
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
