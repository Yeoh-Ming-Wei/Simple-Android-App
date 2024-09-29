package com.example.labweek2task2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labweek2task2.provider.Movie;
import com.example.labweek2task2.provider.MovieViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    String title ;
    double cost ;
    String year ;
    String country ;
    String genre ;
    String keyword ;
    DrawerLayout drawer ;
    ArrayList<String> arr = new ArrayList<String>() ;
    ArrayAdapter myAdapter ;
    FirebaseDatabase database ;
    DatabaseReference myRef ;
    View frameLayout ;
    int initX, initY, finalX, finalY ;
    private MovieViewModel mMovieViewModel ;
    GestureDetector gestureDetector ;
    ScaleGestureDetector scaleGestureDetector ;
    Movie temp ;


//********************************************* ONCREATE *********************************************//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Items/movie");



        /* Request permissions to access SMS */
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 0);
        /* Create and instantiate the local broadcast receiver
           This class listens to messages come from class SMSReceiver
         */
        MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();

        /*
         * Register the broadcast handler with the intent filter that is declared in
         * class SMSReceiver @line 11
         * */
        registerReceiver(myBroadCastReceiver, new IntentFilter(SMSReceiver.SMS_FILTER));


        //-------------------------------------- WEEK 5 ------------------------------------------//
        Toolbar tb = findViewById(R.id.toolbar) ;
        setSupportActionBar(tb);
//


//        ListView listview = findViewById(R.id.unitlist) ;
        myAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arr) ;
//        listview.setAdapter(myAdapter) ;

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText titleText = findViewById(R.id.titleInput) ;
                EditText yearText = findViewById(R.id.yearInput) ;

                title = titleText.getText().toString() ;
                year = yearText.getText().toString() ;

                arr.add(String.format("%s | %s", title, year)) ;
                myAdapter.notifyDataSetChanged();

            }

        });

        drawer = findViewById(R.id.dl) ;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationView) ;
        navigationView.setNavigationItemSelectedListener(new MyNavigationListener()) ;

        mMovieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        mMovieViewModel.getAllMovies().observe(this, newData -> {

        });

        //*********************************** MOTION EVENT ***********************************//
        frameLayout = findViewById(R.id.touchLayout) ;

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int action = motionEvent.getActionMasked() ;


                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        initX = (int) motionEvent.getX() ;
                        initY = (int) motionEvent.getY() ;

                        if (initX > (frameLayout.getWidth() - 50) && initY < 50) {
                            EditText costText = findViewById(R.id.costInput) ;
                            int cost = Integer.parseInt(costText.getText().toString()) ;
                            costText.setText(Integer.toString(cost  + 50)) ;
                        }

                        if (initX < 50 && initY < 50) {
                            EditText costText = findViewById(R.id.costInput) ;
                            int cost = Integer.parseInt(costText.getText().toString()) - 50 ;

                            if (cost > 0) {
                                costText.setText(Integer.toString(cost)) ;
                            } else {
                                costText.setText(Integer.toString(0)) ;

                                }

                        }

                        return true ;
                    case MotionEvent.ACTION_UP:
                        finalX = (int) motionEvent.getX() ;
                        finalY = (int) motionEvent.getY() ;

                        if ((Math.abs(finalX - initX) > Math.abs(finalY - initY)) && (Math.abs(finalX - initX) > 10) && (Math.abs(finalY - initY) > 10)) { //

                            EditText titleText = findViewById(R.id.titleInput) ;
                            EditText costText = findViewById(R.id.costInput) ;
                            EditText yearText = findViewById(R.id.yearInput) ;
                            EditText countryText = findViewById(R.id.countryInput) ;
                            EditText genreText = findViewById(R.id.genreInput) ;
                            EditText keywordText = findViewById(R.id.keywordInput) ;

                            title = titleText.getText().toString() ;
                            cost = Double.parseDouble(costText.getText().toString()) ;
                            year = yearText.getText().toString() ;
                            country = countryText.getText().toString() ;
                            genre = genreText.getText().toString() ;
                            keyword = keywordText.getText().toString() ;

                            Movie movie = new Movie(title, year, country, genre, cost, keyword) ;
                            myRef.push().setValue(movie) ;
                            mMovieViewModel.insert(movie) ;

                            Toast.makeText(getApplicationContext(), "Swipe Horizontally", Toast.LENGTH_SHORT).show() ;

                        }
//**************************** NEW FEATURE ******************************//
                        if ((Math.abs(finalX - initX) < Math.abs(finalY - initY)) && (Math.abs(finalX - initX) > 10) && (Math.abs(finalY - initY) > 10)) {

                            TextView storeText = findViewById(R.id.tempView);

                            if (storeText.getText().toString().length() == 0) {
                                Toast.makeText(getApplicationContext(), "There is nothing to restore!", Toast.LENGTH_SHORT).show() ;
                            } else {
                                EditText titleText = findViewById(R.id.titleInput) ;
                                EditText costText = findViewById(R.id.costInput) ;
                                EditText yearText = findViewById(R.id.yearInput) ;
                                EditText countryText = findViewById(R.id.countryInput) ;
                                EditText genreText = findViewById(R.id.genreInput) ;
                                EditText keywordText = findViewById(R.id.keywordInput) ;

                                titleText.setText(temp.getTitle()) ;
                                costText.setText(temp.getCost() + "") ;
                                yearText.setText(temp.getYear()) ;
                                countryText.setText(temp.getCountry()) ;
                                genreText.setText(temp.getGenre()) ;
                                keywordText.setText(temp.getKeyword()) ;

                                storeText.setText("") ;

                                temp = new Movie("", "", "", "", 0, "");

                                Toast.makeText(getApplicationContext(), "Restore done!", Toast.LENGTH_SHORT).show() ;
                            }


                        }

                        return true ;
                    default:
                        return false ;
                }
            }

            

        });
        //************************************* WEEK 11 *************************************//
        View main = findViewById(R.id.main);

        gestureDetector = new GestureDetector(this, new MyGestureDetector()) ;
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGestureDetector()) ;

        main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent) ;
                scaleGestureDetector.onTouchEvent(motionEvent) ;
                return true;
            }
        });



    }
    //********************************************* ONCREATEOPTIONMENU *********************************************//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu) ;
        return true ;
    }


    //********************************************* ONOPTIONITEMSELECTED *********************************************//
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.clearFields) {

            EditText titleText = findViewById(R.id.titleInput) ;
            EditText costText = findViewById(R.id.costInput) ;
            EditText yearText = findViewById(R.id.yearInput) ;
            EditText countryText = findViewById(R.id.countryInput) ;
            EditText genreText = findViewById(R.id.genreInput) ;
            EditText keywordText = findViewById(R.id.keywordInput) ;

            titleText.setText("") ;
            costText.setText("") ;
            yearText.setText("") ;
            countryText.setText("") ;
            genreText.setText("") ;
            keywordText.setText("") ;

            Toast.makeText(this,"Fields cleared!",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.totalMovies) {
            int total = arr.size() ;
            Toast.makeText(this,String.format("Total: %d", total),Toast.LENGTH_SHORT).show();

        }
        return true;
    }


    //********************************************* MYBROADCASTRECEIVER *********************************************//
    class MyBroadCastReceiver extends BroadcastReceiver {

        /*
         * This method 'onReceive' will get executed every time class SMSReceive sends a broadcast
         * */
        @Override
        public void onReceive(Context context, Intent intent) {

            EditText titleText = findViewById(R.id.titleInput) ;
            EditText costText = findViewById(R.id.costInput) ;
            EditText yearText = findViewById(R.id.yearInput) ;
            EditText countryText = findViewById(R.id.countryInput) ;
            EditText genreText = findViewById(R.id.genreInput) ;
            EditText keywordText = findViewById(R.id.keywordInput) ;

            /*
             * Retrieve the message from the intent
             * */
            String msg = intent.getStringExtra(SMSReceiver.SMS_MSG_KEY);
            /*
             * String Tokenizer is used to parse the incoming message
             * The protocol is to have the account holder name and account number separate by a semicolon
             * */

            StringTokenizer sT = new StringTokenizer(msg, ";");
            String title = sT.nextToken();
            String year = sT.nextToken();
            String country = sT.nextToken();
            String genre = sT.nextToken();
            String cost = sT.nextToken();
            String keyword = sT.nextToken();


            titleText.setText(title);
            costText.setText(cost);
            yearText.setText(year);
            countryText.setText(country);
            genreText.setText(genre);
            keywordText.setText(keyword);
        }

    }

    //********************************************* ADD *********************************************//
    public void add(View view) {

        EditText titleText = findViewById(R.id.titleInput) ;
        EditText costText = findViewById(R.id.costInput) ;
        EditText yearText = findViewById(R.id.yearInput) ;
        EditText countryText = findViewById(R.id.countryInput) ;
        EditText genreText = findViewById(R.id.genreInput) ;
        EditText keywordText = findViewById(R.id.keywordInput) ;

        title = titleText.getText().toString() ;
        cost = Double.parseDouble(costText.getText().toString()) ;
        year = yearText.getText().toString() ;
        country = countryText.getText().toString() ;
        genre = genreText.getText().toString() ;
        keyword = keywordText.getText().toString() ;

        Movie movie = new Movie(title, year, country, genre, cost, keyword) ;
//        newarr.add(movie) ;
        mMovieViewModel.insert(movie) ;
        Toast myToast = Toast.makeText(this, String.format("Movie - %s - is added!", title), Toast.LENGTH_SHORT) ;
        myToast.show() ;



        myRef.push().setValue(movie) ;
    }

    //********************************************* COSTDOUBLE *********************************************//
    public void costDouble(View view) {
        EditText costText = findViewById(R.id.costInput) ;
        int cost = Integer.parseInt(costText.getText().toString()) ;
        costText.setText(Integer.toString(cost * 2)) ;

        Toast myToast = Toast.makeText(this, String.format("Cost is doubled!"), Toast.LENGTH_SHORT) ;
        myToast.show() ;
    }

    //********************************************* RESET *********************************************//
    public void reset(View view) {

        EditText titleText = findViewById(R.id.titleInput) ;
        EditText costText = findViewById(R.id.costInput) ;
        EditText yearText = findViewById(R.id.yearInput) ;
        EditText countryText = findViewById(R.id.countryInput) ;
        EditText genreText = findViewById(R.id.genreInput) ;
        EditText keywordText = findViewById(R.id.keywordInput) ;

        titleText.setText("") ;
        costText.setText("") ;
        yearText.setText("") ;
        countryText.setText("") ;
        genreText.setText("") ;
        keywordText.setText("") ;

        Toast myToast = Toast.makeText(this, String.format("Inputs reset complete!"), Toast.LENGTH_SHORT) ;
        myToast.show() ;

    }

    //********************************************* ONSTART *********************************************//
    @Override
    protected void onStart() {
        super.onStart();

//        SharedPreferences sP = getSharedPreferences("FILE1", 0) ;
//        title = sP.getString("KEY7", "") ;
//        cost = sP.getString("KEY8", "") ;
//        year = sP.getString("KEY9", "") ;
//        country = sP.getString("KEY10", "") ;
//        genre = sP.getString("KEY11", "") ;
//        keyword = sP.getString("KEY12", "") ;
//
//        titleText.setText(title) ;
//        costText.setText(cost) ;
//        yearText.setText(year) ;
//        countryText.setText(country) ;
//        genreText.setText(genre) ;
//        keywordText.setText(keyword) ;

    }

    //********************************************* ONSTOP *********************************************//
    @Override
    protected void onStop() {
        super.onStop();

//        title = titleText.getText().toString() ;
//        cost = Double.parseDouble(costText.getText().toString()) ;
//        year = yearText.getText().toString() ;
//        country = countryText.getText().toString() ;
//        genre = genreText.getText().toString() ;
//        keyword = keywordText.getText().toString() ;

//        SharedPreferences sP = getSharedPreferences("FILE1", 0) ;
//        SharedPreferences.Editor editor = sP.edit() ;
//        editor.putString("KEY7", title) ;
//        editor.putString("KEY8", cost) ;
//        editor.putString("KEY9", year) ;
//        editor.putString("KEY10", country) ;
//        editor.putString("KEY11", genre) ;
//        editor.putString("KEY12", keyword) ;
//        editor.commit() ;
    }

    //********************************************* ONSAVEINSTANCESTATE *********************************************//
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

//        title = titleText.getText().toString() ;
//        cost = Double.parseDouble(costText.getText().toString()) ;
//        year = yearText.getText().toString() ;
//        country = countryText.getText().toString() ;
//        genre = genreText.getText().toString() ;
//        keyword = keywordText.getText().toString() ;

//        outState.putString("KEY1", title);
//        outState.putString("KEY2", cost);
//        outState.putString("KEY3", year);
//        outState.putString("KEY4", country);
//        outState.putString("KEY5", genre);
//        outState.putString("KEY6", keyword);


    }

    //********************************************* ONRESTOREINSTANCESTATE *********************************************//
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        title = savedInstanceState.getString("KEY1").toLowerCase() ;
//        cost = savedInstanceState.getString("KEY2") ;
//        year = savedInstanceState.getString("KEY3") ;
//        country = savedInstanceState.getString("KEY4") ;
//        genre = savedInstanceState.getString("KEY5").toUpperCase() ;
//        keyword = savedInstanceState.getString("KEY6") ;
//
//        EditText titleText = findViewById(R.id.titleInput) ;
//        EditText genreText = findViewById(R.id.genreInput) ;
//
//        titleText.setText(title) ;
//        genreText.setText(genre) ;
//
//
//
//    }

    //********************************************* ONNAVIAGTIONITEMSELECTED *********************************************//
    class MyNavigationListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // get the id of the selected item
            int id = item.getItemId();

            if (id == R.id.addMovie) {

                EditText titleText = findViewById(R.id.titleInput) ;
                EditText costText = findViewById(R.id.costInput) ;
                EditText yearText = findViewById(R.id.yearInput) ;
                EditText countryText = findViewById(R.id.countryInput) ;
                EditText genreText = findViewById(R.id.genreInput) ;
                EditText keywordText = findViewById(R.id.keywordInput) ;

                title = titleText.getText().toString() ;
                cost = Double.parseDouble(costText.getText().toString()) ;
                year = yearText.getText().toString() ;
                country = countryText.getText().toString() ;
                genre = genreText.getText().toString() ;
                keyword = keywordText.getText().toString() ;

                Movie movie = new Movie(title, year, country, genre, cost, keyword) ;
                mMovieViewModel.insert(movie) ;
                myRef.push().setValue(movie) ;

                arr.add(String.format("%s | %s", title, year)) ;
                myAdapter.notifyDataSetChanged();
            } else if (id == R.id.removeLastMovie) {
                mMovieViewModel.deleteLast();
            } else if (id == R.id.removeAllMovies) {
                mMovieViewModel.deleteAll() ;
                myRef.removeValue() ;
            } else if (id == R.id.listAllMovies) {
                RecyclerView recyclerView = findViewById(R.id.recyclerView) ;
                goToNext(recyclerView);

            } else if (id == R.id.close) {
                finish() ;
            }
            // close the drawer
            drawer.closeDrawers();
            // tell the OS
            return true;
        }
    }

    //********************************************* GOTONEXT *********************************************//
    public void goToNext(View v) {

//        Gson gson = new Gson();
//        String dbStr = gson.toJson(newarr);
//        SharedPreferences sP = getSharedPreferences("db1",0);
//        SharedPreferences.Editor edit = sP.edit();
//        edit.putString("MOV_LIST", dbStr);
//        edit.apply();

        Intent intent = new Intent(this, MainActivity2.class) ;
        startActivity(intent) ;
    }
    //********************************************* MYGESTUREDETECTOR *********************************************//
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            EditText titleText = findViewById(R.id.titleInput) ;
            EditText costText = findViewById(R.id.costInput) ;
            EditText yearText = findViewById(R.id.yearInput) ;
            EditText countryText = findViewById(R.id.countryInput) ;
            EditText genreText = findViewById(R.id.genreInput) ;
            EditText keywordText = findViewById(R.id.keywordInput) ;

            titleText.setText("Jujutsu Kaisen 0") ;
            costText.setText("5") ;
            yearText.setText("2022") ;
            countryText.setText("Japan") ;
            genreText.setText("Action") ;
            keywordText.setText("Gojo") ;

            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i("week11", "OnSingleTap") ;
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            EditText costText = findViewById(R.id.costInput) ;
            int cost = Integer.parseInt(costText.getText().toString()) ;
            costText.setText(Integer.toString(cost  + 150)) ;
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            EditText yearText = findViewById(R.id.yearInput) ;
            int year = Integer.parseInt(yearText.getText().toString()) ;
            if(distanceX < 0) {
                yearText.setText(Integer.toString(year + (int) e2.getX())) ;
            } else {
                yearText.setText(Integer.toString(year - (int) e2.getX())) ;
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            EditText titleText = findViewById(R.id.titleInput) ;
            EditText costText = findViewById(R.id.costInput) ;
            EditText yearText = findViewById(R.id.yearInput) ;
            EditText countryText = findViewById(R.id.countryInput) ;
            EditText genreText = findViewById(R.id.genreInput) ;
            EditText keywordText = findViewById(R.id.keywordInput) ;

            titleText.setText("") ;
            costText.setText("") ;
            yearText.setText("") ;
            countryText.setText("") ;
            genreText.setText("") ;
            keywordText.setText("") ;

            super.onLongPress(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (velocityX > 500 || velocityY > 500) {
                moveTaskToBack(true) ;
            }

            return super.onFling(e1, e2, velocityX, velocityY);

        }


    }

    class MyScaleGestureDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//**************************** NEW FEATURE ******************************//
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            EditText titleText = findViewById(R.id.titleInput) ;
            EditText costText = findViewById(R.id.costInput) ;
            EditText yearText = findViewById(R.id.yearInput) ;
            EditText countryText = findViewById(R.id.countryInput) ;
            EditText genreText = findViewById(R.id.genreInput) ;
            EditText keywordText = findViewById(R.id.keywordInput) ;

            if ((titleText.getText().toString().length() == 0) || (costText.getText().toString().length() == 0) || (yearText.getText().toString().length() == 0) || (countryText.getText().toString().length() == 0) || (genreText.getText().toString().length() == 0) || (keywordText.getText().toString().length() == 0)) {
                Toast.makeText(getApplicationContext(), "There is still empty field!", Toast.LENGTH_SHORT).show() ;
            } else {

                title = titleText.getText().toString();
                cost = Double.parseDouble(costText.getText().toString());
                year = yearText.getText().toString();
                country = countryText.getText().toString();
                genre = genreText.getText().toString();
                keyword = keywordText.getText().toString();

                temp = new Movie(title, year, country, genre, cost, keyword);


                TextView storeText = findViewById(R.id.tempView);
                storeText.setText(String.format("Title: %s \nCost: %.2f \nYear: %s \nCountry: %s \nGenre: %s \nKeyword: %s",
                        temp.getTitle(), temp.getCost(), temp.getYear(), temp.getCountry(), temp.getGenre(), temp.getKeyword()));

                titleText.setText("");
                costText.setText("");
                yearText.setText("");
                countryText.setText("");
                genreText.setText("");
                keywordText.setText("");

                Toast.makeText(getApplicationContext(), "Store Temporary!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void storeTemp(View view) {

        EditText titleText = findViewById(R.id.titleInput);
        EditText costText = findViewById(R.id.costInput);
        EditText yearText = findViewById(R.id.yearInput);
        EditText countryText = findViewById(R.id.countryInput);
        EditText genreText = findViewById(R.id.genreInput);
        EditText keywordText = findViewById(R.id.keywordInput);

        if ((titleText.getText().toString().length() == 0) || (costText.getText().toString().length() == 0) || (yearText.getText().toString().length() == 0) || (countryText.getText().toString().length() == 0) || (genreText.getText().toString().length() == 0) || (keywordText.getText().toString().length() == 0)) {
            Toast.makeText(getApplicationContext(), "There is still empty field!", Toast.LENGTH_SHORT).show();
        } else {

            title = titleText.getText().toString();
            cost = Double.parseDouble(costText.getText().toString());
            year = yearText.getText().toString();
            country = countryText.getText().toString();
            genre = genreText.getText().toString();
            keyword = keywordText.getText().toString();

            temp = new Movie(title, year, country, genre, cost, keyword);


            TextView storeText = findViewById(R.id.tempView);
            storeText.setText(String.format("Title: %s \nCost: %.2f \nYear: %s \nCountry: %s \nGenre: %s \nKeyword: %s",
                    temp.getTitle(), temp.getCost(), temp.getYear(), temp.getCountry(), temp.getGenre(), temp.getKeyword()));

            titleText.setText("");
            costText.setText("");
            yearText.setText("");
            countryText.setText("");
            genreText.setText("");
            keywordText.setText("");

            Toast.makeText(getApplicationContext(), "Store Temporary!", Toast.LENGTH_SHORT).show();
        }

    }

    public void restore(View v) {
        TextView storeText = findViewById(R.id.tempView);

        if (storeText.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "There is nothing to restore!", Toast.LENGTH_SHORT).show() ;
        } else {
            EditText titleText = findViewById(R.id.titleInput) ;
            EditText costText = findViewById(R.id.costInput) ;
            EditText yearText = findViewById(R.id.yearInput) ;
            EditText countryText = findViewById(R.id.countryInput) ;
            EditText genreText = findViewById(R.id.genreInput) ;
            EditText keywordText = findViewById(R.id.keywordInput) ;

            titleText.setText(temp.getTitle()) ;
            costText.setText(temp.getCost() + "") ;
            yearText.setText(temp.getYear()) ;
            countryText.setText(temp.getCountry()) ;
            genreText.setText(temp.getGenre()) ;
            keywordText.setText(temp.getKeyword()) ;

            storeText.setText("") ;

            temp = new Movie("", "", "", "", 0, "");

            Toast.makeText(getApplicationContext(), "Restore done!", Toast.LENGTH_SHORT).show() ;
        }
    }

}

