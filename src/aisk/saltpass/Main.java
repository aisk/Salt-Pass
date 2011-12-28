package aisk.saltpass;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.database.Cursor;

public class Main extends Activity
{   
    private DBAdapter dbAdapter;
    private SimpleCursorAdapter adapter;
    private ListView siteListView;
    private Cursor siteCursor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        siteListView = (ListView)findViewById(R.id.siteListView);
        
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        siteCursor = dbAdapter.getAllSitesCursor();

        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, 
                siteCursor,
                new String[] {dbAdapter.KEY_SITE},
                new int[] {android.R.id.text1});
        siteListView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	switch (item.getItemId()) {
            case (R.id.menuAdd):
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.addsite_dialog);
                dialog.show();
                dbAdapter.insertSite("微博");
                siteCursor.requery();
                adapter.notifyDataSetChanged();
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
}
