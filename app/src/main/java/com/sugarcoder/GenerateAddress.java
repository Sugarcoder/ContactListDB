package com.sugarcoder.contactlist;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * Created by Miki on 6/17/16.
 */

public class GenerateAddress extends AsyncTask<String, Void, List<Address>> {

        private GoogleMap googleMap;
        private Context context;
        private String cityAdd;


        public GenerateAddress(GoogleMap googleMap, Context ctx) {
            this.googleMap = googleMap;
            this.context = context;
        }

        @Override
        protected List<Address> doInBackground(String ... param) {
            cityAdd = param[0];
            List<Address> cities = null;

            Geocoder fwdGeocoder = new Geocoder(context, Locale.US);
            try {
                cities = fwdGeocoder.getFromLocationName(cityAdd, 1);
                while (cities.size()==0) {
                    cities = fwdGeocoder.getFromLocationName(cityAdd, 1);
                }
                if (cities.size()>0) {
                    Address address = cities.get(0);
                    Log.v("Location is : ", address.toString());
                }
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }

            return cities;
        }


        protected void onPostExecute(List<Address> address) {
            Address location;
            double lati;
            double longi;
            location = address.get(0);
            lati = location.getLatitude();
            longi = location.getLongitude();
            Log.v("Location is: ",location.toString());
            googleMap.addMarker(new MarkerOptions().position(new LatLng(lati, longi)).title("Location"));
        }


}

