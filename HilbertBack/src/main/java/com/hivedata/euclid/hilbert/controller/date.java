package com.hivedata.euclid.hilbert.controller;

import java.util.Date;

public class date {

    public static Boolean isLeap(int year) {
        // divisible by 4
        Boolean isLeapYear = (year % 4 == 0);

        // divisible by 4 and not 100
        isLeapYear = isLeapYear && (year % 100 != 0);

        // divisible by 4 and not 100 unless divisible by 400
        return isLeapYear || (year % 400 == 0);

    }

    //ex: startDate = "06-24-2018", endDate = "06-25-2018"
    //or startDate = "06-24", endDate = "06-25"
    public static Boolean validateDates(String startDate, String endDate) {
        //today:
        Date currDate = new Date();
        //first part is to check whether they are valid dates
        int[] DaysnotLeap = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int[] DaysLeap = new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        String[] start = startDate.split("-");
        String[] end = endDate.split("-");
        //does date make sense based on calendar
        int startMonth = Integer.valueOf(start[0]);
        int startDay = Integer.valueOf(start[1]);
        int endMonth = Integer.valueOf(end[0]);
        int endDay = Integer.valueOf(end[1]);

        int year1 = 2018;
        int year2 = 2018;
        boolean isLeap1 = isLeap(year1);
        boolean isLeap2 = isLeap(year2);

        if (startDate.length() > 5 && endDate.length() > 5) {
            year1 = Integer.valueOf(start[2]);
            isLeap1 = isLeap(year1);
            year2 = Integer.valueOf(end[2]);
            isLeap2 = isLeap(year2);
        }

        if ((startMonth > 12 || startMonth < 1) || (endMonth > 12 || endMonth < 1)) {
            return false;
        }

        if (isLeap1&& ( startDay< 1 || startDay > DaysLeap[startMonth - 1]) ){

            return false;
        }

        if (!isLeap1&& ( startDay< 1 || startDay > DaysnotLeap[startMonth - 1]) ){

            return false;
        }


        if (isLeap2&& ( endDay< 1 || endDay > DaysLeap[endMonth - 1]) ){

            return false;
        }
        if (!isLeap2&& ( endDay< 1 || endDay > DaysnotLeap[endMonth - 1]) ){

            return false;
        }


        //TODO: check if date is after or on today


        //case 1
        //checks ordering of start or end
        if (year2 > year1){
            return true;
        }

        if (year2 < year1) {
            return false;
        }
        if (endMonth > startMonth) {
            return true;
        } 

        else if (endMonth < startMonth){
            return false;
        }
        else if (endDay > startDay) {
            return true;
        }
        else return false;

    }

    public static void main(String[] args) {
        System.out.println(validateDates("06-24", "06-24"));
        System.out.println(validateDates("06-23-2018", "06-25-2018"));
        System.out.println(validateDates("06-27", "07-01"));
        System.out.println(validateDates("07-20", "06-24"));
        System.out.println(validateDates("07-20", "06-24"));
        System.out.println(validateDates("06-30-2018", "06-21-2018"));

    }


}
