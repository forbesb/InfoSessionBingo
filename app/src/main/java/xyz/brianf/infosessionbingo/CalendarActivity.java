package xyz.brianf.infosessionbingo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import Resources.InfoSession;

/**
 * Created by brian on 16/05/16.
 */
public class CalendarActivity extends AppCompatActivity {
    private ArrayList<InfoSession> sessions;
    private ArrayList<InfoSession> views;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ArrayList<ParcelableInfoSession> parcelableInfoSessions = getIntent().getParcelableArrayListExtra(InfoSessionActivity.ID_SESSIONS);
        sessions = new ArrayList<>();
        for (ParcelableInfoSession ps: parcelableInfoSessions) {
            sessions.add(ps.getData());
        }
        initCalendar();


    }

    private void initCalendar(){
        CalendarView cal = (CalendarView) findViewById(R.id.calendar);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                updateListView(year, month, day);
            }
        });
    }

    private void updateListView(int year, int month, int day){
        GregorianCalendar gc = new GregorianCalendar();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        gc.set(year, month, day);
        String date = df.format(gc.getTime());
        views = InfoSessionActivity.filterByDay(sessions, date, date );
        ListView lst = (ListView) findViewById(R.id.listOfEvents);
    }
}
