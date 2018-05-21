package com.propelld.app.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YouTube extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener
{
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    int id = 0;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        key = bundle.getString("key");

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube);

        youTubeView.initialize(Configuration.APIKey, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b)
    {
        if(!b)
        {
            // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
            youTubePlayer.cueVideo(key);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult)
    {
    }
}