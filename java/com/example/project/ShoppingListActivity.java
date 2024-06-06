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
	public static final int INSERT_ID = Menu.FIRST;
	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_EDIT=1;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	private Long mListId;
	private boolean isFirstTime=true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(DBAdapter.LISTID);

        //get list id
        if (mListId == null) {
            Bundle extras = getIntent().getExtras();
            mListId = extras.getLong(DBAdapter.LISTID);}
        if(mListId != null){
            setContentView(R.layout.items_list);
            fillData();
            fillListsSpinner();
            Button btnAddNewItem=(Button)findViewById(R.id.makeNewItem);
            btnAddNewItem.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(ShoppingListActivity.this, ItemEdit.class);
                    i.putExtra(DBAdapter.LIST_ID, mListId);
                    startActivityForResult(i, ACTIVITY_CREATE);}});
            ImageButton btnGoToList=(ImageButton)findViewById(R.id.goToLists);
            btnGoToList.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    finish();}});}
        registerForContextMenu(getListView());}
	
    public void fillListsSpinner(){
    	    DBAdapter db=new DBAdapter(this);
    	    db.open();
    	    Cursor listsCursor;
    		Spinner listsSpinner = (Spinner) findViewById(R.id.changeList);
    		listsCursor = db.getAllLists();
            startManagingCursor(listsCursor);
    		String[] from = new String[]{DBAdapter.LISTNAME};
    		int[] to = new int[]{R.id.tvListName};
            SimpleCursorAdapter listsAdapter = new SimpleCursorAdapter(this, R.layout.spinner_row, listsCursor, from, to);
    		listsSpinner.setAdapter(listsAdapter);
            db.close();
            SelectSpinnerItemByValue(listsSpinner, mListId);
            listsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	    	Cursor c = (Cursor) (parentView.getAdapter().getItem(position)); 
	    	    	@SuppressLint("Range") long _id=c.getLong(c.getColumnIndex(DBAdapter.LISTID));
	    	    	if(isFirstTime){
	    	    	  fillData();
	    	    	  isFirstTime =false;}
	    	    	else{
	    	    		mListId =_id;
	    	    		fillData();}}});}
	
    public static void SelectSpinnerItemByValue(Spinner spnr, long value)
    {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++)
        {if(adapter.getItemId(position) == value)
            {spnr.setSelection(position);
                return;}}}
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        menu.add(0, EDIT_ID, 0, R.string.menu_edit);
    }
	
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
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.item_title);
        return result;}

    private void fillData() {
        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor c = db.getAllItems(mListId);
        MyAdapter notes = new MyAdapter(this, c);
        setListAdapter(notes);
        db.close();
    }

    private class SpinnerAdapter extends ResourceCursorAdapter {
        public SpinnerAdapter(Context context, Cursor cur) {
            super(context, R.layout.spinner_row, cur);}
	    
        @Override
        public View newView(Context context, Cursor cur, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return li.inflate(R.layout.spinner_row, parent, false);}   
	    
        @SuppressLint("Range")
        @Override
        public void bindView(View view, Context context, Cursor cur) {
        	TextView tv= (TextView)view.findViewById(R.id.tvListName);
            final long  listID = cur.getInt(cur.getColumnIndex(DBAdapter.LISTID));
        	 tv.setText(cur.getString(cur.getColumnIndex(DBAdapter.LISTNAME)));}}
	
    private class MyAdapter extends ResourceCursorAdapter {
    	public boolean[] checked;	
        @SuppressLint({"Range", "SuspiciousIndentation"})
        public MyAdapter(Context context, Cursor cur) {
            super(context, R.layout.list_item, cur);
            checked= new boolean[cur.getCount()];
            int i = 0;  
            while (cur.moveToNext()) {
            	checked[i] = cur.getInt(cur.getColumnIndex(DBAdapter.ISDONE))==0? false: true;
                i++;
            }
        }
        @Override
        public View newView(Context context, Cursor cur, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return li.inflate(R.layout.list_item, parent, false);
        }
        @SuppressLint("Range")
        @Override
        public void bindView(View view, Context context, final  Cursor cur) {
		       TextView tvListText = (TextView)view.findViewById(R.id.text1);
		       CheckBox cbListCheck = (CheckBox)view.findViewById(R.id.chkIsDone);
		       tvListText.setText(cur.getString(cur.getColumnIndex(DBAdapter.TITLE)));
               final int position =cur.getPosition();
		       final int  rowID = cur.getInt(cur.getColumnIndex(DBAdapter.PRODUCTID));
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
        fillData();
    }
}
