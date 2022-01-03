package ru.meefik.busybox;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView versionView = findViewById(R.id.versionView);
        versionView.setText(getString(R.string.app_version,
                PrefStore.getVersion(getApplicationContext())));

        // enable context clickable
        TextView aboutView = findViewById(R.id.aboutTextView);
        aboutView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.title_activity_about);
    }
}
