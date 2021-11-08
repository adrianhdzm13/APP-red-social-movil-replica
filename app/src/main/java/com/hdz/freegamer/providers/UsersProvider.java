package com.hdz.freegamer.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hdz.freegamer.models.User;

import java.util.HashMap;
import java.util.Map;

public class UsersProvider {

    private CollectionReference mCollectio;

    public UsersProvider(){
        mCollectio = FirebaseFirestore.getInstance().collection( "Users");
    }

    public Task<DocumentSnapshot> getUser(String id){
        return mCollectio.document(id).get();
    }

    public Task<Void> create(User user){
        return mCollectio.document(user.getId()).set(user);
    }

    public Task<Void> update(User user){

        Map<String, Object > map = new HashMap<>();
        map.put( "username", user.getUsername());
        return mCollectio.document(user.getId()).update(map);
    }

}
