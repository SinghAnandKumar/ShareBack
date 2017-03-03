package com.fantasticfive.shareback.concept2.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.bean.User;

import java.util.ArrayList;

/**
 * Created by sagar on 17/2/17.
 */
public class JoinedUsersAdapter extends BaseAdapter {

    private static final String TAG = "MY TAG";
    Context context;
    ArrayList<User> userList;
    public JoinedUsersAdapter(Context context, ArrayList<User> userList){
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
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
            view = inflater.inflate(R.layout.c2_inner_joined_user, null);
            holder.name = (TextView) view.findViewById(R.id.session_name);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        User user = userList.get(position);
        holder.name.setText(user.getName());
        return view;
    }

    class ViewHolder{
        TextView name;
    }
}
