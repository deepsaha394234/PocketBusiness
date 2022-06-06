package com.db.pocketbusiness;

public class DashboardData {

    public String monthly_income, daily_income, day, month;
    public boolean monthly_income_flag, daily_income_flag;
    public DashboardData(String monthly_income, String daily_income, String day, String month, boolean monthly_income_flag,
                         boolean daily_income_flag) {
        this.monthly_income = monthly_income;
        this.daily_income = daily_income;
        this.day = day;
        this.month = month;
        this.monthly_income_flag = monthly_income_flag;
        this.daily_income_flag = daily_income_flag;
    }


}
