package de.rlill.modelmanager.persistance;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import de.rlill.modelmanager.model.Team;

public class TeamDbAdapter extends DbAdapter {

	private static final String LOG_TAG = "MM*" + TeamDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_TEAM = "tbl_team";

	public static final String KEY_LEADER1 = "leader1";
	public static final String KEY_LEADER2 = "leader2";
	public static final String KEY_BONUS = "bonus";

	public static final String CREATE_TEAM_TABLE = "create table " + TABLE_NAME_TEAM + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_LEADER1 + " INTEGER, "
			+ KEY_LEADER2 + " INTEGER, "
			+ KEY_BONUS + " INTEGER)";

    /**
     * List all teams
     */
    public static SparseArray<Team> getAllTeams() {
    	SparseArray<Team> result = new SparseArray<Team>();

    	SQLiteDatabase db = open();
    	Cursor cursor = db.query(
    			TABLE_NAME_TEAM,
    			null,
    			null,
    			null,
    			null, null, null, null);

    	if (cursor.moveToFirst()) {
    		do {
    			Team team = readCursorLine(cursor);
    			result.put(team.getId(), team);
    		} while (cursor.moveToNext());
    	}
    	cursor.close();

    	return result;
    }

    private static Team readCursorLine(Cursor cursor) {
    	Team team = new Team(cursor.getInt(0));
    	team.setLeader1(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_LEADER1)));
    	team.setLeader2(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_LEADER2)));
    	team.setBonus(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BONUS)));
    	return team;
    }

    public static int updateTeam(Team team) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_LEADER1, team.getLeader1());
    	values.put(KEY_LEADER2, team.getLeader2());
    	values.put(KEY_BONUS, team.getBonus());

    	// update row
    	return db.update(
    			TABLE_NAME_TEAM,
    			values,
    			KEY_ID + " = ?",
    			new String[] { String.valueOf(team.getId()) }
    			);
    }

    public static int addTeam(Team team) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_LEADER1, team.getLeader1());
    	values.put(KEY_LEADER2, team.getLeader2());
    	values.put(KEY_BONUS, team.getBonus());

    	// update row
    	return (int)db.insert(TABLE_NAME_TEAM, null, values);
    }

}
