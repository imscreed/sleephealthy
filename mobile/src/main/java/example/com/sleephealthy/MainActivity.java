package example.com.sleephealthy;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener  {

    private GoogleApiClient mGoogleApiClient;
    private TextView resultview;
    private String resultstr;
    private int count = 0;

    NotificationCompat.Builder mBuilder;

    Button startButton;
    Button stopButton;

    File heartRateFile;
    File movementFile;
    FileOutputStream fOut;
    OutputStreamWriter myOutWriter;
    BufferedWriter myBufferedWriter;
    PrintWriter myPrintWriter;

    FileOutputStream fOut2;
    OutputStreamWriter myOutWriter2;
    BufferedWriter myBufferedWriter2;
    PrintWriter myPrintWriter2;

    boolean stopFlag = false;
    boolean startFlag = false;
//    boolean isFirstSet = true;

    private long stopTime;
    private long startTime;

    float sensordataArray[];
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultview = (TextView) findViewById(R.id.resulttxtview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addApi(Wearable.API).build();
        resultstr="";

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                // start recording the sensor data
                startTime = System.currentTimeMillis();
                try {
                    heartRateFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + System.currentTimeMillis() + "HEARTRATE.txt");
                    heartRateFile.createNewFile();
                    fOut = new FileOutputStream(heartRateFile);
                    myOutWriter = new OutputStreamWriter(fOut);
                    myBufferedWriter = new BufferedWriter(myOutWriter);
                    myPrintWriter = new PrintWriter(myBufferedWriter);

                    movementFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + System.currentTimeMillis() + "MOVEMENT.txt");
                    movementFile.createNewFile();
                    fOut2 = new FileOutputStream(movementFile);
                    myOutWriter2 = new OutputStreamWriter(fOut2);
                    myBufferedWriter2 = new BufferedWriter(myOutWriter2);
                    myPrintWriter2 = new PrintWriter(myBufferedWriter2);

                    Toast.makeText(getBaseContext(), "Started recording the data sets", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    startFlag = true;
                }
            }
        });

        // stop button
        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                progressBar.setVisibility(View.INVISIBLE);
                stopTime = System.currentTimeMillis();

                // stop recording the sensor data
                try {
                    stopFlag = true;
                    Toast.makeText(getBaseContext(), "Done recording the data sets", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Apnea episode detected")
                        .setContentText("Your heart rate is increasing abnormally " + getFormattedTime());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(this, QuestionActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause(){
        super.onPause();
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mGoogleApiClient.connect();

    }
    @Override
    public void onConnected(Bundle bundle){
        Log.d("TAG", "onConnected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }
    @Override
    public void onConnectionSuspended(int i){
        Log.d("TAG", "onConnectionSuspended");
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.e("TAG", "onConnectionFailed");
    }
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d("TAG", "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d("TAG", "DataItem changed: " + event.getDataItem().getUri());
                DataMap dataMap = DataMap.fromByteArray(event.getDataItem().getData());
                sensordataArray = dataMap.getFloatArray("sensordata");
                if(sensordataArray[4] > 90){
                    //start timer if needed before sending the notification
                    //Using count instead of timer for now
                    count++;
                    if(count > 22){
                        int mNotificationId = 001;
                        NotificationManager mNotifyMgr =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                        count = 0;
                    }
                }
                resultstr = "Detecting Movement: Using Accelerometer Sensor"
                        + "\nX:" + sensordataArray[0]
                        + "\nY:" + sensordataArray[1]
                        + "\nZ:" + sensordataArray[2]
                        + "\nAcc Smoothing Curve:" + sensordataArray[3]
                        + "\n\n" + "Detecting Heart Rate: Using Optical Heart Rate Sensor"
                        + "\nHeart Beat:" + sensordataArray[4];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultview.setText(resultstr);
                        if (startFlag) {
                                if (!stopFlag) {
                                    save();
                                } else {
                                    try {
                                        myPrintWriter.close();
                                        myBufferedWriter.close();
                                        myOutWriter.close();
                                        fOut.close();

                                        myPrintWriter2.close();
                                        myBufferedWriter2.close();
                                        myOutWriter2.close();
                                        fOut2.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }

                        }
                    }
                });

            }
        }
    }

    private void save() {
        Log.i("******Saving***", String.valueOf(sensordataArray[4]));
        myPrintWriter.write(sensordataArray[4] + "\n");

        Log.i("******Saving***",String.valueOf(sensordataArray[3]));
        myPrintWriter2.write(sensordataArray[3] + "\n");
    }
    private String getFormattedTime () {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm a");
        String timeOutput = dateFormat.format(cal1.getTime());
        Log.i("Current time",timeOutput);

        return timeOutput;
    }
}
