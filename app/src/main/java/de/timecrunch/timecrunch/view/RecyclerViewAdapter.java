package de.timecrunch.timecrunch.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.timecrunch.timecrunch.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mHours = new ArrayList<String>();
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<String> hours) {
        mContext = context;
        mHours = hours;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_hour, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        vh.hourText.setText(mHours.get(position));
    }

    @Override
    public int getItemCount() {
        return mHours.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout hourLayout;
        TextView hourText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hourLayout = itemView.findViewById(R.id.hour_layout);
            hourText = itemView.findViewById(R.id.hour_text);
        }
    }

}