package bj.rxjavaexperimentation.artist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import javax.inject.Inject;
import javax.inject.Singleton;

import bj.rxjavaexperimentation.model.artist.ArtistResult;
import bj.rxjavaexperimentation.network.DiscogsInteractor;
import bj.rxjavaexperimentation.schedulerprovider.MySchedulerProvider;
import bj.rxjavaexperimentation.wrappers.LogWrapper;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;

/**
 * Created by Josh Laird on 07/04/2017.
 */
@Singleton
public class ArtistPresenter implements ArtistContract.Presenter
{
    private final String TAG = getClass().getSimpleName();
    private ArtistContract.View view;
    private DiscogsInteractor discogsInteractor;
    private MySchedulerProvider mySchedulerProvider;
    private CompositeDisposable compositeDisposable;
    private LogWrapper log;
    private ArtistController artistController;
    private Function<ArtistResult, ArtistResult> removeUnwantedLinksFunction;

    @Inject
    public ArtistPresenter(@NonNull ArtistContract.View view, @NonNull DiscogsInteractor discogsInteractor,
                           @NonNull MySchedulerProvider mySchedulerProvider, @NonNull CompositeDisposable compositeDisposable,
                           @NonNull LogWrapper log, @NonNull ArtistController artistController, @NonNull Function<ArtistResult, ArtistResult> removeUnwantedLinksFunction)
    {
        this.view = view;
        this.discogsInteractor = discogsInteractor;
        this.mySchedulerProvider = mySchedulerProvider;
        this.compositeDisposable = compositeDisposable;
        this.log = log;
        this.artistController = artistController;
        this.removeUnwantedLinksFunction = removeUnwantedLinksFunction;
    }

    @Override
    public void getData(String id)
    {
        compositeDisposable.add(discogsInteractor.fetchArtistDetails(id)
                .subscribeOn(mySchedulerProvider.io())
                .observeOn(mySchedulerProvider.io())
                .map(removeUnwantedLinksFunction)
                .observeOn(mySchedulerProvider.ui())
                .subscribe(artist ->
                {
                    artistController.setArtist(artist);
                    log.e(TAG, artist.getProfile());
                }, error ->
                {
                    log.e(TAG, "onFetchArtistDetailsError");
                    error.printStackTrace();
                }));
    }

    @Override
    public void setupRecyclerView(Context context, RecyclerView rvDetailed, String title)
    {
        rvDetailed.setLayoutManager(new LinearLayoutManager(context));
        rvDetailed.setAdapter(artistController.getAdapter());
        artistController.setTitle(title);
        artistController.requestModelBuild();
    }

    @Override
    public void unsubscribe()
    {
        compositeDisposable.dispose();
    }
}
