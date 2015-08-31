package com.example.tina.awtter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tina on 8/31/15.
 */
public class LVAdapter extends BaseAdapter {

    Context context;
    ArrayList<HashMap<String, String>> comments;
    private static LayoutInflater inflater = null;
    private static final String TAG_NAME = "__name";
    private static final String TAG_COMMENT = "__comment";

    public LVAdapter(Context context, ArrayList<HashMap<String, String>> comments) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.comments = comments;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.comment_item, null);
        TextView userText = (TextView) vi.findViewById(R.id.user_comment);
        TextView commentText = (TextView) vi.findViewById(R.id.comment_text);
        userText.setText(comments.get(position).get(TAG_NAME)); //name
        commentText.setText(comments.get(position).get(TAG_COMMENT)); //comment
        //text.setText(comments.get(position).get(2)); //createdat

        return vi;
    }
}