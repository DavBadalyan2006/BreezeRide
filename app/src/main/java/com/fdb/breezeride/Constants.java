package com.fdb.breezeride;

import java.util.HashMap;

public class Constants {

    public static final int MIN_PORT = 1000;
    public static final int MAX_PORT = 10000;
    public static final int IMAGE_WIDTH = 1920;
    public static final String KEY_URI = "uri";
    public static final int IMAGE_HEIGHT = 1080;
    public static final String KEY_USER = "user";
    public static final String KEY_PORT = "port";
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final String KEY_COUNT = "count";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_VERIFIED = "verify";
    public static final String KEY_IS_ONLINE = "online";
    public static final String REMOTE_MSG_DATA = "data";

    public static final String PUBLIC_KEY = "publicKey";
    public static final String KEY_IS_IMAGE = "isImage";
    public static final String KEY_PASSWORD = "password";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER_NAME = "username";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_IP_ADDRESS = "ip_address";
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static HashMap<String, String> remoteMsgHeaders = null;
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_NETWORK_ACCESS = "network_access";
    public static final String URL = "https://fcm.googleapis.com/fcm/";
    public static final String KEY_SENDER_USER_NAME = "senderUserName";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String IP_URL = "https://checkip.amazonaws.com/";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String KEY_RECEIVER_USER_NAME = "receiverUserName";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> getRemoteMsgHeaders() {

        if (remoteMsgHeaders == null) {

            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(

                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAGpLRof4:APA91bFe5qR8EhNlOzamOr04zuEht3_ILQl9e4lwRDh4Dgi3jUScc5i_5zmx12yhmJG5_bP_zIxTfLHMeZ5xJfWC_ByQCKHtSH1Cr5ixBFg-Wo7GtfBFPpRX5_U-9hDp8C8ypXfOAL07"

            );

            remoteMsgHeaders.put(

                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"

            );

        }

        return remoteMsgHeaders;

    }

}