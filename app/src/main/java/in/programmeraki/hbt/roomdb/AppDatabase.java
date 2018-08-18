package in.programmeraki.hbt.roomdb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {FeedData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FeedDataDao feedDataDao();
}
