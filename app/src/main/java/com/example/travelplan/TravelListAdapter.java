package com.example.travelplan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TravelListAdapter extends BaseAdapter {
    private Context context;
    private  int layout;
    private ArrayList<TravelPlan> travelPlansList;

    public TravelListAdapter(Context context, int layout, ArrayList<TravelPlan> travelPlansList) {
        this.context = context;
        this.layout = layout;
        this.travelPlansList = travelPlansList;
    }

    @Override
    public int getCount() {
        return travelPlansList.size();
    }

    @Override
    public Object getItem(int position) {
        return travelPlansList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtName, txtPrice;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.txtName);
            holder.txtPrice = (TextView) row.findViewById(R.id.txtPrice);
            holder.imageView = (ImageView) row.findViewById(R.id.imgFood);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        TravelPlan travelPlan = travelPlansList.get(position);

        holder.txtName.setText(travelPlan.getName());
        holder.txtPrice.setText(travelPlan.getPrice());

        byte[] travelPlanImageImage = travelPlan.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(travelPlanImageImage, 0, travelPlanImageImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
