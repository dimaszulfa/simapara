package com.dimas.simapara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements OnClickListener{
   public CardView viewCard, addCard, aboutCard, logoutCard;
    FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewCard = findViewById(R.id.view_card);
        addCard = findViewById(R.id.add_card);
        aboutCard = findViewById(R.id.about_card);
        logoutCard= findViewById(R.id.logout_card);
        mAuth=FirebaseAuth.getInstance();
        viewCard.setOnClickListener(this);
        addCard.setOnClickListener(this);
        aboutCard.setOnClickListener(this);
        logoutCard.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        Intent i;

        if (v.getId() == R.id.add_card) {
            i = new Intent(MainActivity.this, AddActivity.class);
            startActivity(i);
        } else if(v.getId() == R.id.view_card){
            i = new Intent(MainActivity.this, ViewActivity.class);
            startActivity(i);
        } else if(v.getId() == R.id.about_card){
            i = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(i);
        } else if(v.getId() == R.id.logout_card){
                mAuth.signOut();

            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}