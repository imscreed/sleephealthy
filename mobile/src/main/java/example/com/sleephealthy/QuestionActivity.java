package example.com.sleephealthy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button submitButton = (Button) findViewById(R.id.button1);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build an AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this);

                // String array for alert dialog multi choice items
                String[] factors = new String[]{
                        "Irritability",
                        "Feeling depressed",
                        "Mood Swings",
                        "Difficulty in Learning",
                        "Lack of concentration",
                        "Feeling Sleepy"
                };

                // Boolean array for initial selected items
                final boolean[] checkedFactors = new boolean[]{
                        false,
                        false,
                        false,
                        true,
                        false,
                        false

                };

                // Convert the factor array to list
                final List<String> factorsList = Arrays.asList(factors);


                builder.setMultiChoiceItems(factors, checkedFactors, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        // Update the current focused item's checked status
                        checkedFactors[which] = isChecked;

                        // Get the current focused item
                        String currentItem = factorsList.get(which);

                        // Notify the current action
                        Toast.makeText(getApplicationContext(),
                                currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();
                    }
                });

                // Specify the dialog is not cancelable
                builder.setCancelable(false);

                // Set a title for alert dialog
                builder.setTitle("Your preferred factors?");

                // Set the positive/yes button click listener
                builder.setPositiveButton("Get Result", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int count=0;
                        String resultYes = "Based on the answers you have given, you might HAVE Sleep Apnea. " +
                                "Please track your sleep to get a better insight and consult a doctor soon as possible";
                        String resultNo = "Based on the answers you have given, you might NOT HAVE Sleep Apnea. Please track your sleep to get a better insight.";
                        for (int i = 0; i < checkedFactors.length; i++) {
                            boolean checked = checkedFactors[i];
                            if (checked) {
                                count++;
                            }
                        }

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(QuestionActivity.this);
                        builder2.setMessage(count>2 ? resultYes : resultNo)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // FIRE ZE MISSILES!
                                    }
                                });
//                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        // User cancelled the dialog
//                                    }
//                                });
                        // Create the AlertDialog object and return it
                        AlertDialog dialog2 = builder2.create();
                        dialog2.show();
                        // Do something when click positive button
//                        tv.setText("Your preferred factors..... \n");
//                        for (int i = 0; i < checkedColors.length; i++) {
//                            boolean checked = checkedColors[i];
//                            if (checked) {
//                                tv.setText(tv.getText() + colorsList.get(i) + "\n");
//                            }
//                        }
                    }
                });

//                // Set the negative/no button click listener
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Do something when click the negative button
//                    }
//                });

//                // Set the neutral/cancel button click listener
//                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Do something when click the neutral button
//                    }
//                });

                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

}
