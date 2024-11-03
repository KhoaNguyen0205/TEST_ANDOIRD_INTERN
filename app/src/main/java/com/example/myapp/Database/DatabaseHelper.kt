//package com.example.myapp.Database
//
//import android.content.ContentValues
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//import com.example.myapp.model.ImageModel
//
//class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
//
//    companion object {
//        private const val DATABASE_NAME = "ImageDB"
//        private const val DATABASE_VERSION = 1
//        private const val TABLE_NAME = "Images"
//        private const val COL_ID = "id"
//        private const val COL_PATH = "filePath"
//        private const val COL_DATE = "dateTaken"
//    }
//
//    override fun onCreate(db: SQLiteDatabase) {
//        val createTable =
//            "CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY, $COL_PATH TEXT, $COL_DATE INTEGER)"
//        db.execSQL(createTable)
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
//        onCreate(db)
//    }
//
//    fun insertImage(image: String): Long {
//        val db = writableDatabase
//        val values = ContentValues().apply {
//            put(COL_PATH, image.filePath)
//            put(COL_DATE, image.dateTaken)
//        }
//        return db.insert(TABLE_NAME, null, values)
//    }
//
//    fun getAllImages(): List<ImageModel> {
//        val imageList = mutableListOf<ImageModel>()
//        val db = readableDatabase
//        val query = "SELECT * FROM $TABLE_NAME"
//        val cursor = db.rawQuery(query, null)
//
//        if (cursor.moveToFirst()) {
//            do {
//                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
//                val filePath = cursor.getString(cursor.getColumnIndexOrThrow(COL_PATH))
//                val dateTaken = cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE))
//                imageList.add(ImageModel(id, filePath, dateTaken))
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        db.close()
//        return imageList
//    }
//
//}