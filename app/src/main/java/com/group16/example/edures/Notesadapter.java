package com.group16.example.edures;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;

public class Notesadapter extends ArrayAdapter<Notes> {
    private Context context;
    public Notesadapter(Context context){
        this.context=context;
    }
    //    private StorageReference mStorageRef;
//        DownloadManager dm;
//        long queueid;
        public  Notesadapter(Activity context, ArrayList<Notes> notes)
        {
            super(context,0,notes);
        }

        @Override
        public View getView(int position, View convertview, ViewGroup parent)
        {
            View listitemview=convertview;
            if(listitemview == null) {
                listitemview = LayoutInflater.from(getContext()).inflate(R.layout.simple_view, parent, false);
            }
            final Notes currentnumber=getItem(position);
            TextView englishtextview= (TextView) listitemview.findViewById(R.id.title);
            TextView uploader = (TextView)listitemview.findViewById(R.id.uploader);
            RelativeLayout relativeLayout = listitemview.findViewById(R.id.download);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(currentnumber.getDownload_url()));
                    getContext().startActivity(intent);
                    /*dm = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://edures-30879.appspot.com/"
                            +currentnumber.getDownload_url()));
                    queueid = dm.enqueue(request);*/
                   /* StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("/Notes1");
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });*/
                }
            });

            englishtextview.setText(currentnumber.getCourse());
            uploader.setText(currentnumber.getEmail());
            return listitemview;
        }
    }
