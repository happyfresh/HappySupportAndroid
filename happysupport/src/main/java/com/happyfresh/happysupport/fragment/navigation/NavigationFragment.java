package com.happyfresh.happysupport.fragment.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happyfresh.happysupport.R;
import com.happyfresh.happysupport.adapter.pager.DynamicFragmentPagerAdapter;
import com.happyfresh.happysupport.adapter.pager.DynamicPagerUtil;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class NavigationFragment extends Fragment {

    private int offScreenPageLimit = -1;

    private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            onBackPressed();
        }
    };

    public static void navigate(AppCompatActivity activity, @IdRes int idRes,
                                DynamicFragmentPagerAdapter.FragmentHolder fragmentHolder) {
        NavigationFragment navigationFragment = (NavigationFragment) activity.getSupportFragmentManager()
                .findFragmentById(idRes);
        navigationFragment.navigate(fragmentHolder);
    }

    public static void navigate(Fragment fragment, DynamicFragmentPagerAdapter.FragmentHolder fragmentHolder) {
        NavigationFragment navigationFragment = (NavigationFragment) fragment.getParentFragment();
        navigationFragment.navigate(fragmentHolder);
    }

    public static boolean onBackPressed(AppCompatActivity activity, @IdRes int idRes) {
        NavigationFragment navigationFragment = (NavigationFragment) activity.getSupportFragmentManager()
                .findFragmentById(idRes);
        return navigationFragment.onBackPressed();
    }

    public static void setOffsetPageLimit(AppCompatActivity activity, @IdRes int idRes, int offScreenPageLimit) {
        NavigationFragment navigationFragment = (NavigationFragment) activity.getSupportFragmentManager()
                .findFragmentById(idRes);
        navigationFragment.setOffscreenPageLimit(offScreenPageLimit);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DynamicFragmentPagerAdapter adapter = new DynamicFragmentPagerAdapter(getChildFragmentManager(),
                                                                              FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleOnBackPressedCallback();
    }

    @Override
    public void onPause() {
        super.onPause();
        onBackPressedCallback.setEnabled(false);
    }

    public void navigate(DynamicFragmentPagerAdapter.FragmentHolder fragmentHolder) {
        DynamicPagerUtil.open(getViewPager(), getAdapter(), fragmentHolder, true);
        getViewPager().setOffscreenPageLimit(getOffScreenPageLimit());
        toggleOnBackPressedCallback();
    }

    public boolean onBackPressed() {
        if (getAdapter().getCount() == 1) {
            return false;
        }

        DynamicPagerUtil.close(getViewPager(), getAdapter(), true);
        getViewPager().setOffscreenPageLimit(getOffScreenPageLimit());
        toggleOnBackPressedCallback();

        return true;
    }

    private void setOffscreenPageLimit(int offScreenPageLimit) {
        this.offScreenPageLimit = offScreenPageLimit;
    }

    private ViewPager getViewPager() {
        return getView().findViewById(R.id.view_pager);
    }

    private DynamicFragmentPagerAdapter getAdapter() {
        return (DynamicFragmentPagerAdapter) getViewPager().getAdapter();
    }

    private int getOffScreenPageLimit() {
        int offScreenPageLimit = this.offScreenPageLimit;

        if (offScreenPageLimit == -1) {
            offScreenPageLimit = getAdapter().getCount() - 1;
        }

        if (offScreenPageLimit < 2) {
            offScreenPageLimit = 2;
        }

        return offScreenPageLimit;
    }

    private void toggleOnBackPressedCallback() {
        onBackPressedCallback.setEnabled(getAdapter().getCount() > 1);
    }
}
