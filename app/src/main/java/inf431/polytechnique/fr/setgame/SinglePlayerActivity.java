package inf431.polytechnique.fr.setgame;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private TextView cardsLeftTextView;
    private TextView timeTextView;
    private long startTime;
    private Counter idCounter = new Counter(0);
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

        topLayout = (LinearLayout) findViewById(R.id.topLayout);
        middleLayout = (LinearLayout) findViewById(R.id.middleLayout);
        bottomLayout = (LinearLayout) findViewById(R.id.bottomLayout);
        cardsLeftTextView = (TextView) findViewById(R.id.cardsLeftTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);


        int i;

        for (i = 0; i < 3; i++)
        {
            myImageView newView  = new myImageView(getBaseContext());
            newView.setId(idCounter.get());
            idCounter.increment();
            topLayout.addView(newView);
            newView.setLayoutNumber(1);
            imageViews.add(newView);
        }
        for (i = 0; i < 3; i++)
        {
            myImageView newView  = new myImageView(getBaseContext());
            newView.setId(idCounter.get());
            idCounter.increment();
            middleLayout.addView(newView);
            newView.setLayoutNumber(2);
            imageViews.add(newView);
        }
        for (i = 0; i < 3; i++)
        {
            myImageView newView  = new myImageView(getBaseContext());
            newView.setId(idCounter.get());
            idCounter.increment();
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

        completeCards();
        updateCardsLeftTextView();


        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
        startTime = System.currentTimeMillis();

    }

    private void updateTextView()
    {
        long currentTime = System.currentTimeMillis();
        String time = "mm:ss";
        timeTextView.setText(DateFormat.format(time, currentTime-startTime));
    }

    private void removeCard(myImageView v)
    {
        imageViews.remove(v);
        switch (v.getLayoutNumber())
        {
            case 1:
                topLayout.removeView(v);
                topLayout.invalidate();
                break;
            case 2:
                middleLayout.removeView(v);
                middleLayout.invalidate();
                break;
            case 3:
                bottomLayout.removeView(v);
                bottomLayout.invalidate();
                break;
        }

    }

    private void addNewCard(int layoutNumber)
    {

        if (!CardList.isEmpty())
        {
            myImageView newView  = new myImageView(getBaseContext());
            switch(layoutNumber)
            {
                case 1:
                    topLayout.addView(newView);
                    break;
                case 2:
                    middleLayout.addView(newView);
                    break;
                case 3:
                    bottomLayout.addView(newView);
                    break;
            }

            imageViews.add(newView);
            newView.setLayoutNumber(layoutNumber);
            newView.setId(idCounter.get());
            idCounter.increment();
            int currentCard = CardList.pop();
            setImageView(newView.getId(), currentCard);
            newView.setCard(currentCard);
            configureImageView(newView);
        }
    }

    private void addThreeCards()
    {
        int a = topLayout.getChildCount();
        int b = middleLayout.getChildCount();
        int c = bottomLayout.getChildCount();

        int cardsByLine = (a+b+c+3)/3; //assuming it is an integer

        for (int i = a; i < cardsByLine; i++)
            addNewCard(1);
        for (int i = b; i < cardsByLine; i++)
            addNewCard(2);
        for (int i = c; i < cardsByLine; i++)
            addNewCard(3);

        updateCardsLeftTextView();
    }

    private void updateCardsLeftTextView()
    {
        cardsLeftTextView.setText("Cards left : " + Integer.toString(CardList.size()));

    }

    private void completeCards()
    {
        runOnUiThread (new Thread(new Runnable()
        {
            @Override
            public void run() {
                boolean res = false;
                int tabCards[] = new int[imageViews.size()];
                int index = 0;
                for (myImageView i : imageViews)
                {
                    tabCards[index] = i.getCard();
                    index++;
                }
                for(int i = 0; i<tabCards.length - 2; i++){
                    for(int j = i+1; j<tabCards.length -1; j++){
                        for(int k=j+1; k<tabCards.length; k++){
                            res = (Cards.isSet(tabCards[i],tabCards[j],tabCards[k])||res);
                        }
                    }
                }
                if(!res){
                    if (!CardList.isEmpty())
                    {
                        addThreeCards();
                        completeCards();
                    }
                    else
                    {
                        DialogFragment d = FinishDialog.newInstance(System.currentTimeMillis()-startTime);
                        d.show(getFragmentManager(), "Congratulations !");
                    }
                }

            }
        }));
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
                                    removeCard(currentSet[i]);
                                }
                                completeCards();
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
