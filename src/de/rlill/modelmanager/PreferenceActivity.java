package de.rlill.modelmanager;

import android.app.Activity;
import android.os.Bundle;
import de.rlill.modelmanager.dialog.PreferenceDialog;

public class PreferenceActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenceDialog())
                .commit();
    }

}
