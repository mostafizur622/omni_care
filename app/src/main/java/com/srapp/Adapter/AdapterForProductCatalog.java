package com.srapp.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.srapp.Db_Actions.Data_Source;
import com.srapp.Product_policy_Details;
import com.srapp.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_ID;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_NAME;


public class AdapterForProductCatalog extends BaseAdapter {

    // Declare Variables
    Activity context;

    Data_Source db;
    ArrayList<HashMap<String, String>> Product_list = new ArrayList<HashMap<String, String>>();

    public AdapterForProductCatalog(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
        db = new Data_Source(context);
        this.context = context;
        Product_list = arraylistContent;
    }

    @Override
    public int getCount() {
        return Product_list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
//		return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public View getView(final int position, View convertView, ViewGroup parent) {


        final View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_catalog_row, null);
        TextView product_name =  view2.findViewById(R.id.product_name);
        TextView Bp =  view2.findViewById(R.id.Bp);
        LinearLayout layoutprice =  view2.findViewById(R.id.layoutprice);
        ImageView product_image = view2.findViewById(R.id.product_image);
        Log.e("pname",Product_list.get(position).get(PRODUCT_PRODUCT_NAME));
        product_image.setImageBitmap(getimage(Product_list.get(position).get(PRODUCT_PRODUCT_ID)));
        product_name.setText(Product_list.get(position).get(PRODUCT_PRODUCT_NAME));
        //product_name.setText("taniver");
        setPrice(layoutprice,Product_list.get(position).get(PRODUCT_PRODUCT_ID));
        Bp.setTextColor(Color.parseColor(getcolor(Product_list.get(position).get(PRODUCT_PRODUCT_ID))));


        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, Product_policy_Details.class);
                intent.putExtra("product_name",Product_list.get(position).get(PRODUCT_PRODUCT_NAME));
                intent.putExtra("product_id",Product_list.get(position).get(PRODUCT_PRODUCT_ID));
                context.startActivity(intent);
            }
        });



       // product_name.setText(Product_list.get(position).get("policy_name"));






        return view2;
    }

    private String getcolor(String product_id) {
        Data_Source ds = new Data_Source(context);

        Cursor c = ds.rawQueryCoustom("select * from policy_root_product where root_product_id='"+product_id+"'");

        c.moveToFirst();
        if (c!=null && c.getCount()>0) {
          return "#020D57";
        }else {
            return "#959595";
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPrice(LinearLayout layoutprice, String product_id) {

        Data_Source ds = new Data_Source(context);

        Cursor c = ds.rawQueryCoustom("select min_quantity, price from product_combinations where product_id='"+product_id+"' AND combination_id=0 and effective_date=(SELECT max(effective_date) from product_combinations where product_id='"+product_id+"' AND price!='0' and combination_id=0 and effective_date<='"+getCurrentDate()+"') order by min_quantity ASC limit 3");

        c.moveToFirst();
        layoutprice.setWeightSum(c.getCount()+1);
        if (c!=null && c.getCount()>0){
                do {
                    LinearLayout layout = new LinearLayout(context);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
                    layout.setLayoutParams(params);
                    layout.setWeightSum(2);

                    TextView qty = new TextView(context);
                    TextView price = new TextView(context);
                    qty.setLayoutParams(params);
                    price.setLayoutParams(params);
                    qty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    price.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    qty.setText(c.getString(0));
                    price.setText(c.getString(1));

                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.addView(qty);
                    layout.addView(price);
                    layoutprice.addView(layout);
                }while (c.moveToNext());
            }else {

            Cursor c1 = ds.rawQueryCoustom("select  price from product_price where product_id='"+product_id+"' AND  effective_date=(SELECT max(effective_date) from product_combinations where product_id='"+product_id+"' AND effective_date<='"+getCurrentDate()+"')  limit 1");
            c1.moveToFirst();
            if (c!=null && c.getCount()>0) {
                do {

                    LinearLayout layout = new LinearLayout(context);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    layout.setLayoutParams(params);
                    layout.setWeightSum(2);

                    TextView qty = new TextView(context);
                    TextView price = new TextView(context);
                    qty.setLayoutParams(params);
                    price.setLayoutParams(params);
                    qty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    price.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    qty.setText("0");
                    price.setText(c1.getString(0));

                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.addView(qty);
                    layout.addView(price);
                    layoutprice.addView(layout);

                } while (c1.moveToNext());
            }
        }


    }

    public String getCurrentDate (){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);

    }

    private Bitmap getimage(String s) {

        File filepath =context.getFilesDir();
        File imgFile = new File(filepath.getAbsolutePath()
                + "/SMC_image/");

        if(imgFile.exists()){
                File pic = new File(imgFile.getAbsolutePath()+File.separator+s+".png");
            if (!pic.exists()){
               return BitmapFactory.decodeResource(context.getResources(),R.drawable.place_holder);
            }else
            return  BitmapFactory.decodeFile(imgFile.getAbsolutePath()+File.separator+s+".png");

        }else {
            Log.e("imgFile","imgFile = "+imgFile.toString());
            return null;
        }
    }


}
