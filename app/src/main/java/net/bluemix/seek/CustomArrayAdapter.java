package net.bluemix.seek;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.bluemix.seek.model.Uid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<Uid> {

    private Activity context;
    private List<Uid> uidList;

    static class ViewHolder {
        public TextView nameTV;
        public TextView confidenceTV;
        public TextView lonTV;
        public TextView latTV;
        public TextView dateTV;
        public TextView timeTV;
    }

    public CustomArrayAdapter(Activity context, int resource, List<Uid> uidList) {
        super(context, resource, uidList);

        this.context=context;
        this.uidList=uidList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView=convertView;
        if(rowView==null)
        {
            LayoutInflater inflater=context.getLayoutInflater();    //(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView=inflater.inflate(R.layout.single_row_layout,parent,false);

            ViewHolder viewHolder=new ViewHolder();

            viewHolder.nameTV=(TextView)rowView.findViewById(R.id.nameTV);
            viewHolder.confidenceTV=(TextView)rowView.findViewById(R.id.confidenceTV);
            viewHolder.lonTV=(TextView)rowView.findViewById(R.id.lonTV);
            viewHolder.latTV=(TextView)rowView.findViewById(R.id.latTV);
            viewHolder.dateTV=(TextView)rowView.findViewById(R.id.dateTV);
            viewHolder.timeTV=(TextView)rowView.findViewById(R.id.timeTV);

            rowView.setTag(viewHolder);
        }

        ViewHolder holder=(ViewHolder)rowView.getTag();

        String[] name=uidList.get(position).getPrediction().split("1");

        holder.nameTV.setText(name[0]);
        holder.confidenceTV.setText(uidList.get(position).getConfidence()+"");

        double[] geo={};

        if(uidList.get(position).getModel()!=null && uidList.get(position).getModel().getGeo()!=null)
        {
            geo=uidList.get(position).getModel().getGeo().getCordinates();

            if(geo.length>1) {
                holder.lonTV.setText(geo[0] + "");
                holder.latTV.setText(geo[1] + "");
            }

            DateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat formatterTime = new SimpleDateFormat("hh:mm:ss.SSS");

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(uidList.get(position).getModel().getTime()));

            holder.dateTV.setText(formatterDate.format(calendar.getTime()));
            holder.timeTV.setText(formatterTime.format(calendar.getTime()));
        }
        return rowView;
    }


}
