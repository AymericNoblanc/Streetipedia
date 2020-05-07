package com.example.streetipedia.presentation.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streetipedia.R;
import com.example.streetipedia.presentation.model.Rue;
import com.squareup.picasso.Picasso;

import java.util.List;

//Class that fill and show the recyclerview
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
            txtHeader = v.findViewById(R.id.firstLine);//Identify the main textView
            txtFooter = v.findViewById(R.id.secondLine);//Identify the other textView
            Image = v.findViewById(R.id.icon);//Identify the image

            //On Click Listener for go to another activity
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
        values = myDataset; //data given ( the List with all Street)
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Rue name = values.get(position); //Get just the street interesting

        //If the name of the street have a thumbnail on wikipedia show if else show a no view picture
        if(name.getThumbnail()!=null){
            Picasso.get().load(name.getThumbnail()).into(holder.Image);
        }else{
            holder.Image.setImageResource(R.drawable.ic_visibility_off_black_24dp);
        }

        //Show the first result (most probably the current street of the user) yellow
        //DIDN'T WORK (for any strange reason)
        if(name.getNomRue().endsWith("*")){
            holder.layout.setBackgroundResource(R.color.firstResultColor);
            name.setNomRue(name.getNomRue().substring(0,name.getNomRue().indexOf("*")));
        }
        holder.txtHeader.setText(name.getNomRue()); //Write the name of the street in the main textView

        String text = Jsoup.parse(name.getSnippet()).text(); //Delete html tag of the text
        holder.txtFooter.setText(text); //Write the snippet (or small description) of the street in the second textView
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }//Method obligatory

    //Method for prepare the second activity
    public interface SelectedPage{
        void selectedPage (Rue result);
    }

}
