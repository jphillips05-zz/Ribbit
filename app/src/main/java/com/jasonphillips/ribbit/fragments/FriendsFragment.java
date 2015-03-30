package com.jasonphillips.ribbit.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.jasonphillips.ribbit.Constants;
import com.jasonphillips.ribbit.R;
import com.jasonphillips.ribbit.adapters.UserAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by jasonphillips on 3/23/15.
 */
public class FriendsFragment extends Fragment {

    private static String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelations;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.user_grid);

        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelations = mCurrentUser.getRelation(Constants.PARSE_KEY_FRIENDS_RELATION);

        getActivity().setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(Constants.PARSE_KEY_USERNAME);
        query.setLimit(1000);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    //success
                    mFriends = parseUsers;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    if(mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(
                                getActivity(),
                                mFriends
                        );
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter)mGridView.getAdapter()).Refill(mFriends);
                    }



                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(getString(R.string.btn_ok), null);

                    builder.create().show();
                }
            }
        });
    }
}
