package com.example.admin.btl;

import android.content.Intent;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ADMIN on 5/2/2018.
 */

public class Check {

    public String result;

    public  Check(Intent intent){
        Bundle extras = intent.getExtras();
        byte[] imageByte = extras.getByteArray("data");


        try {
            String url = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze";
            String urlParameters = "visualFeatures=Tags&details&language=en";
            url += "?" + urlParameters;

            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/octet-stream");
            con.setRequestProperty("Ocp-Apim-Subscription-Key", "573faea82502498ea5ad0d29e363c71f");


            // Send post request
            con.setDoOutput(true);
            con.setDoInput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());

            wr.write(imageByte);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            result = response.toString();
        }catch (Exception e){

        }
    }


    public String getResult(){
        return this.result;
    }
}
