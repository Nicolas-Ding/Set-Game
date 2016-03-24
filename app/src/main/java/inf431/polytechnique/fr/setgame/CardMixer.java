package inf431.polytechnique.fr.setgame;

import java.util.Stack;


public class CardMixer implements Runnable {

    public Stack<Integer> cardsStack;
    boolean initialized = false;

    public CardMixer(Stack<Integer> cardsStack, boolean initialized)
    {
        this.cardsStack = cardsStack;
        this.initialized = initialized;
    }


    @Override
    public void run() {
        if (initialized==false)
        {
            this.initialise();
            initialized = true;
        }

        if(this.cardsStack.isEmpty()){
            System.out.print("Erreur : pas de cartes à mélanger");
            return;
        }
        int[] cardsTab = new int[this.cardsStack.size()];
        int i = 0;
        while(!this.cardsStack.isEmpty()){
            cardsTab[i] = this.cardsStack.pop();
            i++;
        }
        for(int k = 1; k<cardsTab.length;k++){
            int j = (int)(Math.random()*(k+1));
            swap(cardsTab, k ,j);
        }
        for(int k = 0; k<cardsTab.length;k++){
            this.cardsStack.push(cardsTab[k]);
        }

    }

    public void swap(int[] cardsTab, int i, int j){
        int x = cardsTab[i];
        cardsTab[i] = cardsTab[j];
        cardsTab[j] = x;
    }

    public void initialise()
    {

        for (int i = 1; i <= 3; i++)
        {
            for (int j = 1; j <= 3; j++)
            {
                for (int k = 1; k <= 3; k++)
                {
                    for (int l = 1; l <= 3; l++)
                    {
                        cardsStack.push(Cards.valueOf(i,j,k,l));
                    }
                }
            }
        }

    }

}