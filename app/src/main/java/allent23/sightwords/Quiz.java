package allent23.sightwords;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Locale;
import java.util.Random;

import static android.content.Context.*;
import static android.view.View.OnClickListener;

public class Quiz extends ActionBarActivity
{
    Context context = this; //get application context
    AlertDialog.Builder alertDialogBuilder;
    String FILENAME = "SightWords"; //file name
    String alphabet = "abcdefghijklmnopqrstuvwxyz"; //alphabet to pick random letter
    String right_answer;
    String wrong_answer;

    //sizes, and counts
    int arraySize = 0;
    int count = 0;
    int backcount = 0;

    Random rand = new Random(); //random for picking a random
    StringBuilder builder = new StringBuilder();

    //All arrays and lists needed
    String[] arr;
    String[] temp = new String[arraySize];
    String[] question_arr;
    List<String> wordArr = new ArrayList<>();

    //declare animation to use
    Animation question_spin, jump_forward, wrong_shake, button_shake, slide_in, slide_out;

    LinearLayout lin_layout;
    Button option1_play, option2_play, question_play;
    TextToSpeech ttobj;

    MediaPlayer mediaPlayer;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizlayout);
        intent = new Intent(Quiz.this, MainMenu.class);

        ttobj = new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            ttobj.setLanguage(Locale.US);
                        }
                    }
                });

        alertDialogBuilder = new AlertDialog.Builder(context);

        //set id's needed
        Button home = (Button) findViewById(R.id.home);
        Button next = (Button) findViewById(R.id.next);
        Button back = (Button) findViewById(R.id.back);
        RadioButton option1 = (RadioButton) findViewById(R.id.option1);
        RadioButton option2 = (RadioButton) findViewById(R.id.option2);
        lin_layout = (LinearLayout) findViewById(R.id.lin_layout);
        option1_play = (Button) findViewById(R.id.option1_play);
        option2_play = (Button) findViewById(R.id.option2_play);
        question_play = (Button) findViewById(R.id.question_playboi);

        TextView question = (TextView) findViewById(R.id.question);

        //set listeners
        home.setOnClickListener(buttonListener);
        next.setOnClickListener(buttonListener);
        back.setOnClickListener(buttonListener);
        question_play.setOnClickListener(buttonListener);
        option1_play.setOnClickListener(buttonListener);
        option2_play.setOnClickListener(buttonListener);
        option1.setOnClickListener(radioListener);
        option2.setOnClickListener(radioListener);

        //set animations
        question_spin = AnimationUtils.loadAnimation(this, R.anim.question_full_shake);
        wrong_shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        button_shake = AnimationUtils.loadAnimation(this, R.anim.buttonshake);
        jump_forward = AnimationUtils.loadAnimation(this, R.anim.jumper_forward);
        slide_in = AnimationUtils.loadAnimation(this, R.anim.cardflip_left_in);
        slide_out = AnimationUtils.loadAnimation(this, R.anim.cardflip_left_out);

        //start button shakes
        next.startAnimation(button_shake);
        back.startAnimation(button_shake);

        mediaPlayer = MediaPlayer.create(this, R.raw.blop);

        insertWords(); //insert the words if they are there

        //initialize arrays
        question_arr = new String[arraySize];
        arr = new String[arraySize];

        //if its empty, then pop a dialog up
        if (arraySize == 0)
        {
            alertDialogBuilder.setTitle("Uh oh");

            // set dialog message
            alertDialogBuilder
                    .setMessage("No words have been inputted :(")
                    .setCancelable(false)
                    .setNegativeButton("Return Home", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            startActivity(intent);
                        }
                    })
                    .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            Intent inputwords = new Intent(Quiz.this, InputWords.class);
                            startActivity(inputwords);
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
        else //otherwise start first question
        {
            Collections.shuffle(wordArr);
            wordArr.toArray(arr);
            nextQuestion(question, option1, option2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //listener for the buttons
    private OnClickListener buttonListener;
    {
        buttonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Button activities = (Button) v;
                TextView question = (TextView) findViewById(R.id.question);
                RadioButton option1 = (RadioButton) findViewById(R.id.option1);
                RadioButton option2 = (RadioButton) findViewById(R.id.option2);


                switch (v.getId()) {
                    case R.id.next: //if the next is hit
                        mediaPlayer.start();
                        option1.setChecked(false);
                        option2.setChecked(false);

                        //if the user count has caught back up, start the options again
                        if (backcount <= count  || count >= arraySize)
                        {
                            option1.setVisibility(View.VISIBLE);
                            option2.setVisibility(View.VISIBLE);
                            lin_layout.setVisibility(View.VISIBLE);

                            question.setText("");
                            lin_layout.setAnimation(slide_in);
                            question.startAnimation(slide_in);
                            question.setText("");
                            question_play.startAnimation(slide_in);

                            nextQuestion(question, option1, option2);
                        }
                        else //otherwise hide the options and move along
                        {
                            question.setText("");
                            question.startAnimation(slide_in);
                            question.setText("");
                            question.setText(question_arr[count]);
                            lin_layout.setVisibility(View.INVISIBLE);

                            count++;
                        }
                        break;

                    case R.id.back: //if the back button is clicked
                        mediaPlayer.start();
                        if (count > 1) //if the question isnt the first one
                        {
                            count = count - 2;

                            if(backcount < count )
                                backcount = count;

                            lin_layout.setVisibility(View.INVISIBLE);

                            question.setText("");
                            question.startAnimation(slide_out);
                            question_play.startAnimation(slide_out);

                            option1.setChecked(false);
                            option2.setChecked(false);

                            question.setText(question_arr[count]);
                        }

                        break;

                    case R.id.home: //if the home is click
                        mediaPlayer.start();
                        startActivity(intent);
                        break;

                    case R.id.question_playboi: //if the play button is clicked
                        speakText(arr[count-1]);
                        break;
                    case R.id.option1_play: //if the play button is clicked
                        speakText(option1.getText().toString());
                        break;
                    case R.id.option2_play: //if the play button is clicked
                        speakText(option2.getText().toString());
                        break;
                }
            }
        };
    }

    //if one of the radio options are selected
    private OnClickListener radioListener;

    {
        radioListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();

                //set the ids
                RadioButton option1 = (RadioButton) findViewById(R.id.option1);
                RadioButton option2 = (RadioButton) findViewById(R.id.option2);
                TextView question = (TextView) findViewById(R.id.question);

                //set booleans for the checks
                boolean check_1, check_2;
                mediaPlayer.start();

                // Check which radio button was clicked
                switch (v.getId()) {
                    case R.id.option1: //of the option1 is clicked

                        //set the other option false
                        option1.setChecked(true);
                        option2.setChecked(false);

                        //set the boolean
                        check_1 = option1.isChecked();

                        //if the check is true and is correct
                        if ((check_1 == true) && (option1.getText() == right_answer))
                        {
                            option1.setText("");
                            option2.setText("");

                            question.startAnimation(slide_in);
                            question_play.startAnimation(slide_in);
                            lin_layout.startAnimation(slide_in);

                            check_1 = false; //reset

                            nextQuestion(question, option1, option2); //next question
                        }

                        //if the check is true and is wrong
                        if ((check_1 == true) && (option1.getText() == wrong_answer))
                        {
                            question.startAnimation(wrong_shake);
                            option1.setTextColor(Color.RED);
                            option2.setTextColor(Color.BLACK);
                        }
                        break;

                    case R.id.option2: //if the option2 is clicked

                        //set the other option false
                        option1.setChecked(false);
                        option2.setChecked(true);

                        //set the boolean
                        check_2 = option2.isChecked();

                        //if the check is true and is correct
                        if ((check_2 == true) && (option2.getText() == right_answer)) {

                            option1.setChecked(false);
                            option2.setChecked(false);

                            option1.setText("");
                            option2.setText("");

                            question.startAnimation(slide_in);
                            question_play.startAnimation(slide_in);
                            lin_layout.startAnimation(slide_in);

                            check_2 = false;

                            nextQuestion(question, option1, option2); //move to the next
                        }

                        //if the check is true and is wrong
                        if ((check_2 == true) && (option2.getText() == wrong_answer)) {
                            question.startAnimation(wrong_shake);
                            option2.setTextColor(Color.RED);
                            option1.setTextColor(Color.BLACK);
                        }
                        break;
                }
            }
        };
    }

    //function nextQuestion move the app to the next word
    void nextQuestion(final TextView question, final RadioButton option1, final RadioButton option2) {
        alertDialogBuilder = new AlertDialog.Builder(context);

        //store right and wrong in arrays for back button
        option1.setChecked(false);
        option2.setChecked(false);

        //reset the colors
        option1.setTextColor(Color.BLACK);
        option2.setTextColor(Color.BLACK);

        //set the ids for the tablerows
        final TableRow tb1 = (TableRow) findViewById(R.id.tb1);
        final TableRow tb2 = (TableRow) findViewById(R.id.tb2);

        //if the the array isnt finished
        if (count < (arraySize))
        {
            String temp = arr[count];
            question_arr[count] = temp; //set a temp

            //get a random character for an option
            int random_char = rand.nextInt(temp.length());

            char random_letter = temp.charAt(random_char);
            right_answer = Character.toString(random_letter);

            //pull a letter from the alphabet
            char wrong_letter = alphabet.charAt(rand.nextInt(alphabet.length()));

            //check if the wrong letter picked is different from the right one
            while (wrong_letter == random_letter)
                wrong_letter = alphabet.charAt(rand.nextInt(alphabet.length()));

            wrong_answer = Character.toString(wrong_letter);

            //pull the letter out and recreate the word
            for (int i = 0; i < temp.length(); i++)
            {
                if (temp.charAt(i) == random_letter)
                    builder.append("_");

                else
                    builder.append(temp.charAt(i));
            }

            String modified_word = builder.toString();
            question.setText(modified_word); //display the word

            builder.delete(0, temp.length());

            //set the options randomly
            if (rand.nextInt(2) == 1)
            {
                option1.setText(right_answer);
                option2.setText(wrong_answer);
            } else
            {
                option1.setText(wrong_answer);
                option2.setText(right_answer);
            }
            count++; //update the count
        }
        else //otherwise you finished
        {

            tb1.setVisibility(View.INVISIBLE);
            tb2.setVisibility(View.INVISIBLE);

            alertDialogBuilder.setTitle("Good Job! You Did It!");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Play Again?")
                    .setCancelable(false).setInverseBackgroundForced(true)
                    .setNegativeButton("Yes, Please!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //reset the activity
                            question.setText("All Done! Good Job!");
                            Collections.shuffle(wordArr);
                            wordArr.toArray(arr);

                            option1.setText("");
                            option2.setText("");

                            count = 0;
                            backcount = 0;

                            nextQuestion(question, option1, option2); //start again
                            dialog.cancel();
                            tb1.setVisibility(View.VISIBLE);
                            tb2.setVisibility(View.VISIBLE);

                        }
                    })
                    .setPositiveButton("No, Thank you!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            startActivity(intent); //leave

                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }

    //function insertWords reads in the words from the file
    public void insertWords() {

        FileInputStream in = null;
        try
        {
            in = openFileInput(FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null)
            {
                sb.append(line);
            }

            String text = sb.toString();
            temp = text.split(" "); //split on the spaces in the string

            int blah = 0;
            for (String x : temp)
            {
                if (x != "")
                {
                    wordArr.add(x);
                    blah++;
                }
            }
            arraySize = blah;

            Collections.shuffle(wordArr); //shuffle the words

        } catch (FileNotFoundException e) {}
        catch (IOException ioe)
        {
            System.out.println("Exception while reading file " + ioe);
        }
        finally
        {
            // close the streams using close method
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }
    }
    @Override
    public void onPause()
    {
        if(ttobj !=null){
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onPause();
    }

    //used to speak the words
    public void speakText(String toSpeak)
    {
        Intent intent = new Intent();
        Bundle bundle = intent.getExtras();
        //has a red underline on .speak below, but works fine.
        ttobj.speak(toSpeak, TextToSpeech.QUEUE_ADD, bundle, null);

    }
}

