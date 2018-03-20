package com.timfeid.devils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tim on 3/19/2018.
 */

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    protected MediaDataset mediaDataset = new MediaDataset();
    protected Activity activity;

    public MediaAdapter(Activity activity) {
        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView blurb;
        public CardView card;
        public ImageView image;
        public Button button;
        public ViewHolder(LinearLayout v) {
            super(v);
            title = v.findViewById(R.id.title);
            image = v.findViewById(R.id.image);
            blurb = v.findViewById(R.id.blurb);
            button = v.findViewById(R.id.button);
            card = v.findViewById(R.id.card_view);
        }
    }

    @Override
    public MediaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MediaDataset.DataItem item = mediaDataset.get(position);
        holder.title.setText(item.getKicker());
        Picasso.get().load(item.getImage()).into(holder.image);
        holder.blurb.setText(item.getBlurb());
        holder.button.setOnClickListener(item.getListener(activity));
        if (position == 0) {
            Resources r = activity.getApplicationContext().getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    16,
                    r.getDisplayMetrics()
            );
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.card.getLayoutParams();
            params.setMargins(px, px, px, px / 2);
        }
    }

    @Override
    public int getItemCount() {
        if (mediaDataset == null) {
            return 0;
        }

        return mediaDataset.length();
    }
}