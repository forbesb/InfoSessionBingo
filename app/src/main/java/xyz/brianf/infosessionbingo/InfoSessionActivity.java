package xyz.brianf.infosessionbingo;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //TODO: layout here? or in OnDownloadComplete. Maybe make a "downloadimg" message?

        if (savedInstanceState == null){
            pullInfosessions();
        }
    }

    @Override
    public void onDownloadComplete(APIResult apiResult) {
        rparser.setAPIResult(apiResult);
        rparser.parseJSON();

        ArrayList<InfoSession> sessions = rparser.getInfoSessions();
        filter(sessions, "MATH - Computer Science");

        //TODO: layout here

    }

    @Override
    public void onDownloadFail(String s, int i) {
        Snackbar.make(this.findViewById(android.R.id.content), "Download Failed.", Snackbar.LENGTH_LONG).setAction("Retry?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pullInfosessions();
            }}).show();
    }

    public void pullInfosessions(){
        rparser.setParseType(ResourcesParser.ParseType.INFOSESSIONS);
        String apiURL = UWOpenDataAPI.buildURL(rparser.getEndPoint(), apiKey);

        JSONDownloader downloader = new JSONDownloader(apiURL);
        downloader.setOnDownloadListener(this);
        downloader.start();

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
