package com.group16.example.edures;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

    public class Notesadapter extends ArrayAdapter<Notes> {
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
            Notes currentnumber=getItem(position);
            TextView englishtextview= (TextView) listitemview.findViewById(R.id.Title);
            englishtextview.setText(currentnumber.getTitle());

            return listitemview;

        }
    }
