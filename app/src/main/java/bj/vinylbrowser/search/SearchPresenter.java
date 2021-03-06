package bj.vinylbrowser.search;

import com.jakewharton.rxbinding2.support.design.widget.TabLayoutSelectionEvent;
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent;

import java.util.ArrayList;
import java.util.List;

import bj.vinylbrowser.greendao.DaoManager;
import bj.vinylbrowser.greendao.SearchTerm;
import bj.vinylbrowser.model.search.SearchResult;
import bj.vinylbrowser.utils.schedulerprovider.MySchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Josh Laird on 20/02/2017.
 */
public class SearchPresenter implements SearchContract.Presenter
{
    private MySchedulerProvider mySchedulerProvider;
    private List<SearchResult> searchResults = new ArrayList<>();
    private String currentFilter = "all";
    private SearchContract.View mView;
    private SearchEpxController searchController;
    private Function<SearchViewQueryTextEvent, ObservableSource<List<SearchResult>>> searchModelFunc;
    private DaoManager daoManager;
    private CompositeDisposable disposable;
    private DisposableObserver<List<SearchResult>> searchObserver;

    public SearchPresenter(SearchContract.View mView, SearchEpxController searchController, Function<SearchViewQueryTextEvent,
            ObservableSource<List<SearchResult>>> searchModelFunc, MySchedulerProvider mySchedulerProvider, DaoManager daoManager, CompositeDisposable disposable)
    {
        this.mView = mView;
        this.searchController = searchController;
        this.searchModelFunc = searchModelFunc;
        this.mySchedulerProvider = mySchedulerProvider;
        this.daoManager = daoManager;
        this.disposable = disposable;
    }

    /**
     * Subscribes to the SearchView Observable.
     */
    @Override
    public void setupSearchViewObserver()
    {
        disposable.add(getSearchIntent()
                .subscribeWith(getSearchObserver()));
    }

    /**
     * Subscribes to the TabLayout Observable.
     */
    @Override
    public void setupTabObserver()
    {
        disposable.add(mView.tabIntent()
                .subscribeOn(mySchedulerProvider.ui())
                .subscribeWith(getTabObserver()));
    }

    /**
     * Sets up an Observer on the SearchView.
     *
     * @return Observable to be subscribed to.
     */
    private Observable<List<SearchResult>> getSearchIntent()
    {
        return mView.searchIntent()
                .subscribeOn(mySchedulerProvider.io())
                .doOnNext(onNext ->
                {
                    searchController.setSearching(true);
                    daoManager.storeSearchTerm(onNext.queryText());
                })
                .switchMap(searchModelFunc)
                .doOnError(throwable ->
                {
                    if (throwable.getCause().getCause() != null && !throwable.getCause().getCause().getMessage().equals("thread interrupted"))
                        searchController.setError(true);
                    // Else ignore. The user has just searched again and interrupted the thread
                })
                // Resubscribe to the SearchView onError.
                .onErrorResumeNext(Observable.defer(this::getSearchIntent));
    }

    /**
     * Observer that subscribes to the SearchView.
     * <p>
     * This will filter the results if a filter query exists.
     *
     * @return Observer.
     */
    private DisposableObserver<List<SearchResult>> getSearchObserver()
    {
        if (searchObserver == null || searchObserver.isDisposed())
            searchObserver = new DisposableObserver<List<SearchResult>>()
            {
                @Override
                public void onNext(List<SearchResult> o)
                {
                    searchResults = o;
                    if (o.size() == 0)
                    {
                        // Show no results
                        searchController.setSearchResults(o);
                    }
                    else
                    {
                        if (!currentFilter.equals("all"))
                            Single.just(o)
                                    .subscribeOn(mySchedulerProvider.computation())
                                    .flattenAsObservable(results -> results)
                                    .filter(searchResult ->
                                            searchResult.getType().equals(currentFilter))
                                    .toList()
                                    .observeOn(mySchedulerProvider.ui())
                                    .subscribe(filteredResults ->
                                                    searchController.setSearchResults(filteredResults),
                                            Throwable::printStackTrace);
                        else
                            searchController.setSearchResults(o);
                    }
                }

                @Override
                public void onError(Throwable e)
                {
                    // Will never be reached as I have an onErrorResumeNext()
                    e.printStackTrace();
                    searchController.setError(true);
                }

                @Override
                public void onComplete()
                {
                }
            };
        return searchObserver;
    }

    /**
     * Observer on the TabLayout. This will cause the SearchView Observer to re-emit its values
     * against the new tab text if the user has selected a new tab.
     *
     * @return Observer subscribed to the TabLayout.
     */
    public DisposableObserver<TabLayoutSelectionEvent> getTabObserver()
    {
        return new DisposableObserver<TabLayoutSelectionEvent>()
        {
            @Override
            public void onNext(TabLayoutSelectionEvent tabLayoutSelectionEvent)
            {
                String currentTabText = tabLayoutSelectionEvent.tab().getText().toString().toLowerCase();
                if (!currentFilter.equals(currentTabText))
                {
                    currentFilter = currentTabText;
                    if (!searchController.getShowPastSearches() && !searchController.getSearching())
                        searchObserver.onNext(searchResults);
                }
            }

            @Override
            public void onError(Throwable e)
            {
                e.printStackTrace();
            }

            @Override
            public void onComplete()
            {
            }
        };
    }

    @Override
    public void showPastSearches(boolean showPastSearches)
    {
        if (showPastSearches)
            searchController.setPastSearches(daoManager.getRecentSearchTerms());
        else
            searchController.setShowPastSearches(false);
    }

    @Override
    public List<SearchTerm> getRecentSearchTerms()
    {
        return daoManager.getRecentSearchTerms();
    }

    @Override
    public void unsubscribe()
    {
        disposable.clear();
    }

    @Override
    public void dispose()
    {
        disposable.dispose();
    }
}
