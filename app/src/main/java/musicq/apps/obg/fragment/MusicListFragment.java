package musicq.apps.obg.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import musicq.apps.obg.adapter.ListPagerAdapter;
import musicq.apps.obg.R;
import musicq.apps.obg.adapter.MusicAdapter;
import musicq.apps.obg.domain.MusicVO;

public class MusicListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);
        ListPagerAdapter mListPagerAdapter = new ListPagerAdapter(getChildFragmentManager());
        ViewPager mViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        mViewPager.setAdapter(mListPagerAdapter);

        TabLayout mTab = (TabLayout) rootView.findViewById(R.id.tablayout);
        mTab.setupWithViewPager(mViewPager);

        return rootView;
    }

    public void startMusic(int i, ArrayList<MusicVO> list) {
        Log.d("Music", "" + i);
        ((PlayingMusicFragment) getFragmentManager().findFragmentById(R.id.container)).clickStartMusic(i, list);
    }
}
