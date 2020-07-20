package com.happyfresh.happysupport.adapter.pager;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class DynamicFragmentPagerAdapter extends FragmentPagerAdapter
        implements IDynamicPagerAdapter<DynamicFragmentPagerAdapter.FragmentHolder> {

    private List<FragmentHolder> itemHolders = new ArrayList<>();

    private List<Object> items = new ArrayList<>();

    public DynamicFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public int getCount() {
        return DynamicPagerUtil.getCount(itemHolders);
    }

    @Override
    public long getItemId(int position) {
        return DynamicPagerUtil.getId(itemHolders, position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
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

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return itemHolders.get(position).onCreate(position);
    }

    @Override
    public int add(FragmentHolder itemHolder) {
        return DynamicPagerUtil.add(this, itemHolders, itemHolder);
    }

    @Override
    public int remove(long id) {
        return DynamicPagerUtil.remove(this, itemHolders, id);
    }

    @Override
    public int remove(FragmentHolder itemHolder) {
        return DynamicPagerUtil.remove(this, itemHolders, itemHolder);
    }

    @Override
    public int getPosition(long id) {
        return DynamicPagerUtil.getPosition(itemHolders, id);
    }

    @Override
    public int getPosition(FragmentHolder itemHolder) {
        return DynamicPagerUtil.getPosition(itemHolders, itemHolder);
    }

    @Override
    public int getLastPosition(FragmentHolder itemHolder) {
        return DynamicPagerUtil.getLastPosition(itemHolders, itemHolder);
    }

    @Override
    public int getLastPosition(long id) {
        return DynamicPagerUtil.getLastPosition(itemHolders, id);
    }

    @Override
    public boolean contains(FragmentHolder itemHolder) {
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
    public List<FragmentHolder> getItemHolders() {
        return itemHolders;
    }

    public interface FragmentHolder extends ItemHolder {

        Fragment onCreate(int position);
    }
}
