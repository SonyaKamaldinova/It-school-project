package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DBAdapter {


	public static final String PRODUCTID = "_id";
	public static final String ISDONE = "isdone";
	public static final String TITLE = "title";
	public static final String LIST_ID="list_id";


	public static final String LISTID = "_id";
	public static final String LISTNAME="listName";


	private static final String DATABASE_NAME = "shopping_lists";
	private static final String ITEMS_TABLE_NAME = "list_items";
	private static final String LISTS_TABLE_NAME = "lists";


	private static final int DATABASE_VERSION = 13;


	private static final String LISTS_TABLE_CREATE =
	"create table lists ("
	+" _id integer primary key autoincrement, "
	+" listName text not null,"
	+" recentlyUsed boolean  not null DEFAULT false"
	+" );";


	private static final String ITEMS_TABLE_CREATE =
	"create table list_items ("
	+"_id integer primary key autoincrement, "
	+" isdone boolean  not null DEFAULT false,"
	+" title text not null,"
	+" list_id integer,"
	+" FOREIGN KEY(list_id) REFERENCES lists(_id)"	
	+" );";


	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	public DBAdapter(Context ctx) {
	   this.context = ctx;
	   DBHelper = new DatabaseHelper(context);}


	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
		   super(context, DATABASE_NAME, null, DATABASE_VERSION);}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(LISTS_TABLE_CREATE );
			db.execSQL(ITEMS_TABLE_CREATE );}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + LISTS_TABLE_NAME);
			onCreate(db);}}


	public DBAdapter open() throws SQLException { db = DBHelper.getWritableDatabase(); return this;}

	public void close() { DBHelper.close();}

	public Cursor getAllLists() {
		Cursor c = null;
		c = db.query(LISTS_TABLE_NAME, new String[] {LISTID, LISTNAME}, null, null, null, null, null);
		return c;}

	public boolean updateListItem(long listId, String listname) {
		ContentValues args = new ContentValues();
		args.put(LISTNAME, listname);
		return db.update(LISTS_TABLE_NAME, args, LISTID + "=" + listId, null) > 0;}
	
	public long insertNewList(String listName) {
		Long res = (long) 0;
		ContentValues initialValues = new ContentValues();
		initialValues.put(LISTNAME,  listName);
		res = db.insert(LISTS_TABLE_NAME, null, initialValues);
		return res;}

	public boolean deleteList(long rowId) { return db.delete(LISTS_TABLE_NAME, LISTID + "=" + rowId, null) > 0;}

	public long insertNewItem( String title, long list_id) {
		Long res=(long) 0;
		ContentValues initialValues = new ContentValues();
		initialValues.put(ISDONE, false);
		initialValues.put(TITLE, title);
		initialValues.put(LIST_ID, list_id);
		res= db.insert(ITEMS_TABLE_NAME, null, initialValues);
		return res;}
	
	public boolean deleteItem(long rowId) { return db.delete(ITEMS_TABLE_NAME, PRODUCTID + "=" + rowId, null) > 0;}
	
	public Cursor getAllItems(long ListId) {
		Cursor c =db.query(ITEMS_TABLE_NAME, new String[] {PRODUCTID, ISDONE, TITLE}, LIST_ID+"="+ListId, null, null, null, null);
		return c;}
	
	public Cursor getListItem(long rowId) throws SQLException {
		Cursor c = db.query(true, LISTS_TABLE_NAME, new String[] {LISTID, LISTNAME}, LISTID + "=" + rowId, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();}
		return c;}
	
	public Cursor getItem(long rowId) throws SQLException {
		Cursor c = db.query(true, ITEMS_TABLE_NAME, new String[] {PRODUCTID, ISDONE, TITLE}, PRODUCTID + "=" + rowId, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();}
		return c;}
	
	public boolean updateItem(long rowId, String title) {
		ContentValues args = new ContentValues();
		args.put(TITLE, title);
		return db.update(ITEMS_TABLE_NAME, args, PRODUCTID + "=" + rowId, null) > 0;}
	
        public boolean updateIsDone( long rowId, boolean isdone) {
		ContentValues args = new ContentValues();
		args.put(ISDONE, isdone);
		return db.update(ITEMS_TABLE_NAME, args, PRODUCTID + "=" + rowId, null) > 0;}}
