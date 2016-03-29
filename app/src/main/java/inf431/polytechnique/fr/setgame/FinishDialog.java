package inf431.polytechnique.fr.setgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.format.DateFormat;

public class FinishDialog extends DialogFragment
{

    public static FinishDialog newInstance(long time) {
        FinishDialog frag = new FinishDialog();
        Bundle args = new Bundle();
        args.putLong("time", time);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        long time = getArguments().getLong("time");
        String timeFormat = "mm:ss";
        String text = (String) DateFormat.format(timeFormat, time);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You finished the game in : "+text+" ! ").setTitle("Congratulations !");
        // Create the AlertDialog object and return it
        return builder.create();
    }

}