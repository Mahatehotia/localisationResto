package com.example.mahatehotia.kotnikralijaona;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mahatehotia on 21/10/16.
 */

public class RestaurantAsynchTaskVide extends AsyncTask<String, Void, Void> {


    private Context mContext = null;
    private String content = "";
    private String error = null;

    public RestaurantAsynchTaskVide(Context context){
        super();
        this.mContext = context;
    }


    protected void onPreExecute() {
        Intent i = new Intent(MainActivity.MESSAGE_OPPERATION);
        i.putExtra(MainActivity.CONTENU_MESSAGE, "");
        i.putExtra(MainActivity.CLEF_TYPE_DE_MESSAGE,MainActivity.TYPE_MESSAGE_DEBUT_ATTENTE);
        mContext.sendBroadcast(i);
    }


    @Override
    protected Void doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.flush();
            content = convertStreamToString(connection.getInputStream());
        } catch (Exception e){
            error = e.getMessage();
        }
        return null;
    }

    protected void onPostExecute(Void unused) {
        Intent intent = new Intent(MainActivity.MESSAGE_OPPERATION);
        intent.putExtra(MainActivity.CLEF_TYPE_DE_MESSAGE, MainActivity.TYPE_MESSAGE_FIN_ATTENTE);
        mContext.sendBroadcast(intent);

        intent.putExtra(MainActivity.CLEF_TYPE_DE_MESSAGE, MainActivity.TYPE_MESSAGE_JSON_BRUT);
        if (error != null) {
            intent.putExtra(MainActivity.CONTENU_MESSAGE, "Output : " + error);
            mContext.sendBroadcast(intent);
        } else {
            intent.putExtra(MainActivity.CONTENU_MESSAGE, content);
            mContext.sendBroadcast(intent);
        }
    }

    private String convertStreamToString(InputStream inputStream) {
        String reponse =null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            for (int count; (count = inputStream.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            reponse= new String(response, "UTF-8");
        } catch (Exception e){
        }
        return  reponse;
    }
}
