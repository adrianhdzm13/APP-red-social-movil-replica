package com.hdz.freegamer.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hdz.freegamer.models.Post;

public class PostProvider {

    CollectionReference mCollection;

    //constructor
    public PostProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Posts");
    }


    //metodo para crear un documento en BD coleccion Post
    public Task<Void> save(Post post) {
        return mCollection.document().set(post);
    }

    public Query getAll() {
        return mCollection.orderBy("title", Query.Direction.DESCENDING);
    }

}
