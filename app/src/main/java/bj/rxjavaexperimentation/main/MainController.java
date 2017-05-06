package bj.rxjavaexperimentation.main;

import android.content.Context;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import bj.rxjavaexperimentation.R;
import bj.rxjavaexperimentation.epoxy.common.CarouselModel_;
import bj.rxjavaexperimentation.epoxy.common.DividerModel_;
import bj.rxjavaexperimentation.epoxy.common.ErrorModel_;
import bj.rxjavaexperimentation.epoxy.common.LoadingModel_;
import bj.rxjavaexperimentation.epoxy.main.ListingModel_;
import bj.rxjavaexperimentation.epoxy.main.MainTitleModel_;
import bj.rxjavaexperimentation.epoxy.main.MainUserModel_;
import bj.rxjavaexperimentation.epoxy.main.NoOrderModel_;
import bj.rxjavaexperimentation.epoxy.main.OrderModel_;
import bj.rxjavaexperimentation.epoxy.main.VerifyEmailModel_;
import bj.rxjavaexperimentation.epoxy.main.ViewedReleaseModel_;
import bj.rxjavaexperimentation.greendao.ViewedRelease;
import bj.rxjavaexperimentation.model.listing.Listing;
import bj.rxjavaexperimentation.model.order.Order;
import bj.rxjavaexperimentation.model.search.SearchResult;
import bj.rxjavaexperimentation.utils.AnalyticsTracker;
import bj.rxjavaexperimentation.utils.DateFormatter;
import bj.rxjavaexperimentation.utils.ImageViewAnimator;
import bj.rxjavaexperimentation.utils.SharedPrefsManager;

/**
 * Created by Josh Laird on 17/04/2017.
 * <p>
 * Epoxy v2 Adapter controller.
 */
@Singleton
public class MainController extends EpoxyController
{
    private Context context;
    private MainContract.View mView;
    private SharedPrefsManager sharedPrefsManager;
    private ImageViewAnimator imageViewAnimator;
    private DateFormatter dateFormatter;
    private AnalyticsTracker tracker;
    private boolean loadingMorePurchases = true;
    private boolean loadingMoreSales = true;
    private List<Order> orders = new ArrayList<>();
    private List<Listing> listings = new ArrayList<>();
    private List<ViewedRelease> viewedReleases = new ArrayList<>();
    private List<SearchResult> recommendations = new ArrayList<>();
    private boolean ordersError;
    private boolean confirmEmail;

    @Inject
    public MainController(Context context, MainContract.View mView, SharedPrefsManager sharedPrefsManager,
                          ImageViewAnimator imageViewAnimator, DateFormatter dateFormatter, AnalyticsTracker tracker)
    {
        this.context = context;
        this.mView = mView;
        this.sharedPrefsManager = sharedPrefsManager;
        this.imageViewAnimator = imageViewAnimator;
        this.dateFormatter = dateFormatter;
        this.tracker = tracker;
    }

    @Override
    protected void buildModels()
    {
        new MainUserModel_(context)
                .id("user model")
                .username(sharedPrefsManager.getUsername())
                .avatarUrl(sharedPrefsManager.getAvatarUrl())
                .addTo(this);

        new DividerModel_()
                .id("divider3")
                .addTo(this);

        if (viewedReleases.size() > 0)
        {
            new MainTitleModel_()
                    .id("Viewed releases model")
                    .title("Recently viewed")
                    .size(0) // Hide see all button
                    .addTo(this);

            List<ViewedReleaseModel_> viewedReleaseModels = new ArrayList<>();
            for (ViewedRelease viewedRelease : viewedReleases)
            {
                viewedReleaseModels.add(new ViewedReleaseModel_()
                        .id("viewedReleases" + viewedReleases.indexOf(viewedRelease))
                        .context(context)
                        .onClickListener(v -> mView.displayRelease(viewedRelease.getReleaseName(), viewedRelease.getReleaseId()))
                        .thumbUrl(viewedRelease.getThumbUrl())
                        .releaseName(viewedRelease.getReleaseName()));
            }

            add(new CarouselModel_()
                    .id("viewed release carousel")
                    .models(viewedReleaseModels));
        }

        if (recommendations.size() > 0)
        {
            new MainTitleModel_()
                    .title("You might like")
                    .id("Recommendations model")
                    .tvButtonText("Learn more")
                    .onClickListener(v -> mView.learnMore())
                    .size(recommendations.size())
                    .addTo(this);

            List<ViewedReleaseModel_> viewedReleaseModels = new ArrayList<>();
            for (SearchResult recommendation : recommendations)
            {
                viewedReleaseModels.add(new ViewedReleaseModel_()
                        .id("recommendations" + recommendations.indexOf(recommendation))
                        .context(context)
                        .onClickListener(v -> mView.displayRelease(recommendation.getTitle(), recommendation.getId()))
                        .thumbUrl(recommendation.getThumb())
                        .releaseName(recommendation.getTitle()));

            }
            add(new CarouselModel_()
                    .id("recommendations carousel")
                    .models(viewedReleaseModels));
        }

        new MainTitleModel_()
                .id("orders header")
                .title("Orders")
                .size(orders.size())
                .onClickListener(v -> mView.displayOrdersActivity(sharedPrefsManager.getUsername()))
                .addTo(this);

        new ErrorModel_()
                .id("orders error")
                .onClick(v -> mView.retry())
                .errorString("Unable to fetch orders")
                .addIf(ordersError, this);

        new VerifyEmailModel_()
                .id("confirm email model")
                .addIf(confirmEmail, this);

        new NoOrderModel_()
                .id("no orders")
                .text("No order history")
                .addIf(!loadingMorePurchases && orders.size() == 0 && !ordersError && !confirmEmail, this);

        for (Order order : orders)
        {
            // Only display a maximum of 5
            if (orders.indexOf(order) == 5)
                break;

            new OrderModel_(dateFormatter)
                    .lastActivity(order.getLastActivity())
                    .status(order.getStatus())
                    .buyer(order.getBuyer().getUsername())
                    .onClickListener(v -> mView.displayOrder(order.getId()))
                    .id("order" + String.valueOf(orders.indexOf(order)))
                    .addTo(this);

            new DividerModel_()
                    .id("order divider " + orders.indexOf(order))
                    .addIf(orders.indexOf(order) != orders.size() - 1, this);
        }

        new LoadingModel_()
                .imageViewAnimator(imageViewAnimator)
                .id("loading model")
                .addIf(loadingMorePurchases, this);

        // Selling

        new MainTitleModel_()
                .id("selling header")
                .title(context.getString(R.string.selling))
                .size(listings.size())
                .onClickListener(v -> mView.displayListingsActivity(sharedPrefsManager.getUsername()))
                .addTo(this);

        new ErrorModel_()
                .id("selling error")
                .errorString("Unable to fetch selling")
                .onClick(v -> mView.retry())
                .addIf(ordersError, this);

        new NoOrderModel_()
                .id("not selling")
                .text(context.getString(R.string.not_selling_anything))
                .addIf(!loadingMoreSales && listings.size() == 0 && !ordersError && !confirmEmail, this);

        new VerifyEmailModel_()
                .id("confirm email model2")
                .addIf(confirmEmail, this);

        for (Listing listing : listings)
        {
            if (listings.indexOf(listing) == 5)
                break;

            new ListingModel_(dateFormatter)
                    .datePosted(listing.getPosted())
                    .releaseName(listing.getRelease().getDescription())
                    .onClickListener(v -> mView.displayListing(listing.getId(), listing.getTitle(), sharedPrefsManager.getUsername(), "", listing.getSeller().getUsername()))
                    .id("listing" + String.valueOf(listings.indexOf(listing)))
                    .addTo(this);

            new DividerModel_()
                    .id("sale divider " + listings.indexOf(listing))
                    .addTo(this);
        }

        new LoadingModel_()
                .id("sales loading model")
                .imageViewAnimator(imageViewAnimator)
                .addIf(loadingMoreSales, this);
    }

    public void setOrders(List<Order> purchases)
    {
        this.orders = purchases;
        this.loadingMorePurchases = false;
        this.ordersError = false;
        this.confirmEmail = false;
        requestModelBuild();
    }

    public void setLoadingMorePurchases(boolean loadingMorePurchases)
    {
        this.loadingMorePurchases = loadingMorePurchases;
        this.loadingMoreSales = loadingMorePurchases;
        this.ordersError = false;
        this.confirmEmail = false;
        requestModelBuild();
    }

    public void setSelling(List<Listing> listings)
    {
        this.listings = listings;
        this.loadingMoreSales = false;
        this.confirmEmail = false;
        requestModelBuild();
    }

    public void setOrdersError(boolean b)
    {
        this.ordersError = b;
        this.loadingMorePurchases = false;
        this.loadingMoreSales = false;
        this.confirmEmail = false;
        tracker.send(context.getString(R.string.main_activity), context.getString(R.string.main_activity), context.getString(R.string.error), "fetching orders", 1L);
        requestModelBuild();
    }

    public void setConfirmEmail(boolean confirmEmail)
    {
        this.confirmEmail = confirmEmail;
        this.ordersError = false;
        this.loadingMorePurchases = false;
        this.loadingMoreSales = false;
        requestModelBuild();
    }

    public void setViewedReleases(List<ViewedRelease> viewedReleases)
    {
        this.viewedReleases = viewedReleases;
        requestModelBuild();
    }

    public void setRecommendations(List<SearchResult> recommendations)
    {
        this.recommendations = recommendations;
        requestModelBuild();
    }
}
