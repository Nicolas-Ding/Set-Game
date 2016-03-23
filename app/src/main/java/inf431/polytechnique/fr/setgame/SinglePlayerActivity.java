package inf431.polytechnique.fr.setgame;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SinglePlayerActivity extends AppCompatActivity {
    private static final boolean AUTO_HIDE = false;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */

    private Stack<Integer> CardList;
    private LinearLayout topLayout;
    private LinearLayout middleLayout;
    private LinearLayout bottomLayout;
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private List<myImageView> imageViews = new ArrayList();
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        //mContentView = findViewById(R.id.fullscreen_content);


        try
        {
            initGame();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        // delayedHide(100);
    }

    public void initGame() throws InterruptedException
    {
        this.CardList = new Stack<>();
        Thread initializeCards = new Thread(new CardMixer(CardList,false));
        initializeCards.start();

        LinearLayout topLayout = (LinearLayout) findViewById(R.id.topLayout);
        LinearLayout middleLayout = (LinearLayout) findViewById(R.id.middleLayout);
        LinearLayout bottomLayout = (LinearLayout) findViewById(R.id.bottomLayout);


        int i;

        for (i = 0; i < 3; i++)
        {
            myImageView newView  = new myImageView(getBaseContext());
            newView.setId(i);
            topLayout.addView(newView);
            newView.setLayoutNumber(1);
            imageViews.add(newView);
        }
        for (i = 0; i < 3; i++)
        {
            myImageView newView  = new myImageView(getBaseContext());
            newView.setId(i+3);
            middleLayout.addView(newView);
            newView.setLayoutNumber(2);
            imageViews.add(newView);
        }
        for (i = 0; i < 3; i++)
        {
            myImageView newView  = new myImageView(getBaseContext());
            newView.setId(i+6);
            bottomLayout.addView(newView);
            newView.setLayoutNumber(3);
            imageViews.add(newView);
        }

        initializeCards.join();

        for (myImageView view : imageViews)
        {
            int currentCard = CardList.pop();
            setImageView(view.getId(), currentCard);
            view.setCard(currentCard);
            configureImageView(view);
        }

        removeCard(imageViews.get(4));

    }

    private void removeCard(myImageView v)
    {
        imageViews.remove(v);
        System.out.println("iii");
        switch (v.getLayoutNumber())
        {
            case 1:
                topLayout.removeView(v);
                topLayout.invalidate();
                break;
            case 2:
                middleLayout.removeView(v);
                middleLayout.invalidate();
                System.out.println("u");
                break;
            case 3:
                bottomLayout.removeView(v);
                bottomLayout.invalidate();
                break;
        }

    }

    private void addNewCard(LinearLayout layout, int layoutNumber)
    {
        myImageView newView  = new myImageView(getBaseContext());
        layout.addView(newView);
        imageViews.add(newView);
        newView.setLayoutNumber(layoutNumber);
        if (!CardList.isEmpty())
        {
            int currentCard = CardList.pop();
            setImageView(newView.getId(), currentCard);
            newView.setCard(currentCard);
            configureImageView(newView);
        }
    }

    private void addThreeCards()
    {
        addNewCard(topLayout,1);
        addNewCard(middleLayout,2);
        addNewCard(bottomLayout,3);
    }



    private void configureImageView(final myImageView imgView)
    {
        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                v.onTouchEvent(event);
                if (myImageView.selectedCardCounter.get() == 3)
                {
                    myImageView.selectedCardCounter.decrement();
                    myImageView.selectedCardCounter.decrement();
                    myImageView.selectedCardCounter.decrement();


                    int compteur = 0;
                    final myImageView[] currentSet = new myImageView[3];
                    for (myImageView view:imageViews)
                    {
                        if (view.getState() == myImageView.SELECTED && compteur < 3)
                        {
                            currentSet[compteur] = view;
                            currentSet[compteur].setState(myImageView.OCCUPIED);
                            compteur++;
                        }
                    }


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (Cards.isSet(currentSet[0].getCard(), currentSet[1].getCard(), currentSet[2].getCard()))
                            {
                                for (int i = 0; i < 3; i++)
                                {
                                    currentSet[i].changeBackground(myImageView.VALIDATED);
                                }
                            } else
                            {
                                for (int i = 0; i < 3; i++)
                                    currentSet[i].changeBackground(myImageView.INVALIDATED);
                            }
                        }

                    }, 400);

                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (Cards.isSet(currentSet[0].getCard(), currentSet[1].getCard(), currentSet[2].getCard()))
                            {

                                for (int i = 0; i < 3; i++)
                                {
                                    if (!CardList.isEmpty())
                                    {
                                        int currentCard = CardList.pop();
                                        setImageView(currentSet[i].getId(), currentCard);
                                        currentSet[i].setCard(currentCard);
                                    } else
                                    {
                                        setImageView(currentSet[i].getId(), 0);
                                        currentSet[i].setCard(0);
                                    }
                                }
                            }
                            for (int i = 0; i < 3; i++)
                            {
                                currentSet[i].changeBackground(myImageView.NEUTRAL);
                            }
                        }

                    }, 1000);
                }
                    return true;

                }


        });
    }

    protected void setImageView(int id, int card)
    {
        ImageView imageView = (ImageView) findViewById(id);
        imageView.setImageDrawable(new CardDrawable(card));
        imageView.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
