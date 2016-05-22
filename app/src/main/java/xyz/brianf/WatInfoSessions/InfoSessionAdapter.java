package xyz.brianf.WatInfoSessions;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import Resources.InfoSession;

/**
 * Created by brian on 21/05/16.
 */
public class InfoSessionAdapter extends BaseAdapter implements View.OnClickListener{
    private Activity creator;
    private ArrayList<InfoSession> sessions;
    private static LayoutInflater inflater = null;
    public Resources res;

    public InfoSessionAdapter(Activity a, ArrayList<InfoSession> s, Resources r){
        res = r;
        creator = a;
        sessions = s;
        inflater = (LayoutInflater) creator.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return sessions.size();
    }

    @Override
    public Object getItem(int position) {
        return sessions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView name;
        public TextView time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder v;
        if (convertView == null){
            vi = inflater.inflate(R.layout.item_calendar, null);
            v = new ViewHolder();
            v.name = (TextView) vi.findViewById(R.id.item_name);
            v.time = (TextView) vi.findViewById(R.id.item_time);

            vi.setTag(v);
        } else {
            v = (ViewHolder) vi.getTag();
        }

        InfoSession s;
        if (sessions.size()<=0){
            v.name.setText("No Info Sessions");
            v.time.setText("");
        } else {
            s = sessions.get(position);
            v.name.setText(s.getEmployer());
            v.time.setText(s.getStart_time()+"-"+s.getEnd_time());

            vi.setOnClickListener(new CalendarOnClickListener(position));

        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked====="); //change to better logging message?

    }

    private class CalendarOnClickListener implements View.OnClickListener{
        private int position;

        public CalendarOnClickListener(int pos){
            position = pos;
        }


        @Override
        public void onClick(View v) {
            CalendarActivity ca = (CalendarActivity) creator;
            ca.onItemClick(position);
        }
    }
}
