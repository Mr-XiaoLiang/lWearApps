/*
Copyright 2011-2013 Pieter Pareit

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/
package be.ppareit.swiftp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class App {

    private static Application mInstance;

    public static void bind(Application app) {
        App.mInstance = app;
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public static void onCreate(Application app) {
        bind(app);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FsService.ACTION_STARTED);
        intentFilter.addAction(FsService.ACTION_STOPPED);
        intentFilter.addAction(FsService.ACTION_FAILEDTOSTART);

        if (Build.VERSION.SDK_INT >= 33) {
            app.registerReceiver(new NsdService.ServerActionsReceiver(), intentFilter, FsService.RECEIVER_EXPORTED);
        } else {
            app.registerReceiver(new NsdService.ServerActionsReceiver(), intentFilter);
        }
    }

    /**
     * @return the Context of this application
     */
    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

    /**
     * Get the version from the manifest.
     *
     * @return The version as a String.
     */
    public static String getVersion() {
        Context context = getAppContext();
        String packageName = context.getPackageName();
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getPackageInfo(packageName, 0).versionName;
        } catch (Throwable e) {
            Log.e("Swiftp", "getVersion.ERROR", e);
            return null;
        }
    }

}
