package com.example.progettoium;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.database.Cursor;

public class LoginSignup extends AppCompatActivity implements View.OnClickListener {

    String richiamo = null;
    String corso = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bundle extras = getIntent().getExtras();
        if(extras == null) richiamo= null;
        else richiamo= extras.getString("provenienza");
        if(extras == null) corso= null;
        else corso= extras.getString("corso");
        setContentView(R.layout.activity_login_signup);
        Button one = findViewById(R.id.login);
        one.setOnClickListener(this);
        Button two = findViewById(R.id.signup);
        two.setOnClickListener(this);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login:
                GestioneDB db = new GestioneDB(this);
                db.open();
                EditText edit = (EditText)findViewById(R.id.email);
                EditText edit1 = (EditText)findViewById(R.id.password);
                String email = edit.getText().toString();
                String password = edit1.getText().toString();
                Cursor c = db.getUtenti();
                boolean debug = true;
                if (c.moveToFirst()) {
                    do {
                        if (c.getString(3).equals(email) && c.getString(4).equals(password)) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("Email",c.getString(3));
                            editor.putString("ID",c.getString(0));
                            editor.apply();
                            Toast.makeText(getApplicationContext(), "Login effettuato!", 3500).show();
                            debug = false;
                            db.close();
                            Intent intent = new Intent(this, MainActivity.class);
                            if (richiamo!= null && richiamo.equals("prenotazioni")) {
                                intent = new Intent(this, ListaPrenotazioni.class);
                            } else if (richiamo!= null && richiamo.equals("listaliberi")) {
                                intent = new Intent(this, ListaLiberi.class);
                                intent.putExtra("corso", corso);
                            }
                            this.startActivity(intent);
                        }
                    } while (c.moveToNext());
                    if (debug) Toast.makeText(getApplicationContext(), "L'utente non esiste!", 3500).show();
                }
                db.close();
                break;

            case R.id.signup:
                Intent intent = new Intent(this, Signup.class);
                intent.putExtra("provenienza", richiamo);
                intent.putExtra("corso", corso);
                this.startActivity(intent);
                break;

            default:
                break;
        }

    }

}
