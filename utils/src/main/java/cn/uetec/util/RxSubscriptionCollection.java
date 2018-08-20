package cn.uetec.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;

/**
 * Created by Junhua Lv on 2016/7/1.
 * rxjava subscription 收集器，便于在界面退出时全部取消
 */
public class RxSubscriptionCollection {

    private List<WeakReference<Subscription>> mList = new ArrayList<>();

    @Inject
    public RxSubscriptionCollection() {
    }

    /**
     * 添加subscription
     * @param subscription
     */
    public void add(Subscription subscription){
        mList.add(new WeakReference<>(subscription));
    }

    /**
     * 全部取消
     */
    public void cancelAll(){
        for (WeakReference<Subscription> ref : mList){
            Subscription subscription = ref.get();
            if (null != subscription)
                subscription.unsubscribe();
        }

        mList.clear();
    }
}
