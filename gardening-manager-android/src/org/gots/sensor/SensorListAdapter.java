package org.gots.sensor;

import java.util.List;

import org.gots.R;
import org.gots.sensor.parrot.ParrotSensor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SensorListAdapter extends BaseAdapter {

    private Context mContext;

    private List<ParrotSensor> mSensors;

    public SensorListAdapter(Context context, List<ParrotSensor> sensors) {
        mContext = context;
        mSensors = sensors;
    }

    class SensorHolder {
        ImageView sensorImg;

        TextView sensorName;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        SensorHolder h = null;
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (v == null) {
            v = vi.inflate(R.layout.list_sensor, null);
            h = new SensorHolder();
            h.sensorImg = (ImageView) v.findViewById(R.id.sensorImage);
            h.sensorName = (TextView) v.findViewById(R.id.sensorLocationName);

            v.setTag(h);
        } else {
            h = (SensorHolder) v.getTag();
        }
        h.sensorImg.setImageDrawable(mContext.getResources().getDrawable(getItem(position).getResourceDrawable()));
        h.sensorName.setText(getItem(position).getNickname());
        return v;
    }

    @Override
    public int getCount() {
        return mSensors.size();
    }

    @Override
    public ParrotSensor getItem(int position) {
        return mSensors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}