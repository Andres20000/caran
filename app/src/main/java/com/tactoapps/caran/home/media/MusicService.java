package com.tactoapps.caran.home.media;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.tactoapps.caran.R;
import com.tactoapps.caran.home.DetalleContenido;

import java.util.Random;


public class MusicService extends Service implements EventListener{

    private final IBinder mBinder = new LocalBinder();

    public SimpleExoPlayer player;

    private NotificationManager mNM;

    private OnMediaChanges linea = null;

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        if (linea == null) {
            return;
        }

        linea.onLineaTiempoCambio(timeline);


    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {


    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }


    public class LocalBinder extends Binder {
        public MusicService getService() {
            mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            return MusicService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    public void play(String sUrl, OnMediaChanges linea){

        if (player == null){
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());

            player.setPlayWhenReady(true);
            player.addListener(this);
            this.linea = linea;

        }


        //Uri uri = Uri.parse(getString(R.string.media_url_mp3));
        Uri uri = Uri.parse(sUrl);

        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);

        showNotification();

    }




    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-caran")).
                createMediaSource(uri);
    }



    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "MUSIC!!";

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, DetalleContenido.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.boton_caran_rojo)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("Caran")  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        //mNM.notify(55, notification);
        startForeground(55,notification);

    }




    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }


    public void playPausa(){
        player.setPlayWhenReady(!player.getPlayWhenReady());
    }

    public void adelantar(){

        long pos = player.getContentPosition();
        player.seekTo(pos + 30000);

    }


    public void atrazar(){

        long pos = player.getContentPosition();
        player.seekTo(pos - 30000);

    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();

    }
}
