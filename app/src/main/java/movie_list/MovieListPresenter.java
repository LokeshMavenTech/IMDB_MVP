package movie_list;

import java.util.List;

import model.Movie;

public class MovieListPresenter implements MovieListContract.Presenter, MovieListContract.Model.OnFinishedListener {

    private MovieListContract.View movieListView;
    private MovieListContract.Model movieListModel;

    public MovieListPresenter(MovieListContract.View movieListView) {
        this.movieListView = movieListView;
        this.movieListModel = new MovieListModel();
    }

    @Override
    public void onDestroy() {
        this.movieListView = null;
    }

    @Override
    public void getMoreData(int pageNo) {
        if (movieListView != null) {
            movieListView.showProgress();
        }
        movieListModel.getMovieList(this, pageNo);
        movieListModel.getTopRatedMovieList(this,pageNo);

    }

    @Override
    public void requestDataFromServer() {
        if (movieListView != null) {
            movieListView.showProgress();
        }
        movieListModel.getMovieList(this, 1);
    }

    @Override
    public void getTopRatedDataFromServer() {
        if (movieListView != null) {
            movieListView.showProgress();
        }
        movieListModel.getTopRatedMovieList(this, 1);
    }

    public void onFinished(List<Movie> movieArrayList) {
        if (movieListView != null) {
            movieListView.setDataToRecyclerView(movieArrayList);
            movieListView.hideProgress();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if (movieListView != null) {
            movieListView.onResponseFailure(t);
            movieListView.hideProgress();
        }
    }
}
