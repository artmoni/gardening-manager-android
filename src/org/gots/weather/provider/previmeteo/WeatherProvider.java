package org.gots.weather.provider.previmeteo;

import java.util.Date;

import org.gots.weather.WeatherConditionInterface;

public interface WeatherProvider {

    //    @Override
    //    protected WeatherConditionInterface doInBackground(Object... arg0) {
    public abstract WeatherConditionInterface getCondition(Date requestedDay);

    //    @Override
    //    protected void onPostExecute(WeatherConditionInterface conditionInterface) {
    //        updateCondition(conditionInterface, requestedDay);
    //
    //        if (iserror) {
    //            // Toast.makeText(mContext,
    //            // mContext.getResources().getString(R.string.weather_citynotfound),
    //            // 30).show();
    //            Log.w(TAG, "Error updating weather");
    //
    //            // cache.clean(url);
    //        } else
    //            Log.d(TAG, "Weather updated from " + queryString);
    //
    //        super.onPostExecute(conditionInterface);
    //    }
    public abstract WeatherConditionInterface updateCondition(WeatherConditionInterface condition, Date day);

}