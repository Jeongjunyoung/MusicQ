package musicq.apps.obg.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import musicq.apps.obg.fragment.AlbumFragment;
import musicq.apps.obg.fragment.PlayListFragment;
import musicq.apps.obg.fragment.SongFragment;

public class ListPagerAdapter extends FragmentPagerAdapter {
    private static int PAGE_NUMBER = 3;
    public ListPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PlayListFragment.newInstance();
            case 1:
                return SongFragment.newInstance();
            case 2:
                return AlbumFragment.newInstance();
        }
        return null;
    }


    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "플레이 리스트";
            case 1:
                return "곡";
            case 2:
                return "앨범";
            case 3:
                return null;
        }
        return super.getPageTitle(position);
    }
}
