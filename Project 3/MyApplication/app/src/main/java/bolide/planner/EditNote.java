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

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class EditNote extends AppCompatActivity {
    EditText editTextTitle,editTextBody;
    Button btn_Save, btn_Delete, btn_Record, btn_Play, btn_StopPlayback;
    DatabaseHelperNotes dbNotes;
    String id;

    MediaPlayer mPlayer = new MediaPlayer();
    MediaRecorder recorder = new MediaRecorder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Bundle extras = getIntent().getExtras();

        dbNotes = new DatabaseHelperNotes(this);
        editTextTitle = (EditText)findViewById(R.id.editTextTitle);
        editTextBody = (EditText)findViewById(R.id.editTextBody);
        btn_Save = (Button)findViewById(R.id.btn_Save);
        btn_Delete = (Button)findViewById(R.id.btn_Delete);
        btn_Record = (Button)findViewById(R.id.btn_Record);
        btn_Play = (Button)findViewById(R.id.btn_Play);
        btn_StopPlayback = (Button) findViewById(R.id.btn_StopPlayback);

        editTextTitle.setText(extras.getString("title"));
        editTextBody.setText(extras.getString("body"));
        id = extras.getString("id");
        btn_Save();
        btn_Delete();

        //WIP >>
        btn_Play.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mPlayer.reset();
                        try {
                            mPlayer.setDataSource(getFilesDir()+"/"+id+"n.m4a");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            mPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mPlayer.start();
                    }
                }
        );

        btn_StopPlayback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPlayer.stop();
                mPlayer.reset();
                return false;
            }
        });

        btn_Record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    String fileName = getFilesDir()+"/"+id+"n.m4a";
                    File myFile = new File(fileName);
                    if(myFile.exists())
                        myFile.delete();
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    recorder.setOutputFile(getFilesDir()+"/"+id+"n.m4a");
                    try {
                        recorder.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    recorder.start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    recorder.reset();
                    recorder.release();
                }
                return false;
            }
        });

        //WIP <<
    }



    private void btn_Delete() {
        btn_Delete.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        dbNotes.deleteData(getIntent().getExtras().getString("id"));
                        String fileName = getFilesDir()+"/"+id+"n.m4a";
                        File myFile = new File(fileName);
                        if(myFile.exists())
                            myFile.delete();
                        finish();
                    }
                }
        );
    }

    private void btn_Save() {
        btn_Save.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        boolean isUpdated = dbNotes.updateData(getIntent().getExtras().getString("id"), editTextTitle.getText().toString(),
                                editTextBody.getText().toString());
                        if(isUpdated == true){
                            Toast.makeText(EditNote.this,"Data Saved", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(EditNote.this,"Data not Saved", Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }
                }
        );
    }
}
