package compass.compass;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import compass.compass.models.User;

/**
 * Created by icamargo on 7/18/17.
 */

public class SaveSharedPreference
{
    static final String PREF_USER_NAME= "username";
    static final User CURRENT_USER_PROFILE = null;

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

//    public static void setCurrentProfile(Context ctx, User userName)
//    {
//        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
//        editor.put
//        editor.commit();
//    }

}
