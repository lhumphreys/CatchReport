package com.app.fish.catchreport;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterUser extends AppCompatActivity {

    private Button btnRegister;
    private EditText FirstName;
    private EditText LastName;
    private EditText Email;
    private EditText Password;
    private EditText PasswordConfirm;
    String URL_Register = "http://zoebaker.name/android_login_api/Register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        FirstName = (EditText) findViewById(R.id.firstname);
        LastName = (EditText) findViewById(R.id.lastname);
        Email = (EditText) findViewById(R.id.editText);
        Password = (EditText) findViewById(R.id.editText2);
        PasswordConfirm = (EditText) findViewById(R.id.editText3);
        btnRegister = (Button) findViewById(R.id.button8);
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

        String firstname = FirstName.getText().toString();
        String lastname = LastName.getText().toString();
        String email = Email.getText().toString();
        String password = Password.getText().toString();
        String passwordconfirm = PasswordConfirm.getText().toString();

        if(!firstname.isEmpty() && !lastname.isEmpty() && !email.isEmpty() && !password.isEmpty() && !passwordconfirm.isEmpty())
        {
            if(password.equals(passwordconfirm)) {
                registerUser(firstname, lastname, email, password);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Passwords must match", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please fill in all information", Toast.LENGTH_LONG).show();
        }


        /**
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
         **/
    }

    private void registerUser(final String firstname, final String lastname, final String email, final String password)
    {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_Register, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        // ADD USER TO LOCAL DATABASE
                        //db.addUser(name, email, uid, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully registered!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(RegisterUser.this, Login.class);
                        startActivity(intent);
                        finish();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("firstname", firstname);
                params.put("lastname", lastname);
                params.put("email", email);
                params.put("password", password);
                return params;
            }

        };

        // Adding request to request queue
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.add(strReq);
    }
}
