package com.example.myfirstandroidproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private List<ResultWikiSearch> values;

    private String url = null;

    private SelectedPage selectedPage;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView txtHeader;
        TextView txtFooter;
        ImageView Image;
        View layout;

        ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = v.findViewById(R.id.firstLine);
            txtFooter = v.findViewById(R.id.secondLine);
            Image = v.findViewById(R.id.icon);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPage.selectedPage(values.get(getAdapterPosition()));
                }
            });

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    ListAdapter(List<ResultWikiSearch> myDataset, SelectedPage selectedPage) {
        values = myDataset;
        this.selectedPage = selectedPage;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final ResultWikiSearch name = values.get(position);
        holder.txtHeader.setText(name.getTitle());

        makeAPICallImage(Integer.toString(name.getPageid()));

        if(url!=null){
            Picasso.get().load(url).into(holder.Image);
        }else{
            holder.Image.setImageResource(R.drawable.ic_visibility_off_black_24dp);
        }

        if(position==0){
            holder.layout.setBackgroundResource(R.color.firstResultColor);
        }

        String text = Jsoup.parse(name.getSnippet()).text();
        holder.txtFooter.setText(text);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

    public interface SelectedPage{
        void selectedPage (ResultWikiSearch result);
    }

    private void makeAPICallImage(String search){

        Call<String> call = callRestApiWikipediaImage(search);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful() && response.body() != null){
                    url = response.body();
                    if(url.contains("https://upload.wikimedia.org")) {
                        url = url.substring(url.indexOf("https://upload.wikimedia.org"));
                        url = url.concat(".jpg");
                        url = url.substring(0, url.lastIndexOf(".jpg"));
                        url = url.concat(".jpg");
                    }else{
                        url=null;
                    }
                    //}
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
            }
        });
    }

    private Call<String> callRestApiWikipediaImage(String search) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org/w/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        WikipediaApiImage WikipediaApi = retrofit.create(WikipediaApiImage.class);

        return WikipediaApi.getWikipediaResponseImage("query", search, "json", "pageimages");
    }


}
