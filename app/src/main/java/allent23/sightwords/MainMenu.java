package allent23.sightwords;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

public class MainMenu extends ActionBarActivity {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        MyPagerAdapter adapter = new MyPagerAdapter();

        //set up the view pager that allows users to swipe through options
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setPageTransformer(true, new DepthPageTransformer());
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(0);

        //play music
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        //set up enter button
        Button enter = (Button) findViewById(R.id.enter);
        enter.setOnClickListener(buttonListener);

        //load animation
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.buttonshake);
        enter.startAnimation(shake); //start shaking the enter button
    }

    private View.OnClickListener buttonListener;

    {
        buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button activities = (Button) v;

                switch (v.getId()) {
                    case R.id.enter:

                        switch (mPager.getCurrentItem()) //find which view page the app is on
                        {
                            case 0: //open input words
                                Intent inputwordspage = new Intent(MainMenu.this, InputWords.class);
                                startActivity(inputwordspage);
                                break;

                            case 1: //open flashcards
                                Intent flashcardpage = new Intent(MainMenu.this, FlashCards.class);
                                startActivity(flashcardpage);
                                break;

                            case 2: //open writing activity
                                Intent writing = new Intent(MainMenu.this, MainWritingBoard.class);
                                startActivity(writing);
                                break;

                            case 3: //open quiz
                                Intent quizpage = new Intent(MainMenu.this, Quiz.class);
                                startActivity(quizpage);
                                break;
                        }
                        mediaPlayer.stop();
                        break;
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
}



