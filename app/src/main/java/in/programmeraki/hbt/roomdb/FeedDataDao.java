package in.programmeraki.hbt.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FeedDataDao {

    @Query("SELECT * FROM FeedData")
    List<FeedData> getAll();

    @Query("SELECT * FROM FeedData WHERE uid IN (:feedIds)")
    List<FeedData> loadAllByIds(int[] feedIds);

    @Query("SELECT COUNT(*) FROM FeedData")
    int numberOfRows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(FeedData... feeds);

    @Delete
    void delete(FeedData feed);

    @Query("DELETE FROM FeedData")
    void deleteAll();
}
