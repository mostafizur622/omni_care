package com.srapp.Model;

public class LeaveList {
    String apply_date,leave_type,from_date,to_date,status;

    public LeaveList(String apply_date, String leave_type, String from_date, String to_date,String status) {
        this.apply_date = apply_date;
        this.leave_type = leave_type;
        this.from_date = from_date;
        this.to_date = to_date;
        this.status  = status;
    }

    public String getApply_date() {
        return apply_date;
    }

    public void setApply_date(String apply_date) {
        this.apply_date = apply_date;
    }

    public String getLeave_type() {
        return leave_type;
    }

    public void setLeave_type(String leave_type) {
        this.leave_type = leave_type;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getStatus() {
        return status;
    }
}
