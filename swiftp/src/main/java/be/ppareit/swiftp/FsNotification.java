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

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

public class FsNotification {

    public static final int NOTIFICATION_ID = 7890;
    public static NotificationBuilder notificationBuilder;

    private static final String TAG = "FsNotification";

    public static Notification setupNotification(Context context) {
        Log.d(TAG, "Setting up the notification");
        // Get NotificationManager reference
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (notificationBuilder != null) {
            return notificationBuilder.buildNotification(context);
        }
        return null;
    }

    public interface NotificationBuilder {
        Notification buildNotification(Context context);
    }

}
