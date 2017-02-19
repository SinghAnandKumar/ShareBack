package com.fantasticfive.shareback.concept2.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.bean.Session;

import java.util.ArrayList;

/**
 * Created by sagar on 19/2/17.
 */
public class RunningSessionsAdapter extends BaseAdapter {

    Context context;
    ArrayList<Session> sessions;
    public RunningSessionsAdapter(Context context, ArrayList<Session> sessions){
        this.context = context;
        this.sessions = sessions;
    }

    @Override
    public int getCount() {
        return sessions.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.c2_inner_running_sessions, null);
            holder.sessionName = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        Session session = sessions.get(position);
        holder.sessionName.setText(session.getSessionName());

        return view;
    }

    class ViewHolder{
        TextView sessionName;
    }
}
