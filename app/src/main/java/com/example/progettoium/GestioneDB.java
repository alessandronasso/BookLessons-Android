package com.example.progettoium;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GestioneDB {

    static final String KEY_RIGAID = "id";
    static final String KEY_NOME = "nome";
    static final String KEY_COGNOME = "cognome";
    static final String KEY_EMAIL = "email";
    static final String KEY_PASSWORD = "password";
    static final String TABELLA_UTENTE = "utente";
    static final String TABELLA_LIBERE = "libere";
    static final String TABELLA_DOCENZA = "docenza";
    static final String TABELLA_CORSO = "corso";
    static final String DATABASE_NOME = "TestDB";
    static final int DATABASE_VERSIONE = 1;

    static final String DATABASE_CREAZIONE = "CREATE TABLE utente (id integer primary key autoincrement, nome text not null, cognome text not null, email text not null, password text not null);";
    static final String DATABASE2 = "CREATE TABLE corso (id integer primary key autoincrement, nome text not null);";
    static final String DATABASE3 = "CREATE TABLE libere (id integer primary key autoincrement, id_docenza integer not null, giorno text not null, ora text not null);";
    static final String DATABASE4 = "CREATE TABLE docenza (id integer primary key autoincrement, nome_corso text not null, nome_prof text not null);";
    static final String DATABASE5 = "CREATE TABLE prenotate (id_utente integer, id_prenotazione integer, PRIMARY KEY (id_utente, id_prenotazione));";

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public GestioneDB(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NOME, null, DATABASE_VERSIONE);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE4);
                db.execSQL(DATABASE_CREAZIONE);
                db.execSQL(DATABASE2);
                db.execSQL(DATABASE3);
                db.execSQL(DATABASE5);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            Log.w(DatabaseHelper.class.getName(),"Aggiornamento database dalla versione " + oldVersion + " alla "
                    + newVersion + ". I dati esistenti verranno eliminati.");
            onCreate(db);
        }

    }


    public GestioneDB open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    public void close()
    {
        DBHelper.close();
    }


    public long inserisciUtente(String nome, String cognome, String email, String password)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NOME, nome);
        initialValues.put(KEY_COGNOME, cognome);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_PASSWORD, password);
        return db.insert(TABELLA_UTENTE, null, initialValues);
    }


    public Cursor getUtenti()  {
        return db.query(TABELLA_UTENTE, new String[] {KEY_RIGAID, KEY_NOME, KEY_COGNOME, KEY_EMAIL, KEY_PASSWORD}, null, null, null, null, null);
    }

    public Cursor getUtente(String email) {
        String sql = "SELECT id FROM utente WHERE email='"+email+"'";
        Cursor c = db.rawQuery(sql, null);
        c.moveToNext();
        return c;
    }


    /*public Cursor getUtente(String email) throws SQLException
    {
        Cursor mCursore = db.query(true, TABELLA_UTENTE, new String[] {KEY_RIGAID, KEY_NOME, KEY_COGNOME, KEY_EMAIL, KEY_PASSWORD}, KEY_EMAIL + "= 'email'", null, null, null, null, null);
        if (mCursore != null) {
            mCursore.moveToFirst();
        }
        return mCursore;
    }*/

    public long inserisciCorso(String nome) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NOME, nome);
        return db.insert(TABELLA_CORSO, null, initialValues);
    }

    public long inserisciDocenza(String n1, String n2) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("nome_corso", n1);
        initialValues.put("nome_prof", n2);
        return db.insert(TABELLA_DOCENZA, null, initialValues);
    }

    public Cursor getDocenze() {
        return db.query("docenza", new String[] {KEY_RIGAID, "nome_corso", "nome_prof"}, null, null, null, null, null);

    }

    public long inserisciLibere(int n2, String n3, String n4) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("id_docenza", n2);
        initialValues.put("giorno", n3);
        initialValues.put("ora", n4);
        return db.insert(TABELLA_LIBERE, null, initialValues);
    }

    public Cursor getLiberi(String corso)  {
        String sql= "SELECT docenza.nome_prof, libere.giorno, libere.ora, libere.id "+
                "FROM docenza INNER JOIN libere ON docenza.id = libere.id_docenza "+
                "WHERE docenza.nome_corso ='"+corso+"' "+
                "AND libere.id NOT IN ( "+
                "SELECT id_prenotazione "+
                "FROM prenotate) "+
                "ORDER BY giorno";
        return db.rawQuery(sql, null);
    }

    public Cursor getLiberiLoggato(String corso, String name)  {
        String sql= "SELECT docenza.nome_prof, l1.giorno, l1.ora, l1.id "+
                "FROM docenza INNER JOIN libere l1 ON docenza.id = l1.id_docenza "+
                "WHERE docenza.nome_corso ='"+corso+"' "+
                "AND l1.id NOT IN ( "+
                "SELECT id_prenotazione "+
                "FROM prenotate) "+
                "AND NOT EXISTS (SELECT 1 " +
                                "FROM (SELECT l2.giorno, l2.ora "+
                                       "FROM libere l2 INNER JOIN prenotate ON l2.id = prenotate.id_prenotazione "+
                                       "WHERE prenotate.id_utente='"+name+"') AS l2 "+
                                "WHERE l1.giorno=l2.giorno " +
                                "AND l1.ora=l2.ora)"+
                "ORDER BY giorno";
        return db.rawQuery(sql, null);
    }

    public Cursor getTutteLibere( ) {
        String sql = "SELECT * "+
                "FROM libere";
        return db.rawQuery(sql, null);
    }

    public Cursor getPrenotati(String id_utente)  {
        String sql= "SELECT docenza.nome_prof, libere.giorno, libere.ora, libere.id "+
                "FROM docenza INNER JOIN libere ON docenza.id = libere.id_docenza "+
                "WHERE libere.id IN ( "+
                "SELECT id_prenotazione "+
                "FROM prenotate "+
                "WHERE id_utente ='"+id_utente+"' )"+
                "ORDER BY giorno";
        return db.rawQuery(sql, null);
    }

    public void effettuaPrenotazione(String email, String id_pren) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("id_utente", email);
        initialValues.put("id_prenotazione", id_pren);
        db.insert("prenotate", null, initialValues);
    }

    public Cursor getCorsi()  {
        return db.query(TABELLA_CORSO, new String[] {KEY_RIGAID, KEY_NOME}, null, null, null, null, null);
    }

    public void cancellaPrenotazione(String id) {
        String sql = "DELETE FROM prenotate WHERE id_prenotazione="+id;
        db.execSQL(sql);
    }

    public void cancella () {
        db.delete("libere", null, null);
    }

    public void crea()  {
        db.execSQL("DROP TABLE libere");
        db.execSQL(DATABASE3);
    }

}