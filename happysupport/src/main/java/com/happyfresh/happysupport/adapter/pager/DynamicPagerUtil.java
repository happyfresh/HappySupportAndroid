package com.happyfresh.happysupport.adapter.pager;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

public class DynamicPagerUtil {

    private DynamicPagerUtil() {

    }

    static <T extends ItemHolder> int add(IDynamicPagerAdapter<T> adapter, List<T> itemHolders, T itemHolder) {
        itemHolders.add(itemHolder);
        adapter.notifyDataSetChanged();
        return itemHolders.size() - 1;
    }

    static <T extends ItemHolder> int remove(IDynamicPagerAdapter<T> adapter, List<T> itemHolders, long id) {
        for (int i = 0; i < itemHolders.size(); i++) {
            T itemHolder = itemHolders.get(i);
            if (itemHolder.getId(i) == id) {
                itemHolders.remove(i);
                adapter.notifyDataSetChanged();
                return i;
            }
        }

        return -1;
    }

    static <T extends ItemHolder> int remove(IDynamicPagerAdapter<T> adapter, List<T> itemHolders, T itemHolder) {
        int position = itemHolders.indexOf(itemHolder);
        if (position > -1) {
            itemHolders.remove(position);
        }

        adapter.notifyDataSetChanged();

        return position;
    }

    static <T extends ItemHolder> int getPosition(List<T> itemHolders, long id) {
        if (itemHolders == null || itemHolders.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < itemHolders.size(); i++) {
            T itemHolder = itemHolders.get(i);
            if (id == itemHolder.getId(i)) {
                return i;
            }
        }

        return -1;
    }

    static <T extends ItemHolder> int getPosition(List<T> itemHolders, T itemHolder) {
        return itemHolders.indexOf(itemHolder);
    }

    static <T extends ItemHolder> int getLastPosition(List<T> itemHolders, T itemHolder) {
        return itemHolders.lastIndexOf(itemHolder);
    }

    static <T extends ItemHolder> int getLastPosition(List<T> itemHolders, long id) {
        if (itemHolders == null || itemHolders.isEmpty()) {
            return -1;
        }

        int lastPosition = -1;
        for (int i = 0; i < itemHolders.size(); i++) {
            T itemHolder = itemHolders.get(i);
            if (id == itemHolder.getId(i)) {
                lastPosition = i;
            }
        }

        return lastPosition;
    }

    static <T extends ItemHolder> boolean contains(List<T> itemHolders, T itemHolder) {
        return itemHolders.contains(itemHolder);
    }

    static <T extends ItemHolder> boolean contains(List<T> itemHolders, long id) {
        for (int i = 0; i < itemHolders.size(); i++) {
            T itemHolder = itemHolders.get(i);
            if (itemHolder.getId(i) == id) {
                return true;
            }
        }

        return false;
    }

    static <T extends ItemHolder> int getCount(List<T> itemHolders) {
        if (itemHolders == null) {
            return 0;
        }

        return itemHolders.size();
    }

    static <T extends ItemHolder> boolean isEmpty(List<T> itemHolders) {
        if (itemHolders == null) {
            return true;
        }

        return itemHolders.isEmpty();
    }

    static <T extends ItemHolder> long getId(List<T> itemHolders, int position) {
        if (itemHolders == null || position >= itemHolders.size()) {
            return -1;
        }

        return itemHolders.get(position).getId(position);
    }

    public static <T extends ItemHolder> void open(ViewPager viewPager, IDynamicPagerAdapter<T> adapter, T itemHolder,
                                                   boolean smoothScroll) {
        int position = adapter.getPosition(itemHolder);
        if (position == -1) {
            position = adapter.add(itemHolder);
        }

        viewPager.setCurrentItem(position, smoothScroll);
    }

    public static <T extends ItemHolder> long close(ViewPager viewPager, IDynamicPagerAdapter<T> adapter, T itemHolder,
                                                    boolean smoothScroll) {
        if (!adapter.contains(itemHolder)) {
            return -1;
        }

        int position = adapter.remove(itemHolder);

        if (position == -1 || adapter.isEmpty()) {
            return -1;
        }

        if (position >= adapter.getCount()) {
            position = adapter.getCount() - 1;
        }

        viewPager.setCurrentItem(position, smoothScroll);

        return itemHolder.getId(position);
    }

    public static <T extends ItemHolder> long close(ViewPager viewPager, IDynamicPagerAdapter<T> adapter, long id,
                                                    boolean smoothScroll) {
        if (!adapter.contains(id)) {
            return -1;
        }

        int position = adapter.remove(id);

        if (position == -1 || adapter.isEmpty()) {
            return -1;
        }

        if (position >= adapter.getCount()) {
            position = adapter.getCount() - 1;
        }

        viewPager.setCurrentItem(position, smoothScroll);

        return id;
    }

    public static <T extends ItemHolder> long close(ViewPager viewPager, IDynamicPagerAdapter<T> adapter,
                                                    boolean smoothScroll) {
        if (adapter.isEmpty()) {
            return -1;
        }

        int position = adapter.getCount() - 1;
        T itemHolder = adapter.getItemHolders().get(position);
        long id = itemHolder.getId(position);

        close(viewPager, adapter, itemHolder, smoothScroll);

        return id;
    }

    public static <T extends ItemHolder> long close(FragmentManager fragmentManager, ViewPager viewPager, IDynamicPagerAdapter<T> adapter, T itemHolder,
                                                    boolean smoothScroll) {
        long id = close(viewPager, adapter, itemHolder, smoothScroll);
        if (id == -1) {
            return -1;
        }

        removeFragment(fragmentManager, viewPager, id);

        return id;
    }

    public static <T extends ItemHolder> long close(FragmentManager fragmentManager, ViewPager viewPager, IDynamicPagerAdapter<T> adapter, long id,
                                                    boolean smoothScroll) {
        long idClose = close(viewPager, adapter, id, smoothScroll);
        if (idClose == -1) {
            return -1;
        }

        removeFragment(fragmentManager, viewPager, idClose);

        return idClose;
    }

    public static <T extends ItemHolder> long close(FragmentManager fragmentManager, ViewPager viewPager, IDynamicPagerAdapter<T> adapter,
                                                    boolean smoothScroll) {
        long id = close(viewPager, adapter, smoothScroll);
        if (id == -1) {
            return -1;
        }

        removeFragment(fragmentManager, viewPager, id);

        return id;
    }

    public static void removeFragment(FragmentManager fragmentManager, ViewPager viewPager, long id) {
        Fragment fragment = fragmentManager.findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + id);
        fragmentManager.beginTransaction().remove(fragment).commit();
    }
}
