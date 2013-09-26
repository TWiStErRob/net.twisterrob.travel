package com.twister.london.travel.db;

import java.io.PrintStream;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.*;
import android.database.sqlite.*;

public class DataBaseAccess {
	public static void bindObjectToProgram(SQLiteProgram prog, int index, Object value) {
		DatabaseUtils.bindObjectToProgram(prog, index, value);
	}
	public static void appendEscapedSQLString(StringBuilder sb, String sqlString) {
		DatabaseUtils.appendEscapedSQLString(sb, sqlString);
	}
	public static String sqlEscapeString(String value) {
		return DatabaseUtils.sqlEscapeString(value);
	}
	@TargetApi(11)
	public static String concatenateWhere(String a, String b) {
		return DatabaseUtils.concatenateWhere(a, b);
	}
	public static String getCollationKey(String name) {
		return DatabaseUtils.getCollationKey(name);
	}
	public static String getHexCollationKey(String name) {
		return DatabaseUtils.getHexCollationKey(name);
	}
	public static void dumpCursor(Cursor cursor) {
		DatabaseUtils.dumpCursor(cursor);
	}
	public static void dumpCursor(Cursor cursor, PrintStream stream) {
		DatabaseUtils.dumpCursor(cursor, stream);
	}
	public static void dumpCursor(Cursor cursor, StringBuilder sb) {
		DatabaseUtils.dumpCursor(cursor, sb);
	}
	public static String dumpCursorToString(Cursor cursor) {
		return DatabaseUtils.dumpCursorToString(cursor);
	}
	public static void dumpCurrentRow(Cursor cursor) {
		DatabaseUtils.dumpCurrentRow(cursor);
	}
	public static void dumpCurrentRow(Cursor cursor, PrintStream stream) {
		DatabaseUtils.dumpCurrentRow(cursor, stream);
	}
	public static void dumpCurrentRow(Cursor cursor, StringBuilder sb) {
		DatabaseUtils.dumpCurrentRow(cursor, sb);
	}
	public static String dumpCurrentRowToString(Cursor cursor) {
		return DatabaseUtils.dumpCurrentRowToString(cursor);
	}
	public static void cursorIntToContentValues(Cursor cursor, String field, ContentValues values) {
		DatabaseUtils.cursorIntToContentValues(cursor, field, values);
	}
	public static void cursorRowToContentValues(Cursor cursor, ContentValues values) {
		DatabaseUtils.cursorRowToContentValues(cursor, values);
	}
	@TargetApi(11)
	public static long queryNumEntries(SQLiteDatabase db, String table) {
		return DatabaseUtils.queryNumEntries(db, table);
	}
	@TargetApi(11)
	public static long queryNumEntries(SQLiteDatabase db, String table, String selection) {
		return DatabaseUtils.queryNumEntries(db, table, selection);
	}
	public static long longForQuery(SQLiteDatabase db, String query, String[] selectionArgs) {
		return DatabaseUtils.longForQuery(db, query, selectionArgs);
	}
	public static long longForQuery(SQLiteStatement prog, String[] selectionArgs) {
		return DatabaseUtils.longForQuery(prog, selectionArgs);
	}
	public static String stringForQuery(SQLiteDatabase db, String query, String[] selectionArgs) {
		return DatabaseUtils.stringForQuery(db, query, selectionArgs);
	}
	public static String stringForQuery(SQLiteStatement prog, String[] selectionArgs) {
		return DatabaseUtils.stringForQuery(prog, selectionArgs);
	}
	@TargetApi(11)
	public static int getSqlStatementType(String sql) {
		return DatabaseUtils.getSqlStatementType(sql);
	}
	@TargetApi(11)
	public static String[] appendSelectionArgs(String[] originalValues, String[] newValues) {
		return DatabaseUtils.appendSelectionArgs(originalValues, newValues);
	}
}
