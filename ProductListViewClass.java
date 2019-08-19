package vanunu.deeznuts;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Nevo Vanunu on 04/11/2017.
 */

public class ProductListViewClass extends ArrayAdapter<String> {
    private String[] TitleArr,PriceArr,ImageUrlArr,IdArr,status;
    private Activity context;
    public ProductListViewClass(Activity context, String[] TitleArr, String[] PriceArr, String[]ImageUrlArr, String[]IdArr,String[] status) {
        super(context, R.layout.productlistitemrow,IdArr);
        this.context=context;
        this.TitleArr=TitleArr;
        this.PriceArr=PriceArr;
        this.ImageUrlArr=ImageUrlArr;
        this.IdArr=IdArr;
        this.status=status;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r=convertView;
        ViewHolder viewHolder;
        if(r==null)
        {
            r=context.getLayoutInflater().inflate(R.layout.productlistitemrow,null);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder) r.getTag();

        }

        Picasso.with(context).load(ImageUrlArr[position]).into(viewHolder.ivw);
        viewHolder.tvw1.setText(TitleArr[position]);
        viewHolder.tvw2.setText(PriceArr[position]);
        viewHolder.tvw3.setText(IdArr[position]);
        if(status[position].equals("active")) {viewHolder.tvw1.setTextColor(Color.GREEN);viewHolder.tvw2.setTextColor(Color.GREEN);viewHolder.tvw3.setTextColor(Color.GREEN);}
        if(status[position].equals("inactive")){ viewHolder.tvw1.setTextColor(Color.RED);viewHolder.tvw2.setTextColor(Color.RED);viewHolder.tvw3.setTextColor(Color.RED);}

        return r;
    }

    class ViewHolder
    {
        TextView tvw1,tvw2,tvw3;
        ImageView ivw;
        ViewHolder(View v)
        {
            tvw1=(TextView) v.findViewById(R.id.PTitle);
            tvw2=(TextView) v.findViewById(R.id.PPrice);
            tvw3=(TextView) v.findViewById(R.id.PID);
            ivw=(ImageView) v.findViewById(R.id.PImage);
        }
    }
}