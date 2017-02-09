package pablo_pareja.com.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pablo on 07/02/17.
 */

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.tv_movie_title) TextView mTvMovieTitle;
    @BindView(R.id.movie_poster) ImageView mMoviePoster;
    @BindView(R.id.tv_release_date) TextView mTvReleaseDate;
    @BindView(R.id.tv_vote_average) TextView mTvVoteAverage;
    @BindView(R.id.tv_synopsis) TextView mTvSynopsis;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        if(intent.getStringExtra(Intent.EXTRA_TEXT) != null){
            String jsonText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if(!jsonText.isEmpty()){

                try{
                    JSONObject movieData = new JSONObject(jsonText);
                    mTvReleaseDate.setText(movieData.getString("release_date"));
                    mTvSynopsis.setText(movieData.getString("overview"));
                    mTvVoteAverage.setText(movieData.getString("vote_average") + "/10");
                    mTvMovieTitle.setText(movieData.getString("original_title"));

                    String imageURL = "http://image.tmdb.org/t/p/w185/" + movieData.getString("poster_path");
                    Picasso.with(this)
                            .load(imageURL)
                            .placeholder(R.drawable.movie)
                            .error(R.drawable.error)
                            .into(mMoviePoster);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
