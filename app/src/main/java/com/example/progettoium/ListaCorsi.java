package com.example.progettoium;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListaCorsi extends AppCompatActivity {

    String[] listviewTitle = new String[7];


    int[] listviewImage = new int[]{
            R.drawable.so, R.drawable.analisi, R.drawable.tweb, R.drawable.sic,
            R.drawable.ro, R.drawable.db, R.drawable.algoritmi};

    String[] listviewShortDescription = new String[]{
            "L’insegnamento fornisce una conoscenza di base dell'architettura interna e del funzionamento dei moderni sistemi operativi.",
            "L'insegnamento ha lo scopo di presentare le nozioni di base su funzioni, grafici e loro trasformazioni.",
            "Obiettivi: Imparare diversi linguaggi e tecnologie per lo sviluppo Web client-side, quali HTML5, CSS, JavaScript, JQuery.",
            "Il corso si propone di fornire agli studenti gli strumenti crittografici e tecnici utilizzati per garantire la sicurezza di reti e calcolatori.",
            "Il corso si propone di fornire agli studenti nozioni generali di calcolo matriciale, algebra e geometria.",
            "L'insegnamento è un'introduzione alle basi di dati e ai sistemi di gestione delle medesime (SGBD).",
            "L’insegnamento ha lo scopo di introdurre i concetti e le tecniche fondamentali per l’analisi e la progettazione di algoritmi.", };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listaimg);
        caricamentoLista();
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 7; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("listview_title", listviewTitle[i]);
            hm.put("listview_discription", listviewShortDescription[i]);
            hm.put("listview_image", Integer.toString(listviewImage[i]));
            aList.add(hm);
        }

        String[] from = {"listview_image", "listview_title", "listview_discription"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.activity_lista_corsi, from, to);
        ListView androidListView = (ListView) findViewById(R.id.list_view);
        androidListView.setAdapter(simpleAdapter);
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id){
                HashMap<String, String> item = (HashMap<String, String>) adattatore.getItemAtPosition(pos);
                String value = item.get("listview_title");
                Intent intent = new Intent(getApplicationContext(), ListaLiberi.class);
                intent.putExtra("corso", value);
                startActivity(intent);
            }
        });
    }

    public void caricamentoLista () {
        GestioneDB db = new GestioneDB(this);
        db.open();
        Cursor c = db.getCorsi();
        int i=0;
        if (c.moveToFirst()) {
            do {
                if (!(c.getString(1).equals("ECONOMIA")))
                    listviewTitle[i++] = c.getString(1);
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