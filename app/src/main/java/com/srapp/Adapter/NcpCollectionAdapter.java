package com.srapp.Adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.NcpCollection;
import com.srapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NcpCollectionAdapter extends RecyclerView.Adapter<NcpCollectionAdapter.Holder> {

    ArrayList<HashMap<String,String>>data = new ArrayList<>();

    Context context;

    private int year1;
    private int month1;
    private int day1;
    public NcpCollectionAdapter(Context context,ArrayList<HashMap<String,String>> data) {
        this.context=context;
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ncp_collection_item,parent,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.product.setText(String.valueOf(data.get(position).get("product_name")));
        holder.batch.setText(String.valueOf(data.get(position).get("batch")));
        holder.quantity.setText(String.valueOf(data.get(position).get("qty")));
        holder.exp.setText(String.valueOf(data.get(position).get("exp")));

        holder.product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.batch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("beforeTextChanged",charSequence+" pos=");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("onTextChanged",charSequence+" pos=");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e("batch",data.get(position).toString()+" pos="+position);
                data.get(position).put("batch", editable.toString());

            }
        });

        holder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                data.get(position).put("qty", editable.toString());

            }
        });

        DatePickerDialog.OnDateSetListener dateListener =  new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth,
                                  int selectedDay) {
                // TODO Auto-generated method stub

                year1  = selectedYear;
                month1 = selectedMonth;
                day1  = selectedDay;
                String day="",month="", MONTH="";
                if(day1<10)
                    day="0"+String.valueOf(day1);
                else
                    day=String.valueOf(day1);
                if(month1+1<10)
                    month="0"+String.valueOf(month1+1);
                else
                    month=String.valueOf(month1+1);
                Log.e("day","Day"+ String.valueOf(day));
                Log.e("month", String.valueOf(month));

                if(month.equals("01"))
                {
                    MONTH = "Jan";
                }
                else if(month.equals("02"))
                {
                    MONTH = "Feb";
                }
                else if(month.equals("03"))
                {
                    MONTH = "Mar";
                }
                else if(month.equals("04"))
                {
                    MONTH = "Apr";
                }
                else if(month.equals("05"))
                {
                    MONTH = "May";
                }
                else if(month.equals("06"))
                {
                    MONTH = "Jun";
                }
                else if(month.equals("07"))
                {
                    MONTH = "Jul";
                }
                else if(month.equals("08"))
                {
                    MONTH = "Aug";
                }
                else if(month.equals("09"))
                {
                    MONTH = "Sep";
                }
                else if(month.equals("10"))
                {
                    MONTH = "Oct";
                }
                else if(month.equals("11"))
                {
                    MONTH = "Nov";
                }
                else if(month.equals("12"))
                {
                    MONTH = "Dec";
                }
                String date=MONTH+"-"+String.valueOf(year1).substring(2,4);
                data.get(position).put("exp", date);
                notifyDataSetChanged();



            }



        };

        holder.exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                year1  = c.get(Calendar.YEAR);
                month1 = c.get(Calendar.MONTH);
                day1   = c.get(Calendar.DAY_OF_MONTH);



                Log.e("Called", "Date Picker!");
                new DatePickerDialog(context, dateListener, year1, month1,
                        day1).show();


            }
        });

    }




    @Override
    public int getItemCount() {
        Log.e("Called", data.size()+ "Date Picker!");

        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView product;
        EditText batch, quantity;
        TextView exp;
        ImageView action;

        public Holder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.product_name);
            batch = itemView.findViewById(R.id.batch);
            exp = itemView.findViewById(R.id.exp_date);
            quantity = itemView.findViewById(R.id.qty);
            action = itemView.findViewById(R.id.dlt_product_sales_item);
        }
    }
}
