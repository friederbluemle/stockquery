package org.fbluemle.android.stockquery;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.preview.support.wearable.notifications.RemoteInput;
import android.preview.support.wearable.notifications.WearableNotifications;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {
    public static final String EXTRA_QUERY = "query";

    private static final String ACTION_QUERY = "org.fbluemle.android.stockquery.QUERY";

    private String mLastQuery;

    private BroadcastReceiver mReceiver;
    private StockQueryResponder mResponder;
    private TextView mLastQuoteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processQuery(intent);
            }
        };
        mResponder = new StockQueryResponder();
        mLastQuoteView = (TextView) findViewById(R.id.last_quote);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(ACTION_QUERY));
        if (mLastQuery == null) {
            mLastQuery = "";
            mLastQuoteView.setText(mLastQuery);
        }
        showNotification();
    }

    @Override
    protected void onPause() {
        NotificationManagerCompat.from(this).cancel(0);
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.quote))
                .setContentText(mLastQuery);

        Intent intent = new Intent(ACTION_QUERY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = new WearableNotifications.Builder(builder)
                .setMinPriority()
                .addRemoteInputForContentIntent(
                        new RemoteInput.Builder(EXTRA_QUERY)
                                .setLabel(getString(R.string.query)).build())
                .build();
        NotificationManagerCompat.from(this).notify(0, notification);
    }

    private void processQuery(Intent intent) {
        String input = intent.getStringExtra(EXTRA_QUERY);
        if (input != null && !input.equals("")) {
            mLastQuery = mResponder.query(input);
            mLastQuoteView.setText("\n" + input + "\n" + mLastQuery);
            showNotification();
        }
    }
}
