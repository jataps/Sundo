package com.example.appsundo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class CustomFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // Get the current user's UID
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Store the new FCM token under the user's UID in Firebase
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("fcmToken").setValue(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle incoming messages
        }

}
