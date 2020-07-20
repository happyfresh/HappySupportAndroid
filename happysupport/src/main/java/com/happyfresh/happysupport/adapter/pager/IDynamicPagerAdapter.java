package com.happyfresh.happysupport.adapter.pager;

import java.util.List;

interface IDynamicPagerAdapter<T extends ItemHolder> {

    // region Pager Adapter

    void notifyDataSetChanged();

    int getCount();

    // endregion

    // region Fragment Pager Adapter

    long getItemId(int position);

    // endregion

    int add(T listener);

    int remove(long id);

    int remove(T listener);

    int getPosition(long id);

    int getPosition(T listener);

    int getLastPosition(T listener);

    int getLastPosition(long id);

    boolean contains(T listener);

    boolean contains(long id);

    boolean isEmpty();

    List<T> getItemHolders();
}
