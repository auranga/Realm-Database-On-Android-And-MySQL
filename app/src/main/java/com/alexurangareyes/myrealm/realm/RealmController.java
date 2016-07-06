package com.alexurangareyes.myrealm.realm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.alexurangareyes.myrealm.model.Person;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by alexurangareyes on 7/4/16.
 */
public class RealmController {

    private static RealmController instance;
    private final Realm mRealm;
    private Context ctx;

    public RealmController(Application application) {
        mRealm = Realm.getDefaultInstance();
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }


    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return mRealm;
    }

    //increatementIndex
    public long incId(){

        return mRealm.where(Person.class).max("id").longValue() + 1;
    }

    //find all objects in the Book.class
    public RealmResults<Person> getPersons() {

        return mRealm.where(Person.class).findAll();
    }

    //query a single item with the given id
    public Person getPerson(int id) {

        return mRealm.where(Person.class).equalTo("id", id).findFirst();
    }

    //query example
    /*public RealmResults<Book> queryedBooks() {

        return realm.where(Book.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                .findAll();

    }*/

    //check if Person.class is empty
    public boolean mRealEmpty() {

        if(!this.getPersons().isEmpty()) {

            return false;
        }
        else {

            return true;
            //Toast.makeText(ctx.getApplicationContext(),"mRealm is Empty", Toast.LENGTH_SHORT).show();

        }
    }

}
