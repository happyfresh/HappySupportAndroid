package com.happyfresh.happysupport.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public List<Item> items = new ArrayList<>();

    protected SparseArray<Class> viewHolderMaps = new SparseArray<>();

    protected boolean isAutoNotify = false;

    public SimpleAdapter() {

    }

    public SimpleAdapter(boolean isAutoNotify) {
        this.isAutoNotify = isAutoNotify;
    }

    public static SimpleAdapter newInstance() {
        return new SimpleAdapter();
    }

    public static SimpleAdapter newInstance(boolean isAutoNotify) {
        return new SimpleAdapter(isAutoNotify);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        try {
            Class clazz = viewHolderMaps.get(viewType);
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);
            Constructor constructor = clazz.getConstructor(View.class);
            return (ViewHolder) constructor.newInstance(view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Item item = items.get(position);

        viewHolder.items = items;
        viewHolder.viewType = item.viewType;

        if (item.hasAdditionalItems()) {
            try {
                Method method = viewHolder.getClass().getDeclaredMethod("onBindAdditional", item.getAdditionalItemClasses());
                method.invoke(viewHolder, item.getAdditionalItems());
            } catch (Exception e) {
                // Do nothing.
            }
        }

        viewHolder.onBind(item.getItem());
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    public void addViewHolder(@LayoutRes int viewType, Class<? extends ViewHolder> viewHolder) {
        viewHolderMaps.put(viewType, viewHolder);
    }

    public void addItems(@LayoutRes int viewType, List items, Object... additionalItems) {
        int positionStart = getItemCount();
        for (Object item : items) {
            this.items.add(new Item<>(viewType, item, additionalItems));
        }

        if (isAutoNotify) {
            notifyItemRangeInserted(positionStart, items.size());
        }
    }

    public void addItems(@LayoutRes int viewType, int positionStart, List items, Object... additionalItems) {
        int i = positionStart;
        for (Object item : items) {
            this.items.add(i++, new Item<>(viewType, item, additionalItems));
        }

        if (isAutoNotify) {
            notifyItemRangeInserted(positionStart, items.size());
        }
    }

    public void addItem(@LayoutRes int viewType, Object item, Object... additionalItems) {
        this.items.add(new Item<>(viewType, item, additionalItems));

        if (isAutoNotify) {
            notifyItemInserted(getItemCount());
        }
    }

    public void addItem(@LayoutRes int viewType, Integer position, Object item, Object... additionalItems) {
        this.items.add(position, new Item<>(viewType, item, additionalItems));

        if (isAutoNotify) {
            notifyItemInserted(position);
        }
    }

    // Make sure items already added before
    public void setAdditionalItems(@LayoutRes int viewType, Object... additionalItems) {
        for (Item item : items) {
            if (item.getViewType() == viewType) {
                item.setAdditionalItems(additionalItems);
            }
        }
        if (isAutoNotify) {
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position) {
        this.items.remove(position);
        if (isAutoNotify) {
            notifyItemRemoved(position);
        }
    }

    public void removeItem(int fromPosition, int count) {
        while (count > 0) {
            count--;
            removeItem(fromPosition);
        }
    }

    public void removeItem(Object item) {
        for (int i = 0; i < this.items.size(); i++) {
            if (this.items.get(i).getItem().equals(item)) {
                removeItem(i);
                break;
            }
        }
    }

    public void removeItems(List items) {
        for (Object item : items) {
            removeItem(item);
        }
    }

    public void removeItems(@LayoutRes int viewType) {
        List<Item> itemsToRemove = new ArrayList<>();
        for (Item item : items) {
            if (item.getViewType() == viewType) {
                itemsToRemove.add(item);
            }
        }

        for (Item item : itemsToRemove) {
            items.remove(item);
        }

        if (isAutoNotify) {
            notifyDataSetChanged();
        }
    }

    public void removeAll() {
        int itemCount = getItemCount();
        this.items.clear();

        if (isAutoNotify) {
            notifyItemRangeRemoved(0, itemCount);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getItem(int position) {
        return (T) items.get(position).getItem();
    }

    public static class ViewHolder<T> extends RecyclerView.ViewHolder {

        protected T item;

        int viewType;

        List<Item> items;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void onBind(T item) {
            this.item = item;
        }

        // This is different from adapter position. itemPosition are position the item by viewType.
        // It should be use after onBind
        public int getItemPosition() {
            int position = -1;
            for (Item i : items) {
                if (i.viewType != viewType) {
                    continue;
                }

                position++;
                if (i.item.equals(this.item)) {
                    return position;
                }
            }

            return -1;
        }
    }

    public static class Item<T> {

        private int viewType;

        private T item;

        private Object[] additionalItems;

        public Item(int viewType, T item) {
            this.viewType = viewType;
            this.item = item;
        }

        public Item(int viewType, T item, Object... additionalItems) {
            this(viewType, item);
            this.additionalItems = additionalItems;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof Item) {
                return ((Item) obj).item.equals(this.item);
            }
            return item.equals(obj);
        }

        @Override
        public int hashCode() {
            return item == null ? super.hashCode() : item.hashCode();
        }

        public T getItem() {
            return item;
        }

        public int getViewType() {
            return viewType;
        }

        public void setAdditionalItems(Object[] additionalItems) {
            this.additionalItems = additionalItems;
        }

        public void addAdditionalItems(Object additionalItem) {
            Object[] objects = new Object[this.additionalItems.length + 1];
            for (int i = 0; i < this.additionalItems.length; i++) {
                objects[i] = this.additionalItems[i];
            }
            objects[this.additionalItems.length] = additionalItem;
            this.additionalItems = objects;
        }

        public Object[] getAdditionalItems() {
            return additionalItems;
        }

        public Class[] getAdditionalItemClasses() {
            if (!hasAdditionalItems()) {
                return null;
            }

            Class[] classes = new Class[additionalItems.length];

            for (int i = 0; i < additionalItems.length; i++) {
                classes[i] = additionalItems[i].getClass();
            }

            return classes;
        }

        public boolean hasAdditionalItems() {
            return additionalItems != null && additionalItems.length > 0;
        }
    }
}
