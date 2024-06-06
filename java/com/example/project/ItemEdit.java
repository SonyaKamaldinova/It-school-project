package com.example.project;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class ItemEdit extends Activity {
	private Long nId;
	private EditText nText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.item_edit);
        setTitle(R.string.item_title);
        Button confirmButton = (Button) findViewById(R.id.add);
		nText = (EditText) findViewById(R.id.body);
		nId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(DBAdapter.PRODUCTID);
		//because use this activity for edit and make item
		if (nId == null) {
			Bundle extras = getIntent().getExtras();
			nId = extras != null ? (extras.getLong(DBAdapter.PRODUCTID)>0 ? extras.getLong(DBAdapter.PRODUCTID) : null) : null;}
		if(nId != null) {
			DBAdapter db=new DBAdapter(this);
			db.open();
			Cursor c =db.getItem(nId) ;
			nText.setText(c.getString(c.getColumnIndexOrThrow(DBAdapter.TITLE)));
		    db.close();}
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				DBAdapter db = new DBAdapter(view.getContext());
				db.open(); 
				if (nId == null) {
					db.insertNewItem(nText.getText().toString(), getIntent().getExtras().getLong(DBAdapter.LIST_ID));}
				else {
					db.updateItem(nId, nText.getText().toString());}
				db.close();
				setResult(RESULT_OK);
				finish();}});}}
