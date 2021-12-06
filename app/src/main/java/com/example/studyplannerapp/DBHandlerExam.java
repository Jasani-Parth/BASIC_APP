package com.example.studyplannerapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DBHandlerExam extends SQLiteOpenHelper {

    public DBHandlerExam(Context context) {
        super(context,"DataBaseExamEvent",null,1) ;
    }

    public DBHandlerExam(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create = "CREATE TABLE myexamevent (ID INTEGER PRIMARY KEY, date TEXT, time TEXT, Subject TEXT, examDuration INTEGER, Description TEXT)";
        sqLiteDatabase.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String drop = String.valueOf("DROP TABLE IF EXISTS");
        sqLiteDatabase.execSQL(drop, new String[]{"myexamevent"});
        onCreate(sqLiteDatabase);
    }

    public void addExamEvent(EventExam eventExam) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("date", eventExam.getDate());
        values.put("time", eventExam.getTime());
        values.put("Subject", eventExam.getSubject());
        values.put("examDuration", eventExam.getExamDuration());
        values.put("Description", eventExam.getDescription());

        long k = sqLiteDatabase.insert("myexamevent", null, values);
        Log.d("mytag", Long.toString(k));
        sqLiteDatabase.close();
    }

    public void getExamEvent(int ID) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("myexamevent", new String[]{"ID", "Subject", "examDuration", "Description", "date", "time"}, "ID=?", new String[]{String.valueOf(ID)}, null, null, null);

        if ( cursor != null && cursor.moveToFirst()) {
            Log.d("mytag",cursor.getString(1)) ;
            Log.d("mytag",cursor.getString(2)) ;
            Log.d("mytag",cursor.getString(3)) ;
            Log.d("mytag",cursor.getString(4)) ;
            Log.d("mytag",cursor.getString(5)) ;
        }
        else {
            Log.d("mytag","Error!!") ;
        }
    }

    public List<EventExam> getAllEvents(){
        List<EventExam> examList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //Generate the query to read from the database
        String select = "SELECT * FROM " + "myexamevent";
        Cursor cursor = db.rawQuery(select,null);

        //Loop through now
        if(cursor.moveToFirst()) {
            do {
                EventExam obj = new EventExam(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),cursor.getString(3),Integer.parseInt(cursor.getString(4)), cursor.getString(5));
                examList.add(obj);
            } while (cursor.moveToNext());
        }
        return examList;
    }

    public int getCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorEvents = db.rawQuery("SELECT * FROM " + "myexamevent", null);
        return cursorEvents.getCount();
    }

    public int getAllEventFilterByDate(String Date) {
        int Count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorEvents = db.rawQuery("SELECT * FROM " + "myexamevent", null);

        if (cursorEvents.moveToFirst()) {
            do {
                if (cursorEvents.getString(1).equals(Date)) {
                    Count++ ;
                }
            } while (cursorEvents.moveToNext()) ;
        }
        return Count ;
    }

    public List<Long> getAllDates(DBHandlerExam dbHandlerExam) {
        List<EventExam> studyList = dbHandlerExam.getAllEvents() ;
        List<Long> dates = new ArrayList<>() ;

        for ( EventExam obj : studyList ) {
            String[] D = obj.getDate().split("  ",3) ;
            int day  = Integer.parseInt(D[1]) ;
            int year = Integer.parseInt(D[2]) ;
            int month = getmonthIndex(D[0]) ;

            dates.add(getEpochNumber(year,month,day)) ;
        }
        return dates ;
    }

    public Long getEpochNumber(int year , int month , int day ) {
        long num = 0 ;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String dateString = Integer.toString(day)+"-"+Integer.toString(month+1)+"-"+Integer.toString(year)+" 11:18:32" ;

        try{
            Date date = sdf.parse(dateString) ;
            Calendar calendar = Calendar.getInstance() ;
            assert date != null;
            calendar.setTime(date);
            num = calendar.getTimeInMillis() ;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return num;
    }

    public int getmonthIndex(String month) {

        if (month.equals("JAN")) return 0;       if (month.equals("FEB")) return 1;
        if (month.equals("MAR")) return 2;       if (month.equals("APR")) return 3;
        if (month.equals("MAY")) return 4;       if (month.equals("JUNE")) return 5 ;
        if (month.equals("JULY")) return  6;      if (month.equals("AUG")) return 7;
        if (month.equals("SEPT")) return  8;      if (month.equals("OCT")) return 9;
        if (month.equals("NOV")) return 10;      if (month.equals("DEC")) return 11;

        return 0 ;
    }


    public void deleteEvent(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("myexamevent","ID=?",new String[]{String.valueOf(id)});
        db.close();
    }
}