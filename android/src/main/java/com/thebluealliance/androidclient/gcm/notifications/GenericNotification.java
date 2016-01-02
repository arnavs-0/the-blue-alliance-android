package com.thebluealliance.androidclient.gcm.notifications;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.RecentNotificationsActivity;
import com.thebluealliance.androidclient.helpers.JSONHelper;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;

public class GenericNotification extends BaseNotification {

    public static final String TITLE = "title";
    public static final String TEXT = "desc";
    public static final String URL = "url";
    public static final String APP_VERSION = "app_version";

    protected String title, message;
    protected PendingIntent intent;
    protected Context context;

    public GenericNotification(Context c, String type, String messageData) {
        super(type, messageData);
        context = c;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public PendingIntent getContentIntent() {
        return intent;
    }

    @Override
    public void parseMessageData() throws JsonParseException {
        JsonObject jsonData = JSONHelper.getasJsonObject(messageData);
        if (!jsonData.has(TITLE)) {
            throw new JsonParseException("Notification data does not contain 'title'");
        }
        title = jsonData.get(TITLE).getAsString();
        if (!jsonData.has(TEXT)) {
            throw new JsonParseException("Notification data does not contain 'desc'");
        }
        message = jsonData.get(TEXT).getAsString();

        if (jsonData.has(URL)) {
            Uri uri = Uri.parse(jsonData.get(URL).getAsString());
            Intent launch = Utilities.getIntentForTBAUrl(context, uri);
            launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent = PendingIntent.getActivity(context, 0, launch, 0);
        } else {
            Intent launch = RecentNotificationsActivity.newInstance(context);
            intent = PendingIntent.getActivity(context, getNotificationId(), launch, 0);
        }

        if (jsonData.has(APP_VERSION)) {
            int targetVersion = jsonData.get(APP_VERSION).getAsInt();
            int currentVersion = BuildConfig.VERSION_CODE;
            if (currentVersion < targetVersion) {
                // The broadcast is not targeted at this version, don't show it
                Log.d(Constants.LOG_TAG, "Not displaying received broadcast target at version " +
                        targetVersion + " (this is version " + currentVersion + ")");
                display = false;
            }
        }
    }

    @Override
    public Notification buildNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.tba_blue_background)));

        if (intent != null) {
            builder.setContentIntent(intent);
        }

        return builder.build();
    }

    @Override
    public Intent getIntent(Context c) {
        /* Don't open anything */
        return null;
    }

    @Override
    public void updateDataLocally() {
        /* No data to be stored locally */
    }

    @Override
    public int getNotificationId() {
        return (new Date().getTime() + ":" + getNotificationType() + ":" + messageData).hashCode();
    }
}
