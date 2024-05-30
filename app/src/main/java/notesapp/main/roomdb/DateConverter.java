package notesapp.main.roomdb;

import java.util.Date;

import androidx.room.TypeConverter;

public class DateConverter {

    @TypeConverter
    public Date fromTimeStamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimeStamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
