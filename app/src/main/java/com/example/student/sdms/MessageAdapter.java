package com.example.student.sdms;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        public TextView messageId,message,subject,link,author,date,urgent,urgent_text;
        public MessageHolder(View tv) {
            super(tv);
            message = (TextView)tv.findViewById(R.id.message_message);
            author = (TextView)tv.findViewById(R.id.author_message);
            date = (TextView)tv.findViewById(R.id.time_message);
            messageId = (TextView)tv.findViewById(R.id.messageid);
            urgent = (TextView)tv.findViewById(R.id.urgent);
            subject =(TextView)tv.findViewById(R.id.subject_message);
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
    public void onBindViewHolder(final MessageHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ImageView options = (ImageView)holder.itemView.findViewById(R.id.delete_message);
        Context popup = new ContextThemeWrapper(context,R.style.popup);
        final PopupMenu optionsMenu = new PopupMenu(popup,options);
        MenuInflater inflater = optionsMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_delete,optionsMenu.getMenu());

        holder.message.setText(mDataset.get(position).getMessage());
        holder.author.setText(mDataset.get(position).getAuthor());
        holder.date.setText(mDataset.get(position).getDate());
        holder.messageId.setText(""+mDataset.get(position).getMessageId());
        holder.subject.setText("Subject:\t"+mDataset.get(position).getSubject());
        if(mDataset.get(position).getUrgent().toString().equals("Yes"))
        {
            holder.urgent.setBackgroundResource(R.drawable.important);
        }
        //animate(holder);
        final SqliteController controller = new SqliteController(context);

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionsMenu.show();
            }
        });

        optionsMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem menuItem) {
               int item = menuItem.getItemId();
               if (item == R.id.delete_messages) {
                   removeItem(position);
                   controller.deleteMessage(Integer.parseInt(holder.messageId.getText().toString()));
               }
               return false;
           }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.cardview_popup_message);
                dialog.setCanceledOnTouchOutside(false);
                TextView message = (TextView)dialog.findViewById(R.id.message_message);
                TextView author = (TextView)dialog.findViewById(R.id.author_message);
                TextView time = (TextView)dialog.findViewById(R.id.time_message);
                ImageView back = (ImageView)dialog.findViewById(R.id.left_arrow);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                message.setText(holder.message.getText());
                time.setText(holder.date.getText());
                author.setText(" "+holder.author.getText());
                dialog.show();
                return false;
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
    public void addItem( List<ItemObject> newData)
    {
        mDataset.addAll(newData);
        notifyItemInserted(0);
        notifyDataSetChanged();
    }

}
