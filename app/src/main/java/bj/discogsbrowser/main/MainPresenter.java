package bj.discogsbrowser.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;
import javax.inject.Singleton;

import bj.discogsbrowser.R;
import bj.discogsbrowser.greendao.DaoSession;
import bj.discogsbrowser.greendao.ViewedRelease;
import bj.discogsbrowser.greendao.ViewedReleaseDao;
import bj.discogsbrowser.model.listing.Listing;
import bj.discogsbrowser.model.order.Order;
import bj.discogsbrowser.model.search.RootSearchResponse;
import bj.discogsbrowser.network.DiscogsInteractor;
import bj.discogsbrowser.utils.AnalyticsTracker;
import bj.discogsbrowser.utils.NavigationDrawerBuilder;
import bj.discogsbrowser.utils.SharedPrefsManager;
import bj.discogsbrowser.utils.schedulerprovider.MySchedulerProvider;
import bj.discogsbrowser.wrappers.LogWrapper;
import io.reactivex.Single;

/**
 * Created by j on 18/02/2017.
 */
@Singleton
public class MainPresenter implements MainContract.Presenter
{
    private final String TAG = getClass().getSimpleName();
    private Context context;
    private MainContract.View mView;
    private DiscogsInteractor discogsInteractor;
    private MySchedulerProvider mySchedulerProvider;
    private NavigationDrawerBuilder navigationDrawerBuilder;
    private MainController mainController;
    private SharedPrefsManager sharedPrefsManager;
    private LogWrapper log;
    private ViewedReleaseDao viewedReleaseDao;
    private AnalyticsTracker tracker;
    private boolean recommendationsFetched;

    @Inject
    public MainPresenter(@NonNull Context context, @NonNull MainContract.View view, @NonNull DiscogsInteractor discogsInteractor,
                         @NonNull MySchedulerProvider mySchedulerProvider, @NonNull NavigationDrawerBuilder navigationDrawerBuilder,
                         @NonNull MainController mainController, @NonNull SharedPrefsManager sharedPrefsManager,
                         @NonNull LogWrapper log, @NonNull DaoSession daoSession, @NonNull AnalyticsTracker tracker)
    {
        this.context = context;
        mView = view;
        this.discogsInteractor = discogsInteractor;
        this.mySchedulerProvider = mySchedulerProvider;
        this.navigationDrawerBuilder = navigationDrawerBuilder;
        this.mainController = mainController;
        this.sharedPrefsManager = sharedPrefsManager;
        this.log = log;
        viewedReleaseDao = daoSession.getViewedReleaseDao();
        this.tracker = tracker;
    }

    @Override
    public void connectAndBuildNavigationDrawer(MainActivity mainActivity, Toolbar toolbar)
    {
        mView.showLoading(true);
        fetchMainPageInformation()
                .subscribe(listing ->
                        {
                            mainController.setSelling(listing);
                            // As RecyclerView gets detached, these must be called after attaching NavDrawer
                            mView.setDrawer(navigationDrawerBuilder.buildNavigationDrawer(mainActivity, toolbar));
                            mView.setupRecyclerView();
                        },
                        error ->
                        {
                            if (error.getCause() != null && error.getCause().getCause() != null && error.getCause().getCause().getMessage().equals("HTTP 403 FORBIDDEN"))
                                mainController.setConfirmEmail(true);
                            else
                                mainController.setOrdersError(true);
                            mView.setDrawer(navigationDrawerBuilder.buildNavigationDrawer(mainActivity, toolbar));
                            mView.setupRecyclerView();
                            error.printStackTrace();
                            log.e(TAG, "Wtf");
                        });
    }

    @Override
    public void buildHistoryAndRecommendations()
    {
        mainController.setLoadingViewedReleases(true);
        List<ViewedRelease> viewedReleases = viewedReleaseDao.queryBuilder().orderDesc(ViewedReleaseDao.Properties.Date).limit(6).build().list();
        mainController.setViewedReleases(viewedReleases);
        if (viewedReleases.size() > 0)
        {
            if (!recommendationsFetched)
            {
                String latestReleaseViewedStyle = viewedReleases.get(0).getStyle();
                String latestReleaseViewedLabel = viewedReleases.get(0).getLabelName();
                discogsInteractor.searchByStyle(latestReleaseViewedStyle, "1", false) // Get results for those genres
                        .doOnSubscribe(onSubscribe ->
                                mainController.setLoadingRecommendations(true))
                        .subscribeOn(mySchedulerProvider.io())
                        .flatMap(rootSearchResponse ->
                        {
                            // Get a random page from the search results
                            int maxPageNumber = rootSearchResponse.getPagination().getPages();
                            int randomPageNumber = ThreadLocalRandom.current().nextInt(1, maxPageNumber + 1);
                            return discogsInteractor.searchByStyle(latestReleaseViewedStyle, String.valueOf(randomPageNumber), true);
                        })
                        .map(RootSearchResponse::getSearchResults)
                        .flattenAsObservable(searchResults ->
                                searchResults)
                        .take(12)
                        // Merge the list with releases from the label that their last viewed release was on
                        .concatWith(discogsInteractor.searchByLabel(latestReleaseViewedLabel)
                                .observeOn(mySchedulerProvider.io())
                                .flattenAsObservable(searchResults ->
                                        searchResults)
                                .take(12))
                        .toList()
                        .map(searchResults ->
                        {
                            Collections.shuffle(searchResults);
                            return searchResults;
                        })
                        .observeOn(mySchedulerProvider.ui())
                        .subscribe(recommendationList ->
                        {
                            mainController.setRecommendations(recommendationList);
                            recommendationsFetched = true;
                        }, error ->
                        {
                            error.printStackTrace();
                            mainController.setRecommendationsError(true);
                        });


            }
        }
        else
        {
            mainController.setRecommendations(Collections.emptyList());
            mainController.setViewedReleases(Collections.emptyList());
        }
    }

    private Single<List<Listing>> fetchMainPageInformation()
    {
        return discogsInteractor.fetchUserDetails()
                .observeOn(mySchedulerProvider.ui())
                .flatMap(userDetails ->
                {
                    tracker.send(context.getString(R.string.main_activity), context.getString(R.string.main_activity), context.getString(R.string.logged_in), userDetails.getUsername(), 1L);
                    sharedPrefsManager.storeUserDetails(userDetails);
                    return fetchOrders();
                })
                .flatMap(orders ->
                {
                    mainController.setOrders(orders);
                    return fetchSelling();
                })
                .flattenAsObservable(listings -> listings)
                .filter(listing -> listing.getStatus().equals("For Sale"))
                .toList();
    }

    @Override
    public void retry()
    {
        fetchMainPageInformation()
                .subscribe(listing ->
                                mainController.setSelling(listing),
                        error ->
                        {
                            error.printStackTrace();
                            mainController.setOrdersError(true);
                        });
    }

    @Override
    public Single<List<Order>> fetchOrders()
    {
        return discogsInteractor.fetchOrders()
                .observeOn(mySchedulerProvider.ui())
                .subscribeOn(mySchedulerProvider.io())
                .doOnSubscribe(disposable -> mainController.setLoadingMorePurchases(true));
    }

    @Override
    public Single<List<Listing>> fetchSelling()
    {
        return discogsInteractor.fetchSelling(sharedPrefsManager.getUsername())
                .observeOn(mySchedulerProvider.ui())
                .subscribeOn(mySchedulerProvider.io());
    }

    @Override
    public void setupRecyclerView(MainActivity mainActivity, RecyclerView recyclerView)
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerView.setAdapter(mainController.getAdapter());
        mainController.requestModelBuild();
    }
}