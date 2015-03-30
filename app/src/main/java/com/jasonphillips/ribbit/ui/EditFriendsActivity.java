package com.jasonphillips.ribbit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasonphillips.ribbit.Constants;
import com.jasonphillips.ribbit.R;
import com.jasonphillips.ribbit.adapters.UserAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends Activity {

    public static String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    private GridView mGridView;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);

        mGridView = (GridView) findViewById(R.id.user_grid);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClientListener);

        TextView emptyView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyView);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(Constants.PARSE_KEY_FRIENDS_RELATION);

        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(Constants.PARSE_KEY_USERNAME);
        query.setLimit(1000);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    mUsers = parseUsers;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }


                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(
                                EditFriendsActivity.this,
                                mUsers
                        );
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).Refill(mUsers);
                    }
                    addFriendCheckMarks();

                } else {
                    //error
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(getString(R.string.btn_ok), null);

                    builder.create().show();
                }
            }
        });
    }

    private void addFriendCheckMarks() {
        mFriendsRelation.getQuery()
                .findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> parseUsers, ParseException e) {
                        if (e == null) {
                            //success
                            for (int i = 0; i < mUsers.size(); i++) {
                                ParseUser user = mUsers.get(i);
                                for (ParseUser friend : parseUsers) {
                                    if (friend.getObjectId().equals(user.getObjectId())) {
                                        mGridView.setItemChecked(i, true);
                                    }

                                }
                            }
                        } else {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
    }

    private SaveCallback currentUserSaveCallback = new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e != null) {
                Log.e(TAG, e.getMessage());
            }
        }
    };

    private AdapterView.OnItemClickListener mOnItemClientListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView checkImageView = (ImageView) mGridView.findViewById(R.id.friend_checkbox);

            if (mGridView.isItemChecked(position)) {
                //add
                mFriendsRelation.add(mUsers.get(position));
                checkImageView.setVisibility(View.VISIBLE);
            } else {
                //remove
                mFriendsRelation.remove(mUsers.get(position));
                checkImageView.setVisibility(View.INVISIBLE);
            }
            mCurrentUser.saveInBackground(currentUserSaveCallback);
        }
    };

}
