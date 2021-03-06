package bj.vinylbrowser.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.airbnb.epoxy.EpoxyController;

import bj.vinylbrowser.App;
import bj.vinylbrowser.AppComponent;
import butterknife.Unbinder;

/**
 * Created by j on 18/02/2017.
 */
public abstract class BaseActivity extends AppCompatActivity
{
    protected Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setupComponent(App.appComponent);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    public abstract void setupComponent(AppComponent appComponent);

    protected void setupToolbar(Toolbar toolbar, String title)
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    protected void setupToolbar(Toolbar toolbar)
    {
        setupToolbar(toolbar, "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }

    protected void setupRecyclerView(RecyclerView recyclerView, EpoxyController controller)
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(controller.getAdapter());
    }
}
