package ar.com.flowbox.motivation;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends Activity {
  private static final String TAG = "VideoSorpresa";
  static final String FOLDER = "MOTIVATION";
  private static final int READ_REQUEST = 1;

  static File videoFolder() {
    return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), FOLDER);
  }

  @Override public void onCreate(Bundle state) {
    super.onCreate(state);
    getWindow().getDecorView().setBackgroundColor(Color.rgb(8, 21, 30));
    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQUEST);
    } else {
      playRandom();
    }
  }

  @Override public void onRequestPermissionsResult(int request, String[] permissions, int[] results) {
    super.onRequestPermissionsResult(request, permissions, results);
    if (request == READ_REQUEST && results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
      playRandom();
    } else {
      showMessage("Permití el acceso a archivos para reproducir los videos.");
    }
  }

  private void playRandom() {
    List<Video> videos = indexedVideos();
    if (videos.isEmpty()) {
      showMessage("Todavía no hay videos disponibles en Movies/MOTIVATION. Sincronizá esa carpeta con Round Sync.");
      return;
    }
    String last = getPreferences(MODE_PRIVATE).getString("last", "");
    if (videos.size() > 1) {
      for (int i = videos.size() - 1; i >= 0; i--) {
        if (videos.get(i).path.equals(last)) videos.remove(i);
      }
    }
    Video chosen = videos.get(ThreadLocalRandom.current().nextInt(videos.size()));
    Log.i(TAG, "Selected " + chosen.path + " from " + videos.size() + " indexed videos");
    Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, chosen.id);
    VideoView player = new VideoView(this);
    long startedAt = SystemClock.elapsedRealtime();
    player.setKeepScreenOn(true);
    MediaController controls = new MediaController(this);
    controls.setAnchorView(player);
    player.setMediaController(controls);
    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override public void onPrepared(MediaPlayer mediaPlayer) {
        Log.i(TAG, "Prepared, duration=" + mediaPlayer.getDuration() + "ms");
        player.start();
      }
    });
    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override public void onCompletion(MediaPlayer mediaPlayer) {
        Log.i(TAG, "Completed after " + (SystemClock.elapsedRealtime() - startedAt) + "ms");
        finish();
      }
    });
    player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
      @Override public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        Log.e(TAG, "Playback error " + what + "/" + extra);
        showMessage("No se pudo reproducir este video. Probá con otro.");
        return true;
      }
    });
    setContentView(player);
    player.setVideoURI(uri);
    getPreferences(MODE_PRIVATE).edit().putString("last", chosen.path).apply();
  }

  private List<Video> indexedVideos() {
    List<Video> videos = new ArrayList<>();
    String root = videoFolder().getAbsolutePath() + File.separator;
    String[] columns = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA};
    try (Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        columns, MediaStore.Video.Media.DATA + " LIKE ?", new String[]{root + "%"}, null)) {
      if (cursor == null) return videos;
      while (cursor.moveToNext()) {
        String path = cursor.getString(1);
        if (path == null || !path.startsWith(root) || !new File(path).isFile()) continue;
        String name = path.toLowerCase(Locale.ROOT);
        if (name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".webm")
            || name.endsWith(".mov") || name.endsWith(".avi")) {
          videos.add(new Video(cursor.getLong(0), path));
        }
      }
    }
    return videos;
  }

  private static class Video {
    final long id;
    final String path;

    Video(long id, String path) {
      this.id = id;
      this.path = path;
    }
  }

  private void showMessage(String message) {
    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setGravity(Gravity.CENTER);
    layout.setPadding(40, 40, 40, 40);
    TextView text = new TextView(this);
    text.setText(message);
    text.setTextSize(24);
    text.setTextColor(Color.WHITE);
    text.setGravity(Gravity.CENTER);
    layout.addView(text);
    Button retry = new Button(this);
    retry.setText("Volver a probar");
    retry.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) { playRandom(); }
    });
    layout.addView(retry);
    setContentView(layout);
  }
}
