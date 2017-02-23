package com.example.student.sdms;

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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
    ImageView imageView;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView message,subject,link,author,date,filename;
        public ViewHolder(View tv) {
            super(tv);
            message = (TextView)tv.findViewById(R.id.message);
            subject = (TextView)tv.findViewById(R.id.info_text);
            author = (TextView)tv.findViewById(R.id.author);
            date = (TextView)tv.findViewById(R.id.time);
            link = (TextView)tv.findViewById(R.id.link);
            filename = (TextView)tv.findViewById(R.id.filename);


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
        holder.message.setText(mDataset.get(position).getMessage());
        holder.subject.setText("Subject:\t"+mDataset.get(position).getSubject());
        holder.link.setText(mDataset.get(position).getLink());
        holder.filename.setText(mDataset.get(position).getLink());
        holder.author.setText("by:\t"+mDataset.get(position).getAuthor());
        holder.date.setText(mDataset.get(position).getDate());

        Button b = (Button)holder.itemView.findViewById(R.id.download);
        Button bview = (Button)holder.itemView.findViewById(R.id.view);
        Button delete = (Button)holder.itemView.findViewById(R.id.clear);

        final SqliteController controller = new SqliteController(context);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
                controller.deleteDocuments(position);
            }
        });
        bview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse( holder.link.getText().toString());
                Intent browser = new Intent(Intent.ACTION_VIEW,uri);
                context.startActivity(browser);
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs = PreferenceManager.getDefaultSharedPreferences(context);
                dm = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
                Uri resource = Uri.parse(holder.link.getText().toString());
                DownloadManager.Request request = new DownloadManager.Request(resource);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setAllowedOverRoaming(false);
                request.setTitle("SDMS");
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, holder.filename.getText().toString());
                request.setDescription("downloading ..... " + holder.filename.getText().toString());
                request.setVisibleInDownloadsUi(true);
                long id = dm.enqueue(request);
                prefs.edit().putLong(DL_ID, id).commit();
                context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        });

        imageView = (ImageView)holder.itemView.findViewById(R.id.options_menu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(),v);
                popupMenu.getMenuInflater().inflate(R.menu.operation_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });
                popupMenu.show();
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
