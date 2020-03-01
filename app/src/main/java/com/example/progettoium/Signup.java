package com.example.progettoium;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;

public class Signup extends AppCompatActivity implements View.OnClickListener {

    String richiamo;
    String corso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bundle extras = getIntent().getExtras();
        if(extras == null) richiamo= null;
        else richiamo= extras.getString("provenienza");
        if(extras == null) corso= null;
        else corso= extras.getString("corso");
        setContentView(R.layout.activity_signup);
        Button one = findViewById(R.id.conferma);
        one.setOnClickListener(this);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.conferma:
                GestioneDB db = new GestioneDB(this);
                db.open();
                EditText edit1 = (EditText)findViewById(R.id.nome);
                EditText edit2 = (EditText)findViewById(R.id.cognome);
                EditText edit3 = (EditText)findViewById(R.id.email);
                EditText edit4 = (EditText)findViewById(R.id.password);
                String nome = edit1.getText().toString();
                String cognome = edit2.getText().toString();
                String email = edit3.getText().toString();
                String password = edit4.getText().toString();
                long id = db.inserisciUtente(nome, cognome, email, password);
                Toast.makeText(getApplicationContext(), "Registrazione effettuata!", 3500).show();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                Cursor c = db.getUtente(email);
                editor.putString("Email",email);
                editor.putString("ID",c.getString(0));
                editor.apply();
                db.close();
                Intent intent = null;
                if (richiamo == null)
                    intent = new Intent(this, MainActivity.class);
                else if (richiamo.equals("prenotazioni"))
                    intent = new Intent(this, ListaPrenotazioni.class);
                else if (richiamo.equals("listaliberi")) {
                    intent = new Intent(this, ListaLiberi.class);
                    intent.putExtra("corso", corso);
                }
                this.startActivity(intent);
                break;

            default:
                break;
        }

    }
}
