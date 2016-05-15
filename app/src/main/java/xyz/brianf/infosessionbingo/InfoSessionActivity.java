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

        updateDisplay(35);
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
        System.out.println(i.getLocation());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) InfoSessionActivity.this.findViewById(R.id.sessionName)).setText(i.getEmployer());
                ((TextView) InfoSessionActivity.this.findViewById(R.id.locationText)).setText(i.getLocation());
                ((TextView) InfoSessionActivity.this.findViewById(R.id.timeText)).setText(i.getDate()+ ":" + i.getStart_time() + "-" + i.getEnd_time());
                ((Button) InfoSessionActivity.this.findViewById(R.id.registerButton)).setOnClickListener(register);
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
