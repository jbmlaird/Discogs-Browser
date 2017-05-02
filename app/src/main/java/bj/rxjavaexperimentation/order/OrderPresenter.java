package bj.rxjavaexperimentation.order;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import javax.inject.Inject;

import bj.rxjavaexperimentation.network.DiscogsInteractor;
import bj.rxjavaexperimentation.utils.schedulerprovider.MySchedulerProvider;

/**
 * Created by Josh Laird on 18/04/2017.
 */
public class OrderPresenter implements OrderContract.Presenter
{
    private OrderContract.View view;
    private DiscogsInteractor discogsInteractor;
    private MySchedulerProvider mySchedulerProvider;
    private OrderController orderController;

    @Inject
    public OrderPresenter(OrderContract.View view, DiscogsInteractor discogsInteractor, MySchedulerProvider mySchedulerProvider, OrderController orderController)
    {
        this.view = view;
        this.discogsInteractor = discogsInteractor;
        this.mySchedulerProvider = mySchedulerProvider;
        this.orderController = orderController;
    }

    @Override
    public void fetchOrderDetails(String orderId)
    {
        // Only sellers can see order messages
//        if (seller)
//            discogsInteractor.fetchOrderDetails(orderId)
//                    .subscribeOn(mySchedulerProvider.io())
//                    .observeOn(mySchedulerProvider.ui())
//                    .flatMap(orderDetails ->
//                    {
//                        orderController.setOrderDetails(orderDetails);
//                        return discogsInteractor.fetchOrderMessages(orderId)
//                                .subscribeOn(mySchedulerProvider.io());
//                    })
//                    .subscribe(
//                            orderMessages ->
//                                    orderController.setOrderMessages(orderMessages),
//                            error ->
//                                    orderController.errorFetchingDetails());
//        else
        discogsInteractor.fetchOrderDetails(orderId)
                .subscribeOn(mySchedulerProvider.io())
                .observeOn(mySchedulerProvider.ui())
                .subscribe(orderDetails ->
                                orderController.setOrderDetails(orderDetails),
                        error ->
                                orderController.errorFetchingDetails());
    }

    public void setupRecyclerView(OrderActivity orderActivity, RecyclerView recyclerView)
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(orderActivity));
        recyclerView.setAdapter(orderController.getAdapter());
        orderController.requestModelBuild();
    }
}
