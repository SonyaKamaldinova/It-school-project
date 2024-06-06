package com.example.project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Lists extends Activity {
	private static final int LIST_EDIT=1;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_list);
        SetInformation();
        Button btnAddNewList=(Button)findViewById(R.id.btnAddNewList);
        btnAddNewList.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
            	Intent i = new Intent(v.getContext(), ListItemEdit.class);
            	Long list_id = null;
            	i.putExtra(DBAdapter.LIST_ID, list_id);
                startActivityForResult(i, LIST_EDIT);}});
        registerForContextMenu((ListView)findViewById(R.id.listview));
    }
    //menu for edit or delete
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        menu.add(0, EDIT_ID, 0, R.string.menu_edit);
    }

    //capture created data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        SetInformation();
    }

    //set data
    private void SetInformation(){
        ListView lv= (ListView)findViewById(R.id.listview);
        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor c=db.getAllLists();
        MyAdapter adapter = new MyAdapter(this, c);
        lv.setAdapter(adapter);
        db.close();
    }
    private class MyAdapter extends ResourceCursorAdapter {
        public MyAdapter(Context context, Cursor cur) {
            super(context, R.layout.list_item, cur);
        }

        //make new view for data pointed to by cursor
        @Override
        public View newView(Context context, Cursor cur, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return li.inflate(R.layout.lists_list_item, parent, false);
        }

        //existing view to the data pointed to by cursor
        @SuppressLint("Range")
        @Override
        public void bindView(View view, Context context, Cursor cur) {
        	TextView tvListNameText = (TextView)view.findViewById(R.id.itemName);
            ImageButton btnGoToList= (ImageButton) view.findViewById(R.id.btnGoToList);
            tvListNameText.setText(cur.getString(cur.getColumnIndex(DBAdapter.LISTNAME)));
            btnGoToList.setOnClickListener( new View.OnClickListener() {  
                  public void onClick(View v) {
                	Intent i = new Intent(v.getContext(), ShoppingListActivity.class);
                    ListView lv= (ListView)findViewById(R.id.listview);
                    int p  = lv.getPositionForView(v);
                    Cursor c = (Cursor) (lv.getAdapter().getItem(p));
                    final long  rowID = c.getInt(c.getColumnIndex(DBAdapter.LISTID));
                	i.putExtra(DBAdapter.LISTID, rowID);
                    startActivityForResult(i, (int)(long)rowID);}});
            tvListNameText.setOnClickListener( new View.OnClickListener() {  
                public void onClick(View v) {
                    ListView lv= (ListView)findViewById(R.id.listview);
                    int p  = lv.getPositionForView(v);
                    Cursor c = (Cursor) (lv.getAdapter().getItem(p));
                    @SuppressLint("Range") final long  rowID = c.getInt(c.getColumnIndex(DBAdapter.LISTID));
                	openContextMenu(v);
                }});}}

    //edit or remove list
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	int res=item.getItemId();
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(res) {
            case EDIT_ID:
                Intent i = new Intent(this, ListItemEdit.class);
                i.putExtra(DBAdapter.LIST_ID, info.id);
                startActivityForResult(i, LIST_EDIT);
                return true;
            case DELETE_ID:
                DBAdapter db = new DBAdapter(this);
                db.open();
                db.deleteList(info.id);
                db.close();
                SetInformation();
                return true;}
        return super.onContextItemSelected(item);}}