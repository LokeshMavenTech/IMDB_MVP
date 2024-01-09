package movie_list;

import java.util.List;

import model.Movie;

public interface MovieListContract {

    interface Model {
        interface OnFinishedListener {
            void onFinished(List<Movie> movieArrayList);
            void onFailure(Throwable t);
        }

        void getMovieList(OnFinishedListener onFinihedListener, int pageNo);
        void getTopRatedMovieList(OnFinishedListener onFinishedListener,int pageNO);
    }

    interface View {
        void showProgress();
        void hideProgress();
        void setDataToRecyclerView(List<Movie> movieArrayList);
        void onResponseFailure(Throwable throwable);
    }

    interface Presenter {
        void onDestroy();
        void getMoreData(int pageNo);
        void requestDataFromServer();
        void getTopRatedDataFromServer();
    }
}
