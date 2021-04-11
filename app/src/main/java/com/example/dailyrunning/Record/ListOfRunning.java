package com.example.dailyrunning.Record;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyrunning.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class ListOfRunning extends AppCompatActivity {

    ListView listview;
    ArrayList<String> list = new ArrayList<>();
    Intent intent;
    private String INTENT_DATETIMEKEY = "dateTime";
     ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_running);
        setTitle(R.string.listOfRunning);

        //create the view elements
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listview = (ListView) findViewById(R.id.listview);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);
        listview.setAdapter(adapter);
        intent = new Intent(this, IndividualRunningActivity.class);

        //before the retrieve of data from database, show the circular progress bar.
        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rt = database.getReference("user1");


        rt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //  retrieve the dateTime of all the walks in the database and display them on the listview
                // once data has been fetch, the progress bar is removed.
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String dateTime = (String) postSnapshot.child("time").getValue();
                    list.add(dateTime);
                    Collections.reverse(list);
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // when an item of the list is selected, pass info of the selected walk to the next activity.

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String clickedValue =(String) parent.getItemAtPosition(position);
                intent.putExtra(INTENT_DATETIMEKEY, clickedValue);
                startActivity(intent);

            }
        });
    }
}
