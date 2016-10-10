package com.xy.psn.managers;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.xy.network.base.IApiResponseListener;
import com.xy.psn.BuildConfig;
import com.xy.psn.R;
import com.xy.psn.activities.PSNApplication;
import com.xy.psn.apis.PostFirebaseApiManager;
import com.xy.psn.beans.custom.DeviceData;
import com.xy.psn.beans.custom.UserData;
import com.xy.psn.beans.json.PostFirebaseJb;
import com.xy.psn.beans.param.PostFirebasePB;
import com.xy.utils.Logger;

/**
 * Created by Xavier Yin on 10/4/16.
 */

public class RequestManager {
    private Logger LOGGER = Logger.getInstance(RequestManager.class);

    private static RequestManager FIREBASE_D2D_MANAGER = null;
    private DatabaseReference databaseRefUsers;
    private PostFirebaseApiManager postFirebaseApiManager;

    private RequestManager() {
        this.databaseRefUsers = FirebaseDatabase.getInstance().getReference();
        this.postFirebaseApiManager = PostFirebaseApiManager.getMemberApiManager(PSNApplication.getAPPLICATION());
    }

    public synchronized static final RequestManager getInstance() {
        if (FIREBASE_D2D_MANAGER == null) {
            FIREBASE_D2D_MANAGER = new RequestManager();
        }

        return FIREBASE_D2D_MANAGER;
    }

    public void insertUserPushData(String id) {
        LOGGER.d("insert user data: " + id);
        this.insertUserData(id);
    }

    private void insertUserData(final String id) {
        final DeviceData device = new DeviceData(UserDataManager.getInstance().getPushToken());

        this.databaseRefUsers.child(UserData.DATABASE_USERS).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData userData = dataSnapshot.getValue(UserData.class);

                if (userData == null) {
                    userData = new UserData();
                }

                for (DeviceData pushDevice : userData.getDeviceList()) {
                    if (pushDevice.getToken().equals(device.getToken())) {
                        return;
                    }

                    if (pushDevice.getDevice() != null && pushDevice.getDevice().equals("android")) {
                        return;
                    }
                }

                userData.addDevice(device);
                databaseRefUsers.child(UserData.DATABASE_USERS).child(id).setValue(userData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LOGGER.d("error: " + databaseError.getDetails());
            }
        });
    }

    public void shareProduct(final String sendId, final String saleName, final String message) {
        this.databaseRefUsers.child(UserData.DATABASE_USERS).child(sendId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final UserData pushUser = dataSnapshot.getValue(UserData.class);

                if (pushUser == null || pushUser.getDeviceList() == null || pushUser.getDeviceList().size() == 0) {
                    return;
                }

                if (pushUser.getDeviceList() != null) {
                    LOGGER.d("push data: " + pushUser.getDeviceList().size());

                    for (DeviceData device : pushUser.getDeviceList()) {
                        LOGGER.d("device: " + device.getDevice());
                        LOGGER.d("token: " + device.getToken());

                        if (device.getDevice() == null) {
                            continue;
                        }

                        pushProduct(saleName, message, device.getToken());
                    }
                } else {
                    LOGGER.d("Push Failed.");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LOGGER.d("DatabaseError");
            }
        });
    }


    private void pushProduct(String saleName, String message, String sharingTargetRegisterId) {
        PostFirebaseJb postFirebaseJb = new PostFirebaseJb();
        postFirebaseJb.setProductName(saleName);
        postFirebaseJb.setMessage(message);
        postFirebaseJb.setProductPhoto("http://image.yipee.cc/index/2013/12/BenQ-G2F-產品圖_1-copy.jpg");
        postFirebaseJb.setTargetRegisterId(sharingTargetRegisterId);

        this.postFirebaseApiManager.launchPostFirebaseApi(
                new PostFirebasePB(PSNApplication.getRESOURCE().getString(R.string.psn_key)),
                postFirebaseJb.toJSONObject(),
                new IApiResponseListener<String>() {
                    @Override
                    public void preExecute() {
                        LOGGER.d("preExecute");
                    }

                    @Override
                    public void onApiSuccess(String response) {
                        LOGGER.d("response: " + response);
                    }

                    @Override
                    public void onApiError(String statusCode) {
                        LOGGER.d("onApiError");
                    }

                    @Override
                    public void postSuccessExecute() {
                        LOGGER.d("postSuccessExecute");
                    }

                    @Override
                    public void postErrorExecute() {
                        LOGGER.d("postErrorExecute");
                    }
                }
        );
    }
}
