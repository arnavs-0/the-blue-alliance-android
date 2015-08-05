package com.thebluealliance.androidclient.database.tables;

import android.database.Cursor;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.ModelInflater;
import com.thebluealliance.androidclient.database.ModelTable;
import com.thebluealliance.androidclient.models.Award;

import java.util.ArrayList;
import java.util.List;

public class AwardsTable extends ModelTable<Award> {
    public static final String KEY = "key",
            ENUM = "enum",
            EVENTKEY = "eventKey",
            NAME = "name",
            YEAR = "year",
            WINNERS = "winners";

    private Database db;

    public AwardsTable(Database db){
        super(db.mDb);
        this.db = db;
    }

    @Override
    protected String getTableName() {
        return Database.TABLE_AWARDS;
    }

    /*
     * For Awards, key is eventKey:awardName
     */
    @Override
    protected String getKeyColumn() {
        return KEY;
    }

    @Override
    public Award inflate(Cursor cursor) {
        return ModelInflater.inflateAward(cursor);
    }

    public List<Award> getTeamAtEventAwards(String teamKey, String eventKey) {
        Cursor cursor = db.mDb.rawQuery("SELECT * FROM `" + Database.TABLE_AWARDS + "` WHERE `" + EVENTKEY
          + "` = ? AND `" + WINNERS + "` LIKE '%" + teamKey + "," + "%'", new String[]{eventKey});
        List<Award> models = new ArrayList<>(cursor == null ? 0 : cursor.getCount());
        if (cursor == null || !cursor.moveToFirst()) {
            return models;
        }
        do {
            models.add(inflate(cursor));
        } while (cursor.moveToNext());
        return models;
    }
}