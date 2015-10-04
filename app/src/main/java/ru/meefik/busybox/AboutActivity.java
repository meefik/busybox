package ru.meefik.busybox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by anton on 19.09.15.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PrefStore.setLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView versionView = (TextView) findViewById(R.id.versionView);
        versionView.setText(getString(R.string.app_version,
                PrefStore.getVersion(getApplicationContext())));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        PrefStore.setLocale(this);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String url = null;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_donate:
                url = "http://meefik.github.io/donate";
                break;
            case R.id.menu_forum:
                //url = "http://4pda.ru/forum/index.php?showtopic=692805";
                break;
            case R.id.menu_doc:
                url = "https://github.com/meefik/busybox/wiki";
                break;
            case R.id.menu_issues:
                url = "https://github.com/meefik/busybox/issues";
                break;
            case R.id.menu_source:
                url = "https://github.com/meefik/busybox";
                break;
        }
        if (url != null) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTheme(int resid) {
        super.setTheme(PrefStore.getTheme(this));
    }

}
