package ar.com.flowbox.motivation;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class VideoProvider extends ContentProvider {
  @Override public boolean onCreate() { return true; }

  private File resolve(Uri uri) throws FileNotFoundException {
    if (uri.getPathSegments().size() != 2 || !"video".equals(uri.getPathSegments().get(0))) throw new FileNotFoundException();
    try {
      File root = MainActivity.videoFolder().getCanonicalFile();
      File file = new File(root, uri.getLastPathSegment()).getCanonicalFile();
      if (!file.getPath().startsWith(root.getPath() + File.separator) || !file.isFile()) throw new FileNotFoundException();
      return file;
    } catch (IOException e) {
      throw new FileNotFoundException();
    }
  }

  @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
    if (!"r".equals(mode)) throw new FileNotFoundException();
    return ParcelFileDescriptor.open(resolve(uri), ParcelFileDescriptor.MODE_READ_ONLY);
  }

  @Override public String getType(Uri uri) { return "video/*"; }

  @Override public Cursor query(Uri uri, String[] projection, String selection, String[] args, String sortOrder) {
    try {
      File file = resolve(uri);
      MatrixCursor cursor = new MatrixCursor(new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE});
      cursor.addRow(new Object[]{file.getName(), file.length()});
      return cursor;
    } catch (FileNotFoundException e) { return null; }
  }

  @Override public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
  @Override public int update(Uri uri, ContentValues values, String selection, String[] args) { throw new UnsupportedOperationException(); }
  @Override public int delete(Uri uri, String selection, String[] args) { throw new UnsupportedOperationException(); }
}
