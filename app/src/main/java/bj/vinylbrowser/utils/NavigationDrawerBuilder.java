package bj.vinylbrowser.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import org.fuckboilerplate.rx_social_connect.RxSocialConnect;

import bj.vinylbrowser.R;
import bj.vinylbrowser.about.AboutActivity;
import bj.vinylbrowser.greendao.DaoManager;
import bj.vinylbrowser.login.LoginActivity;
import bj.vinylbrowser.main.MainActivity;
import bj.vinylbrowser.search.SearchController;
import bj.vinylbrowser.singlelist.SingleListController;

/**
 * Created by Josh Laird on 16/04/2017.
 * <p>
 * Class to build the NavigationDrawer.
 */
public class NavigationDrawerBuilder
{
    private Context context;
    private SharedPrefsManager sharedPrefsManager;
    private DaoManager daoManager;

    public NavigationDrawerBuilder(Context context, SharedPrefsManager sharedPrefsManager, DaoManager daoManager)
    {
        this.context = context;
        this.sharedPrefsManager = sharedPrefsManager;
        this.daoManager = daoManager;
    }

    public Drawer buildNavigationDrawer(AppCompatActivity activity, Router router, Toolbar toolbar)
    {
        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(ContextCompat.getDrawable(context, R.drawable.gradient))
                .addProfiles(
                        new ProfileDrawerItem().withName(sharedPrefsManager.getName()).withEmail(sharedPrefsManager.getUsername()).withIcon(sharedPrefsManager.getAvatarUrl())
                )
                .build();

        return new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withHasStableIds(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(context.getString(R.string.drawer_item_collection))
                                .withIcon(R.drawable.ic_library_music_black_24dp)
                                .withIdentifier(1).withSelectable(false).withBadge(String.valueOf(sharedPrefsManager.getNumCollection())).withBadgeStyle(new BadgeStyle().withTextColor(ContextCompat.getColor(context, android.R.color.white)).withColorRes(R.color.colorAccent)),
                        new PrimaryDrawerItem().withName(context.getString(R.string.drawer_item_wantlist))
                                .withIcon(R.drawable.ic_remove_red_eye_black_24dp)
                                .withIdentifier(2).withSelectable(false).withBadge(String.valueOf(sharedPrefsManager.getNumWantlist())).withBadgeStyle(new BadgeStyle().withTextColor(ContextCompat.getColor(context, android.R.color.white)).withColorRes(R.color.colorAccent)),
                        new ExpandableDrawerItem().withName("Marketplace")
                                .withIcon(R.drawable.ic_attach_money_black_24dp)
                                .withIdentifier(3).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(context.getString(R.string.selling)).withLevel(2)
                                        .withIdentifier(3001),
                                new SecondaryDrawerItem().withName("Orders").withLevel(2)
                                        .withIdentifier(3002)
                        ),
                        new PrimaryDrawerItem().withName(context.getString(R.string.drawer_item_search))
                                .withIcon(R.drawable.ic_search_black_24dp)
                                .withIdentifier(4).withSelectable(false),
//                        new PrimaryDrawerItem().withName(context.getString(R.string.drawer_item_settings))
//                                .withIcon(R.drawable.ic_settings_black_24dp)
//                                .withIdentifier(5).withSelectable(false),
                        new PrimaryDrawerItem().withName(context.getString(R.string.drawer_item_about))
                                .withIcon(R.drawable.ic_info_outline_black_24dp)
                                .withIdentifier(6).withSelectable(false),
                        new PrimaryDrawerItem().withName(context.getString(R.string.drawer_item_logout))
                                .withIcon(R.drawable.ic_exit_to_app_black_24dp)
                                .withIdentifier(7).withSelectable(false)
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) ->
                        {
                            switch ((int) drawerItem.getIdentifier())
                            {
                                case 1:
                                    router.pushController(RouterTransaction.with(new SingleListController(R.string.drawer_item_collection, sharedPrefsManager.getUsername()))
                                            .popChangeHandler(new FadeChangeHandler())
                                            .pushChangeHandler(new FadeChangeHandler())
                                            .tag("SingleListController"));
                                    break;
                                case 2:
                                    router.pushController(RouterTransaction.with(new SingleListController(R.string.drawer_item_wantlist, sharedPrefsManager.getUsername()))
                                            .popChangeHandler(new FadeChangeHandler())
                                            .pushChangeHandler(new FadeChangeHandler())
                                            .tag("SingleListController"));
                                    break;
                                case 3001:
                                    router.pushController(RouterTransaction.with(new SingleListController(R.string.selling, sharedPrefsManager.getUsername()))
                                            .popChangeHandler(new FadeChangeHandler())
                                            .pushChangeHandler(new FadeChangeHandler())
                                            .tag("SingleListController"));
                                    break;
                                case 3002:
                                    router.pushController(RouterTransaction.with(new SingleListController(R.string.orders, sharedPrefsManager.getUsername()))
                                            .popChangeHandler(new FadeChangeHandler())
                                            .pushChangeHandler(new FadeChangeHandler())
                                            .tag("SingleListController"));
                                    break;
                                case 4:
                                    router.pushController(RouterTransaction.with(new SearchController())
                                            .popChangeHandler(new FadeChangeHandler())
                                            .pushChangeHandler(new FadeChangeHandler())
                                            .tag("SearchController"));
                                    break;
//                                case 5:
//                                    // TODO: Implement Settings
//                                    break;
                                case 6:
                                    activity.startActivity(AboutActivity.createIntent(activity));
                                    break;
                                case 7:
                                    RxSocialConnect.closeConnections();
                                    sharedPrefsManager.removeUserDetails();
                                    daoManager.clearRecentSearchTerms();
                                    daoManager.clearViewedReleases();
                                    activity.startActivity(LoginActivity.createIntent(activity));
                                    activity.finish();
                                    break;
                            }
                            return false;
                        }
                )
                .build();
    }

    /**
     * Lifted: https://github.com/mikepenz/MaterialDrawer#load-images-via-url
     */
    public static void initialiseMaterialDrawerImageLoader()
    {
        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader()
        {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag)
            {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView)
            {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag)
            {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag))
                {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                }
                else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag))
                {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                }
                else if ("customUrlItem".equals(tag))
                {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
    }
}
