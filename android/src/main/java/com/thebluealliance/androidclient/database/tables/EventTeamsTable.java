package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.List;

public class EventTeamsTable extends ModelTable<EventTeam> {

    public static final String KEY = "key",
            TEAMKEY = "teamKey",
            EVENTKEY = "eventKey",
            YEAR = "year",
            COMPWEEK = "week";

    private Database mDb;

    public EventTeamsTable(Database mDb){
        super(mDb);
        this.mDb = mDb;
    }

    /**
     * Get a list of {@link Event} models for an EventTeam (teamKey + year)
     */
    public List<Event> getEvents(String teamKey, int year) {
        // INNER JOIN EventTeams + Events on KEY, select where teamKey and year = args
        String query = String.format("SELECT * FROM %1$s JOIN %2$s ON %1$s.%3$s = %2$s.%4$s " +
          "WHERE %1$s.%5$s = ? AND %1$s.%6$s = ?",
          Database.TABLE_EVENTTEAMS, Database.TABLE_EVENTS, EVENTKEY, EventsTable.KEY, TEAMKEY, YEAR);
        Cursor cursor = mDb.rawQuery(query, new String[]{teamKey, Integer.toString(year)});
        ArrayList<Event> results = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                results.add(ModelInflater.inflateEvent(cursor));
            } while (cursor.moveToNext());
        }
        return results;
    }

    /**
     * Get a list of {@link Team} models for a given event
     */
    public List<Team> getTeams(String eventKey) {
        // INNER JOIN EventTeams + TEAMS on KEY, select where eventKey = args
        String query = String.format("SELECT * FROM %1$s JOIN %2$s ON %1$s.%3$s = %2$s.%4$s " +
            "WHERE %1$s.%3$s = ?",
          Database.TABLE_EVENTTEAMS, Database.TABLE_TEAMS, TEAMKEY, TeamsTable.KEY);
        Cursor cursor = mDb.rawQuery(query, new String[]{eventKey});
        ArrayList<Team> results = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                results.add(ModelInflater.inflateTeam(cursor));
            } while (cursor.moveToNext());
        }
        return results;
    }

    @Override
    protected String getTableName() {
        return Database.TABLE_EVENTTEAMS;
    }

    @Override
    protected String getKeyColumn() {
        return KEY;
    }

    @Override
    public EventTeam inflate(Cursor cursor) {
        return ModelInflater.inflateEventTeam(cursor);
    }
}