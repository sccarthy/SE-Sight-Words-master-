package allent23.sightwords;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.Spanned;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.view.ViewParent;
import android.widget.Button;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.Toast;

import com.paragon.open.dictionary.api.*;


public class InputWords extends ActionBarActivity {
    Context context = this; // set application context

    //create all needed lists and arrays
    List<String> list = new ArrayList<String>();
    ArrayList<EditText> edit_list = new ArrayList<EditText>();
    ArrayList<CheckBox> check_list = new ArrayList<CheckBox>();
    String[] temp;

    int bytecounter; //used for creating the file
    String FILENAME = "SightWords"; //the filename used

    ScrollView scrollView;
    Button back, apply, delete, add;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputwords);
        intent = new Intent(InputWords.this, MainMenu.class);

        //set all the buttons used
        back = (Button) findViewById(R.id.home);
        apply = (Button) findViewById(R.id.applywords);
        delete = (Button) findViewById(R.id.delete);
        add = (Button) findViewById(R.id.add);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        //set all the buttons to the button listener
        back.setOnClickListener(buttonListener);
        apply.setOnClickListener(buttonListener);
        delete.setOnClickListener(buttonListener);
        add.setOnClickListener(buttonListener);

        outputWords(); //enter words into edit texts if they exist

        filter(); //set filters for all the words
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

    private View.OnClickListener buttonListener;
    {
        buttonListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

                // Check which button was clicked
                switch (v.getId())
                {
                    case R.id.home: //pressing the home button
                        startActivity(intent);
                        break;

                    case R.id.applywords: //pressing the apply button
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        // set title
                        alertDialogBuilder.setTitle("Update");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Are you sure you want to update this list?")
                                .setCancelable(false)
                                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String FILENAME = "SightWords";
                                        try {
                                            // Store Serialized User Object in File
                                            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);

                                            String blank = " ";
                                            bytecounter = 0;

                                            //for each word entered, add it to the file
                                            for (EditText edittext : edit_list) {

                                                String string = edittext.getText().toString();

                                                try {
                                                    fos.write(string.getBytes());
                                                    fos.write(blank.getBytes());
                                                    bytecounter += (string.length() + 1);

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            fos.close(); //close the file


                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        //display message
                                        Toast.makeText(getApplicationContext(), "All words have been added",
                                                Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();

                        break;

                    case R.id.delete: //pressing the delete button

                        int size = edit_list.size(); //get the size of the list

                        //create the arrays needed for temp storage
                        CheckBox[] check_arr = new CheckBox[size];
                        EditText[] edit_arr = new EditText[size];

                        //push the list data into the arrays
                        check_list.toArray(check_arr);
                        edit_list.toArray(edit_arr);

                        //if the checkbox in the check list is checked, remove the parent table row
                        for (int i = 0; i < size; i++)
                        {
                            if (check_arr[i].isChecked())
                            {
                                EditText text = edit_arr[i];
                                View viewParent = (View) text.getParent();
                                viewParent.setVisibility(View.GONE);

                                edit_list.remove(text); //remove the text from the list
                            }
                        }

                        //rewrite to the file
                        try
                        {
                            // Store Serialized User Object in File
                            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);

                            String blank = " ";
                            bytecounter = 0;
                            for (EditText edittext : edit_list) {

                                String string = edittext.getText().toString();
                                try {
                                    fos.write(string.getBytes());
                                    fos.write(blank.getBytes());
                                    bytecounter += (string.length() + 1);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            fos.close();

                        }

                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }

                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplicationContext(), "All selected words have been deleted",
                                Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.add: //clicking on the add button
                        addEditText(layout); //call function to add a new field
                        filter(); //place a filter on the new field
                        //display the message
                        Toast.makeText(getApplicationContext(), "New field added!\nPlease enter a word", Toast.LENGTH_SHORT).show();
                        scrollView.fullScroll(View.FOCUS_DOWN); //scroll down
                        break;
                }
            }
        };
    }

    //Function reads the file and enter the word into the edit texts if they exist
    public void outputWords()
    {
        FileInputStream in = null;
        try
        {
            LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
            in = openFileInput(FILENAME); //open the file

            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;

            //add the lines of text to a string
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            int counter = 0;
            String text = sb.toString();
            temp = text.split(" "); //split the text on spaces


            //add the strings edit texts
            for (String x : temp)
            {
                if (!x.equals("") && x != null)
                    addEditText(layout);
            }

            //add the strings to the list
            for (EditText edittext : edit_list) {
                edittext.setText(temp[counter]);
                counter++;
            }


        } catch (FileNotFoundException e) {
        } catch (IOException ioe) {
            System.out.println("Exception while reading file " + ioe);
        } finally
        {
            // close the streams using close method
            try {
                if (in != null)
                {
                    in.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }

    }

    //function filter keeps certain characters from being passed into the edit texts
    public void filter() {
        for (EditText x : edit_list) {
            x.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence src, int start,
                                           int end, Spanned dst, int dstart, int dend) {

                    if (src.equals(""))  // for backspace
                    {
                        return src;
                    }

                    if (src.toString().matches("[a-zA-Z]*")) //constraints
                    {
                        return src;
                    }
                    return "";
                }
            };
            x.setFilters(filters);
        }
    }

    //function creates new Edit text fields
    public void addEditText(LinearLayout layout) {
        TableRow new_row = new TableRow(getApplicationContext());
        EditText new_edit = new EditText(getApplicationContext());
        CheckBox new_check = new CheckBox(getApplicationContext());

        new_row.setWeightSum(100);

        //set the parameters for all the new fields
        TableRow.LayoutParams rowprops = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);


        TableRow.LayoutParams checkprops = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        checkprops.weight = 90; //set the weight for the check box

        TableRow.LayoutParams editprops = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        editprops.weight = 10; //set the weight for the edit tet

        //pass the parameters in
        new_row.setLayoutParams(rowprops);
        new_edit.setLayoutParams(editprops);
        new_check.setLayoutParams(checkprops);

        new_row.setGravity(Gravity.CENTER);
        new_check.setGravity(Gravity.CENTER);
        new_edit.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        new_edit.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        //set colors
        new_edit.setBackgroundColor(Color.argb(188, 46, 46, 46));
        new_check.setBackgroundColor(Color.argb(188, 155, 239, 255));

        //set sizes and hint
        new_edit.setHint("Input Word");
        new_edit.setTextSize(30);

        //add each child view to each respective parent view
        layout.addView(new_row);
        new_row.addView(new_edit);
        new_row.addView(new_check);

        //add them to the lists
        edit_list.add(new_edit);
        check_list.add(new_check);
    }
}

