package com.example.photoedge.ui.list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.photoedge.R;
import com.example.photoedge.ui.customview.RatioValues;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class RatioValuesAdapter extends ArrayAdapter<RatioValues> {

    private final Context mContext;
    private final List<RatioValues> ratioList;
    private String selectedRatio = "";
    private RatioChangeListener mListener;
    private final List<View> inflatedLayouts = new ArrayList<>();
    private int lastInflatedWidth = -1;
    private int lastInflatedHeight = -1;

    public RatioValuesAdapter(@NonNull Context context, int resource, List<RatioValues> list) {
        super(context, resource);
        this.mContext = context;
        this.ratioList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomRootView(position, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @Override
    public int getCount() {
        return ratioList.size();
    }

    public View getCustomView(int position, ViewGroup parent) {
        if (lastInflatedWidth == parent.getWidth() && lastInflatedHeight == parent.getHeight())
            return inflatedLayouts.get(position);

        if (position == ratioList.size() - 1) {
            lastInflatedWidth = parent.getWidth();
            lastInflatedHeight = parent.getHeight();
        }

        if (inflatedLayouts.size() <= position) {
            Log.d("Body", "Inflating body at " + position);

            String rh = ratioList.get(position).getRatioHorizontal();
            String rv = ratioList.get(position).getRatioVertical();

            LayoutInflater inflater = LayoutInflater.from(mContext);

            View view = position == 0 ?
                    inflater.inflate(R.layout.ratio_table_item_top, parent, false) :
                    inflater.inflate(R.layout.ratio_table_item, parent, false);

            MaterialButton r1 = view.findViewById(R.id.ratio_1);
            MaterialButton r2 = view.findViewById(R.id.ratio_2);

            r1.setText(rh);
            r2.setText(rv);

            if (rv.isEmpty()) r2.setVisibility(View.GONE);
            else r2.setVisibility(View.VISIBLE);

            if (position == 0) r1.setVisibility(View.GONE);
            else r1.setVisibility(View.VISIBLE);

            r1.setOnClickListener(v -> {
                ratioList.get(0).setRatioHorizontal(rh);
                setSelectedRatio(rh);
                if (mListener != null) mListener.onRatioChanged(selectedRatio);
                notifyDataSetChanged();
            });
            r2.setOnClickListener(v -> {
                ratioList.get(0).setRatioHorizontal(rv);
                setSelectedRatio(rv);
                if (mListener != null) mListener.onRatioChanged(selectedRatio);
                notifyDataSetChanged();
            });
            inflatedLayouts.add(view);
        }

        return inflatedLayouts.get(position);
    }

    public View getCustomRootView(int position, ViewGroup parent) {
        Log.d("Root", "Inflating root at " + position);
        String rh = ratioList.get(position).getRatioHorizontal();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.ratio_table_item_root, parent, false);
        TextView r1 = view.findViewById(R.id.ratio_tv);
        r1.setText(rh);
        return view;
    }

    public String getSelectedRatio() {
        return selectedRatio;
    }

    public void setSelectedRatio(String selectedRatio) {
        this.selectedRatio = selectedRatio;
    }

    public void setListener(RatioChangeListener mListener) {
        this.mListener = mListener;
    }

    public void resetAdapter() {
        ratioList.get(0).setRatioHorizontal(ratioList.get(0).getRatioVertical());
        setSelectedRatio(ratioList.get(0).getRatioVertical());
        notifyDataSetChanged();
    }

    public interface RatioChangeListener {
        void onRatioChanged(String ratio);
    }
}
