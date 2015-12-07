package com.app.fish.catchreport;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_user, menu);
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


    public void buttonOnClick2(View view) {

        TextView errmsg = (TextView)findViewById(R.id.textView5);
        errmsg.setText("");
        EditText userid = (EditText)findViewById(R.id.editText);
        EditText pass = (EditText)findViewById(R.id.editText2);
        EditText passconfirm = (EditText)findViewById(R.id.editText3);
        String semail = userid.getText().toString();
        String spass = pass.getText().toString();
        String spassconfirm = passconfirm.getText().toString();

        if(!spass.equals(spassconfirm))
        {
            errmsg.setText("*Passwords must match");
        }
        else {

            boolean exists = false;
            DatabaseHandler auth = new DatabaseHandler(this, "Users.db");

            try {

                auth.createDatabase();
                auth.openDatabase();

                SQLiteCursor curs = auth.runQuery("SELECT * FROM UserInfo WHERE Email = ?", new String[]{semail});
                while(curs.moveToNext()) {
                    exists = true;
                }

                auth.close();

            } catch (Exception e) {

            }

            if(exists == true)
            {
                errmsg.setText("*This Email is taken!");
            }
            else {
                MessageDigest mydigest = null;
                try {
                    mydigest = MessageDigest.getInstance("SHA-1");
                } catch (NoSuchAlgorithmException e) {
                    //algorithm not found
                }
                mydigest.update(spass.getBytes());
                byte[] data = mydigest.digest();
                StringBuffer sb = new StringBuffer();
                String hex = null;

                hex = Base64.encodeToString(data, data.length);
                sb.append(hex);
                String hashpass = sb.toString();

                try {

                    auth.openDatabase();
                    auth.getWritableDatabase().execSQL("INSERT INTO UserInfo(Email,Pass) Values(?,?)",new String[]{semail,hashpass});
                    auth.close();
                    Intent intent = new Intent(this,Login.class);
                    startActivity(intent);

                } catch (Exception e)
                {

                }
            }
        }
    }
}
