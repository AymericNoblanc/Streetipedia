package com.example.myfirstandroidproject;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<Result> values;
    private int row_index = -1;

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
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtFooter = (TextView) v.findViewById(R.id.secondLine);
            Image = (ImageView) v.findViewById(R.id.icon);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPage.selectedPage(values.get(getAdapterPosition()));
                }
            });

        }
    }

    public void add(int position, Result item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapter(List<Result> myDataset, SelectedPage selectedPage) {
        values = myDataset;
        this.selectedPage = selectedPage;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Result name = values.get(position);
        holder.txtHeader.setText(name.getTitle());
        if(position==0){
           // holder.layout.setBackgroundColor(Color.parseColor("#EFE395"));
            holder.layout.setBackgroundResource(R.color.firstResultColor);
        }
        /*holder.txtHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(position);
            }
        });*/

        String text = Jsoup.parse(name.getSnippet()).text();
        holder.txtFooter.setText(text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //holder.Image.setImageBitmap(getIconById(name.getPageid()));
        }

       /* holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                notifyDataSetChanged();
            }
        });
        if(row_index==position){
            holder.layout.setBackgroundColor(Color.parseColor("#E82222"));
        }else{
            if(position==0){
                holder.layout.setBackgroundResource(R.color.firstResultColor);
            }else{
                holder.layout.setBackgroundColor(Color.parseColor("#F9F9F9"));
            }
        }*/
    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }


    private Bitmap getIconById(Integer id){

        //Bitmap icon = null;

        try{
            URL url = new URL("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/JulesVallèsCourbetCarnavalet.jpg/43px-JulesVallèsCourbetCarnavalet.jpg");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap icon = BitmapFactory.decodeStream(input);
            return icon;
        }catch (IOException e){
            return null;
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //icon.createWithFilePath("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/JulesVallèsCourbetCarnavalet.jpg/43px-JulesVallèsCourbetCarnavalet.jpg");
            icon.createWithFilePath("mipmap-hdpi/ic_launcher_round.png");
            Log.d(TAG, "It's Work");
            icon.
        }*/

        //return icon;
    }

    public interface SelectedPage{
        void selectedPage (Result result);
    }

}
