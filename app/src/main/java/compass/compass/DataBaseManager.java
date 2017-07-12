package compass.compass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by brucegatete on 7/11/17.
 */

public class DataBaseManager extends SQLiteOpenHelper {
    public static final String DATA_BASE_NAME = "our_users.db";
    public static final String TABLE_NAME = "users_table";
    public static final String COL_1 = "First_name";
    public static final String COL_2 = "Last_name";
    public static final String COL_3 = "Year";

    public DataBaseManager(Context context) {
        super(context, DATA_BASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, FIRST_NAME TEXT, SECOND_NAME TEXT, YEAR INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);
    }
}
