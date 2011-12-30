package aisk.saltpass;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.ClipData;
import android.content.SharedPreferences;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.text.InputType;
import android.text.ClipboardManager;
import android.preference.PreferenceManager;
import android.database.Cursor;

import android.util.Log;

public class Main extends Activity
{   
    private DBAdapter dbAdapter;
    private SimpleCursorAdapter adapter;
    private ListView siteListView;
    private Cursor siteCursor;
    private ClipboardManager clipboard;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        siteListView = (ListView)findViewById(R.id.siteListView);
        clipboard = (ClipboardManager)this.getSystemService(CLIPBOARD_SERVICE);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Main.this);

        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        siteCursor = dbAdapter.getAllSitesCursor();

        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, 
                siteCursor,
                new String[] {dbAdapter.KEY_SITE},
                new int[] {android.R.id.text1});
        siteListView.setAdapter(adapter);

        siteListView.setOnItemLongClickListener(new OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView parent, View view, int position, final long id) {
                Log.d("Long Click:", String.valueOf(id));
                AlertDialog dialog = new AlertDialog.Builder(Main.this)
                    .setTitle("Would you want delete this site salt?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int btn) {
                            boolean result = dbAdapter.deleteSite(id);
                            siteCursor.requery();
                            adapter.notifyDataSetChanged();
                            String msg = result ? "Done" : "Failed";
                            Toast.makeText(Main.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
                    dialog.show();
                return true;
            }
        });

        siteListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, final View view, int position, long id) {
                LayoutInflater factory = LayoutInflater.from(Main.this);
                final View addSiteDialog = factory.inflate(R.layout.input_dialog, null);
                final EditText edit = (EditText)addSiteDialog.findViewById(R.id.input_edit);
                edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                AlertDialog inputDialog = new AlertDialog.Builder(Main.this)
                    .setTitle("Input your password")
                    .setView(addSiteDialog)
                    .setPositiveButton("Salt it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int btn) {
                            String pswd = edit.getText().toString();
                            String salt = prefs.getString("SALT_STRING", "");
                            int count = Integer.valueOf(prefs.getString("SALTED_COUNT", "100"));
                            int offset = Integer.valueOf(prefs.getString("SALTED_OFFSET", "0"));
                            String pass = salt + ((TextView)view).getText().toString() + pswd;
                            String saltType = prefs.getString("SALT_TYPE", "SHA-1");
                            final String saltedPass = Utils.limitCut(Utils.shaConvert(pass, saltType),
                                count, offset);
                            AlertDialog resultDialog = new AlertDialog.Builder(Main.this)
                                .setTitle("Salted password")
                                .setMessage(saltedPass)
                                .setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int btn) {
                                        clipboard.setText(saltedPass);
                                    }
                                })
                                .setNegativeButton("Done", null)
                                .create();
                            resultDialog.show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
                inputDialog.show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	switch (item.getItemId()) {
            case (R.id.menuAdd):
                LayoutInflater factory = LayoutInflater.from(this);
                final View addSiteDialog = factory.inflate(R.layout.input_dialog, null);
                AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Add new site salt")
                    .setView(addSiteDialog)
                    .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int btn) {
                            EditText edit = (EditText)addSiteDialog.findViewById(R.id.input_edit);
                            dbAdapter.insertSite(edit.getText().toString());
                            siteCursor.requery();
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
                dialog.show();
                break;
    		case (R.id.menuExit):
    			Main.this.finish();
                break;
    		case (R.id.meunAbout):
    			AlertDialog.Builder ad = new AlertDialog.Builder(Main.this);
    			ad.setTitle("About");
    			ad.setMessage(R.string.about_text);
    			ad.show();
    			break;
            case (R.id.menuSetting):
                Intent intent = new Intent(this, Setting.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbAdapter.close();
    }
}
