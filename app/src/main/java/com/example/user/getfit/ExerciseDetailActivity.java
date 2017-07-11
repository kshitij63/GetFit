package com.example.user.getfit;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ExerciseDetailActivity extends YouTubeBaseActivity {
    String name;
    // ImageView button_play;
    ProgressBar bar;
    YouTubePlayerView playerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    ListView listView;
    String play_this;
    ArrayList<BodyPart> parts;
    //private static final String LOAD_THUMB_YOUTUBE="http://img.youtube.com/vi/" +GDFUdMvacI0 +"/0.jpg";
    ArrayList<String> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);
        bar = (ProgressBar) findViewById(R.id.bar);

        listView = (ListView) findViewById(R.id.listview);

        if (savedInstanceState != null) {
            listView.setVisibility(View.GONE);

            bar.setVisibility(View.VISIBLE);
            playerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);
            play_this = savedInstanceState.getString("play_id");
            if (onInitializedListener == null) {
                onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        bar.setVisibility(View.GONE);
                        if (play_this != null) {

                            youTubePlayer.loadVideo(play_this);
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                    }
                };
            }
            playerView.setVisibility(View.VISIBLE);
            playerView.initialize(getResources().getString(R.string.map_api_key), onInitializedListener);

        } else {
            playerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);
            playerView.setVisibility(View.GONE);

            listView.setVisibility(View.VISIBLE);

            Bundle bundle = getIntent().getExtras();
            parts = new ArrayList<>();
            name = bundle.getString("name");
            idList = getExerciseList(name);
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //              listView.setVisibility(View.GONE);
//                playerView.setVisibility(View.VISIBLE);
                    //            bar.setVisibility(View.VISIBLE);
                    //     playerView.initialize(getResources().getString(R.string.map_api_key),onInitializedListener);
                    play_this = idList.get(position);

                }
            });

        }

    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("codebeautify (3).json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private ArrayList<String> getExerciseList(String name) {
        ArrayList<String> id_ex = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String m_name = object.getString("Body part");
                if (name.equalsIgnoreCase(m_name)) {
                    Toast.makeText(ExerciseDetailActivity.this, m_name, Toast.LENGTH_SHORT).show();
                    id_ex.add(object.getString("ex1"));
                    id_ex.add(object.getString("ex2"));
                    id_ex.add(object.getString("ex3"));
                    id_ex.add(object.getString("ex4"));
                    id_ex.add(object.getString("ex5"));

                    //      Toast.makeText(ExerciseDetailActivity.this,object.getString("ex1"),Toast.LENGTH_SHORT).show();
                }

            }


            for (int i = 0; i < id_ex.size(); i++) {
                String Thumurl = getResources().getString(R.string.youtube1) + id_ex.get(i) + getResources().getString(R.string.youtube2);
                BodyPart part = new BodyPart(Thumurl);
                parts.add(part);
            }


            listView.setAdapter(new BodyPartAdapter(this, parts));

        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

        }

        return id_ex;
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("play_id", play_this);
    }
}
