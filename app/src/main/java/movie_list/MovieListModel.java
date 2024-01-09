package movie_list;


import android.util.Log;

import java.util.List;

import model.Movie;
import model.MovieListResponse;
import network.ApiClient;
import network.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListModel implements MovieListContract.Model {

    private final String TAG = "MovieListModel";

    @Override
    public void getMovieList(final OnFinishedListener onFinishedListener, int pageNo) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MovieListResponse> call = apiService.getPopularMovies(ApiClient.API_KEY, pageNo);
        call.enqueue(new Callback<MovieListResponse>() {
            @Override
            public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                List<Movie> movies = response.body().getResults();
                Log.d(TAG, "Number of movies received: " + movies.size());
                onFinishedListener.onFinished(movies);
            }

            @Override
            public void onFailure(Call<MovieListResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                onFinishedListener.onFailure(t);
            }
        });
    }
/////////////////

    @Override
    public void getTopRatedMovieList(OnFinishedListener onFinishedListener, int pageNO) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MovieListResponse> call = apiService.getTopRated(ApiClient.API_KEY, pageNO);

        call.enqueue(new Callback<MovieListResponse>() {
            @Override
            public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                if (response.isSuccessful()) {
                    MovieListResponse movieListResponse = response.body();
                    if (movieListResponse != null) {
                        List<Movie> movies = movieListResponse.getResults();
                        if (movies != null) {
                            Log.d(TAG, "Number of Top Rated Movies Received: " + movies.size());
                            onFinishedListener.onFinished(movies);
                        } else {
                            Log.e(TAG, "Top-rated movies list is null");
                            onFinishedListener.onFailure(new Throwable("Failed to fetch top-rated movies"));
                        }
                    } else {
                        Log.e(TAG, "Response body is null");
                        onFinishedListener.onFailure(new Throwable("Failed to fetch top-rated movies"));
                    }
                } else {
                    Log.e(TAG, "Error fetching top-rated movies. Code: " + response.code());
                    onFinishedListener.onFailure(new Throwable("Failed to fetch top-rated movies. Code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<MovieListResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching top-rated movies: " + t);
                onFinishedListener.onFailure(t);
            }
        });
    }


    //////////////////////////////////////////////////////////

}
