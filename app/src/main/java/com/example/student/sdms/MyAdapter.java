package com.example.student.sdms;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Wiseman on 2016-10-02.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<ItemObject> mDataset;
    private Context context;
    private static final String DL_ID = "downloadId";
    private SharedPreferences prefs;
    private DownloadManager dm;
    private PopupMenu optionsMenu;
    ImageView imageView;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView messageId,message,subject,link,author,date,filename;
        public ViewHolder(View tv) {
            super(tv);
            message = (TextView)tv.findViewById(R.id.message);
            subject = (TextView)tv.findViewById(R.id.info_text);
            author = (TextView)tv.findViewById(R.id.author);
            date = (TextView)tv.findViewById(R.id.time);
            link = (TextView)tv.findViewById(R.id.link);
            filename = (TextView)tv.findViewById(R.id.filename);
            messageId = (TextView)tv.findViewById(R.id.docId);

        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context,List<ItemObject>mDataset) {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // create a new view
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ImageView options = (ImageView)holder.itemView.findViewById(R.id.options);
        Context popup = new ContextThemeWrapper(context,R.style.popup);
        final PopupMenu optionsMenu = new PopupMenu(popup,options);
        MenuInflater inflater = optionsMenu.getMenuInflater();
        inflater.inflate(R.menu.operation_menu,optionsMenu.getMenu());

        holder.message.setText(mDataset.get(position).getMessage());
        holder.subject.setText("Subject:\t"+mDataset.get(position).getSubject());
        holder.link.setText(mDataset.get(position).getLink());
        holder.filename.setText(mDataset.get(position).getLink());
        holder.author.setText("by:\t"+mDataset.get(position).getAuthor());
        holder.date.setText(mDataset.get(position).getDate());
        holder.messageId.setText(""+mDataset.get(position).getMessageId());


        final SqliteController controller = new SqliteController(context);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.cardview_popup_document);

                TextView message = (TextView)dialog.findViewById(R.id.message);
                TextView subject = (TextView)dialog.findViewById(R.id.info_text);
                TextView author = (TextView)dialog.findViewById(R.id.author);
                TextView date = (TextView)dialog.findViewById(R.id.time);
                TextView link = (TextView)dialog.findViewById(R.id.link);
                TextView filename = (TextView)dialog.findViewById(R.id.filename);
                TextView messageId = (TextView)dialog.findViewById(R.id.docId);

                ImageView back = (ImageView)dialog.findViewById(R.id.left_arrow);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                message.setText(holder.message.getText());
                date.setText(holder.date.getText());
                subject.setText(holder.subject.getText());
                link.setText(holder.link.getText());
                filename.setText(holder.filename.getText());
                messageId.setText(holder.messageId.getText());
                author.setText(holder.author.getText());
                dialog.show();
                return true;
            }
        });


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
                if(item==R.id.view_doc)
                {
                    Uri uri = Uri.parse( holder.link.getText().toString());
                    Intent browser = new Intent(Intent.ACTION_VIEW,uri);
                    context.startActivity(browser);
                }
                else
                    if(item==R.id.download_doc)
                    {
                        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                        if(isConnected)
                        {
                            prefs = PreferenceManager.getDefaultSharedPreferences(context);
                            dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                            Uri resource = Uri.parse(holder.link.getText().toString());
                            DownloadManager.Request request = new DownloadManager.Request(resource);
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                            request.setAllowedOverRoaming(false);
                            request.setTitle(holder.filename.getText().toString());
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, holder.filename.getText().toString());
                            request.setDescription("downloading ..... " + holder.filename.getText().toString());
                            request.setVisibleInDownloadsUi(true);
                            long id = dm.enqueue(request);
                            prefs.edit().putLong(DL_ID, id).commit();
                            context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        }
                        else
                        {
                            LayoutInflater infla = LayoutInflater.from(holder.itemView.getContext());
                            View layout =infla.inflate(R.layout.toast_container_layout,(ViewGroup)holder.itemView.findViewById(R.id.toast_layout));
                            TextView textview = (TextView)layout.findViewById(R.id.toast_message);
                            layout.setBackgroundResource(R.color.toastColor);
                            textview.setText("No internet connection. Please check internet settings or the strength isn't good enough.");
                            Toast toast = new Toast(context);
                            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();
                        }
                    }
                    else
                        if(item==R.id.delete_doc)
                        {
                            removeItem(position);
                            controller.deleteDocuments(Integer.parseInt(holder.messageId.getText().toString()));
                        }
                return true;
            }
        });



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return this.mDataset.size();
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //queryDownloadStatus();
        }
    };
    public void removeItem(int position)
    {
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mDataset.size());
    }

}
