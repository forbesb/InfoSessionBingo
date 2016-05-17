package xyz.brianf.infosessionbingo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

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

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //TODO: layout here? or in OnDownloadComplete. Maybe make a "downloadimg" message?
        setContentView(R.layout.activity_infosession);

        Toolbar toolbar = (Toolbar) findViewById(R.id.topbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null){
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
        filter(sessions, "MATH - Computer Science");
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



    private void updateDisplay(int index){
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

    private void filter(ArrayList<InfoSession> lst, String audience ){
        ArrayList<InfoSession> filtered = new ArrayList<>();
        for (InfoSession item: lst){
            if ((getAudienceSplit(item.getAudience()).contains(audience))){
                filtered.add(item);
            }
        }

        views = filtered;
    }

    private void filterBasedOnDay(ArrayList<InfoSession> lst, String startdate){

    }

    private ArrayList<String> getAudienceSplit(String audience){
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

}
