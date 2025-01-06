/*
Copyright 2011-2013 Pieter Pareit
Copyright 2009 David Revell

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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.ArraySet;
import android.util.Log;

import com.lollipop.swiftp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import be.ppareit.swiftp.server.FtpUser;
import be.ppareit.swiftp.utils.FileUtil;

public class FsSettings {

    private final static String TAG = FsSettings.class.getSimpleName();
    private static final SharedPreferences sp = getSharedPreferences();

    private static List<FtpUser> parseUsers(String usersStr) {
        if (usersStr == null || usersStr.isEmpty()) return Collections.emptyList();
        try {
            JSONArray jsonArray = new JSONArray(usersStr);
            ArrayList<FtpUser> users = new ArrayList<>();
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String username = jsonObject.getString("username");
                String password = jsonObject.getString("password");
                String chroot = jsonObject.getString("chroot");
                String uriString = jsonObject.getString("uriString");
                users.add(new FtpUser(username, password, chroot, uriString));
            }
            return users;
        } catch (Throwable e) {
            Log.e(TAG, "parseUsers.ERROR", e);
        }
        return Collections.emptyList();
    }

    private static String toJson(List<FtpUser> users) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (FtpUser user : users) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", user.getUsername());
                jsonObject.put("password", user.getPassword());
                jsonObject.put("chroot", user.getChroot());
                jsonObject.put("uriString", user.getUriString());
                jsonArray.put(jsonObject);
            }
            return jsonArray.toString();
        } catch (Throwable e) {
            Log.e(TAG, "toJson.ERROR", e);
        }
        return "[]";
    }

    public static List<FtpUser> getUsers() {
        final Context context = App.getAppContext();
        if (sp.contains("users")) {
            String usersStr = sp.getString("users", null);
            return parseUsers(usersStr);
        } else if (sp.contains("username")) {
            // on ftp server version < 2.19 we had username/password preference
            String username = sp.getString("username", context.getString(R.string.username_default));
            String password = sp.getString("password", context.getString(R.string.password_default));
            String chroot = sp.getString("chrootDir", "");
            String uriString = sp.getString("uriString", "");
            return new ArrayList<>(Collections.singletonList(new FtpUser(username, password, chroot, uriString)));
        } else {
            FtpUser defaultUser = new FtpUser(context.getString(R.string.username_default), context.getString(R.string.password_default), "\\", "");
            return new ArrayList<>(Collections.singletonList(defaultUser));
        }
    }

    public static FtpUser getUser(String username) {
        for (FtpUser user : getUsers()) {
            if (user.getUsername().equals(username))
                return user;
        }
        return null;
    }

    public static void addUser(FtpUser user) {
        if (getUser(user.getUsername()) != null) {
            throw new IllegalArgumentException("User already exists");
        }
        List<FtpUser> userList = getUsers();
        userList.add(user);
        sp.edit().putString("users", toJson(userList)).apply();
    }

    public static void removeUser(String username, boolean actualDelete) {
        List<FtpUser> users = getUsers();
        ArrayList<FtpUser> found = new ArrayList<>();
        for (FtpUser user : users) {
            if (user.getUsername().equals(username)) {
                found.add(user);
            }
        }
        users.removeAll(found);
        if (actualDelete /*Don't do this on modify.*/) {
            for (FtpUser user : found) {
                removeUserUriPerm(user);
            }
        }
        sp.edit().putString("users", toJson(users)).apply();
    }

    private static void removeUserUriPerm(FtpUser user) {
        final String userUriString = user.getUriString();
        if (userUriString == null || userUriString.isEmpty()) return;
        List<UriPermission> list = App.getAppContext().getContentResolver().getPersistedUriPermissions();
        for (UriPermission uriToRemove : list) {
            if (uriToRemove == null) continue;
            final String s = uriToRemove.getUri().getPath();
            if (s == null) continue;
            if (s.equals(userUriString)) {
                App.getAppContext().getContentResolver().releasePersistableUriPermission(uriToRemove.getUri(),
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                break;
            }
        }
    }

    public static void modifyUser(String username, FtpUser newUser) {
        removeUser(username, false);
        addUser(newUser);
    }

    public static boolean allowAnonymous() {
        return sp.getBoolean("allow_anonymous", false);
    }

    public static File getDefaultChrootDir() {
        // Get the path from the app's MANAGE USERS chroot folder UI text field that the user will use during setup.
        String subFix = null;
        if (Util.useScopedStorage()) {
            // The app's MANAGE USERS chroot folder UI selection cannot select the sd card at least on Android 11+.
            //  The picker does all that's needed there so that use should be switched with ADVANCED SETTINGS >
            //  WRITE EXTERNAL picker or just make it invisible when on A11+ ? Could also just pull open the same
            //  picker on both with A11+ or something else. It also presents possible conflicts with the Uri path
            //  eg "/storage/sd card/" verses "/sd card/Test/".
            String s = FileUtil.cleanupUriStoragePath(FileUtil.getTreeUri());
            if (s != null && !s.contains("primary:")) {
                final String chroot = FileUtil.getSdCardBaseFolderScopedStorage();
                // Need to return eg "/storage" for sd card and "/storage/emulated/0" for internal.
                if (chroot != null && !chroot.isEmpty()) return new File(chroot);
                // otherwise just get the other path from below.
            } else if (s != null && s.contains("primary:")) {
                // Fix for issue seen on Android 8.0:
                // Had to implement over below as the below chroot is forced to
                // getExternalStorageDirectory() when actual chroot may include further sub dirs.
                subFix = s.replace("primary:", "");
                // At the moment, have to do it below, as a StackOverflow is happening on the test device
                // here with any additional code for an unknown reason.
            }
        }

        // Original below incorrectly returns "/storage/emulated/0" for sd card with Android 11+
        File chrootDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            chrootDir = Environment.getExternalStorageDirectory();
        } else {
            chrootDir = new File("/");
        }
        if (!chrootDir.isDirectory()) {
            Log.e(TAG, "getChrootDir: not a directory");
            // if this happens, we are screwed
            // we give it the application directory
            // but this will probably not be what the user wants
            return App.getAppContext().getFilesDir();
        }
        if (subFix != null) return new File(chrootDir, subFix);
        return chrootDir;
    }

    public static int getPortNumber() {
        // TODO: port is always an number, so store this accordingly
        String portString = sp.getString("portNum", "2121");
        int port = Integer.valueOf(portString);
        Log.v(TAG, "Using port: " + port);
        return port;
    }

    public static int getPortNumberImplicit() {
        // TODO: port is always an number, so store this accordingly
        String portString = sp.getString("portNumImplicit",
                App.getAppContext().getString(R.string.portnumber_default_implicit));
        int port = Integer.parseInt(portString);
        Log.v(TAG, "Using port implicit: " + port);
        return port;
    }

    public static String getBatterySaverChoice(String val) {
        String s = sp.getString("battery_saver", "1");
        if (val != null) s = val;
        if (s.equals("0")) return App.getAppContext().getString(R.string.bs_high);
        if (s.equals("1")) return App.getAppContext().getString(R.string.bs_low);
        return App.getAppContext().getString(R.string.bs_deep);
    }

    public static int getAnonMaxConNumber() {
        String s = sp.getString("anon_max", "1");
        int i = Integer.parseInt(s);
        Log.v(TAG, "Using anon max connections: " + i);
        return i;
    }

    public static boolean shouldTakeFullWakeLock() {
        return sp.getBoolean("stayAwake", false);
    }

    public static int getTheme() {
        String theme = sp.getString("theme", "0");
        if (theme == null) {
            return R.style.AppThemeDark;
        }

        switch (theme) {
            case "0":
                return R.style.AppThemeDark;
            case "1":
                return R.style.AppThemeLight;
            case "2":
                return R.style.AppThemeLight_DarkActionBar;
            default:
                return R.style.AppThemeDark;
        }

    }

    public static boolean showNotificationIcon() {
        return sp.getBoolean("show_notification_icon_preference", true);
    }

    /**
     * @return the SharedPreferences for this application
     */
    private static SharedPreferences getSharedPreferences() {
        final Context context = App.getAppContext();
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getExternalStorageUri() {
        return sp.getString("externalStorageUri", null);
    }

    public static void setExternalStorageUri(String externalStorageUri) {
        sp.edit().putString("externalStorageUri", externalStorageUri).apply();
    }

    /*
     * Returns whether the FTPS implicit port is enabled or not.
     * */
    public static boolean isImplicitUsed() {
        return sp.getBoolean("enableImplicitPort", false);
    }

    /*
     * Returns whether the plain port is disabled for FTPS implicit only use.
     * */
    public static boolean isImplicitOnly() {
        return sp.getBoolean("disablePlainPort", false);
    }

    /*
     * Returns the FTPS implicit port.
     * */
    public static int getImplicitPort() {
        return Integer.parseInt(getImplicitPortString());
    }

    /*
     * Returns the FTPS implicit port.
     * */
    public static String getImplicitPortString() {
        String implicitPortDefault = App.getAppContext().getString(R.string.portnumber_default_implicit);
        return sp.getString("portNumImplicit", implicitPortDefault);
    }

    /*
     * Returns whether the client early 150 workaround is enabled or not.
     * */
    public static boolean isEarly150Enabled() {
        return sp.getBoolean("early150Response", false);
    }

    /*
     * Returns whether the FTPS implicit port is enabled or not.
     * */
    public static boolean isFeatDisabled() {
        return sp.getBoolean("disableFeat", false);
    }

    /*
     * Returns whether to require a client certificate.
     * */
    public static boolean useClientCert() {
        return sp.getBoolean("useClientCert", false);
    }

    /*
     * Returns the data connection range low port;
     * */
    public static int getPortRangeLow() {
        return Integer.parseInt(getPortRangeLowString());
    }

    /*
     * Returns the data connection range low port as a String;
     * */
    public static String getPortRangeLowString() {
        final String def = App.getAppContext().getString(R.string.portnumber_default_pasv_low);
        return sp.getString("portRangePasvLow", def);
    }

    /*
     * Returns the data connection range high port;
     * */
    public static int getPortRangeHigh() {
        return Integer.parseInt(getPortRangeHighString());
    }

    /*
     * Returns the data connection range high port as a String;
     * */
    public static String getPortRangeHighString() {
        final String def = App.getAppContext().getString(R.string.portnumber_default_pasv_high);
        return sp.getString("portRangePasvHigh", def);
    }

    /*
     * Returns whether FTPS SSL is enabled or not.
     * */
    public static boolean useSSL() {
        return sp.getBoolean("enableSsl", false);
    }

    /*
     * Returns whether SYST is disabled.
     * */
    public static boolean isSystDisabled() {
        return sp.getBoolean("disableSyst", false);
    }

    /*
     * Returns whether the banner text is disabled.
     * */
    public static boolean isBannerDisabled() {
        return sp.getBoolean("disableBanner", false);
    }

    /*
     * Returns whether user enabled encryption only (FTPS implicit and explicit only).
     * */
    public static boolean isEncryptionOnlyEnabled() {
        return sp.getBoolean("disablePlainNotExplicit", false);
    }

    /*
     * Returns the list of all IPs that have connected.
     * */
    public static Set<String> getIPList() {
        return sp.getStringSet("IPList", new ArraySet<>());
    }

    /*
     * Returns the list of IPs that have been allowed.
     * */
    public static Set<String> getAllowList() {
        return sp.getStringSet("AllowIPs", new ArraySet<>());
    }

    /*
     * Returns the list of IPs that are denied by failure.
     * */
    public static Set<String> getFailList() {
        return sp.getStringSet("FailIPs", new ArraySet<>());
    }

    /*
     * Puts reworked set of IPs to the all connected IPs list.
     * */
    public static void putIPList(Set<String> newList) {
        sp.edit().putStringSet("IPList", newList).apply();
    }

    /*
     * Puts reworked set of IPs to the denied by failure list.
     * */
    public static void putFailList(Set<String> newList) {
        sp.edit().putStringSet("FailIPs", newList).apply();
    }

    /*
     * Returns whether IP list deny until allowed is enabled.
     * */
    public static boolean isDenyUntilAllowed() {
        return sp.getBoolean("denyUntilAllowed", false);
    }

    /*
     * Returns whether IP list deny on failure is enabled.
     * */
    public static boolean isDenyOnFailedLogins() {
        return sp.getBoolean("denyOnFailedLogins", false);
    }

    /*
     * Returns the FTPS protocols selected by the user.
     * */
    public static Set<String> getAllowedProtocols() {
        return sp.getStringSet("FTPSProtocolList", new ArraySet<>());
    }

    public static boolean isLoggingEnabled() {
        final SharedPreferences sp = getSharedPreferences();
        return sp.getBoolean("enable_logging", false);
    }

}
