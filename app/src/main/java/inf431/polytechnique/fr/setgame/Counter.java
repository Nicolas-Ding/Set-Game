package inf431.polytechnique.fr.setgame;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by montesson on 17/03/2016.
 */
class Counter {

    Lock lock = new ReentrantLock();
    private int val;

    public Counter(int val) {
        this.val = val;
    }

    public int get() {
        return val;
    }

    public void set(int val) {
        this.val = val;
    }

    public void increment()
    {
        lock.lock();
        try {
            val = val+1;
        } finally {
            lock.unlock();
        }
    }


    public void decrement()
    {
        lock.lock();
        try {
            val = val-1;
        } finally {
            lock.unlock();
        }
    }

    public void toNull()
    {
        lock.lock();
        try {
            val = 0;
        } finally {
            lock.unlock();
        }
    }

    //used to print value convinently
    public String toString(){
        return Integer.toString(val);
    }
}