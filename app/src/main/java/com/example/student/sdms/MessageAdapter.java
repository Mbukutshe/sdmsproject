package com.example.student.sdms;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Wiseman on 2016-10-02.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private List<ItemObject> mDataset;
    private Context context;

    public static class MessageHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView message,subject,link,author,date;
        public MessageHolder(View tv) {
            super(tv);
            message = (TextView)tv.findViewById(R.id.message_message);
            author = (TextView)tv.findViewById(R.id.author_message);
            date = (TextView)tv.findViewById(R.id.time_message);
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public MessageAdapter(Context context,List<ItemObject>mDataset) {
        this.mDataset = mDataset;
        this.context=context;
    }
    public void animate(RecyclerView.ViewHolder viewHolder)
    {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.bounce);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }
    // Create new views (invoked by the layout manager)
    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_messages, parent, false);
        // set the view's size, margins, paddings and layout parameters

        MessageHolder vh = new MessageHolder(view);
        return vh;
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MessageHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.message.setText(mDataset.get(position).getMessage());
        holder.author.setText("by: "+mDataset.get(position).getAuthor());
        holder.date.setText(mDataset.get(position).getDate());
        //animate(holder);
        final SqliteController controller = new SqliteController(context);

        ImageView delete = (ImageView)holder.itemView.findViewById(R.id.delete_message);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeItem(position);
                controller.deleteMessage(position);
            }
        });
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return this.mDataset.size();
    }
    public void removeItem(int position)
    {
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mDataset.size());
    }

}
