package com.xy.psn.services;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.xy.psn.activities.PSNApplication;
import com.xy.psn.constants.KeyData;
import com.xy.psn.managers.NotificationManager;
import com.xy.utils.Logger;

import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Xavier Yin on 9/29/16.
 */

public class PSNMessagingService extends FirebaseMessagingService {
    private Logger LOGGER = Logger.getInstance(this.getClass());

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            LOGGER.d(remoteMessage.getData().toString());

            Map<String, String> map = remoteMessage.getData();
            final String nickName = map.get(KeyData.PRODUCT_NAME);
            final String message = map.get(KeyData.MESSAGE);
            final String userPhoto = map.get(KeyData.PRODUCT_PHOTO);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(PSNApplication.getAPPLICATION())
                            .load(userPhoto)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    LOGGER.d("FCM Received, and now Loaded Image!");
                                    NotificationManager.getInstance().generateNotification(PSNApplication.getAPPLICATION(), resource, nickName, message);
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    super.onLoadFailed(e, errorDrawable);
                                    NotificationManager.getInstance().generateNotification(PSNApplication.getAPPLICATION(), nickName, message);
                                }
                            });
                }
            });
        }
    }
}
