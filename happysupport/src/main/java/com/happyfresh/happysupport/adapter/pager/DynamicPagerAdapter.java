package com.happyfresh.happysupport.adapter.pager;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class DynamicPagerAdapter extends PagerAdapter
        implements IDynamicPagerAdapter<DynamicPagerAdapter.ViewHolder> {

    private List<ViewHolder> itemHolders = new ArrayList<>();

    private List<Object> items = new ArrayList<>();

    @Override
    public int getCount() {
        return DynamicPagerUtil.getCount(itemHolders);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Object object = itemHolders.get(position).onBind(container, position);
        if (!items.contains(object)) {
            items.add(object);
        }
        return object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        int position = POSITION_NONE;
        if (items.contains(object)) {
            position = POSITION_UNCHANGED;
        }
        return position;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object.equals(view);
    }

    @Override
    public int add(ViewHolder itemHolder) {
        return DynamicPagerUtil.add(this, itemHolders, itemHolder);
    }

    @Override
    public int remove(long id) {
        return DynamicPagerUtil.remove(this, itemHolders, id);
    }

    @Override
    public int remove(ViewHolder itemHolder) {
        return DynamicPagerUtil.remove(this, itemHolders, itemHolder);
    }

    @Override
    public int getPosition(long id) {
        return DynamicPagerUtil.getPosition(itemHolders, id);
    }

    @Override
    public int getPosition(ViewHolder itemHolder) {
        return DynamicPagerUtil.getPosition(itemHolders, itemHolder);
    }

    @Override
    public int getLastPosition(ViewHolder itemHolder) {
        return DynamicPagerUtil.getLastPosition(itemHolders, itemHolder);
    }

    @Override
    public int getLastPosition(long id) {
        return DynamicPagerUtil.getLastPosition(itemHolders, id);
    }

    @Override
    public boolean contains(ViewHolder itemHolder) {
        return DynamicPagerUtil.contains(itemHolders, itemHolder);
    }

    @Override
    public boolean contains(long id) {
        return DynamicPagerUtil.contains(itemHolders, id);
    }

    @Override
    public boolean isEmpty() {
        return DynamicPagerUtil.isEmpty(itemHolders);
    }

    @Override
    public List<ViewHolder> getItemHolders() {
        return itemHolders;
    }

    @Override
    public long getItemId(int position) {
        return DynamicPagerUtil.getId(itemHolders, position);
    }

    public interface ViewHolder extends ItemHolder {

        Object onBind(@NonNull ViewGroup container, int position);
    }
}
