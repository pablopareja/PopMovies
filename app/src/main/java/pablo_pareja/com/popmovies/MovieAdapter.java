package pablo_pareja.com.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.View.OnClickListener;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pablo on 07/02/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private JSONArray mMovieData;

    final private ListItemClickListener mOnClicklistener;
    private static int viewHolderCount;
    private int mNumberItems;

    public MovieAdapter(JSONArray movieData, ListItemClickListener listener){
        mNumberItems = movieData.length();
        mMovieData = movieData;
        mOnClicklistener = listener;
        viewHolderCount = 0;
    }

    public interface ListItemClickListener {
        void onListItemClick(JSONObject movieData);
    }



    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements OnClickListener{

        ImageView posterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            posterImageView = (ImageView) itemView.findViewById(R.id.movie_poster_grid);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            try{
                mOnClicklistener.onListItemClick(mMovieData.getJSONObject(clickedPosition));
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.length();
    }

    @Override
    public void onBindViewHolder(MovieViewHolder movieViewHolder, int position) {
        try{
            JSONObject movieData = mMovieData.getJSONObject(position);

            String imageURL = "http://image.tmdb.org/t/p/w185/" + movieData.getString("poster_path");
            System.out.println("imageURL = " + imageURL);
            Picasso.with(movieViewHolder.itemView.getContext()).load(imageURL).into(movieViewHolder.posterImageView);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setMovieData(JSONArray movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

}
