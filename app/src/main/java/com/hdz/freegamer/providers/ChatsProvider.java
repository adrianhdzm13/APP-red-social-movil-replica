package com.hdz.freegamer.providers;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hdz.freegamer.models.Chat;


public class ChatsProvider {

    CollectionReference mCollection;

    public ChatsProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    public void create(Chat chat) {
        mCollection.document(chat.getIdUser1()).collection("Users").document(chat.getIdUser2()).set(chat);
        mCollection.document(chat.getIdUser2()).collection("Users").document(chat.getIdUser1()).set(chat);
    }

}
