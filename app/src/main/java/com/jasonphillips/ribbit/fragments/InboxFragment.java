package com.jasonphillips.ribbit.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.jasonphillips.ribbit.Constants;
import com.jasonphillips.ribbit.R;
import com.jasonphillips.ribbit.adapters.MessageAdapter;
import com.jasonphillips.ribbit.ui.ViewImageActivity;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jasonphillips on 3/23/15.
 */
public class InboxFragment extends ListFragment {

    private static final String TAG = InboxFragment.class.getSimpleName();
    private List<ParseObject> mMessages;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_LONG).show();
            getInbox();
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.focused_purple_button,
                R.color.light_purple_background,
                R.color.light_purple_button,
                R.color.pressed_purple_button);

        return rootView;
    }

    private FindCallback<ParseObject> mFindCallback = new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> parseObjects, ParseException e) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            if(mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);

            }
            if (e == null) {
                mMessages = parseObjects;
                String[] usernames = new String[mMessages.size()];
                int i = 0;
                for (ParseObject message : mMessages) {
                    usernames[i] = message.getString(Constants.PARSE_KEY_MESSAGE_SENDER_NAME);
                    i++;
                }

                if (getListView().getAdapter() == null) {
                    MessageAdapter adapter = new MessageAdapter(
                            getListView().getContext(),
                            mMessages
                    );

                    setListAdapter(adapter);
                } else {
                    ((MessageAdapter) getListView().getAdapter()).Refill(mMessages);
                }

            } else {

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setProgressBarIndeterminateVisibility(true);
        getInbox();

    }

    DeleteCallback mDeleteCallback = new DeleteCallback() {
        @Override
        public void done(ParseException e) {
            if (e != null) {
                Log.e(TAG, e.getMessage());
            }
        }
    };

    SaveCallback mSaveCallback = new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e != null) {
                Log.e(TAG, e.getMessage());
            }
        }
    };

    private void getInbox() {
        ParseQuery<ParseObject> query = new ParseQuery(Constants.PARSE_CLASS_MESSAGES);
        query.whereEqualTo(Constants.PARSE_KEY_MESSAGE_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder("createdAt");
        query.findInBackground(mFindCallback);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(Constants.PARSE_KEY_MESSAGE_FILE_TYPE);
        ParseFile file = message.getParseFile(Constants.PARSE_KEY_MESSAGE_FILE);
        Uri uri = Uri.parse(file.getUrl());

        if (messageType.equals(Constants.TYPE_IMAGE)) {
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(uri);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);
        }

        List<String> ids = message.getList(Constants.PARSE_KEY_MESSAGE_RECIPIENT_IDS);
        if (ids.size() == 1) {
            message.deleteInBackground(mDeleteCallback);
        } else {
            ids.remove(ParseUser.getCurrentUser().getObjectId());
            ArrayList<String> toRemove = new ArrayList<String>();

            toRemove.add(ParseUser.getCurrentUser().getObjectId());
            message.removeAll(Constants.PARSE_KEY_MESSAGE_RECIPIENT_IDS, toRemove);
            message.saveInBackground(mSaveCallback);
            message.deleteInBackground(mDeleteCallback);
        }
    }
}
