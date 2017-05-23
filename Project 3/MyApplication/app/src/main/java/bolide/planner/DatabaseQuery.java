/*
-Project description.
Create a planner that has daily, weekly and monthly views. Be able to associate alerts and alarms to
dates. Be able to have a section for just notes. The notes and events should have text and audio
capture capabilities. When an event is due, a notification is displayed and if the event has a
voice message, an option to play the message is given.

-Preliminary development plan.
The idea was to have fragments for the views. We thought we wanted fragments for adding information,
but it turns out we wanted intents. We would have to figure out how to implement the calender and
audio aspects.

-Design and Architecture approach.
We used fragments for the daily, weekly, and monthly views. To capture the date/time, we used a
calender widget. We had databaseHelper classes for the notes and events. We had a viewNotes class.
A note class to store the information from the database (we put it in an arraylist for use).
There was an addNote class and an editNote activities to do as their names would suggest. We would
have addEvent and editEvent activities, but we were unable to complete our assignment.

-Discussion of Implementation.
SQLite was a big aspect of the databases. It is part of Android Studio and it helps create
databases. Other than that, everything else is pretty straight forward as far as navigation is
concerned. Getting from one activity to another involved starting intents. If we had a view that
we wanted to change, we would be manipulating the fragments by showing and hiding accordingly.
What we did for the audio was creating a new temp item to the database, get that last entry, take
that id and added 1 to it. Then the last entry (the one we just created) will be deleted so the
database is like normal, and we pass the id+1 to the next activity, which is the title of the
audio file. If it's for notes, we add an "n" right after to let it be known that the audio file is
for notes. For editNote, we just have an arraylist with all the entries taken from the database and
pass that information to the editNote activity with the intent. Each editNote entry is a button
that has the appropriate information to see its contents.

-Discussion of most prominent issues encountered during development.
Other than not finishing the project, when implementing a database, there were a few hoops
that had to be jumped to get verify information. For example, Android Studio takes away root
privileges from api 24 and upward. That means I had to emulate the project in api 23. Simple enough.
I was able to verify my audio files and sql files. The problem arose when apply this to the phone.
This worked fine on the emulator and when tried with the phone, it would crash. It would say it
couldn't find the file and throw a null pointer error. I kept looking online for help, but no one
had a solution. Solutions were pointing to external storage and rooting the phone (and other things
that didn't work), neither which I had or wanted to do. I started following where the Android
Monitor was pointing to that was the problem, but it was pointing to the read. Everything was in
order and worked on the emulator so that wasn't the issue. I started to mess with anything that
looked like it could be changed and in the constructor for the databaseHelper, I changed the
super(context, DATABASE_NAME, null, 1); to
super(context, DATABASE_NAME, null, 2); After I changed that 1 to a 2, it worked perfectly fine.
The documentation says that is the version parameter, but I don't exactly know what it means. I just
know it works. I had finally found this after trying to change things in the manifesto and gradle.
For the gradle, I was messing with targetAPI and adding snippets, but that didn't work.

-Testing approach and testing data.
We had to make sure the data was actually being written and saved so that is why being able to
access these files were very important. When we could verify the information, we could move forward.
Otherwise it was typical debugging for the code. Implement something new, get output, if correct,
move forward.

-Suggestions of future improvements.
Actually finishing the project. I really liked the layout and thought the different views and
activities looked sleek.

 */
package bolide.planner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseQuery extends SQLiteOpenHelper{
    private SQLiteDatabase db;
    public static final String DATABASE_NAME = "events.db";
    public static final String TABLE_NAME = "events";
    public static final String COL_0 = "_id";    //Audio files will be related to ID
    public static final String COL_1 = "message";
    public static final String COL_2 = "reminder";
    public static final String COL_3 = "end";

    public DatabaseQuery(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (`_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `message` TEXT NOT NULL, `reminder` TEXT NOT NULL, `end` TEXT");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_0,1);
        contentValues.put(COL_1, "test");
        contentValues.put(COL_2, "test");
        contentValues.put(COL_3, "test");
        db.insert(TABLE_NAME, null, contentValues);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public List<EventObjects> getAllFutureEvents(){
        Date dateToday = new Date();
        List<EventObjects> events = new ArrayList<>();
        /**Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));
                //convert start date to date object
                Date reminderDate = convertStringToDate(startDate);
                if(reminderDate.after(dateToday) || reminderDate.equals(dateToday)){
                    events.add(new EventObjects(id, message, reminderDate));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();**/
        return events;
    }
    private Date convertStringToDate(String dateInString){
        DateFormat format = new SimpleDateFormat("d-MM-yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}