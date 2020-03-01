package com.example.progettoium;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListaPrenotazioni extends AppCompatActivity {

    String[] listviewTitle = new String[8];

    String[] id_pren = new String[8];

    int[] listviewImage = new int[8];

    String[] listviewShortDescription = new String[8];

    int contaElem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listaimg);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        caricamentoLista(preferences.getString("ID", ""));
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < contaElem; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("listview_title", listviewTitle[i]);
            hm.put("listview_discription", listviewShortDescription[i]);
            hm.put("listview_image", Integer.toString(listviewImage[i]));
            hm.put("id_pren", id_pren[i]);
            aList.add(hm);
        }

        if (contaElem==0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Non hai ancora effettuato prenotazioni!\nVuoi visualizzare la lista dei corsi?")
                    .setCancelable(true)
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), ListaCorsi.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }}).show();;
            AlertDialog alert = builder.create();
            alert.show();
        }

        String[] from = {"listview_image", "listview_title", "listview_discription"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.activity_lista_prenotazioni, from, to);
        ListView androidListView = (ListView) findViewById(R.id.list_view);
        androidListView.setAdapter(simpleAdapter);
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id){
                final HashMap<String, String> item = (HashMap<String, String>) adattatore.getItemAtPosition(pos);
                final String value = item.get("listview_title");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final String name = preferences.getString("ID", "");
                if(name.equalsIgnoreCase("")) {
                    Intent intent = new Intent(getApplicationContext(), LoginSignup.class);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(ListaPrenotazioni.this)
                            .setTitle("Prenotazione")
                            .setMessage("Vuoi eliminare la prenotazione?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    GestioneDB db = new GestioneDB(getApplicationContext());
                                    db.open();
                                    db.cancellaPrenotazione(item.get("id_pren"));
                                    db.close();
                                    Intent intent = new Intent(getApplicationContext(), ListaPrenotazioni.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "Prenotazione eliminata!", Toast.LENGTH_SHORT).show();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });
    }

    public void caricamentoLista (String id_utente) {
        GestioneDB db = new GestioneDB(this);
        db.open();
        Cursor c = db.getPrenotati(id_utente);
        contaElem = c.getCount();
        id_pren = new String[contaElem];
        listviewTitle = new String[contaElem];
        listviewShortDescription = new String[contaElem];
        int i=0;
        if (c.moveToFirst()) {
            do {
                listviewTitle[i] = "Professor "+c.getString(0);
                listviewShortDescription[i] = "Giorno: "+c.getString((1))+"      Ora: "+c.getString((2));
                String[] words = c.getString(0).split("\\s+");
                listviewImage[i] = getResources().getIdentifier(words[0].toLowerCase(), "drawable", getPackageName());
                id_pren[i++] = c.getString(3);
            } while (c.moveToNext());
        }
        db.close();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
