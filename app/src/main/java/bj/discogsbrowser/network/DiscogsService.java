package bj.discogsbrowser.network;

import bj.discogsbrowser.model.artist.ArtistResult;
import bj.discogsbrowser.model.artistrelease.RootArtistReleaseResponse;
import bj.discogsbrowser.model.collection.AddToCollectionResponse;
import bj.discogsbrowser.model.collection.RootCollectionRelease;
import bj.discogsbrowser.model.label.Label;
import bj.discogsbrowser.model.labelrelease.RootLabelResponse;
import bj.discogsbrowser.model.listing.Listing;
import bj.discogsbrowser.model.listing.RootListingResponse;
import bj.discogsbrowser.model.master.Master;
import bj.discogsbrowser.model.order.Order;
import bj.discogsbrowser.model.order.RootOrderResponse;
import bj.discogsbrowser.model.release.Release;
import bj.discogsbrowser.model.search.RootSearchResponse;
import bj.discogsbrowser.model.user.User;
import bj.discogsbrowser.model.user.UserDetails;
import bj.discogsbrowser.model.version.RootVersionsResponse;
import bj.discogsbrowser.model.wantlist.AddToWantlistResponse;
import bj.discogsbrowser.model.wantlist.RootWantlistResponse;
import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Josh Laird on 18/02/2017.
 * <p>
 * Interface to interact with the Discogs API.
 */
public interface DiscogsService
{
    @GET("database/search")
    Single<RootSearchResponse> getSearchResults(@Query("q") String searchTerm, @Query("per_page") String perPage);

    @GET("database/search")
    Single<RootSearchResponse> searchByStyle(@Query("style") String style, @Query("type") String type, @Query("per_page") String perPage, @Query("page") String page);

    @GET("database/search")
    Single<RootSearchResponse> searchByLabel(@Query("label") String label, @Query("type") String type, @Query("per_page") String perPage);

    @GET("releases/{release_id}")
    Single<Release> getRelease(@Path("release_id") String releaseId);

    @GET("artists/{artist_id}")
    Single<ArtistResult> getArtist(@Path("artist_id") String artistId);

    @GET("masters/{master_id}")
    Single<Master> getMaster(@Path("master_id") String masterId);

    @GET("masters/{master_id}/versions")
    Single<RootVersionsResponse> getMasterVersions(@Path("master_id") String masterId);

    @GET("labels/{label_id}")
    Single<Label> getLabel(@Path("label_id") String labelId);

    @GET("labels/{label_id}/releases")
    Single<RootLabelResponse> getLabelReleases(@Path("label_id") String labelId, @Query("sort_order") String sort, @Query("per_page") String perPage);

    @GET("artists/{artist_id}/releases")
    Single<RootArtistReleaseResponse> getArtistReleases(@Path("artist_id") String artistId, @Query("sort_order") String sort, @Query("per_page") String perPage);

    @GET("/marketplace/listings/{listing_id}")
    Single<Listing> getListing(@Path("listing_id") String listingId, @Query("curr_abbr") String currency);

    @GET("/oauth/identity")
    Single<User> fetchIdentity();

    @GET("/users/{username}")
    Single<UserDetails> fetchUserDetails(@Path("username") String username);

    // 0 means it will always look in the user's "Uncategorized" folder (must be authenticated)
    @GET("/users/{username}/collection/folders/1/releases")
    Single<RootCollectionRelease> fetchCollection(@Path("username") String username, @Query("sort") String sortBy, @Query("sort_order") String sortOrder, @Query("per_page") String perPage);

    // 0 means it will always look in the user's "Uncategorized" folder (must be authenticated)
    @POST("/users/{username}/collection/folders/1/releases/{release_id}")
    Single<AddToCollectionResponse> addToCollection(@Path("username") String username, @Path("release_id") String releaseId);

    @DELETE("/users/{username}/collection/folders/1/releases/{release_id}/instances/{instance_id}")
    Single<Response<Void>> removeFromCollection(@Path("username") String username, @Path("release_id") String releaseId, @Path("instance_id") String instanceId);

    @PUT("/users/{username}/wants/{release_id}")
    Single<AddToWantlistResponse> addToWantlist(@Path("username") String username, @Path("release_id") String releaseId);

    @DELETE("/users/{username}/wants/{release_id}")
    Single<Response<Void>> removeFromWantlist(@Path("username") String username, @Path("release_id") String releaseId);

    @GET("/users/{username}/wants")
    Single<RootWantlistResponse> fetchWantlist(@Path("username") String username, @Query("sort") String sortBy, @Query("sort_order") String sortOrder, @Query("per_page") String perPage);

    @GET("/marketplace/orders")
    Single<RootOrderResponse> fetchOrders(@Query("sort_order") String sortOrder, @Query("per_page") String perPage, @Query("sort") String sortBy);

    @GET("/users/{username}/inventory")
    Single<RootListingResponse> fetchSelling(@Path("username") String username, @Query("sort_order") String desc, @Query("per_page") String perPage, @Query("sort") String sortBy);

    @GET("/marketplace/orders/{order_id}")
    Single<Order> fetchOrderDetails(@Path("order_id") String orderId);

    // Only viewable if you're the seller
//    @GET("/marketplace/orders/{order_id}/messages/")
//    Single<RootOrderMessagesResponse> fetchOrderMessages(@Path("order_id") String orderId);
}