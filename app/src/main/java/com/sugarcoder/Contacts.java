package com.sugarcoder.contactlist;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.location.Geocoder;
import android.location.Address;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


/*
 * Created by Miki on 6/16/16.
 */

public class Contacts extends MainActivity implements OnMapReadyCallback {
    int from_Where_I_Am_Coming = 0;
    private DBHelper myDatabase;

    TextView name;
    TextView phone;
    TextView email;
    TextView city;

    private String cities;
    int id_To_Update = 0;

    private GoogleMap mMap;


    Button cam_button;
    ImageView imageView;
    static final int CAMERA_REQUEST_CODE = 1;




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact);

        name = (TextView) findViewById(R.id.editTextName);
        phone = (TextView) findViewById(R.id.editTextPhone);
        city = (TextView) findViewById(R.id.editTextCity);
        email = (TextView) findViewById(R.id.editTextEmail);

        myDatabase = new DBHelper(this);

        Bundle extras = getIntent().getExtras();

        if(extras !=null) {
            int Value = extras.getInt("id");

            if (Value > 0) {
                //means this is the view part not the add contact part.
                Cursor rs = myDatabase.getData(Value);
                id_To_Update = Value;
                rs.moveToFirst();

                String names = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_NAME));
                String phones = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_PHONE));
                String emails = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_EMAIL));
                String cities = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_CITY));

                if (!rs.isClosed()) {
                    rs.close();
                }

                Button b = (Button) findViewById(R.id.button1);
                b.setVisibility(View.INVISIBLE);

                name.setText((CharSequence) names);
                name.setFocusable(false);
                name.setClickable(false);

                phone.setText((CharSequence) phones);
                phone.setFocusable(false);
                phone.setClickable(false);

                email.setText((CharSequence) emails);
                email.setFocusable(false);
                email.setClickable(false);

                city.setText((CharSequence) cities);
                city.setFocusable(false);
                city.setClickable(false);

            }
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        cam_button = (Button) findViewById(R.id.cam_button);
        imageView = (ImageView) findViewById(R.id.imageV);

        cam_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getFile();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });

    }



    private File getFile() {
        File folder = new File("sdcard/app_images");
        if(folder.exists())
            folder.mkdir();

        File imageFile = new File("cameraImage.jpg");

        return imageFile;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        String path = "sdcard/app_images/cameraImage.jpg";
        imageView.setImageDrawable(Drawable.createFromPath(path));
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at the location, and move the camera
        LatLng sanFrancisco = new LatLng(37.77, -122.41);
        mMap.addMarker(new MarkerOptions().position(sanFrancisco).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sanFrancisco));
    }



  /*  @Override
    public void onMapReady(GoogleMap googleMap) {
        loadLocation(googleMap);
    }

    public void loadLocation(GoogleMap googleMap){
        GenerateAddress genAddressTask = new GenerateAddress(googleMap,this);
        genAddressTask.execute(cities);
    }*/




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Bundle extras = getIntent().getExtras();

        if(extras !=null)
        {
            int Value = extras.getInt("id");

            if(Value>0){
                getMenuInflater().inflate(R.menu.show_contact, menu);
            }

            else{
                getMenuInflater().inflate(R.menu.main_menu, menu);
            }
        }
        return true;
    }




    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case R.id.Edit_Contact:
                Button b = (Button)findViewById(R.id.button1);
                b.setVisibility(View.VISIBLE);
                name.setEnabled(true);
                name.setFocusableInTouchMode(true);
                name.setClickable(true);

                phone.setEnabled(true);
                phone.setFocusableInTouchMode(true);
                phone.setClickable(true);

                email.setEnabled(true);
                email.setFocusableInTouchMode(true);
                email.setClickable(true);

                city.setEnabled(true);
                city.setFocusableInTouchMode(true);
                city.setClickable(true);


                return true;


            case R.id.Delete_Contact:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteContact)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                myDatabase.deleteContact(id_To_Update);
                                Toast.makeText(getApplicationContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }
                        })


                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Are you certain?");
                d.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }



    public void run(View view)
    {
        Bundle extras = getIntent().getExtras();

        if(extras !=null)
        {
            int Value = extras.getInt("id");

            if(Value>0){
                if(myDatabase.updateContact(id_To_Update,name.getText().toString(), phone.getText().toString(), email.getText().toString(), city.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                if(myDatabase.insertContact(name.getText().toString(), phone.getText().toString(), email.getText().toString(), city.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(getApplicationContext(), "Not done yet", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        }
    }

}
