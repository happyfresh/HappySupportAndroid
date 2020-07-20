package com.happyfresh.happysupport.adapter.pager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class DynamicPagerUtilTest {

    @Mock
    private IDynamicPagerAdapter<ItemHolder> adapter;

    @Mock
    private ViewPager viewPager;

    @Mock
    private ItemHolder itemHolder;

    @Mock
    private FragmentManager fragmentManager;

    @Mock
    private Fragment fragment;

    @Mock
    private FragmentTransaction fragmentTransaction;

    private int viewPagerId = 1;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        doReturn(viewPagerId).when(viewPager).getId();
        doReturn(fragment).when(fragmentManager).findFragmentByTag(anyString());
        doReturn(fragmentTransaction).when(fragmentManager).beginTransaction();
        doReturn(fragmentTransaction).when(fragmentTransaction).remove(fragment);
        doReturn(1).when(fragmentTransaction).commit();
    }

    @Test
    public void add_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();

        assertEquals(0, DynamicPagerUtil.add(adapter, itemHolders, itemHolder), 0);
        assertTrue(itemHolders.contains(itemHolder));
        verify(adapter).notifyDataSetChanged();
    }

    @Test
    public void remove_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        assertEquals(0, DynamicPagerUtil.remove(adapter, itemHolders, itemHolder), 0);
        assertFalse(itemHolders.contains(itemHolder));
        verify(adapter).notifyDataSetChanged();
    }

    @Test
    public void remove_id_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        doReturn(1L).when(itemHolder).getId(0);

        assertEquals(0, DynamicPagerUtil.remove(adapter, itemHolders, 1), 0);
        assertFalse(itemHolders.contains(itemHolder));
        verify(adapter).notifyDataSetChanged();
    }

    @Test
    public void remove_id_notFound_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        doReturn(1L).when(itemHolder).getId(0);

        assertEquals(-1, DynamicPagerUtil.remove(adapter, itemHolders, 2), 0);
        assertTrue(itemHolders.contains(itemHolder));
        verify(adapter, never()).notifyDataSetChanged();
    }

    @Test
    public void getPosition_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        assertEquals(0, DynamicPagerUtil.getPosition(itemHolders, itemHolder), 0);
    }

    @Test
    public void getPosition_id_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        doReturn(1L).when(itemHolder).getId(0);

        assertEquals(0, DynamicPagerUtil.getPosition(itemHolders, 1), 0);
    }

    @Test
    public void getPosition_id_notFound_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        doReturn(1L).when(itemHolder).getId(0);

        assertEquals(-1, DynamicPagerUtil.getPosition(itemHolders, 2), 0);
    }

    @Test
    public void getPosition_id_empty_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();

        assertEquals(-1, DynamicPagerUtil.getPosition(itemHolders, 2), 0);
    }

    @Test
    public void getLastPosition_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);
        itemHolders.add(itemHolder);

        assertEquals(1, DynamicPagerUtil.getLastPosition(itemHolders, itemHolder), 0);
    }

    @Test
    public void getLastPosition_id_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);
        itemHolders.add(itemHolder);

        doReturn(1L).when(itemHolder).getId(anyInt());

        assertEquals(1, DynamicPagerUtil.getLastPosition(itemHolders, 1), 0);
    }

    @Test
    public void getLastPosition_id_notFound_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);
        itemHolders.add(itemHolder);

        doReturn(1L).when(itemHolder).getId(anyInt());

        assertEquals(-1, DynamicPagerUtil.getLastPosition(itemHolders, 2), 0);
    }

    @Test
    public void getLastPosition_id_emtpy_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();

        assertEquals(-1, DynamicPagerUtil.getLastPosition(itemHolders, 2), 0);
    }

    @Test
    public void contains_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        assertTrue(DynamicPagerUtil.contains(itemHolders, itemHolder));
    }

    @Test
    public void contains_id_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        doReturn(1L).when(itemHolder).getId(0);

        assertTrue(DynamicPagerUtil.contains(itemHolders, 1));
    }

    @Test
    public void contains_id_notFound_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        doReturn(1L).when(itemHolder).getId(0);

        assertFalse(DynamicPagerUtil.contains(itemHolders, 2));
    }

    @Test
    public void getCount_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        assertEquals(1, DynamicPagerUtil.getCount(itemHolders));
    }

    @Test
    public void getCount_null_test() {
        assertEquals(0, DynamicPagerUtil.getCount(null));
    }

    @Test
    public void isEmpty_test() {
        assertTrue(DynamicPagerUtil.isEmpty(new ArrayList<ItemHolder>()));
    }

    @Test
    public void isEmpty_false_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        assertFalse(DynamicPagerUtil.isEmpty(itemHolders));
    }

    @Test
    public void isEmpty_null_test() {
        assertTrue(DynamicPagerUtil.isEmpty(null));
    }

    @Test
    public void getId_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        doReturn(1L).when(itemHolder).getId(0);

        assertEquals(1, DynamicPagerUtil.getId(itemHolders, 0));
    }

    @Test
    public void getId_notFound_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);

        doReturn(1L).when(itemHolder).getId(0);

        assertEquals(-1, DynamicPagerUtil.getId(itemHolders, 1));
    }

    @Test
    public void open_test() {
        doReturn(-1).when(adapter).getPosition(itemHolder);
        doReturn(0).when(adapter).add(itemHolder);

        DynamicPagerUtil.open(viewPager, adapter, itemHolder, true);

        verify(adapter).add(itemHolder);
        verify(viewPager).setCurrentItem(0, true);
    }

    @Test
    public void close_test() {
        doReturn(true).when(adapter).contains(itemHolder);
        doReturn(2).when(adapter).remove(itemHolder);
        doReturn(2).when(adapter).getCount();

        DynamicPagerUtil.close(fragmentManager, viewPager, adapter, itemHolder, true);

        verify(adapter).remove(itemHolder);
        verify(viewPager).setCurrentItem(1, true);
        verify(fragmentTransaction).remove(fragment);
    }

    @Test
    public void close_notContains_test() {
        doReturn(false).when(adapter).contains(itemHolder);

        DynamicPagerUtil.close(fragmentManager, viewPager, adapter, itemHolder, true);

        verify(adapter, never()).remove(itemHolder);
        verify(fragmentTransaction, never()).remove(fragment);
    }

    @Test
    public void close_isEmpty_test() {
        doReturn(true).when(adapter).contains(itemHolder);
        doReturn(0).when(adapter).remove(itemHolder);
        doReturn(true).when(adapter).isEmpty();

        DynamicPagerUtil.close(fragmentManager, viewPager, adapter, itemHolder, true);

        verify(adapter).remove(itemHolder);
        verify(viewPager, never()).setCurrentItem(0, true);
        verify(fragmentTransaction, never()).remove(fragment);
    }

    @Test
    public void close_id_test() {
        doReturn(true).when(adapter).contains(1);
        doReturn(2).when(adapter).remove(1);
        doReturn(2).when(adapter).getCount();
        doReturn(1L).when(itemHolder).getId(0);

        DynamicPagerUtil.close(fragmentManager, viewPager, adapter, 1, true);

        verify(adapter).remove(1);
        verify(viewPager).setCurrentItem(1, true);
        verify(fragmentTransaction).remove(fragment);
    }

    @Test
    public void close_id_notContains_test() {
        doReturn(false).when(adapter).contains(1);

        DynamicPagerUtil.close(fragmentManager, viewPager, adapter, 1, true);

        verify(adapter, never()).remove(itemHolder);
        verify(fragmentTransaction, never()).remove(fragment);
    }

    @Test
    public void close_id_isEmpty_test() {
        doReturn(true).when(adapter).contains(1);
        doReturn(0).when(adapter).remove(itemHolder);
        doReturn(true).when(adapter).isEmpty();
        doReturn(1L).when(itemHolder).getId(0);

        DynamicPagerUtil.close(fragmentManager, viewPager, adapter, 1, true);

        verify(adapter).remove(1);
        verify(viewPager, never()).setCurrentItem(0, true);
        verify(fragmentTransaction, never()).remove(fragment);
    }

    @Test
    public void close_last_test() {
        List<ItemHolder> itemHolders = new ArrayList<>();
        itemHolders.add(itemHolder);
        doReturn(false).when(adapter).isEmpty();
        doReturn(true).when(adapter).contains(itemHolder);
        doReturn(0).when(adapter).remove(itemHolder);
        doReturn(1).when(adapter).getCount();
        doReturn(itemHolders).when(adapter).getItemHolders();

        DynamicPagerUtil.close(fragmentManager, viewPager, adapter, true);

        verify(adapter).remove(itemHolder);
        verify(viewPager).setCurrentItem(0, true);
        verify(fragmentTransaction).remove(fragment);
    }

    @Test
    public void close_last_isEmpty_test() {
        doReturn(true).when(adapter).isEmpty();

        DynamicPagerUtil.close(fragmentManager, viewPager, adapter, true);

        verify(adapter, never()).remove(itemHolder);
        verify(viewPager, never()).setCurrentItem(0, true);
        verify(fragmentTransaction, never()).remove(fragment);
    }
}
