package de.timecrunch.timecrunch.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.model.TaskModel;

public class DBHandler extends SQLiteOpenHelper {
    public static DBHandler dbHandler;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "timecrunch.db";

    private static final String SQL_CREATE_CATEGORIES = "" +
            "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry.COLUMN_NAME_CATEGORY_ID + " INTEGER PRIMARY KEY, " +
                CategoryEntry.COLUMN_NAME_TITLE + " text NOT NULL, " +
                CategoryEntry.COLUMN_NAME_COLOR + " INTEGER NOT NULL" +
            ");";

    private static final String SQL_CREATE_SUBCATEGORIES = "" +
            "CREATE TABLE " + SubcategoryEntry.TABLE_NAME + " (" +
            SubcategoryEntry.COLUMN_NAME_PARENT_ID + " INTEGER NOT NULL, " +
            SubcategoryEntry.COLUMN_NAME_CHILD_ID + " INTEGER NOT NULL, " +
            "PRIMARY KEY (" + SubcategoryEntry.COLUMN_NAME_PARENT_ID + ", " +
            SubcategoryEntry.COLUMN_NAME_CHILD_ID + ")" +
            ");";

    private static final String SQL_CREATE_TASKS = "" +
            "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
            TaskEntry.COLUMN_NAME_TASK_ID + " INTEGER PRIMARY KEY, " +
            TaskEntry.COLUMN_NAME_TITLE + " text NOT NULL, " +
            TaskEntry.COLUMN_NAME_CATEGORY + " INTEGER NOT NULL" +
            ");";

    private static final String SQL_DELETE_ENTRIES = "";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CATEGORIES);
        db.execSQL(SQL_CREATE_SUBCATEGORIES);
        db.execSQL(SQL_CREATE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SubcategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /*****************************
     * Categories
     *****************************/

    public int createCategory(String title, int color, boolean hasTemplate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CategoryEntry.COLUMN_NAME_TITLE, title);
        values.put(CategoryEntry.COLUMN_NAME_COLOR, color);

        long rowIndex = db.insert(CategoryEntry.TABLE_NAME, null, values);

        if (hasTemplate) {
            // TODO
        }

        return (int)rowIndex;
    }

    public boolean removeCategory(int categoryId) {
        // TODO: Implement
        return false;
    }

    public LinkedHashMap<Category, List<Category>> getCategories() {

        // select * from
        // categories where category_id not in (select child_id from subcategories)

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery =
                "SELECT * FROM " + CategoryEntry.TABLE_NAME + " WHERE "
                        + CategoryEntry.COLUMN_NAME_CATEGORY_ID + " NOT IN "
                        + "(" + "SELECT " + SubcategoryEntry.COLUMN_NAME_CHILD_ID
                        + " FROM " + SubcategoryEntry.TABLE_NAME + ");";

        Cursor c = db.rawQuery(selectQuery, null);

        LinkedHashMap<Category,List<Category>> categories = new LinkedHashMap<>();

        if(c.moveToFirst()) {
            do {
                Category category = new Category(
                        c.getString(c.getColumnIndex(CategoryEntry.COLUMN_NAME_CATEGORY_ID)),
                        c.getString(c.getColumnIndex(CategoryEntry.COLUMN_NAME_TITLE)),
                        c.getInt(c.getColumnIndex(CategoryEntry.COLUMN_NAME_COLOR)),
                        false);
                categories.put(category, getSubcategories(category.getId()));
            } while (c.moveToNext());
        } return categories;
    }

    /*****************************
     * Subcategories
     *****************************/

    public int createSubcategory(String title, int color, boolean hasTemplate, int parentId) {

        long childId = createCategory(title, color, hasTemplate);

        if (childId > -1) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(SubcategoryEntry.COLUMN_NAME_PARENT_ID, parentId);
            values.put(SubcategoryEntry.COLUMN_NAME_CHILD_ID, childId);
            long rowIndex = db.insert(SubcategoryEntry.TABLE_NAME, null, values);
            return (int)rowIndex;
        } else {
            return -1;
        }
    }

    private List<Category> getSubcategories(String categoryId) {

        // select * from
        // categories where category_id in (select child_id from subcategories where parent_id = 1)

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery =
                "SELECT * FROM " + CategoryEntry.TABLE_NAME + " WHERE "
                        + CategoryEntry.COLUMN_NAME_CATEGORY_ID +
                        " IN (SELECT " + SubcategoryEntry.COLUMN_NAME_CHILD_ID +
                        " FROM " + SubcategoryEntry.TABLE_NAME +
                        " WHERE " + SubcategoryEntry.COLUMN_NAME_PARENT_ID + "=" + categoryId + ")";

        Cursor c = db.rawQuery(selectQuery, null);
        List<Category> categoryList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                int subcategoryId = c.getInt((c.getColumnIndex(CategoryEntry.COLUMN_NAME_CATEGORY_ID)));
                String subcategoryTitle = c.getString((c.getColumnIndex(CategoryEntry.COLUMN_NAME_TITLE)));
                int subcategoryColor = c.getInt(c.getColumnIndex(CategoryEntry.COLUMN_NAME_COLOR));
                //TODO get hasTimeBlock
                //categoryList.add(new Category(subcategoryId, subcategoryTitle, subcategoryColor,false));
            } while (c.moveToNext());
        }
        return categoryList;
    }

    /*****************************
     * Tasks
     *****************************/

    public int createTask(String title, String categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_TITLE, title);
        values.put(TaskEntry.COLUMN_NAME_CATEGORY, categoryId);

        long rowIndex = db.insert(TaskEntry.TABLE_NAME, null, values);
        return (int) rowIndex;
    }

    public boolean removeTask(String entryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TaskEntry.TABLE_NAME,TaskEntry.COLUMN_NAME_TASK_ID + "=?", new String[]{String.valueOf(entryId)});
        return result==1;
    }

    public List<TaskModel> getTasks(String categoryId) {

        // select title from tasks
        // where category = categoryId;

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery =
                "SELECT * FROM " + TaskEntry.TABLE_NAME + " WHERE "
                        + TaskEntry.COLUMN_NAME_CATEGORY + " = " + categoryId;

        Cursor c = db.rawQuery(selectQuery, null);
        List<TaskModel> taskList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                int taskId = c.getInt((c.getColumnIndex(TaskEntry.COLUMN_NAME_TASK_ID)));
                String taskText = c.getString((c.getColumnIndex(TaskEntry.COLUMN_NAME_TITLE)));
                //taskList.add(new TaskModel(taskId, taskText));
            } while (c.moveToNext());
        }
        return taskList;

    }

    // Categories
    public static abstract class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_COLOR = "color";
    }

    // Subcategories
    public static abstract class SubcategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "subcategories";
        public static final String COLUMN_NAME_PARENT_ID = "parent_id";
        public static final String COLUMN_NAME_CHILD_ID = "child_id";
    }

    // Entries
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_TASK_ID = "task_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CATEGORY = "category";
    }

}
