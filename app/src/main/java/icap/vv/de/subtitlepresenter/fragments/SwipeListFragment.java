package icap.vv.de.subtitlepresenter.fragments;

import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by robUx4 on 12/29/2014.
 */
public class SwipeListFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener;
    private ContainerSwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup listContainer = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        refreshLayout = new ContainerSwipeRefreshLayout(inflater.getContext());
        refreshLayout.addView(listContainer);
        refreshLayout.setOnRefreshListener(this);
        return refreshLayout;
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener refreshListener) {
        this.mRefreshListener = refreshListener;
    }

    @Override
    public final void onRefresh() {
        if (null != mRefreshListener) {
            mRefreshListener.onRefresh();
        }
    }

    @Override
    public void setListShown(boolean shown) {
        super.setListShown(shown);
        refreshLayout.setEnabled(shown);
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return refreshLayout;
    }

    private class ContainerSwipeRefreshLayout extends SwipeRefreshLayout {

        public ContainerSwipeRefreshLayout(Context context) {
            super(context);
        }

        public ContainerSwipeRefreshLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean canChildScrollUp() {
            return ViewCompat.canScrollVertically(getListView(), -1);
        }
    }
}
