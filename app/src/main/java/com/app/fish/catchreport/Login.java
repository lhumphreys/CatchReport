package com.app.fish.catchreport;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteCursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.security.MessageDigest;

public class Login extends AppCompatActivity {

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefs = this.getSharedPreferences(this.getPackageName(),this.MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonOnClick(View v) {

        Button button = (Button)findViewById(R.id.button);
        Button button2 = (Button)findViewById(R.id.button8);
        ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);

        button.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);

        EditText userid = (EditText)findViewById(R.id.editText);
        EditText pass = (EditText)findViewById(R.id.editText2);
        String semail = userid.getText().toString();
        String spass = pass.getText().toString();

        boolean found = false;

        try {
            DatabaseHandler auth = new DatabaseHandler(this, "Users.db");
            auth.createDatabase();
            auth.openDatabase();
            try {
                SQLiteCursor curs = auth.runQuery("SELECT * FROM UserInfo WHERE Email = ?", new String[]{semail});
                found = true;
                try {
                    MessageDigest mydigest = MessageDigest.getInstance("SHA-1");
                    mydigest.update(spass.getBytes());
                    byte[] data = mydigest.digest();
                    StringBuffer sb = new StringBuffer();
                    String hex = null;

                    hex = Base64.encodeToString(data, data.length);
                    sb.append(hex);

                    curs.moveToFirst();
                    String getpass = curs.getString(2);

                    if (sb.toString().equals(getpass)) {
                        prefs.edit().putBoolean("FishAppAuth",true).commit();

                    } else {
                        prefs.edit().putBoolean("FishAppAuth",false).commit();
                    }

                } catch (Exception e) {

                }
            }catch(Exception e)
            {
                //if exception was caught email was not found
                found = false;
            }
            if(found == false) {
                button.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                pb.setVisibility(View.INVISIBLE);
                TextView errmsg = (TextView) findViewById(R.id.textView3);
                errmsg.setText("*The email or password you entered was incorrect.\n" +
                        "*If you have not made an account please press Register.");
            }


            if (prefs.getBoolean("FishAppAuth",false) == true) {

                final Intent intent = new Intent(Login.this, MainActivity.class);
                prefs.edit().putString("FishAppID",semail).commit();
                startActivity(intent);

            } else {
                AlertDialog.Builder authFail = new AlertDialog.Builder(this);
                authFail.setMessage("Authentication Failed!");
                authFail.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Button button = (Button) findViewById(R.id.button);
                        Button button2 = (Button) findViewById(R.id.button8);
                        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                        pb.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.VISIBLE);
                        button2.setVisibility(View.VISIBLE);
                    }
                });

                AlertDialog alertDialog = authFail.create();
                alertDialog.show();
            }
        }catch(Exception e)
        {
            //do something
        }
    }

    public void buttonOnClick2(View view) {

        Intent intent = new Intent(this,RegisterUser.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }
}
