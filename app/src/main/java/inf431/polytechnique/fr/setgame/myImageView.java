package inf431.polytechnique.fr.setgame;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class myImageView extends ImageView {


    public static final int NEUTRAL = 0;
    public static final int SELECTED = 1;
    public static final int VALIDATED = 2;
    public static final int INVALIDATED = 3;
    public static final int OCCUPIED = 4;

    public static boolean locked = false;
    public static Lock lock = new ReentrantLock();

    static Counter selectedCardCounter = new Counter(0);
    int state = NEUTRAL;
    private Rect rect;
    private int card;
    private int layoutNumber; //1 = top, 2 = middle, 3 = bot

    public myImageView(Context context)
    {
        super(context);
        this.setAttributes();
    }

    public myImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.setAttributes();
    }

    public myImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.setAttributes();
    }

    private void setAttributes()
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1.0f);
        int myMargin = getResources().getDimensionPixelSize(R.dimen.my_margin);
        params.setMargins(myMargin, myMargin, myMargin, myMargin);
        this.setLayoutParams(params);
        this.setBackgroundResource(R.drawable.round_card);
        this.setClickable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

            super.onTouchEvent(event);
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
            {
                rect = new Rect(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
                this.invalidate();
            }
            if (event.getActionMasked() == MotionEvent.ACTION_UP)
            {
                if (rect != null)
                {
                    if (rect.contains(this.getLeft() + (int) event.getX(), this.getTop() + (int) event.getY()))
                    {
                        // User moved outside bounds
                        if (this.getState() == myImageView.NEUTRAL)
                        {
                            this.changeBackground(myImageView.SELECTED);
                            selectedCardCounter.increment();
                        } else if (this.getState() == myImageView.SELECTED)
                        {
                            this.changeBackground(myImageView.NEUTRAL);
                            selectedCardCounter.decrement();
                        }

                    }
                }
            }




        return true;
    }

    public void changeBackground(int newBackground)
    {
        switch (newBackground)
        {
            case myImageView.NEUTRAL:
                this.setBackgroundResource(R.drawable.round_card);
                this.setState(myImageView.NEUTRAL);
                break;

            case myImageView.SELECTED:
                this.setBackgroundResource(R.drawable.round_card_selected);
                this.setState(myImageView.SELECTED);
                break;

            case myImageView.VALIDATED:
                this.setBackgroundResource(R.drawable.round_card_validated);
                this.setState(myImageView.VALIDATED);
                break;

            case myImageView.INVALIDATED:
                this.setBackgroundResource(R.drawable.round_card_invalidated);
                this.setState(myImageView.INVALIDATED);
                break;
        }
        this.invalidate();
    }

    public int getCard() {return this.card;}

    public void setCard(int newCard) {this.card = newCard;}

    public int getState()
    {
        return this.state;
    }

    public void setState(int newState) {this.state = newState; }

    public int getLayoutNumber()
    {
        return layoutNumber;
    }

    public void setLayoutNumber(int layoutNumber)
    {
        this.layoutNumber = layoutNumber;
    }
}
