package com.srapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.srapp.Model.LeaveList;
import com.srapp.R;

import java.util.ArrayList;

public class LeaveListAdapter extends RecyclerView.Adapter<LeaveListAdapter.MyViewHolder> {
    ArrayList<LeaveList> leaveLists=new ArrayList<>();
    Context context;

    public LeaveListAdapter(ArrayList<LeaveList> leaveLists, Context context) {
        this.leaveLists = leaveLists;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.leave_list_child,parent,false);

        return  new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.applyDate.setText("Apply Date: "+leaveLists.get(position).getApply_date());
        holder.startDateEndDate.setText("From : "+leaveLists.get(position).getFrom_date()+"    To : "+leaveLists.get(position).getTo_date());
        holder.leaveType.setText(leaveLists.get(position).getLeave_type());
        holder.status.setText(leaveLists.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return leaveLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView applyDate,startDateEndDate,leaveType,status;
        public MyViewHolder(android.view.View itemView) {
            super(itemView);
            applyDate = itemView.findViewById(R.id.apply_date);
            leaveType = itemView.findViewById(R.id.leave_type);
            startDateEndDate = itemView.findViewById(R.id.fromDateToDate);
            status = itemView.findViewById(R.id.status);
        }
    }
}
