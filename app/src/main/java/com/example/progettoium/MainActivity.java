package com.example.progettoium;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button one = findViewById(R.id.corsi);
        one.setOnClickListener(this);
        Button two = findViewById(R.id.prenotazioni);
        two.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("Email", "");
        if(!(name.equalsIgnoreCase(""))) menu.findItem(R.id.account).setTitle("Logout");
        return true;
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch(item.getItemId()) {
            case R.id.account:
                String name = preferences.getString("Email", "");
                if(name.equalsIgnoreCase("")) {
                    Intent intent = new Intent(this, LoginSignup.class);
                    this.startActivity(intent);
                } else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("Email","");
                    editor.putString("ID","");
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Logout effettuato!", 3500).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                }
                break;
            case R.id.info:
                Toast.makeText(getApplicationContext(), "Progetto di Nasso Alessandro", 3500).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.corsi:
                Intent intent = new Intent(this, ListaCorsi.class);
                this.startActivity(intent);
                break;

            case R.id.prenotazioni:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String name = preferences.getString("Email", "");
                if(name.equalsIgnoreCase("")) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Prenotazione")
                            .setMessage("Devi prima effettuare il login! Procedere ora?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent intent2 = new Intent(getApplicationContext(), LoginSignup.class);
                                    intent2.putExtra("provenienza", "prenotazioni");
                                     startActivity(intent2);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    Intent intent2 = new Intent(this, ListaPrenotazioni.class);
                    startActivity(intent2);
                }
                break;

            default:
                break;
        }

    }
}
