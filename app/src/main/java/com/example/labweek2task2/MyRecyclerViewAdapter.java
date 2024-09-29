package com.example.labweek2task2;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labweek2task2.provider.Movie;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

//    ArrayList<Movie> data ;
    List<Movie> data = new ArrayList<>() ;



    public MyRecyclerViewAdapter() {

    }

    public void setData(List<Movie> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false); //CardView inflated as RecyclerView list item
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText("Title: " + data.get(position).getTitle());
        holder.year.setText("Year: " + data.get(position).getYear());
        holder.country.setText("Country: " + data.get(position).getCountry());
        holder.genre.setText("Genre: " + data.get(position).getGenre());
        holder.cost.setText("Cost: " + data.get(position).getCost());
        holder.keyword.setText("Keyword: " + data.get(position).getKeyword());

        holder.itemView.setOnClickListener(new View.OnClickListener() { //set back to itemView for students
            @Override public void onClick(View v) {
                int year = Integer.parseInt(data.get(position).getYear()) ;
                MainActivity2.mMovieViewModel.deleteByYear(year);
               Toast toast =  Toast.makeText(v.getContext(), "Movie No." + position + " with title: " + data.get(position).getTitle() + " is selected" , Toast.LENGTH_SHORT);
               toast.show();
            }

        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title ;
        public TextView year ;
        public TextView country ;
        public TextView genre ;
        public TextView cost ;
        public TextView keyword ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.cvTitle) ;
            year = itemView.findViewById(R.id.cvYear) ;
            country = itemView.findViewById(R.id.cvCountry) ;
            genre = itemView.findViewById(R.id.cvGenre) ;
            cost = itemView.findViewById(R.id.cvCost) ;
            keyword = itemView.findViewById(R.id.cvKeywords) ;
        }
    }
}
