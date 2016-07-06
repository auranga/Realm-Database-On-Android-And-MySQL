package com.alexurangareyes.myrealm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.alexurangareyes.myrealm.model.Person;
import com.alexurangareyes.myrealm.realm.RealmController;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.EachExceptionsHandler;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

//http://www.androidhive.info/2016/05/android-working-with-realm-database-replacing-sqlite-core-data/

//http://www.thedroidsonroids.com/blog/android/realm-database-example/
//http://jakewharton.github.io/butterknife/

//https://github.com/kosalgeek/generic_asynctask_v2
//http://www.kosalgeek.com/2015/09/how-to-connect-android-with-php-mysql.html

public class MainActivity extends AppCompatActivity {


    private Realm mRealm;
    private RealmResults<Person> results;

    @BindView(R.id.nomb_editText)
    EditText username;
    //10.0.3.2 is the alias IP for GenyMotion
    //10.0.2.2 is the alias IP for Android native Emulator
    String url = "http://10.0.3.2:8888/Android/Reaml/insertPerson.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RealmController.with(this).refresh();
        //RealmController.with(this).getBooks();
        //get realm instance
        this.mRealm = RealmController.with(this.getApplication()).getRealm();

        results = RealmController.with(this.getApplication()).getPersons();

        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_add)
    public void onAddClick() {

        long nextID;

        if(!RealmController.with(this.getApplication()).mRealEmpty()) {
            // increatement index
            //nextID = (mRealm.where(Person.class).max("id").longValue() + 1);
            nextID = RealmController.getInstance().incId();

        }
        else{
            nextID = 1;
        }

        mRealm.beginTransaction();

        final Person person = mRealm.createObject(Person.class);

        // insert new value
        person.setId(nextID);
        person.setName(username.getText().toString().trim());

        mRealm.commitTransaction();
        username.setText("");
        Toast.makeText(this.getApplicationContext(),"Person added", Toast.LENGTH_SHORT).show();


    }
    @OnClick(R.id.button_remove)
    public void onRemoveClick() {

        mRealm.beginTransaction();

        if(!RealmController.with(this.getApplication()).mRealEmpty()) {

            Person person = RealmController.getInstance().getPerson(Integer.parseInt(username.getText().toString()));
            person.deleteFromRealm();
            Toast.makeText(this.getApplicationContext(),"Person Removed", Toast.LENGTH_SHORT).show();

        }else{
            //Toast.makeText(this.getApplicationContext(),"Person not found", Toast.LENGTH_SHORT).show();
            Toast.makeText(this.getApplicationContext(),"mRealm is Empty", Toast.LENGTH_SHORT).show();
        }

        mRealm.commitTransaction();
    }

    @OnClick(R.id.button_ReadAll)
    public void onReadAllClick() {


        //RealmResults<Person> results = RealmController.with(this.getApplication()).getPersons();


        if(!RealmController.with(this.getApplication()).mRealEmpty()) {

            for (int i = 0; i <results.size(); i++) {
                Person u = results.get(i);
                Toast.makeText(this.getApplicationContext(),"" + u, Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Toast.makeText(this.getApplicationContext(),"mRealm is Empty", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.button_synDataBase)
    public void onReadSyncDataBase() {

       // RealmResults<Person> results = mRealm.where(Person.class).findAll();

        if(!RealmController.with(this.getApplication()).mRealEmpty()) {

            for (int i = 0; i < results.size(); i++) {

                final  Person u = results.get(i);

                HashMap postData = new HashMap();
                postData.put("Id",String.valueOf(u.getId()));
                postData.put("Name",u.getName());
                PostResponseAsyncTask insertTask = new PostResponseAsyncTask(MainActivity.this, postData, new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {

                        if(output.equals("success")){
                            Toast.makeText(MainActivity.this, "Add Successfully " + u.getName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                insertTask.execute(url);
                insertTask.setEachExceptionsHandler(new EachExceptionsHandler() {
                    @Override
                    public void handleIOException(IOException e) {
                        Toast.makeText(MainActivity.this, "Error with internet or web server.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void handleMalformedURLException(MalformedURLException e) {
                        Toast.makeText(MainActivity.this, "Error with the URL.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void handleProtocolException(ProtocolException e) {
                        Toast.makeText(MainActivity.this, "Error with protocol.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                        Toast.makeText(MainActivity.this, "Error with text encoding.", Toast.LENGTH_LONG).show();
                    }
                });

            }

        }
        else{
            Toast.makeText(this.getApplicationContext(),"mRealm is Empty", Toast.LENGTH_SHORT).show();
        }


    }

    @OnClick(R.id.button_removeAll)
    public void onRemoveAllClick() {



        if(!RealmController.with(this.getApplication()).mRealEmpty()) {


            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    // Delete all matches
                    results.deleteAllFromRealm();
                    //Toast.makeText(this.getApplicationContext(),"mRealm is Total Empty", Toast.LENGTH_SHORT).show();
                }
            });

            Toast.makeText(this.getApplicationContext(),"mRealm is Total Empty", Toast.LENGTH_SHORT).show();


        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }


}