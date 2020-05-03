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
    private List<Rue> values;

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
    ListAdapter(List<Rue> myDataset, SelectedPage selectedPage) {
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
        final Rue name = values.get(position);

        //makeAPICallImage(Integer.toString(name.getPageid()));

        if(name.getThumbnail()!=null){
            Picasso.get().load(name.getThumbnail()).into(holder.Image);
        }else{
            holder.Image.setImageResource(R.drawable.ic_visibility_off_black_24dp);
        }

        if(name.getNomRue().endsWith("*")){
            holder.layout.setBackgroundResource(R.color.firstResultColor);
            name.setNomRue(name.getNomRue().substring(0,name.getNomRue().indexOf("*")));
        }
        holder.txtHeader.setText(name.getNomRue());

        String text = Jsoup.parse(name.getSnippet()).text();
        holder.txtFooter.setText(text);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

    public interface SelectedPage{
        void selectedPage (Rue result);
    }

}
