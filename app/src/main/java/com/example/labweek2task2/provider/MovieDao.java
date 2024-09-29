package com.example.labweek2task2.provider;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("select * from movies")
    LiveData<List<Movie>> getAllMovie();

//    @Query("select * from customers where customerName=:name")
//    List<Customer> getCustomer(String name);

    @Insert
    void addMovie(Movie movie);

//    @Query("delete from customers where customerName= :name")
//    void deleteCustomer(String name);

    @Query("delete FROM movies")
    void deleteAllMovies();

    @Query("delete FROM movies WHERE movieId= (SELECT MAX(movieId) FROM movies)")
    void deleteLastMovie();

    @Query("delete FROM movies WHERE movieYear == :year")
    void deleteMovieYear(int year) ;
}
