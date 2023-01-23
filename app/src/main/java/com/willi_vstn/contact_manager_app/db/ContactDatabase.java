package com.willi_vstn.contact_manager_app.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.willi_vstn.contact_manager_app.db.entity.Contact;

@Database(entities = {Contact.class}, version = 1)
public abstract class ContactDatabase extends RoomDatabase {

    //Linking the DAO with our Database
    public abstract ContactDAO getContactDAO();
}
