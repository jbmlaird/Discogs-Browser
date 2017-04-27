package bj.rxjavaexperimentation.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent;

import javax.inject.Inject;

import bj.rxjavaexperimentation.AppComponent;
import bj.rxjavaexperimentation.R;
import bj.rxjavaexperimentation.artist.ArtistActivity;
import bj.rxjavaexperimentation.common.BaseActivity;
import bj.rxjavaexperimentation.common.MyRecyclerView;
import bj.rxjavaexperimentation.label.LabelActivity;
import bj.rxjavaexperimentation.master.MasterActivity;
import bj.rxjavaexperimentation.model.search.SearchResult;
import bj.rxjavaexperimentation.release.ReleaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;

/**
 * Created by Josh Laird on 20/02/2017.
 */

public class SearchActivity extends BaseActivity implements SearchContract.View
{
    private static final String TAG = "SearchActivity";

    @Inject SearchPresenter presenter;
    @BindView(R.id.searchView) SearchView searchView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.pbRecyclerView) ProgressBar pbRecyclerView;
    @BindView(R.id.rvResults) MyRecyclerView rvResults;
    private SearchComponent component;

    @Override
    public void setupComponent(AppComponent appComponent)
    {
        component = DaggerSearchComponent.builder()
                .appComponent(appComponent)
                .searchModule(new SearchModule(this))
                .build();
        component.inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);
        unbinder = ButterKnife.bind(this);
        presenter.setupSubscription();
        presenter.setupRecyclerView(rvResults);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchView.setIconified(false);
        searchView.requestFocus();
        // Set SearchView text color to white
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(ContextCompat.getColor(this, android.R.color.white));
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void hideProgressBar()
    {
        pbRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showProgressBar()
    {
        pbRecyclerView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.search_close_btn)
    public void onClose()
    {
        searchView.setQuery("", false);
        presenter.showSuggestions();
    }

    @Override
    public Observable<SearchViewQueryTextEvent> searchIntent()
    {
        return RxSearchView.queryTextChangeEvents(searchView)
                .debounce(500, java.util.concurrent.TimeUnit.MILLISECONDS)
                // Gotta comment this out otherwise it doesn't track when search box is cleared
                // Adverse effects of searching straightaway
                .filter(searchViewQueryTextEvent -> searchViewQueryTextEvent.queryText().length() > 2);
    }

    @Override
    public void startDetailedActivity(SearchResult searchResult)
    {
        Intent intent = null;
        switch (searchResult.getType())
        {
            case "artist":
                intent = new Intent(this, ArtistActivity.class);
                break;
            case "label":
                intent = new Intent(this, LabelActivity.class);
                break;
            case "release":
                intent = new Intent(this, ReleaseActivity.class);
                break;
            case "master":
                intent = new Intent(this, MasterActivity.class);
                break;
        }
        intent.putExtra("title", searchResult.getTitle());
        intent.putExtra("id", searchResult.getId());
        startActivity(intent);
    }

    @Override
    public void fillSearchBox(String searchTerm)
    {
        searchView.setQuery(searchTerm, false);
    }
}
