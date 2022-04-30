package com.example.recipegenerator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int resourceLayout;

    public ListAdapter(Context context, int resource, List<String> itemArrayList) {
        super(context,resource, itemArrayList);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(R.layout.list_item, null);
        }
        String p = getItem(position);

        if (p != null){
            TextView title = v.findViewById(R.id.recipe_title);
            TextView count = v.findViewById(R.id.recipe_count);

            title.setText(p);
            count.setText(" Resipe "+(position+1));

        }

        return v;
    }
}
