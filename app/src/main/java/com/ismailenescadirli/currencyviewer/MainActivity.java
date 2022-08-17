package com.ismailenescadirli.currencyviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRefresh=findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(view -> reload());

        reload();
    }

     private String getDataFromUrl(String httpsAddress) {
        try {
            URL url = new URL(httpsAddress);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            int resCode = httpsConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpsConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String url = "https://api.currencyapi.com/v3/latest?apikey=p0WloxNOvfAPUHiUdvepuhuwY7Fxq1z8xbOeNrTo&currencies=EUR%2CUSD%2CGBP%2CDOP%2CRUB%2CAZN%2CBRL%2CSEK&base_currency=TRY";

    class Root {
        public Abbreviation data;
    }
    class Abbreviation{
        public Currencies AZN;
        public Currencies BRL;
        public Currencies DOP;
        public Currencies EUR;
        public Currencies GBP;
        public Currencies RUB;
        public Currencies SEK;
        public Currencies USD;
    }
    class Currencies {
        public String code;
        public double value;
    }

    private String calculation(double value){
        return "" + Math.round(1/value*100)/100.0;
    }

    private void reload() {
        TextView euro = findViewById(R.id.EuroId);
        TextView rouble = findViewById(R.id.RoubleId);
        TextView dollar = findViewById(R.id.DollarId);
        TextView pound = findViewById(R.id.PoundId);
        TextView krona = findViewById(R.id.KronaId);
        TextView manat = findViewById(R.id.ManatId);
        TextView real = findViewById(R.id.RealId);
        TextView peso = findViewById(R.id.PesoId);
        euro.setText("-");
        rouble.setText("-");
        dollar.setText("-");
        pound.setText("-");
        krona.setText("-");
        manat.setText("-");
        real.setText("-");
        peso.setText("-");
        Thread thread = new Thread(() -> {
            String jsonData = getDataFromUrl(url);
            if (jsonData == null) return;
            Gson gson = new Gson();
            Root user = gson.fromJson(jsonData,Root.class);
            runOnUiThread(() -> {
                euro.setText(calculation(user.data.EUR.value));
                rouble.setText(calculation(user.data.RUB.value));
                dollar.setText(calculation(user.data.USD.value));
                pound.setText(calculation(user.data.GBP.value));
                krona.setText(calculation(user.data.SEK.value));
                manat.setText(calculation(user.data.AZN.value));
                real.setText(calculation(user.data.BRL.value));
                peso.setText(calculation(user.data.DOP.value));
            });
        });
        thread.start();
    }

}