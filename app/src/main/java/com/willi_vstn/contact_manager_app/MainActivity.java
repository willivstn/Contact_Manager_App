package com.willi_vstn.contact_manager_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.willi_vstn.contact_manager_app.adapter.ContactsAdapter;
import com.willi_vstn.contact_manager_app.db.DatabaseHelper;
import com.willi_vstn.contact_manager_app.db.entity.Contact;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Variables
    private ContactsAdapter contactsAdapter;
    private final ArrayList<Contact> contactArrayList = new ArrayList<>();
    private DatabaseHelper db;


    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My Contact Manager");

        //RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_contacts);
        db = new DatabaseHelper(this);

        //Contacts List
        contactArrayList.addAll(db.getAllContacts());

        contactsAdapter = new ContactsAdapter(this, contactArrayList, MainActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> addAndEditContacts(false, null, -1));


        // NO ROOM Database project
        //Using SQLITE
    }

    public void addAndEditContacts(final boolean isUpdated, final Contact contact, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.layout_add_contact, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(view);

        TextView contactTitle = view.findViewById(R.id.new_contact_title);
        final EditText newContact = view.findViewById(R.id.name);
        final EditText contactEmail = view.findViewById(R.id.email);

        contactTitle.setText(!isUpdated ? "Add New Contact" : "Edit Contact");

        if (isUpdated && contact != null){
            newContact.setText(contact.getName());
            newContact.setText(contact.getEmail());
        }

        alertDialogBuilder.setCancelable(false).setPositiveButton(isUpdated ? "Update" : "Save", (dialogInterface, i) -> {

        })
                .setNegativeButton("Delete", (dialogInterface, i) -> {
                    if(isUpdated){
                        DeleteContact(contact, position);
                    }else {
                        dialogInterface.cancel();
                    }
                }

                );

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
            if(TextUtils.isEmpty(newContact.getText().toString())){
                Toast.makeText(MainActivity.this, "Please Enter a Name", Toast.LENGTH_SHORT).show();

                return;
            }else{
                alertDialog.dismiss();
            }

            if (isUpdated && contact != null){
                UpdateContact(newContact.getText().toString(), contactEmail.getText().toString(), position);

            }else {
                CreateContact(newContact.getText().toString(), contactEmail.getText().toString());
            }
        });

    }

    private void CreateContact(String name, String email) {
        long id = db.insertContact(name, email);
        Contact contact = db.getContact(id);

        if (contact != null){
            contactArrayList.add(0, contact);
            contactsAdapter.notifyDataSetChanged();
        }
    }

    //Menu bar


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings){
            return true;
        }

        return super.onOptionsItemSelected(item);


    }

    private void UpdateContact(String name, String email, int position){
        Contact contact = contactArrayList.get(position);

        contact.setName(name);
        contact.setEmail(email);

        db.updateContact(contact);

        contactArrayList.set(position, contact);
        contactsAdapter.notifyItemChanged(position);
    }

    private void DeleteContact(Contact contact, int position) {
        contactArrayList.remove(position);
        db.deleteContact(contact);
        contactsAdapter.notifyItemChanged(position);
    }




}