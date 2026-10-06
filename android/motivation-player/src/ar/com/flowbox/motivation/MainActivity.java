package ar.com.flowbox.motivation;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends Activity {
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
    List<File> files = new ArrayList<>();
    collect(videoFolder(), files, 0);
    if (files.isEmpty()) {
      showMessage("Todavía no hay videos en Movies/MOTIVATION. Sincronizá esa carpeta con Round Sync.");
      return;
    }
    String last = getPreferences(MODE_PRIVATE).getString("last", "");
    if (files.size() > 1) {
      for (int i = files.size() - 1; i >= 0; i--) {
        if (files.get(i).getAbsolutePath().equals(last)) files.remove(i);
      }
    }
    File chosen = files.get(ThreadLocalRandom.current().nextInt(files.size()));
    String relativePath = chosen.getAbsolutePath().substring(videoFolder().getAbsolutePath().length() + 1);
    Uri uri = Uri.parse("content://ar.com.flowbox.motivation.files/video/" + Uri.encode(relativePath));
    Intent play = new Intent(Intent.ACTION_VIEW);
    play.setDataAndType(uri, "video/*");
    play.setPackage("org.videolan.vlc");
    play.setClipData(ClipData.newUri(getContentResolver(), "Video sorpresa", uri));
    play.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    try {
      startActivity(play);
      getPreferences(MODE_PRIVATE).edit().putString("last", chosen.getAbsolutePath()).apply();
      finish();
    } catch (ActivityNotFoundException e) {
      showMessage("No se encontró VLC para reproducir el video.");
    }
  }

  private void collect(File folder, List<File> result, int depth) {
    if (depth > 5) return;
    File[] children = folder.listFiles();
    if (children == null) return;
    for (File file : children) {
      if (file.isDirectory()) collect(file, result, depth + 1);
      else if (file.isFile()) {
        String name = file.getName().toLowerCase(Locale.ROOT);
        if (name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".webm") || name.endsWith(".mov") || name.endsWith(".avi")) result.add(file);
      }
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
