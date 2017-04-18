package bj.rxjavaexperimentation.main.epoxy;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import bj.rxjavaexperimentation.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Josh Laird on 17/04/2017.
 */

@EpoxyModelClass(layout = R.layout.model_main_header)
public abstract class MainHeaderModel extends EpoxyModel<LinearLayout>
{
    @EpoxyAttribute String title;
    @BindView(R.id.tvHeader) TextView tvHeader;

    @Override
    public void bind(LinearLayout view)
    {
        ButterKnife.bind(this, view);
        tvHeader.setText(title);
    }
}