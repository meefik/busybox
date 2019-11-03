package ru.meefik.busybox;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefStore.setLocale(this);
        setContentView(R.layout.activity_about);

        TextView versionView = findViewById(R.id.versionView);
        versionView.setText(getString(R.string.app_version,
                PrefStore.getVersion(getApplicationContext())));

        // enable context clickable
        TextView aboutView = findViewById(R.id.aboutTextView);
        aboutView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void setTheme(int resId) {
        super.setTheme(PrefStore.getTheme(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.title_activity_about);
    }
}
