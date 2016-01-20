package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.joey.mobilesafe52.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Joey on 2015/11/18.
 */
public class ContactActivity extends Activity {
    private final String TAG = "调试ContactActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ListView lvList = (ListView) findViewById(R.id.lv_contact_list);
        final ArrayList<HashMap<String, String>> contactList = readContacts();
        lvList.setAdapter(new SimpleAdapter(this, contactList, R.layout.item_contact_list,
                new String[]{"name", "phone"}, new int[]{R.id.tv_contact_name, R.id.tv_contact_phone}));
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone=contactList.get(position).get("phone");
                Intent intent=new Intent();
                intent.putExtra("phone",phone);
                setResult(RESULT_OK,intent);
                finish();
            }
        });



    }

    private ArrayList<HashMap<String, String>> readContacts() {
        /**
         * 1.获取URI  data  raw_contacts
         */
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        Uri rawContactsUri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver cr = this.getContentResolver();
        Cursor rawCursor = cr.query(rawContactsUri, new String[]{"contact_id"}, null, null, null);
        ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
        while (rawCursor.moveToNext()) {
            String contact_id = rawCursor.getString(0);
            Cursor dataCursor = cr.query(dataUri, new String[]{"data1", "mimetype"},
                    "raw_contact_id = ?", new String[]{contact_id}, null);
            HashMap<String, String> map = new HashMap<String, String>();
            while (dataCursor.moveToNext()) {
                String data1 = dataCursor.getString(0);
                String mimeTyep = dataCursor.getString(1);
                if (mimeTyep.equals("vnd.android.cursor.item/phone_v2")) {
                    //说明data1是电话号码
                    map.put("phone", data1.trim());
                } else if (mimeTyep.equals("vnd.android.cursor.item/name")) {
                    //说明data1是姓名
                    map.put("name", data1.trim());
                }
            }
            contactList.add(map);
            dataCursor.close();
        }
        rawCursor.close();
        for (HashMap<String, String> map : contactList) {
            String info = map.get("name") + " : " + map.get("phone");
            Log.e(TAG, info);
        }
        return contactList;
    }
}
