package compass.compass.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by brucegatete on 8/3/17.
 */

public class ResourcePagerAdapter extends FragmentPagerAdapter{

    private String tabTitles [] = new String[] {"Near You", "School Help"};
    private Context context;
    public ResourceSchoolFragment resourceSchoolFragment;
    public  ResourceLocationFragment resourceLocationFragment;

    public ResourcePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        resourceSchoolFragment = new ResourceSchoolFragment();
        resourceLocationFragment= new ResourceLocationFragment();
    }


    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return resourceLocationFragment;
        }else if (position == 1){
            return resourceSchoolFragment;
        }else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        //generate title based on position
        return tabTitles [position];
    }

}
