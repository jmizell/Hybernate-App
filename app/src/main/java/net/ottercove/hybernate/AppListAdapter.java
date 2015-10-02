package net.ottercove.hybernate;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AppListAdapter extends ArrayAdapter<AppListModel> {
    private final Context context;
    private final ArrayList<AppListModel> modelsArrayList;

    public AppListAdapter(Context context, ArrayList<AppListModel> modelsArrayList) {
        super(context, R.layout.app_list_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = null;
        rowView = inflater.inflate(R.layout.app_list_item, parent, false);

        ImageView imgView = (ImageView) rowView.findViewById(R.id.app_icon);
        TextView titleView = (TextView) rowView.findViewById(R.id.app_name);

        imgView.setImageDrawable(modelsArrayList.get(position).getIcon());
        titleView.setText(modelsArrayList.get(position).getTitle());

        if(modelsArrayList.get(position).getSystemApp()) {
            titleView.setTypeface(null, Typeface.BOLD_ITALIC);
            titleView.setTextColor(Color.LTGRAY);
            titleView.setText(modelsArrayList.get(position).getTitle() + " (System App)");
        } else {
            titleView.setText(modelsArrayList.get(position).getTitle());
        }

        return rowView;
    }
}