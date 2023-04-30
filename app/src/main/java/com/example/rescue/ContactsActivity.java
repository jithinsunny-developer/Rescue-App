package com.example.rescue;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsActivity extends AppCompatActivity {

    private Button addContactBtn;
    private String finalName, finalNumber;
    private String[] names, mobiles;
    private SQLiteDatabase database;
    private ListView addContactListView;

    private void deleteContactFromDB(String name){
        database.execSQL("DELETE FROM rescuecontacts WHERE name = '"+name+"'");
        retriveDataFromDB();
        if(names != null && mobiles != null){
            if(names.length == mobiles.length){
                addContactListView.setAdapter(new CustomSavedList());
            }
        }
    }

    private void  retriveDataFromDB(){
        database.execSQL("CREATE TABLE IF NOT EXISTS rescuecontacts(name varchar not null, mobile varchar not null);");
        Cursor cursor = database.rawQuery("SELECT * FROM rescuecontacts", null);
        names = new String[cursor.getCount()];
        mobiles = new String[cursor.getCount()];
        int i = 0;
        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            do {
                names[i] = cursor.getString(cursor.getColumnIndex("name"));
                mobiles[i] = cursor.getString(cursor.getColumnIndex("mobile"));
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void  storeToDatabase(String name, String mobile){
        database.execSQL("CREATE TABLE IF NOT EXISTS rescuecontacts(name varchar not null, mobile varchar not null);");
//        database.execSQL("DELETE FROM rescuecontacts;");

        Cursor cursor = database.rawQuery("SELECT * FROM rescuecontacts WHERE name = \""+name+"\"", null);
//        String[] names = new String[cursor.getCount()];
//        String[] mobiles = new String[cursor.getCount()];
//        int i = 0;
//        if (cursor.getCount() > 0)
//        {
//            cursor.moveToFirst();
//            do {
//                names[i] = cursor.getString(cursor.getColumnIndex("name"));
//                mobiles[i] = cursor.getString(cursor.getColumnIndex("mobile"));
//                i++;
//            } while (cursor.moveToNext());
//            cursor.close();
//        }

        if(cursor.getCount() == 0){
            Cursor limit_cursor = database.rawQuery("SELECT * FROM rescuecontacts ", null);
            if(limit_cursor.getCount() > 4){
                Toast.makeText(getApplicationContext(), "Not select more then 5 contacts !", Toast.LENGTH_SHORT).show();
            }else{
                database.execSQL("INSERT INTO rescuecontacts VALUES(\""+name+"\", \""+mobile+"\");");
            }

        }else{
            Toast.makeText(this, "Already Selected !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar toolbar = (Toolbar)findViewById(R.id.contacts_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        database = openOrCreateDatabase("Rescue", Context.MODE_PRIVATE, null);
        addContactBtn = (Button)findViewById(R.id.add_contact_btn);
        addContactListView = (ListView)findViewById(R.id.add_contacts_list_view);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)){
                addContactBtn.setEnabled(false);
                Toast.makeText(this, "Change settings manually to go to your contacts database settings!", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS}, 1618);
            }
        }

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1620);
            }
        });

        addContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(ContactsActivity.this)
                        .setTitle("Delete From Rescue")
                        .setMessage("Are you sure you want to delete this contact("+names[position].toUpperCase()+")?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                deleteContactFromDB(names[position]);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        retriveDataFromDB();
        if(names != null && mobiles != null){
            if(names.length==mobiles.length){
                addContactListView.setAdapter(new CustomSavedList());
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1618){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

            }else{
                addContactBtn.setEnabled(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1620 && data!=null){
            Uri contactsURI = data.getData();
            ContentResolver contentResolver = getContentResolver();
            String id = "";

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Cursor cursor = contentResolver.query(contactsURI, new String[]{
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME
                }, null, null);



                try{
                    cursor.moveToFirst();
                    id = cursor.getString(0);
                    finalName = cursor.getString(1);
                }finally {
                    cursor.close();
                }

                Cursor cursor1 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                        new String[]{id}, null);

                try{
                    cursor1.moveToFirst();
                    String number = cursor1.getString(0);

                    String[] strarr = number.split(" ");
                    number = "";
                    for(String s: strarr){
                        number+=s;
                    }
                    finalNumber = number;
                    Log.i(finalName, finalNumber);
                    storeToDatabase(finalName, finalNumber);
                    retriveDataFromDB();
                    if(names != null && mobiles != null){
                        if(names.length==mobiles.length){
                            addContactListView.setAdapter(new CustomSavedList());
                        }
                    }
                }finally {
                    cursor1.close();
                }

            }else{
                Toast.makeText(this, "Your OS version is not supported !", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class CustomSavedList extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.custom_contact_list, parent, false);
            TextView name = (TextView) view.findViewById(R.id.contact_name);
            TextView num = (TextView)view.findViewById(R.id.contact_num);
            name.setText(names[position]);
            num.setText(mobiles[position]);
            return view;
        }
    }
}

/*
*
*
* contactBottomNavigationView = (BottomNavigationView)findViewById(R.id.contacts_bottom_navigationView);

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragement_container, new SavedContactFragement());
        transaction.addToBackStack(null);
        transaction.commit();

        contactBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()){
                    case R.id.saved_contacts_id:
                        fragment = new SavedContactFragement();
                        loadFragement(fragment);
                        return true;
                    case R.id.list_id:
                        fragment = new ContactListFragement();
                        loadFragement(fragment);
                        return true;
                }
                return false;
            }
        });
*
* private void loadFragement(Fragment fragment){
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragement_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
*
* <FrameLayout
        android:id="@+id/fragement_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior"
        android:layout_above="@id/contacts_bottom_navigationView"/>

    <android.support.design.widget.BottomNavigationView
        android:id="@+id/contacts_bottom_navigationView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/color0"
        android:layout_alignParentBottom="true"
        app:menu="@menu/bottom_navigation" />
*
*
* **/

