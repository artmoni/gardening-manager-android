package org.gots.weather.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.view.WeatherView;

import java.util.List;

public class WeatherWidgetAdapter extends BaseAdapter {

    Context mContext;
    List<WeatherConditionInterface> mConditions;
    private int mType;

    public WeatherWidgetAdapter(Context context, int type, List<WeatherConditionInterface> conditions) {
        mContext = context;
        mType = type;
        mConditions = conditions;
    }

    @Override
    public int getCount() {
        return mConditions.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        WeatherView weatherItem = (WeatherView) convertView;

        if (convertView == null)
            weatherItem = new WeatherView(mContext);

        weatherItem.setType(mType);

        weatherItem.setWeather(getItem(position));

        return weatherItem;
    }

    @Override
    public WeatherConditionInterface getItem(int position) {
        return mConditions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setConditions(List<WeatherConditionInterface> conditions) {
        this.mConditions = conditions;
    }

}
