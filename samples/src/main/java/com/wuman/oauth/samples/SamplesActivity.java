
package com.wuman.oauth.samples;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wuman.oauth.samples.ui.HtmlLicensesActivity;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SamplesActivity extends ListActivity {

    public static final String KEY_AUTH_MODE = "auth_mode";

    private SharedPreferences mPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreference = getSharedPreferences("Preference", 0);

        Intent intent = getIntent();
        String path = intent.getStringExtra(getApplicationInfo().packageName
                + ".Path");

        if (path == null) {
            path = "";
        }

        setListAdapter(new SimpleAdapter(this, getData(path),
                android.R.layout.simple_list_item_1, new String[]{
                "title"
        },
                new int[]{
                        android.R.id.text1
                }));
        getListView().setTextFilterEnabled(true);
    }

    protected List<Map<String, Object>> getData(String prefix) {
        List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(getApplicationInfo().packageName + ".EXAMPLE");

        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

        if (null == list)
            return myData;

        String[] prefixPath;
        String prefixWithSlash = prefix;

        if (prefix.equals("")) {
            prefixPath = null;
        } else {
            prefixPath = prefix.split("/");
            prefixWithSlash = prefix + "/";
        }

        int len = list.size();

        Map<String, Boolean> entries = new HashMap<String, Boolean>();

        for (int i = 0; i < len; i++) {
            ResolveInfo info = list.get(i);
            CharSequence labelSeq = info.loadLabel(pm);
            String label = labelSeq != null ? labelSeq.toString()
                    : info.activityInfo.name;

            if (prefixWithSlash.length() == 0
                    || label.startsWith(prefixWithSlash)) {

                String[] labelPath = label.split("/");

                String nextLabel = prefixPath == null ? labelPath[0]
                        : labelPath[prefixPath.length];

                if ((prefixPath != null ? prefixPath.length : 0) == labelPath.length - 1) {
                    addItem(myData,
                            nextLabel,
                            activityIntent(
                                    info.activityInfo.applicationInfo.packageName,
                                    info.activityInfo.name));
                } else {
                    if (entries.get(nextLabel) == null) {
                        addItem(myData, nextLabel,
                                browseIntent(prefix.equals("") ? nextLabel
                                        : prefix + "/" + nextLabel));
                        entries.put(nextLabel, true);
                    }
                }
            }
        }

        Collections.sort(myData, sDisplayNameComparator);

        return myData;
    }

    private final static Comparator<Map<String, Object>> sDisplayNameComparator = new Comparator<Map<String, Object>>() {
        private final Collator collator = Collator.getInstance();

        public int compare(Map<String, Object> map1, Map<String, Object> map2) {
            String class1 = ((Intent) map1.get("intent")).getComponent().getClassName();
            String class2 = ((Intent) map2.get("intent")).getComponent().getClassName();
            boolean contains1 = class1.contains("linkedin") || class1.contains("foursquare");
            boolean contains2 = class2.contains("linkedin") || class2.contains("foursquare");
            if (contains1 && !contains2) {
                return -1;
            } else if (!contains1 && contains2) {
                return 1;
            }
            return collator.compare(map1.get("title"), map2.get("title"));
        }
    };

    protected Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }

    protected Intent browseIntent(String path) {
        Intent result = new Intent();
        result.setClass(this, SamplesActivity.class);
        result.putExtra("com.example.android.apis.Path", path);
        return result;
    }

    protected void addItem(List<Map<String, Object>> data, String name,
                           Intent intent) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", name);
        temp.put("intent", intent);
        data.add(temp);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map<String, Object> map = (Map<String, Object>) l
                .getItemAtPosition(position);
        Intent intent = (Intent) map.get("intent");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_actions_menu, menu);
        MenuItem dialog_mode = menu.findItem(R.id.dialog);
        MenuItem full_screen_mode = menu.findItem(R.id.fullscreen);
        if (dialog_mode != null && full_screen_mode != null) {
            if(mPreference.getBoolean(KEY_AUTH_MODE, false)) {
                full_screen_mode.setChecked(true);
            } else {
                dialog_mode.setChecked(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.licenses: {
                Intent intent = new Intent(this, HtmlLicensesActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.fullscreen: {
                item.setChecked(true);
                mPreference.edit().putBoolean(KEY_AUTH_MODE, true).apply();
                return true;
            }
            case R.id.dialog: {
                item.setChecked(true);
                mPreference.edit().putBoolean(KEY_AUTH_MODE, false).apply();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
