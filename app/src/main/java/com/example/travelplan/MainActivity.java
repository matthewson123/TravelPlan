package com.example.travelplan;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    GridView gridView;
    ArrayList<TravelPlan> list;
    TravelListAdapter adapter = null;
    public static SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants.activityList.add(this);
        switchLanguage(Constants.langae);

        sqLiteHelper = new SQLiteHelper(this, "TravelPlanDB.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS TRAVEL(Id INTEGER PRIMARY KEY AUTOINCREMENT, place VARCHAR, day VARCHAR, time VARCHAR , address VARCHAR, image BLOB)");

        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new TravelListAdapter(this, R.layout.travel_item, list);
        gridView.setAdapter(adapter);

        // get all data from sqlite
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM TRAVEL");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String place = cursor.getString(1);
            String day = cursor.getString(2);
            String time = cursor.getString(3);
            String address = cursor.getString(4);
            byte[] image = cursor.getBlob(5);

            list.add(new TravelPlan(place, day, time, address, image, id));
        }
        adapter.notifyDataSetChanged();

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence[] items = {getResources().getText(R.string.update), getResources().getText(R.string.delete)};
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                dialog.setTitle(R.string.choose_an_action);
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            // update
                            Cursor c = sqLiteHelper.getData("SELECT id FROM TRAVEL");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            // show dialog update at here
                            showDialogUpdate(MainActivity.this, arrID.get(position));

                        } else {
                            // delete
                            Cursor c = sqLiteHelper.getData("SELECT id FROM TRAVEL");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });

    }

    ImageView travelImage;

    private void showDialogUpdate(Activity activity, final int position) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_travel_plan_activity);
        dialog.setTitle("Update");

        travelImage = (ImageView) dialog.findViewById(R.id.imageViewTravel);
        final EditText edtPlace = (EditText) dialog.findViewById(R.id.edtTravelPlaceName);
        final EditText edtDay = (EditText) dialog.findViewById(R.id.edtDay);
        final EditText edtTime = (EditText) dialog.findViewById(R.id.edtTime);
        final EditText edtAddress = (EditText) dialog.findViewById(R.id.edtPlaceAddress);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);

        // get  data of row clicked from sqlite
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM TRAVEL WHERE id=" + position);
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String place = cursor.getString(1);
            edtPlace.setText(place);
            String day = cursor.getString(2);
            edtDay.setText(day);
            String time = cursor.getString(3);
            edtTime.setText(time);
            String address = cursor.getString(4);
            edtAddress.setText(address);
            byte[] image = cursor.getBlob(5);
            travelImage.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

            list.add(new TravelPlan(place, day, time, address, image, id));
        }

        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        travelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request photo library
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sqLiteHelper.updateData(
                            edtPlace.getText().toString().trim(),
                            edtDay.getText().toString().trim(),
                            edtTime.getText().toString().trim(),
                            edtAddress.getText().toString().trim(),
                            AddTravelPlan.imageViewToByte(travelImage),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update successfully!!!", Toast.LENGTH_SHORT).show();
                } catch (Exception error) {
                    Log.e("Update error", error.getMessage());
                }
                updateTravelList();
            }
        });
    }

    private void showDialogDelete(final int idTravel) {
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(MainActivity.this);

        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure you want to this delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sqLiteHelper.deleteData(idTravel);
                    Toast.makeText(getApplicationContext(), "Delete successfully!!!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                updateTravelList();
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void updateTravelList() {
        // get all data from sqlite
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM TRAVEL");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String place = cursor.getString(1);
            String day = cursor.getString(2);
            String time = cursor.getString(3);
            String address = cursor.getString(4);
            byte[] image = cursor.getBlob(5);

            list.add(new TravelPlan(place, day, time, address, image, id));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 888) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 888 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                travelImage.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add_record) {
            Intent add_item = new Intent(this, AddTravelPlan.class);
            startActivity(add_item);
        } else if (id == R.id.map) {
            Intent map = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(map);
        } else {
            Intent language = new Intent(this, ChangeLanguage.class);
            startActivity(language);
        }
        return super.onOptionsItemSelected(item);
    }

    public static class Constants {
        //system default lanhuage to english
        public static String langae = "en";
        public static List<Activity> activityList = new ArrayList<>();
    }

    //main code of change language
    protected void switchLanguage(String language) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        switch (language) {
            case "en":
                config.locale = Locale.ENGLISH;
                resources.updateConfiguration(config, dm);
                break;
            case "zh":
                config.locale = Locale.CHINESE;
                resources.updateConfiguration(config, dm);
                break;
            default:
                config.locale = Locale.US;
                resources.updateConfiguration(config, dm);
                break;
        }
    }
}
