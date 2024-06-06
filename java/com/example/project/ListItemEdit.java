package com.example.project;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ListItemEdit extends Activity{
	private Long mId;
	private EditText mListName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_edit);
		mId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(DBAdapter.LIST_ID);
		//because use this activity for edit and make list
		if (mId == null) {
			Bundle extras = getIntent().getExtras();
			mId = (extras != null) ? (extras.getLong(DBAdapter.LIST_ID)>0?extras.getLong(DBAdapter.LIST_ID):null) : null;}
		if(mId != null){
			DBAdapter db=new DBAdapter(this);
			db.open();
			Cursor c = db.getListItem(mId) ;
			mListName= (EditText) findViewById(R.id.txtListName);
			mListName.setText(c.getString(c.getColumnIndexOrThrow(DBAdapter.LISTNAME)));}
		Button saveButton = (Button) findViewById(R.id.btnListadd);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (mId == null) {
					mListName = (EditText) findViewById(R.id.txtListName);
					DBAdapter db = new DBAdapter(view.getContext());
					db.open();
					String slistName = mListName.getText().toString();
					db.insertNewList(slistName);
					db.close();
					setResult(RESULT_OK);
					finish();}
				else{
					DBAdapter db = new DBAdapter(view.getContext());
					db.open(); 
					db.updateListItem(mId, mListName.getText().toString());
					db.close();                                      
					setResult(RESULT_OK);
					finish();}}});}}
