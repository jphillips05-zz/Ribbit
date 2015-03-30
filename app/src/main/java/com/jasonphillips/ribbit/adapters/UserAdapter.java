package com.jasonphillips.ribbit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasonphillips.ribbit.R;
import com.jasonphillips.ribbit.utils.MD5Util;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jasonphillips on 3/25/15.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

    private Context mContext;
    private List<ParseUser> mUsers;


    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.user_item, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);

            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.user_image_view);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.name_label);
            holder.checkImageView = (ImageView) convertView.findViewById(R.id.friend_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();

        if (email.equals("")) {
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        } else {
            String hash = MD5Util.md5Hex(email);
            String gavUrl = String.format("http://www.gravatar.com/avatar/%s?s=204&d=404", hash);

            Picasso.with(mContext)
                    .load(gavUrl)
                    .placeholder(R.drawable.avatar_empty)
                    .into(holder.userImageView);

        }

        holder.nameLabel.setText(user.getUsername());

        GridView gridView = (GridView)parent;
        if(gridView.isItemChecked(position)) {
            holder.checkImageView.setVisibility(View.VISIBLE);
        } else {
            holder.checkImageView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView userImageView;
        ImageView checkImageView;
        TextView nameLabel;
    }

    public void Refill(List<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}
