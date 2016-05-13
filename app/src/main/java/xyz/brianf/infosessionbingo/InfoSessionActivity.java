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
        //TODO: layout here

        if (savedInstanceState == null){
            pullInfosessions();
        }
    }

    @Override
    public void onDownloadComplete(APIResult apiResult) {
        rparser.setAPIResult(apiResult);
        rparser.parseJSON();

        ArrayList<InfoSession> sessions = rparser.getInfoSessions();
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
}
