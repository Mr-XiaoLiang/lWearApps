package be.ppareit.swiftp;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.util.Timer;

/**
 * This media scanner runs in the background. The rescan might
 * not happen immediately.
 */
public enum MediaUpdater {
    INSTANCE;

    private final static String TAG = MediaUpdater.class.getSimpleName();

    // the system broadcast to remount the media is only done after a little while (5s)
    private static Timer sTimer = new Timer();

    private static class ScanCompletedListener implements
            MediaScannerConnection.OnScanCompletedListener {
        @Override
        public void onScanCompleted(String path, Uri uri) {
            Log.i(TAG, "Scan completed: " + path + " : " + uri);
        }
    }

    public static void notifyFileCreated(String path) {
        Log.d(TAG, "Notifying others about new file: " + path);
        Context context = App.getAppContext();
        MediaScannerConnection.scanFile(context, new String[]{path}, null,
                new ScanCompletedListener());
    }

    public static void notifyFileDeleted(String path) {
        Log.d(TAG, "Notifying others about deleted file: " + path);
        // on newer devices, we hope that this works correctly:
        Context context = App.getAppContext();
        MediaScannerConnection.scanFile(context, new String[]{path}, null,
                new ScanCompletedListener());
    }
}
