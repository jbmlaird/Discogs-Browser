package bj.rxjavaexperimentation.artistreleases.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxrelay2.BehaviorRelay;

import java.util.List;

import javax.inject.Inject;

import bj.rxjavaexperimentation.R;
import bj.rxjavaexperimentation.artistreleases.ArtistReleasesActivity;
import bj.rxjavaexperimentation.model.artistrelease.ArtistRelease;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by Josh Laird on 11/04/2017.
 */

public class ArtistReleasesFragment extends Fragment
{
    private static final String TAG = "MixesFragment";
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @Inject BehaviorRelay<List<ArtistRelease>> behaviorRelay;
    @Inject ArtistResultFunction artistResultFunction;
    private RecyclerViewRemixesAdapter rvRemixesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        ArtistReleasesActivity.component.inject(this);
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ButterKnife.bind(this, view);
        behaviorRelay
                .map(artistResultFunction.map(getArguments().getString("map")))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getConsumer());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvRemixesAdapter = new RecyclerViewRemixesAdapter(getActivity());
        recyclerView.setAdapter(rvRemixesAdapter);
        return view;
    }

    private Consumer<List<ArtistRelease>> getConsumer()
    {
        return o ->
        {
            rvRemixesAdapter.setRemixes(o);
            rvRemixesAdapter.notifyDataSetChanged();
        };
    }
}
