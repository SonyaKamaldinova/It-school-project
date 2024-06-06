package com.example.project;


import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ResourceCursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;




public class ShoppingListActivity extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	private Long mListId;
	private boolean isFirstTime=true;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		mListId = extras.getLong(DBAdapter.LISTID);
		setContentView(R.layout.items_list);
		fillData();
		fillListsSpinner();
		Button btnAddItem=(Button)findViewById(R.id.makeNewItem);
		btnAddItem.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(ShoppingListActivity.this, ItemEdit.class);
				i.putExtra(DBAdapter.LIST_ID, mListId);
				startActivityForResult(i, ACTIVITY_CREATE);}});
		ImageButton btnGoToList=(ImageButton)findViewById(R.id.goToLists);
		btnGoToList.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				finish();}});
		registerForContextMenu(getListView());}

	public void fillListsSpinner(){
		DBAdapter db = new DBAdapter(this);
		db.open();
    		Spinner lSpinner = (Spinner) findViewById(R.id.changeList);
    		Cursor lCursor = db.getAllLists();
    		String[] from = new String[]{DBAdapter.LISTNAME};
    		int[] to = new int[]{R.id.tvListName};
		SimpleCursorAdapter lAdapter = new SimpleCursorAdapter(this, R.layout.spinner_row, lCursor, from, to);
    		lSpinner.setAdapter(lAdapter);
		db.close();
		SelectSpinner(lSpinner, mListId);
		lSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	    	Cursor c = (Cursor) (parentView.getAdapter().getItem(position)); 
	    	    	@SuppressLint("Range") long id = c.getLong(c.getColumnIndex(DBAdapter.LISTID));
	    	    	if(isFirstTime){
	    	    	  fillData();
	    	    	  isFirstTime = false;}
	    	    	else{
			  mListId = id;
	    	    	  fillData();}}});}

	public static void SelectSpinner(Spinner s, long value) {
		SimpleCursorAdapter a = (SimpleCursorAdapter) s.getAdapter();
		for (int position = 0; position < a.getCount(); position++){ 
			if (a.getItemId(position) == value) { 
				s.setSelection(position);
				return;}}}

    
	//make menu for edit or delete item
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, EDIT_ID, 0, R.string.menu_edit);}

	//edit or delete item
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int res = item.getItemId();
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(res) {
			case DELETE_ID:                
				DBAdapter db = new DBAdapter(this);
				db.open();
				db.deleteItem(info.id);
				db.close();
				fillData();
				return true;
			case EDIT_ID:
				Intent i = new Intent(this, ItemEdit.class);
				i.putExtra(DBAdapter.PRODUCTID, info.id);
				startActivityForResult(i, ACTIVITY_EDIT);
				return true;}
		return super.onContextItemSelected(item);}

	//set data
	private void fillData() {
		DBAdapter db = new DBAdapter(this);
		db.open();
		Cursor c = db.getAllItems(mListId);
		MyAdapter notes = new MyAdapter(this, c);
		setListAdapter(notes);
		db.close();}

    
	private class SpinnerAdapter extends ResourceCursorAdapter {
		public SpinnerAdapter(Context context, Cursor cur) {
			super(context, R.layout.spinner_row, cur);}
	    
		@Override
		public View newView(Context context, Cursor cur, ViewGroup parent) {
			LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return li.inflate(R.layout.spinner_row, parent, false);}   

		//existing view to the data pointed to by cursor
		@SuppressLint("Range")
		@Override
		public void bindView(View view, Context context, Cursor cur) {
			TextView tv = (TextView)view.findViewById(R.id.tvListName);
			final long listID = cur.getInt(cur.getColumnIndex(DBAdapter.LISTID));
			tv.setText(cur.getString(cur.getColumnIndex(DBAdapter.LISTNAME)));}}
	
	private class MyAdapter extends ResourceCursorAdapter {
		public boolean[] checked;	
		
		@SuppressLint({"Range", "SuspiciousIndentation"})
		public MyAdapter(Context context, Cursor c) {
			super(context, R.layout.list_item, cur);
			checked = new boolean[cur.getCount()];
			int i = 0;  
			while (c.moveToNext()) {
				checked[i] = (c.getInt(cur.getColumnIndex(DBAdapter.ISDONE))==0) ? false : true;
				i++;}}
		
        
		@Override
		public View newView(Context context, Cursor cur, ViewGroup parent) {
			LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return li.inflate(R.layout.list_item, parent, false);}
		
		@SuppressLint("Range")
		@Override
		public void bindView(View view, Context context, final  Cursor cur) {
			TextView tvListText = (TextView)view.findViewById(R.id.text1);
		        CheckBox cbListCheck = (CheckBox)view.findViewById(R.id.chkIsDone);
		        tvListText.setText(cur.getString(cur.getColumnIndex(DBAdapter.TITLE)));
			final int position = cur.getPosition();
		        final int rowID = cur.getInt(cur.getColumnIndex(DBAdapter.PRODUCTID));
		        cbListCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton v, boolean isChecked) {
					CheckBox cb = (CheckBox) v ;                  				                				             				                
					SaveDone(cb.isChecked(), v.getContext(), rowID);
			                checked[position] =cb.isChecked();}});
			cbListCheck.setChecked(checked[position]);}
		
		private void SaveDone(boolean isDone,Context contex,int  rowID){
			DBAdapter db = new DBAdapter(contex);
			db.open();                                 
			db.updateIsDone( rowID,isDone);
			db.close();}}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();}}
