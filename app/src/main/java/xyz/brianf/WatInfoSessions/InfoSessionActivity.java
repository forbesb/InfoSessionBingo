package xyz.brianf.WatInfoSessions;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Core.APIResult;
import Core.JSONDownloader;
import Core.UWOpenDataAPI;
import Resources.InfoSession;
import Resources.ResourcesParser;

/**
 * Created by brian on 12/05/16.
 */
//public class InfoSessionActivity extends AppCompatActivity implements JSONDownloader.onDownloadListener {
//}

public class InfoSessionActivity extends AppCompatActivity implements JSONDownloader.onDownloadListener {
    String apiKey = "525683dba54d5b8d1b1de8d30606c00f"; //// TODO: 12/05/16 : Not hardcode this?
    ResourcesParser rparser = new ResourcesParser();
    ArrayList<InfoSession> sessions = null;
    ArrayList<InfoSession> views = null;

    int currentSession = 0;
    public final static String ID_SESSIONS = "xyz.brianf.infosessionbingo.SESSIONS";
    public final static String FILTER_DAY_NEVER = "@never";
    public final static String FILTER_DAY_TODAY = "@today";
    private int startDay, startMonth, startYear, endDay=-1, endMonth=-1, endYear=-1;



    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //TODO: layout here? or in OnDownloadComplete. Maybe make a "downloadimg" message?
        setContentView(R.layout.activity_infosession);

        Toolbar toolbar = (Toolbar) findViewById(R.id.topbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null){
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            startDay = cal.get(Calendar.DAY_OF_MONTH);
            startMonth = cal.get(Calendar.MONTH);
            startYear = cal.get(Calendar.YEAR);
            System.out.println(startDay+ " " + startMonth + " " + startYear);
            pullInfosessions();
        }

    }

    @Override
    public void onDownloadComplete(APIResult apiResult) {
        rparser.setAPIResult(apiResult);
        rparser.parseJSON();

        sessions = rparser.getInfoSessions();
        views = sessions;
        //TODO: filter on request
        //filterByAudience(sessions, "MATH - Computer Science");
        views = filterByDay(views, FILTER_DAY_TODAY, FILTER_DAY_NEVER);

        final View.OnClickListener next = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSession<(views.size()-1)) {
                    currentSession += 1;
                    updateDisplay(currentSession);
                }
            }};
        final View.OnClickListener prev = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSession>0) {
                    currentSession -= 1;
                    updateDisplay(currentSession);
                }
            }};

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InfoSessionActivity.this.findViewById(R.id.previousButton).setOnClickListener(prev);
                InfoSessionActivity.this.findViewById(R.id.nextButton).setOnClickListener(next);
            }
        });

        updateDisplay(0);
    }

    @Override
    public void onDownloadFail(String s, int i) {
        Snackbar.make(this.findViewById(android.R.id.content), "Download Failed.", Snackbar.LENGTH_LONG).setAction("Retry?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pullInfosessions();
            }}).show();
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                pullInfosessions();
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                //open and make settings
                break;
            case R.id.action_calendar:
                createCalendarActivity();
                break;
            case R.id.action_filters:
                createStartDateFilter();
                break;
            default:
                break;
        }

        return true;
    }


    private void createCalendarActivity(){
        ArrayList<ParcelableInfoSession> parcelledSessions = new ArrayList<>();
        for (InfoSession i: sessions){
            parcelledSessions.add(new ParcelableInfoSession(i));
        }

        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putParcelableArrayListExtra(ID_SESSIONS, parcelledSessions);
        startActivity(intent);
    }


    private void pullInfosessions(){
        rparser.setParseType(ResourcesParser.ParseType.INFOSESSIONS);
        String apiURL = UWOpenDataAPI.buildURL(rparser.getEndPoint(), apiKey);

        JSONDownloader downloader = new JSONDownloader(apiURL);
        downloader.setOnDownloadListener(this);
        downloader.start();

    }

    private void createStartDateFilter(){
        final PopupMenu filterPopup = new PopupMenu(this, findViewById(R.id.action_filters));
        filterPopup.getMenuInflater().inflate(R.menu.menu_filters, filterPopup.getMenu());
        filterPopup.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.startDateFilter:
                                buildStartDialog();
                                filterPopup.show();
                                break;
                            case R.id.endDateFilter:
                                buildEndDialog();
                                filterPopup.show();
                                break;
                            case R.id.updateFilters:
                                updateFilters();
                                break;
                            default:
                                break;
                        }

                        return false;
                    }
                }
        );
        filterPopup.show();


    }

    private void updateDisplay(int index){
        if (index>=views.size()){
            System.out.println(index);
            ((TextView) InfoSessionActivity.this.findViewById(R.id.sessionName)).setText("No Info Sessions Found");
            return;
        }
        final InfoSession i = views.get(index);
        final View.OnClickListener register = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = i.getId();
                String url = "https://info.uwaterloo.ca/infocecs/students/rsvp/index.php?id="+id+"&mode=on";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);

            }};

        final View.OnClickListener map = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "geo:0,0?q="+i.getBuilding().getLatitude()+","+i.getBuilding().getLongitude()+"("+i.getBuilding().getCode()+")"+"?z=17";
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(mapIntent);

            }};
        //minimizing work done on UI thread, might as well build strings first
        final String employer = i.getEmployer();
        final String location = i.getBuilding().getName()+" ("+i.getBuilding().getCode()+")";
        final String room = "Room: " + i.getBuilding().getRoom();
        final String date = i.getDate();
        final String time = i.getStart_time() + "-" + i.getEnd_time();
        final String description = i.getDescription();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) InfoSessionActivity.this.findViewById(R.id.sessionName)).setText(employer);
                ((TextView) InfoSessionActivity.this.findViewById(R.id.locationText)).setText(location);
                ((TextView) InfoSessionActivity.this.findViewById(R.id.roomText)).setText(room);
                ((TextView) InfoSessionActivity.this.findViewById(R.id.dateText)).setText(date);
                ((TextView) InfoSessionActivity.this.findViewById(R.id.timeText)).setText(time);
                ((TextView) InfoSessionActivity.this.findViewById(R.id.description)).setText(description);
                ((TextView) InfoSessionActivity.this.findViewById(R.id.description)).scrollTo(0,0);
                ((Button) InfoSessionActivity.this.findViewById(R.id.registerButton)).setOnClickListener(register);
                ((Button) InfoSessionActivity.this.findViewById(R.id.mapButton)).setOnClickListener(map);
            }
        });
    }

    public static ArrayList<InfoSession> filterByAudience(ArrayList<InfoSession> lst, String[] audience ){
        ArrayList<InfoSession> filtered = new ArrayList<>();
        for (InfoSession item: lst){
            for (String field: audience){
                if ((getAudienceSplit(item.getAudience()).contains(field))){
                    filtered.add(item);
                    break;
                }
            }

        }

        return filtered;
    }

    public static ArrayList<InfoSession> filterByDay(ArrayList<InfoSession> lst, String startdate, String enddate){
        long range = Long.MAX_VALUE;
        ArrayList<InfoSession> filtered = new ArrayList<>();
        if (startdate == FILTER_DAY_TODAY){
            startdate = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        }
        if (enddate != FILTER_DAY_NEVER){
            range = dateDifference(startdate, enddate);
        }
        long dd;
        for (InfoSession sesh: lst){
            dd = dateDifference(startdate, sesh.getDate()) ;

            if (dd <= range && dd >= 0){
                System.out.println(sesh.getDate());
                filtered.add(sesh);
            }
        }
        return filtered;

    }

    private static long dateDifference(String first, String second){
        java.text.DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        try {
            Date firstDate = format.parse(first);
            Date secondDate = format.parse(second);

            return (secondDate.getTime() - firstDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L; //in case of an error

    }

    private static ArrayList<String> getAudienceSplit(String audience){
        String[] splits = audience.split("\"");
        ArrayList<String> audiences = new ArrayList<>();
        for (int i = 0; i< splits.length; i++){
            if ((splits[i]).length()>1)
                audiences.add(splits[i]);
        }
        for (String a: audiences){
            //System.out.println(a);
        }
        return audiences;
    }

    private void updateFilters(){
        views = filterByDay(sessions, String.format("%04d-%02d-%02d", startYear, startMonth, startDay), endYear == -1 ? FILTER_DAY_NEVER : String.format("%04d-%02d-%02d", endYear, endMonth, endDay));
        updateDisplay(0);
    }

    private void buildStartDialog(){
        DatePickerDialog dateDialog = new DatePickerDialog(InfoSessionActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startYear = year;
                startMonth = monthOfYear;
                startDay = dayOfMonth;
            }
        }, startYear, startMonth, startDay);
        dateDialog.show();
    }

    private void buildEndDialog(){
        DatePickerDialog dateDialog = new DatePickerDialog(InfoSessionActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                endYear = year;
                endMonth = monthOfYear;
                endDay = dayOfMonth;
            }
        },
                endYear == -1 ? startYear : endYear,
                endMonth == -1 ? startMonth : endMonth,
                endDay == -1 ? startDay : endDay);

        dateDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Never", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endYear = -1;
                endDay = -1;
                endMonth = -1;
            }
        });
        dateDialog.show();

    }

    final ArrayList<String> audiences = new ArrayList<>(); //populate properly
    private void populateAuduences(){
        audiences.clear();
        audiences.add("MATH - Computer Science");
        audiences.add("ENG - Software");
        audiences.add("ENG - Electrical");
        audiences.add("ENG - Computer");
        audiences.add("ARTS - Speech Communication");
    }

    private void buildAudienceDialog(){
        if (audiences.isEmpty()){
            populateAuduences();
        }
        boolean[] checkSelected = new boolean[audiences.size()];
        for (int i = 0; i<audiences.size(); i++){
            checkSelected[i]=false;
        }
        LayoutInflater inflater = (LayoutInflater) InfoSessionActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.popup_audience, (ViewGroup) findViewById(R.id.audiencePopup));

        final PopupWindow popup = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.setTouchable(true);
        popup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_OUTSIDE){
                    popup.dismiss();
                    return true;
                }
                return false;
            }
        });
        popup.setContentView(layout);
        popup.showAsDropDown(findViewById(R.id.main_layout));



    }

}
