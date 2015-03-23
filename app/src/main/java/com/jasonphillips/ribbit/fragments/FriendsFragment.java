package com.jasonphillips.ribbit.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jasonphillips.ribbit.R;

/**
 * Created by jasonphillips on 3/23/15.
 */
public class FriendsFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        return rootView;
    }

}
