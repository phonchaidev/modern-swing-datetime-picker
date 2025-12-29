package dev.phonchai.datetime.picker.component.date;

import java.time.LocalDate;
import java.util.Calendar;

public class SingleDate {

    private int day;
    private int month;
    private int year; // เก็บเป็น ค.ศ. สำหรับการคำนวณ

    public SingleDate() {
        this(LocalDate.now());
    }

    public SingleDate(LocalDate date) {
        this.day = date.getDayOfMonth();
        this.month = date.getMonthValue();
        this.year = date.getYear(); // เก็บเป็น ค.ศ.
    }

    public SingleDate(Calendar calendar) {
        this(calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    public SingleDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year; // เก็บเป็น ค.ศ.
    }

    // เพิ่ม utility methods
    public static int adToBuddhistYear(int adYear) {
        return adYear + 543;
    }

    public static int buddhistToAdYear(int buddhistYear) {
        return buddhistYear - 543;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year; // คืน ค.ศ. สำหรับการคำนวณ
    }

    public int getBuddhistYear() {
        return adToBuddhistYear(year); // คืน พ.ศ. สำหรับแสดงผล
    }

    public void setYear(int year) {
        this.year = year; // ตั้งเป็น ค.ศ.
    }

    public boolean same(SingleDate date) {
        return date != null && year == date.year && month == date.month && day == date.day;
    }

    public boolean after(SingleDate date) {
        if (date == null) {
            return false;
        }
        if (year > date.year) {
            return true;
        } else if (year < date.year) {
            return false;
        } else {
            if (month > date.month) {
                return true;
            } else if (month < date.month) {
                return false;
            } else {
                return day > date.day;
            }
        }
    }

    public boolean before(SingleDate date) {
        if (date == null) {
            return false;
        }
        if (year < date.year) {
            return true;
        } else if (year > date.year) {
            return false;
        } else {
            if (month < date.month) {
                return true;
            } else if (month > date.month) {
                return false;
            } else {
                return day < date.day;
            }
        }
    }

    public boolean between(SingleDate from, SingleDate to) {
        if (from.before(to)) {
            return after(from) && before(to);
        } else {
            return after(to) && before(from);
        }
    }

    public LocalDate toLocalDate() {
        // ไม่แสดง debug แล้วเพื่อความสะอาด
        return LocalDate.of(year, month, day); // ใช้ ค.ศ. ในการสร้าง LocalDate
    }

    @Override
    public String toString() {
        return day + "/" + month + "/" + getBuddhistYear(); // แสดง พ.ศ.
    }

    // เพิ่ม method แสดง ค.ศ. สำหรับ debug
    public String toStringAD() {
        return day + "/" + month + "/" + year; // แสดง ค.ศ.
    }
}
