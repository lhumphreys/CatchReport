package com.app.fish.catchreport;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteCursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    SharedPreferences prefs;

    String URL_Login = "http://zoebaker.name/android_login_api/Login.php";

    private Button button;
    private Button button2;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefs = this.getSharedPreferences(this.getPackageName(), this.MODE_PRIVATE);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button8);
        pb = (ProgressBar) findViewById(R.id.progressBar);
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

        button.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);

        EditText userid = (EditText) findViewById(R.id.editText);
        EditText pass = (EditText) findViewById(R.id.editText2);

        final String semail = userid.getText().toString();
        final String spass = pass.getText().toString();

        final StringRequest strReq = new StringRequest(Request.Method.POST, URL_Login, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in

                        // THIS IS WHERE WE SAVE USER IN LOCAL DATABASE
                        addUser(semail, spass);

                        // Create login session
                        prefs.edit().putBoolean("FishAppAuth", true).commit();
                        String id = getID(semail);
                        prefs.edit().putString("FishAppId", id).commit();


                        // Launch main activity
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        button.setVisibility(View.VISIBLE);
                        button2.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Something went wrong: " +
                        error.getMessage(), Toast.LENGTH_LONG).show();
                button.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                pb.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", semail);
                params.put("password", spass);

                return params;
            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.add(strReq);
    }

    private String getID(String email) {
        DatabaseHandler db = null;
        String id = "";
        try {
            db = new DatabaseHandler(this, "Users.db");
            db.createDatabase();
            db.openDatabase();
            SQLiteCursor cursor = db.runQuery("SELECT _id FROM UserInfo WHERE Email=?", new String[]{email});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getString(0);
            }
        } catch (IOException e) {
            Toast.makeText(Login.this, "ID not found in the local database", Toast.LENGTH_LONG).show();
        } finally {
            if (db != null)
                db.close();
        }
        return id;
    }

    private void addUser(String email, String pass)
    {
        DatabaseHandler db = null;
        try{
            db = new DatabaseHandler(this, "Users.db");
            db.createDatabase();
            db.openDatabase();
            db.getWritableDatabase().execSQL("INSERT INTO UserInfo (Email, Pass) VALUES (?, ?)", new String[]{email, pass});
        }catch (IOException e){
            Toast.makeText(Login.this, "ID failed to add to local database", Toast.LENGTH_LONG).show();
        } finally{
            if(db!=null)
                db.close();
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
