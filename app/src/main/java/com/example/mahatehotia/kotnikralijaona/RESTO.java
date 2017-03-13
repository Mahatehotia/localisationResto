package com.example.mahatehotia.kotnikralijaona;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by Guillaume on 21/10/2016.
 */
public class RESTO {
    public static final String FICHIER_SAUVEGARDE_OBJETS = "/sdcard/FICHIER_DOBJETS.bin";

    private int id;
    private String nom;
    private String adresse;
    private static com.example.mahatehotia.kotnikralijaona.DatabaseHandler dbh = null;

    public RESTO(int id, String nom, String adresse, Context context) {
        this.id=id;
        this.nom=nom;
        this.adresse=adresse;
        if (dbh ==null){
            dbh = new DatabaseHandler(context);
        }
    }
    public RESTO(String nom, String adresse) {
        this.nom=nom;
        this.adresse=adresse;
    }

    public String getNom() {
        return nom;
    }
    public String getAdresse() {
        return adresse;
    }
    public String toString(){
        return    " Nom : "+ getNom()
                + ", Adresse : " + getAdresse();
    }

    public void save(){
        try {
            if (dbh != null){
                dbh.addResto(this);
            }
        }
        catch(Exception ex) {
            Log.e("Serialization Error:", ex.getMessage());
        }
    }

    public static String dernier_5(Context context){
        if (dbh ==null){
            dbh = new DatabaseHandler(context);
        }
        String st = "";
        try {
            if (dbh != null){
                ArrayList<RESTO> restos = dbh.recupereLesRestos();
                for (RESTO i : restos){
                    st += i.toString() + "\n";
                }
            }
        }
        catch(Exception ex) {
            Log.e("Serialization Error:", ex.getMessage());
        }
        return st;
    }

    public static RESTO restaureResto() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(FICHIER_SAUVEGARDE_OBJETS)));
            Object o = ois.readObject();
            RESTO resto = (RESTO) o;
            ois.close();
            return resto;
        }
        catch(Exception ex)
        {
            Log.e("Serialization Error:", ex.getMessage());
        }
        return null;
    }
}
