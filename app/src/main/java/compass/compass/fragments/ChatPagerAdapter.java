package compass.compass.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by brucegatete on 7/26/17.
 */

public class ChatPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles [] = new String[] {"Chat Home", "Messages"};
    private Context context;
    public ChatHomeFragment chatHomeFragment;
    public  MessagesFragment messagesFragment;

    public ChatPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        chatHomeFragment = new ChatHomeFragment();
        messagesFragment = new MessagesFragment();


    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return chatHomeFragment;
        }else if (position == 1){
            return messagesFragment;
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
