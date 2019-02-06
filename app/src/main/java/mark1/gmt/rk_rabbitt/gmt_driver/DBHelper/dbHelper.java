package mark1.gmt.rk_rabbitt.gmt_driver.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

//import androidx.annotation.Nullable;

public class dbHelper extends SQLiteOpenHelper {

    public static String DATABASE = "your_job.db";
    public static String TABLE = "jobs";
    public static String timeat = "timeat";
    public static String bookid = "bookid";
    public static String travel_type = "travel_type";
    public static String v_type = "v_type";
    public static String ori = "ori";
    public static String dest = "dest";
    public static String package_id= "package_id";
    String br = "";

    public dbHelper(Context context) {
        super(context, DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        br = "CREATE TABLE " + TABLE + "(" + bookid + " text, " + timeat + " Text, " + travel_type + " Text, " + v_type + " Text, " + ori + " Text, " + dest + " Text);";
        db.execSQL(br);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + " ;");
    }

    public void insertdata(String bookid1, String timeat1, String travel_type1, String v_type1, String ori1, String dest1, String package_id1) {
        System.out.print("Hello " + br);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(bookid, bookid1);
        contentValues.put(timeat, timeat1);
        contentValues.put(travel_type, travel_type1);
        contentValues.put(v_type, v_type1);
        contentValues.put(ori, ori1);
        contentValues.put(dest, dest1);
        contentValues.put(package_id, package_id1);
        Log.i("logtaginsert",contentValues.get(bookid).toString());
        db.insert(TABLE, null, contentValues);
    }

    public List<recycleAdapter> getdata() {
        // DataModel dataModel = new DataModel();
        List<recycleAdapter> data = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String getride = "select * from "+TABLE;
        Log.i("logtag","query"+getride  );

        Cursor cursor = db.rawQuery(getride, null);
        StringBuilder stringBuffer = new StringBuilder();
        recycleAdapter dataModel = null;
        Log.i("logtag","outside while");
        Log.i("logtag",cursor.toString());
        Log.i("logtag",String.valueOf(cursor.getCount()));


        if (cursor.moveToFirst())
        { Log.i("logtag","inside do");
             do {
                dataModel = new recycleAdapter();
                 Log.i("logtag","outside while");
                String bookid = cursor.getString(cursor.getColumnIndexOrThrow("bookid"));
                String travel_type = cursor.getString(cursor.getColumnIndexOrThrow("travel_type"));
                String v_type = cursor.getString(cursor.getColumnIndexOrThrow("v_type"));
                String timeat = cursor.getString(cursor.getColumnIndexOrThrow("timeat"));
                String dest = cursor.getString(cursor.getColumnIndexOrThrow("dest"));
                String ori = cursor.getString(cursor.getColumnIndexOrThrow("ori"));
                String package_id = cursor.getString(cursor.getColumnIndexOrThrow("package_id"));

                Log.i("logtag",bookid);
                dataModel.setBook_id(bookid);
                dataModel.setOrigin(ori);
                dataModel.setDestination(dest);
                dataModel.setTimeat(timeat);
                dataModel.setTravel_type(travel_type);
                dataModel.setV_type(v_type);
                dataModel.setPackage_id(package_id);

                stringBuffer.append(dataModel);
                // stringBuffer.append(dataModel);
                data.add(dataModel);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();


        for (recycleAdapter mo : data) {

            Log.i("bookID", "" + mo.getBook_id());
        }

        //

        return data;
    }

}
