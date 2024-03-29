package adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.scrollfrom.mvp_movies.R;
import model.Movie;
import movie_list.MovieListActivity;
import network.ApiClient;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> implements Filterable {

    private MovieListActivity movieListActivity;
    private List<Movie> movieList;
    private List<Movie> originalMovieList;
    boolean isTopRated;
    private String fromDate;
    private String toDate;

    public MoviesAdapter(MovieListActivity movieListActivity, List<Movie> movieList,boolean isTopRated) {
        this.movieListActivity = movieListActivity;
        this.movieList = movieList;
        this.originalMovieList = movieList;
        this.isTopRated = isTopRated;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Movie movie = movieList.get(position);

        holder.tvMovieTitle.setText(movie.getTitle());
        holder.tvMovieRatings.setText(String.valueOf(movie.getRating()));
        holder.tvReleaseDate.setText(movie.getReleaseDate());

        // loading album cover using Glide library
        Glide.with(movieListActivity)
                .load(ApiClient.IMAGE_BASE_URL + movie.getThumbPath())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.pbLoadImage.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.pbLoadImage.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(new RequestOptions().placeholder(R.drawable.ic_place_holder).error(R.drawable.ic_place_holder))
                .into(holder.ivMovieThumb);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movieListActivity.onMovieItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    /**
     * This function will set parameters for Filter
     * @param fromDate - From Release date.
     * @param toDate - To Release date.
     */
    public void setFilterParameter(String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                List<Movie> filteredResults = null;
                if (fromDate.isEmpty() || toDate.isEmpty()) {
                    filteredResults = originalMovieList;
                } else {
                    filteredResults = getFilteredResults(fromDate, toDate);
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                movieList = (List<Movie>) filterResults.values;
                MoviesAdapter.this.notifyDataSetChanged();

                if (getItemCount() == 0) {
                    movieListActivity.showEmptyView();
                } else {
                    movieListActivity.hideEmptyView();
                }
            }
        };
    }

    /**
     * This function will filter the data by using from and to date.
     * @param fromDate - From release date
     * @param toDate - To release date.
     * @return List of movies which satisfies the criteria
     */
    protected List<Movie> getFilteredResults(String fromDate, String toDate) {
        List<Movie> results = new ArrayList<>();

        for (Movie item : originalMovieList) {

            if (item.getReleaseDate().isEmpty()) {
                continue;
            }

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date minDate = null;
            Date maxDate = null;
            Date releaseDate = null;
            try {
                minDate = format.parse(fromDate);
                maxDate = format.parse(toDate);
                releaseDate = format.parse(item.getReleaseDate());
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }

            if (releaseDate.after(minDate) && releaseDate.before(maxDate)) {
                results.add(item);
            }
        }
        return results;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvMovieTitle;

        public TextView tvMovieRatings;

        public TextView tvReleaseDate;

        public ImageView ivMovieThumb;

        public ProgressBar pbLoadImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvMovieTitle = itemView.findViewById(R.id.tv_movie_title);
            tvReleaseDate = itemView.findViewById(R.id.tv_release_date);
            tvMovieRatings = itemView.findViewById(R.id.tv_movie_ratings);
            ivMovieThumb = itemView.findViewById(R.id.iv_movie_thumb);
            pbLoadImage = itemView.findViewById(R.id.pb_load_image);
        }
    }
    public boolean isTopRated() {
        return isTopRated;
    }
}
