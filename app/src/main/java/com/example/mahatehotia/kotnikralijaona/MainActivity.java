package com.example.mahatehotia.kotnikralijaona;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "MainActivity";

    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;
    private EditText et_Rayon;
    private RadioButton rNote;
    private RadioButton rPres;
    //private TextView textNom;
    //private TextView textAdresse;
    private TextView txtJson;
    private Button bTrouver;

    private ProgressDialog dialog;
    private Context context;
    private Intent intent;
    private BroadcastReceiver br;

    public final static String MESSAGE_OPPERATION ="MESSAGE";
    public final static String CLEF_TYPE_DE_MESSAGE ="CLEF TYPE DE MESSAGE";
    public final static int TYPE_MESSAGE_DEBUT_ATTENTE =0;
    public final static int TYPE_MESSAGE_FIN_ATTENTE=1;
    public final static int TYPE_MESSAGE_JSON_BRUT =3;
    public final static String CONTENU_MESSAGE="contenu message";

    private SharedPreferences sharedPreferences;

    private String monUrl;
    private String nomRestaurant;
    private String adresseRestaurant;
    private Double mLat,mLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_Rayon = (EditText) findViewById(R.id.et_Rayon);
        rNote = (RadioButton) findViewById(R.id.rNote);
        rPres = (RadioButton) findViewById(R.id.rPres);
        bTrouver = (Button) findViewById(R.id.bTrouver);
        txtJson = (TextView) findViewById(R.id.txtJson);

        dialog = new ProgressDialog(this);


        bTrouver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monUrl = leChoix();
                new RestaurantAsynchTaskVide(getApplicationContext()).execute(monUrl);
                //sharedPreferences.edit()
                //        .putString(getString(R.string.this),nomRestaurant,adresseRestaurant)
                //.commit();
            }
        });

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getIntExtra(CLEF_TYPE_DE_MESSAGE, -1)) {
                    case (TYPE_MESSAGE_DEBUT_ATTENTE):
                        dialog.setMessage("Patientez...");
                        dialog.show();
                        break;
                    case (TYPE_MESSAGE_FIN_ATTENTE):
                        dialog.dismiss();
                        break;
                    case (TYPE_MESSAGE_JSON_BRUT):
                        String content = intent.getStringExtra(CONTENU_MESSAGE);
                        txtJson.setText(content);
                        try {
                            //recupere le contenu de la réponse
                            String contentReponse = txtJson.getText().toString();

                            //a partir de la chaine de caractere, on peut parcourir le JSON...
                            JSONObject racine = new JSONObject(contentReponse);
                            nomRestaurant = racine.getJSONArray("results")
                                    .getJSONObject(0)
                                    .getString("name");
                            adresseRestaurant = racine.getJSONArray("results")
                                    .getJSONObject(0)
                                    .getString("vicinity");

                            RESTO resto = new RESTO(nomRestaurant,adresseRestaurant);
                            resto.save();

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        txtJson.setText("Résultat"+"\n"+"Nom: "+nomRestaurant+"\n"+"Adresse: "+adresseRestaurant);
                        break;
                }
            }

        };
    }


    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(br, new IntentFilter(MESSAGE_OPPERATION));
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
    }

    public String leChoix(){
        if (rNote.isChecked()){
            String distance = "rankby=distance";
            return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?type=restaurant&"
                    +distance+"&location=47.643352,6.840979&key=AIzaSyB5GLgexpiERVr5KGTv0aEqdvrcQUWV6jY";
        } else {
            int distanceMetre = Integer.parseInt(et_Rayon.getText().toString());
            return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?type=restaurant&radius="
                    +distanceMetre+"&location=47.643352,6.840979&key=AIzaSyB5GLgexpiERVr5KGTv0aEqdvrcQUWV6jY";
        }
    }
}
