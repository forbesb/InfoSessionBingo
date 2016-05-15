package xyz.brianf.infosessionbingo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    ArrayList<InfoSession> sessions;
    int currentSession = 0;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //TODO: layout here? or in OnDownloadComplete. Maybe make a "downloadimg" message?
        setContentView(R.layout.activity_infosession);
        if (savedInstanceState == null){
            pullInfosessions();
        }
    }

    @Override
    public void onDownloadComplete(APIResult apiResult) {
        rparser.setAPIResult(apiResult);
        rparser.parseJSON();

        sessions = rparser.getInfoSessions();
        //TODO: filter on request
        final View.OnClickListener next = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSession<sessions.size()) {
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

    private void pullInfosessions(){
        rparser.setParseType(ResourcesParser.ParseType.INFOSESSIONS);
        String apiURL = UWOpenDataAPI.buildURL(rparser.getEndPoint(), apiKey);

        JSONDownloader downloader = new JSONDownloader(apiURL);
        downloader.setOnDownloadListener(this);
        downloader.start();

    }



    private void updateDisplay(int index){
        final InfoSession i = sessions.get(index);
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


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) InfoSessionActivity.this.findViewById(R.id.sessionName)).setText(i.getEmployer());
                ((TextView) InfoSessionActivity.this.findViewById(R.id.locationText)).setText(i.getBuilding().getName()+" ("+i.getBuilding().getCode()+")");
                ((TextView) InfoSessionActivity.this.findViewById(R.id.dateText)).setText(i.getDate());
                ((TextView) InfoSessionActivity.this.findViewById(R.id.timeText)).setText(i.getStart_time() + "-" + i.getEnd_time());
                ((TextView) InfoSessionActivity.this.findViewById(R.id.description)).setText(i.getDescription());
                ((Button) InfoSessionActivity.this.findViewById(R.id.registerButton)).setOnClickListener(register);
                ((Button) InfoSessionActivity.this.findViewById(R.id.mapButton)).setOnClickListener(map);
            }
        });
    }

    private void filter(ArrayList<InfoSession> lst, String audience ){
        ArrayList<InfoSession> rems = new ArrayList<>();
        for (InfoSession item: lst){
            if (! (getAudienceSplit(item.getAudience()).contains(audience))){
                rems.add(item);
            }
        }
        for (InfoSession item: rems){
            lst.remove(item);
        }
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