package com.app.fish.catchreport;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class AskBiologist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_biologist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public void submitQuestion(View view)
    {
        Animation anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.press);
        view.startAnimation(anim);

        /** new intent will be called on animation completion**/
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TextView sub = (TextView) findViewById(R.id.askBioSub);
                TextView bod = (TextView) findViewById(R.id.askBioBod);
                String subject = sub.getText().toString();
                String body = bod.getText().toString();

                if(subject.isEmpty() || body.isEmpty())
                {
                    Toast.makeText(AskBiologist.this, "Please fill in subject and body!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ewufisheriesresearchcenter@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, subject);
                    i.putExtra(Intent.EXTRA_TEXT, body);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(AskBiologist.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
