package pablo_pareja.com.popmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import pablo_pareja.com.popmovies.utilities.NetworkUtils;


public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener{

    private RecyclerView mRecyclerView;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private MovieAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNoOfColumns(getBaseContext()));
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        makeMoviesQuery(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.movies_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_most_popular) {
            Log.i(MainActivity.class.getName(), "most popular!");
            makeMoviesQuery(true);
            return true;
        } else if (id == R.id.action_top_rated) {
            Log.i(MainActivity.class.getName(), "top rated!");
            makeMoviesQuery(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public URL buildOpenMovieDBUrl(boolean popular) {

        String baseURL = getString(R.string.the_movie_db_base_url);

        if(popular){
            baseURL += getString(R.string.movie_db_popular);
        }else{
            baseURL += getString(R.string.movie_db_top_rated);
        }

        Uri builtUri = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter(getString(R.string.the_movie_db_api_key_param),
                        getString(R.string.the_movie_db_api_key))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public void makeMoviesQuery(boolean popular){

        mLoadingIndicator.setVisibility(View.VISIBLE);

        URL url = buildOpenMovieDBUrl(popular);
        new MoviesQueryTask().execute(url);
    }

    private void showErrorMessage() {
        // First, hide the currently visible data
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMovies(String movieList){
        System.out.println("movieList = " + movieList);
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);

        try{
            JSONObject jsonObject = new JSONObject(movieList);

            JSONArray jsonArray = jsonObject.getJSONArray("results");
            //System.out.println(jsonArray.length());

            mAdapter = new MovieAdapter(jsonArray, this);
            mRecyclerView.setAdapter(mAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public class MoviesQueryTask extends AsyncTask<URL, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL queryURL = params[0];
            String moviesQueryResult = null;
            try{
                moviesQueryResult = NetworkUtils.getResponseFromHttpUrl(queryURL);
            }catch(IOException e){
                e.printStackTrace();
            }
            return moviesQueryResult;
        }

        @Override
        protected void onPostExecute(String moviesQueryResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if(moviesQueryResults != null && !moviesQueryResults.equals("")){
                showMovies(moviesQueryResults);
            }else{
                showErrorMessage();
            }
        }
    }

    public void onListItemClick(JSONObject movieClickedData) {
        System.out.println("onMovieClick!");

        Context context = MainActivity.this;
        Class destinationActivity = DetailActivity.class;

        Intent startDetailActivityIntent = new Intent(context, destinationActivity);
        startDetailActivityIntent.putExtra(Intent.EXTRA_TEXT, movieClickedData.toString());

        startActivity(startDetailActivityIntent);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
