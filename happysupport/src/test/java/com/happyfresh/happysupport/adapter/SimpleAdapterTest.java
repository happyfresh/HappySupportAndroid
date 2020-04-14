package com.happyfresh.happysupport.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LayoutInflater.class, RecyclerView.Adapter.class})
public class SimpleAdapterTest {

    @Mock
    private LayoutInflater layoutInflater;

    private SimpleAdapter adapter;

    @Before
    public void before() {
        mockStatic(LayoutInflater.class);
        when(LayoutInflater.from(any(Context.class))).thenReturn(layoutInflater);

        SimpleAdapter.newInstance();
        adapter = spy(SimpleAdapter.newInstance(true));
        doNothing().when(adapter).notifyDataSetChanged();
        doNothing().when(adapter).notifyItemInserted(anyInt());
        doNothing().when(adapter).notifyItemRangeInserted(anyInt(), anyInt());
        doNothing().when(adapter).notifyItemRemoved(anyInt());
        doNothing().when(adapter).notifyItemRangeRemoved(anyInt(), anyInt());
    }

    @Test
    public void addItems_test() {
        List<Object> objects = new ArrayList<>();
        objects.add(new Object());
        adapter.addItems(1, objects);

        for (int i = 0; i < adapter.getItemCount(); i++) {
            assertEquals(adapter.getItem(i), objects.get(i));
        }
        verify(adapter).notifyItemRangeInserted(0, 1);
    }

    @Test
    public void addItems_position_test() {
        List<Object> objects = new ArrayList<>();
        objects.add(new Object());
        objects.add(new Object());

        adapter.isAutoNotify = false;

        adapter.addItems(1, 0, objects);

        for (int i = 0; i < adapter.getItemCount(); i++) {
            assertEquals(adapter.getItem(i), objects.get(i));
        }
        verify(adapter, never()).notifyItemRangeInserted(0, 2);
    }

    @Test
    public void addItems_position_isAutoNotify_test() {
        List<Object> objects = new ArrayList<>();
        objects.add(new Object());
        objects.add(new Object());

        adapter.addItems(1, 0, objects);

        for (int i = 0; i < adapter.getItemCount(); i++) {
            assertEquals(adapter.getItem(i), objects.get(i));
        }
        verify(adapter).notifyItemRangeInserted(0, 2);
    }

    @Test
    public void addItem_test() {
        Object object = new Object();
        adapter.addItem(1, object);

        for (int i = 0; i < adapter.getItemCount(); i++) {
            assertEquals(adapter.getItem(i), object);
        }
        verify(adapter).notifyItemInserted(1);
    }

    @Test
    public void addItem_position_test() {
        Object object = new Object();
        adapter.addItem(1, 0, object);

        for (int i = 0; i < adapter.getItemCount(); i++) {
            assertEquals(adapter.getItem(i), object);
        }
        verify(adapter).notifyItemInserted(0);
    }

    @Test
    public void removeItem_test() {
        Object object = new Object();
        Object object2 = new Object();
        adapter.addItem(1, object);
        adapter.addItem(1, object2);

        adapter.isAutoNotify = true;

        adapter.removeItem(object2);

        assertEquals(adapter.getItem(0), object);
        verify(adapter).notifyItemRemoved(1);
    }

    @Test
    public void removeItems_test() {
        Object object = new Object();
        Object object2 = new Object();
        Object object3 = new Object();
        adapter.addItem(1, object);
        adapter.addItem(1, object2);

        adapter.isAutoNotify = true;

        List<Object> objects = new ArrayList<>();
        objects.add(object2);
        objects.add(object3);
        adapter.removeItems(objects);

        assertEquals(adapter.getItem(0), object);
        verify(adapter).notifyItemRemoved(1);
    }

    @Test
    public void removeItem_byPosition_test() {
        Object object = new Object();
        adapter.isAutoNotify = true;
        adapter.addItem(1, object);
        adapter.removeItem(0);

        assertTrue(adapter.items.isEmpty());
        verify(adapter).notifyItemRemoved(0);
    }

    @Test
    public void removeItem_byPosition_count_test() {
        adapter.addItem(1, new Object());
        adapter.addItem(1, new Object());
        adapter.addItem(1, new Object());
        adapter.isAutoNotify = true;
        adapter.removeItem(1, 2);

        assertEquals(1, adapter.items.size());
        verify(adapter, times(2)).notifyItemRemoved(1);
    }

    @Test
    public void removeItems_viewType_test() {
        Object object = new Object();
        adapter.addItem(1, new Object());
        adapter.addItem(2, object);
        adapter.addItem(1, new Object());
        adapter.isAutoNotify = true;

        adapter.removeItems(1);

        assertEquals(1, adapter.items.size());
        assertEquals(object, adapter.items.get(0).getItem());
        assertEquals(2, adapter.items.get(0).getViewType(), 0);
        verify(adapter).notifyDataSetChanged();
    }

    @Test
    public void removeAll_test() {
        Object object = new Object();
        Object object2 = new Object();
        adapter.addItem(1, object);
        adapter.addItem(1, object2);
        adapter.removeAll();

        assertTrue(adapter.items.isEmpty());
        verify(adapter).notifyItemRangeRemoved(0, 2);
    }

    @Test
    public void onCreateViewHolder_test() {
        View view = mock(View.class);
        ViewGroup viewGroup = mock(ViewGroup.class);
        Context context = mock(Context.class);
        adapter.viewHolderMaps = mock(SparseArray.class);

        doReturn(context).when(viewGroup).getContext();
        doReturn(view).when(layoutInflater).inflate(1, viewGroup, false);
        doReturn(SimpleAdapter.ViewHolder.class).when(adapter.viewHolderMaps).get(1);

        adapter.addViewHolder(1, SimpleAdapter.ViewHolder.class);

        assertTrue(adapter.onCreateViewHolder(viewGroup, 1) instanceof SimpleAdapter.ViewHolder);
    }

    @Test(expected = RuntimeException.class)
    public void onCreateViewHolder_exception_test() {
        View view = mock(View.class);
        ViewGroup viewGroup = mock(ViewGroup.class);
        Context context = mock(Context.class);

        doReturn(context).when(viewGroup).getContext();
        doReturn(view).when(layoutInflater).inflate(1, viewGroup, false);

        adapter.onCreateViewHolder(viewGroup, 1);
    }

    @Test
    public void onBindViewHolder_test() {
        Object object = new Object();
        adapter.addItem(1, object, 1);

        SimpleAdapter.ViewHolder viewHolder = mock(SimpleAdapter.ViewHolder.class);
        adapter.onBindViewHolder(viewHolder, 0);

        verify(viewHolder).onBind(object);
    }

    @Test
    public void onBindViewHolder_additional_test() {
        Object object = new Object();
        adapter.addItem(1, object, 1);

        AdditionalViewHolder viewHolder = mock(AdditionalViewHolder.class);
        adapter.onBindViewHolder(viewHolder, 0);

        verify(viewHolder).onBindAdditional(1);
        verify(viewHolder).onBind(object);
    }

    @Test
    public void getItemViewType_test() {
        Object object = new Object();
        adapter.addItem(1, object);

        assertEquals(adapter.getItemViewType(0), 1);
    }

    @Test
    public void onBind_test() {
        SimpleAdapter.ViewHolder viewHolder = new SimpleAdapter.ViewHolder(mock(View.class));
        viewHolder.onBind(null);

        assertNull(viewHolder.item);
    }

    @Test
    public void getItemCount_null_test() {
        adapter.items = null;
        assertEquals(adapter.getItemCount(), 0);
    }

    @Test
    public void setAdditionalItems_test() {
        Object object = new Object();
        Object object2 = new Object();

        adapter.addItem(1, object);

        adapter.setAdditionalItems(1, object2);

        assertEquals(object2, adapter.items.get(0).getAdditionalItems()[0]);
        verify(adapter).notifyDataSetChanged();
    }

    @Test
    public void item_hashCode_test() {
        Object object = new Object();
        SimpleAdapter.Item item = new SimpleAdapter.Item(1, object);

        assertEquals(object.hashCode(), item.hashCode());
    }

    @Test
    public void item_hashCode_null_test() {
        SimpleAdapter.Item item = new SimpleAdapter.Item(1, null);

        assertNotNull(item.hashCode());
    }

    @Test
    public void item_equals_object_test() {
        Object object = new Object();
        SimpleAdapter.Item item = new SimpleAdapter.Item(1, object);

        assertTrue(item.equals(object));
    }

    @Test
    public void item_equals_test() {
        Object object = new Object();
        SimpleAdapter.Item item = new SimpleAdapter.Item(1, object);

        assertTrue(item.equals(new SimpleAdapter.Item(1, object)));
    }

    @Test
    public void item_getAdditionalClass_null_test() {
        SimpleAdapter.Item item = new SimpleAdapter.Item(1, new Object());

        assertNull(item.getAdditionalItemClasses());
    }

    @Test
    public void item_addAdditionalItems_test() {
        SimpleAdapter.Item item = new SimpleAdapter.Item(1, new Object());
        item.setAdditionalItems(new Object[] {new Object()});

        Object object = new Object();
        item.addAdditionalItems(object);

        assertEquals(object, item.getAdditionalItems()[1]);

        Object object2 = new Object();
        item.addAdditionalItems(object2);

        assertEquals(object2, item.getAdditionalItems()[2]);
    }

    @Test
    public void viewHolder_getItemPosition_0_test() {
        Object object = new Object();
        Object object2 = new Object();
        Object object3 = new Object();

        adapter.addItem(1, object);
        adapter.addItem(2, object2);
        adapter.addItem(1, object3);

        SimpleAdapter.ViewHolder viewHolder = new SimpleAdapter.ViewHolder(mock(View.class));
        viewHolder.items = adapter.items;
        viewHolder.viewType = 1;
        viewHolder.item = adapter.items.get(0).getItem();

        assertEquals(0, viewHolder.getItemPosition());
    }

    @Test
    public void viewHolder_getItemPosition_1_test() {
        Object object = new Object();
        Object object2 = new Object();
        Object object3 = new Object();

        adapter.addItem(1, object);
        adapter.addItem(2, object2);
        adapter.addItem(1, object3);

        SimpleAdapter.ViewHolder viewHolder = new SimpleAdapter.ViewHolder(mock(View.class));
        viewHolder.items = adapter.items;
        viewHolder.viewType = 1;
        viewHolder.item = adapter.items.get(2).getItem();

        assertEquals(1, viewHolder.getItemPosition());
    }

    @Test
    public void viewHolder_getItemPosition_minus1_test() {
        Object object = new Object();
        Object object2 = new Object();
        Object object3 = new Object();

        adapter.addItem(1, object);
        adapter.addItem(2, object2);
        adapter.addItem(1, object3);

        SimpleAdapter.ViewHolder viewHolder = new SimpleAdapter.ViewHolder(mock(View.class));
        viewHolder.items = adapter.items;
        viewHolder.viewType = 1;
        viewHolder.item = new Object();

        assertEquals(-1, viewHolder.getItemPosition());
    }

    public static class WrongViewHolder extends SimpleAdapter.ViewHolder {

        public WrongViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class AdditionalViewHolder extends SimpleAdapter.ViewHolder {

        public AdditionalViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void onBindAdditional(Integer test) {

        }
    }
}
