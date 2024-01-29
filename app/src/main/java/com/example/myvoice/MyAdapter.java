package com.example.myvoice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * make a special adapter for the ListView
 */
public class MyAdapter extends ArrayAdapter<History> {
    ArrayList<History> objects;
    Context context;
    /**
     * constructor function for the special adapter
     * @param context who runs me
     * @param resource the row for the regular adapter
     * @param objects the arrayList of history
     */
    public MyAdapter(@NonNull Context context, int resource, @NonNull ArrayList<History> objects) {
        super(context, resource, objects);
        this.context=context;
        this.objects=objects;
    }
    /**
     * create what the adapter will do ,how it will look like according to the line you chose in the listView
     * @param position get the position of the user in the arrayList
     * @param convertView gets the layout you run it on
     * @param parent not in use
     * @return how a line will look like
     */
    public View getView(int position , View convertView , ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_row, null);
        TextView tvTime = v.findViewById(R.id.tvTime);
        TextView tvWhatIDid = v.findViewById(R.id.tvWhatIDid);
        TextView tvDetails = v.findViewById(R.id.tvDetails);
        ImageView ivWhatIDid = v.findViewById(R.id.ivWhatIDid);
        tvTime.setText(objects.get(position).getTime());
        tvWhatIDid.setText(objects.get(position).getWhatIdid());
        tvDetails.setText(objects.get(position).getDetails());
        if (objects.get(position).getWhatIdid().contains("התקשרת ל")) {
            ivWhatIDid.setImageResource(R.drawable.phone);
        } else if (objects.get(position).getWhatIdid().contains("שלחת הודעה ל")) {
            ivWhatIDid.setImageResource(R.drawable.sms);
        } else if (objects.get(position).getWhatIdid().contains("נסעת עם וויז")) {
            ivWhatIDid.setImageResource(R.drawable.waze);
        } else if (objects.get(position).getWhatIdid().contains("חיפשת בגוגל"))
            ivWhatIDid.setImageResource(R.drawable.google);
        return v;
    }
}
