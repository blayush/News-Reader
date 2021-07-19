package com.example.hackernews;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> titles = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask task = new DownloadTask();
        try {
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        } catch (Exception e) {
            e.printStackTrace();
        }
        listView = findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection = null;
            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }
                JSONArray jsonArray = new JSONArray(result);
                int numofItems = 20;
                if (jsonArray.length() < 20) {
                    numofItems = jsonArray.length();
                }
                for (int i = 0; i < numofItems; i++) {
                    String articleId = jsonArray.getString(i);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" + articleId + ".json?print=pretty");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    inputStream = httpURLConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream);
                    data = inputStreamReader.read();
                    String articleInfo="";
                    while (data != -1) {
                        char current = (char) data;
                        articleInfo += current;
                        data = inputStreamReader.read();
                    }
                    JSONObject jsonObject=new JSONObject(articleInfo);
                    if(!jsonObject.isNull("title")&&!jsonObject.isNull("url")){
                        String articleTitle=jsonObject.getString("title");
                        String articleUrl=jsonObject.getString("url");
                        url=new URL(articleUrl);
                        httpURLConnection=(HttpURLConnection)url.openConnection();
                        inputStream=httpURLConnection.getInputStream();
                        inputStreamReader=new InputStreamReader(inputStream);
                        data= inputStreamReader.read();
                        String articleContent="";
                        while(data!=-1){
                            char current=(char)data;
                            articleContent+=current;
                            data= inputStreamReader.read();
                        }
                        Log.i("article content",articleContent);
                    }
                    Log.i("ArticleInfo",articleInfo);
                }
                Log.i("URL CONTENT",result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("URL CONTENT", result);
            return result;

        }
    }
}