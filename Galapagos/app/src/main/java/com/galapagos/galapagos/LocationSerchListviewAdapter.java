package com.galapagos.galapagos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galapagos.galapagos.poivalueobject.LocationSeachResult;

import java.util.ArrayList;

/**
 * Created by Arous on 2016-11-24.
 */

public class LocationSerchListviewAdapter extends RecyclerView.Adapter<LocationSerchListviewAdapter.LocationWriteItemView> {

    Context mContext;
    ArrayList<LocationSeachResult> items;

    public interface onLocationItemClickListener {
        public void onLocationItemClicked(RecyclerView.ViewHolder viewHolder, View view, LocationSeachResult result, int position);
    }

    onLocationItemClickListener mOnLocationItemClickListener;


    public void setOnLocationItemClickListener(onLocationItemClickListener listener){
        mOnLocationItemClickListener = listener;
    }


    public LocationSerchListviewAdapter(Context mContext) {
        this.mContext = mContext;
        items = new ArrayList<>();
    }

    public void itemClaer(){
        items.clear();
    }

    public void addAll(ArrayList<LocationSeachResult> locationSearchList) {
        items.addAll(locationSearchList);
        notifyDataSetChanged();
    }

    public void addItem(LocationSeachResult result){
        items.add(result);
        notifyDataSetChanged();
    }

    public void detleteItem(){
        int size = this.items.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.items.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public LocationWriteItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_location_write_item, parent, false);
        return new LocationWriteItemView(view);
    }

    @Override
    public void onBindViewHolder(LocationWriteItemView holder, int position) {
        holder.setLocationItemTextView(items.get(position));
        holder.setOnLocationItemClickListener(mOnLocationItemClickListener);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class LocationWriteItemView extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView locationItemTextView;
        LocationSeachResult data;


        public LocationWriteItemView(View itemView) {
            super(itemView);
            locationItemTextView = (TextView) itemView.findViewById(R.id.textview_location_listview_item);
            locationItemTextView.setOnClickListener(this);

        }

        public void setOnLocationItemClickListener(onLocationItemClickListener listener){
            mOnLocationItemClickListener = listener;
        }

        public void setLocationItemTextView(LocationSeachResult result) {
            data = result;
            locationItemTextView.setText(data.locationSub);
        }

        @Override
        public void onClick(View view) {
            if (mOnLocationItemClickListener != null){
                mOnLocationItemClickListener.onLocationItemClicked(LocationWriteItemView.this,view,data,getAdapterPosition());
            }

        }
    }
}
