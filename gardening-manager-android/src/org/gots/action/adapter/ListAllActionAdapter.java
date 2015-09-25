/**
 * ****************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p>
 * Contributors:
 * sfleury - initial API and implementation
 * ****************************************************************************
 */
package org.gots.action.adapter;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.gots.R;
import org.gots.action.ActionOnSeed;
import org.gots.action.BaseAction;
import org.gots.action.bean.PhotoAction;
import org.gots.action.util.ActionState;
import org.gots.action.view.ActionWidget;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.seed.view.GrowingSeedWidget;
import org.gots.weather.WeatherManager;
import org.gots.weather.view.WeatherView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ListAllActionAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<ActionOnSeed> actions = new ArrayList<ActionOnSeed>();

    private int current_status = STATUS_DONE;

    public static final int STATUS_TODO = 0;

    public static final int STATUS_DONE = 1;

    private WeatherManager manager;

    public static final int THUMBNAIL_HEIGHT = 48;

    public static final int THUMBNAIL_WIDTH = 66;

    private static final String TAG = "ListAllActionAdapter";

    public ListAllActionAdapter(Context context, List<ActionOnSeed> allActions, int status) {
        this.mContext = context;
        current_status = status;
        if (allActions != null) {
            actions.addAll(allActions);
        }

        if (current_status == STATUS_TODO)
            Collections.sort(actions, new IActionAscendantComparator());
        else
            Collections.sort(actions, new IActionDescendantComparator());
        manager = new WeatherManager(mContext);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return actions.size();
    }

    @Override
    public ActionOnSeed getItem(int position) {
        return actions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Holder {
        ActionWidget actionWidget;

        GrowingSeedWidget seedView;

        TextView textviewActionDate;

        Switch switchActionStatus;

        TextView textviewActionDescription;

        WeatherView weatherView;

        ImageView seedImage;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ActionOnSeed currentAction = getItem(position);
        final GrowingSeed seed = GotsGrowingSeedManager.getInstance().initIfNew(mContext).getGrowingSeedById(
                currentAction.getGrowingSeedId());
        View ll = convertView;
        Holder holder;

        // if (convertView == null) {
        // ll = new LinearLayout(mContext);
        if (ll == null) {
            holder = new Holder();
            ll = LayoutInflater.from(mContext).inflate(R.layout.list_action, parent, false);
            holder.actionWidget = (ActionWidget) ll.findViewById(R.id.idActionView);
            holder.seedView = (GrowingSeedWidget) ll.findViewById(R.id.idSeedView);
            holder.textviewActionDate = (TextView) ll.findViewById(R.id.IdSeedActionDate);
            holder.switchActionStatus = (Switch) ll.findViewById(R.id.switchSeedActionStatus);
            holder.textviewActionDescription = (TextView) ll.findViewById(R.id.IdSeedActionDescription);
            holder.weatherView = (WeatherView) ll.findViewById(R.id.idWeatherView);
            holder.seedImage = (ImageView) ll.findViewById(R.id.imageviewPhoto);

            ll.setTag(holder);
        } else
            holder = (Holder) ll.getTag();

        if (seed != null && BaseAction.class.isInstance(currentAction)) {
            holder.seedView.setSeed(seed);

            SimpleDateFormat dateFormat = new SimpleDateFormat(" dd/MM/yyyy", Locale.FRANCE);

            if (currentAction.getDateActionDone() == null) {
                holder.switchActionStatus.setChecked(false);
                holder.switchActionStatus.setEnabled(true);
                Calendar rightNow = Calendar.getInstance();
                rightNow.setTime(seed.getDateSowing());
                rightNow.add(Calendar.DAY_OF_YEAR, currentAction.getDuration());
                holder.textviewActionDate.setText(dateFormat.format(rightNow.getTime()));

                holder.switchActionStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked)
                            showNoticeDialog(position, seed, currentAction, buttonView);

                    }
                });

                holder.weatherView.setVisibility(View.GONE);

            } else {
                // textviewActionStatus.setText(mContext.getResources().getString(R.string.seed_action_done));
                holder.switchActionStatus.setEnabled(false);
                holder.switchActionStatus.setChecked(true);
                Calendar rightNow = Calendar.getInstance();
                if (currentAction.getDateActionDone() != null) {
                    rightNow.setTime(currentAction.getDateActionDone());
                    holder.textviewActionDate.setText(dateFormat.format(rightNow.getTime()));

                    if (currentAction.getData() != null) {
                        String data = (String) currentAction.getData();
                        String description = data.length() < 30 ? data : data.substring(0, 29).concat("...");
                        holder.textviewActionDescription.setText(description);
                    } else
                        holder.textviewActionDescription.setVisibility(View.GONE);

                    holder.weatherView.setWeather(manager.getCondition(rightNow.getTime()));

                    currentAction.setState(ActionState.NORMAL);

                    if (PhotoAction.class.isInstance(currentAction) && currentAction.getData() != null) {
                        final File imgFile = new File(currentAction.getData().toString());

                        try {
                            Bitmap imageBitmap = getThumbnail(imgFile);
                            // Bitmap imageBitmap = getThumbnail(
                            // mContext.getContentResolver(),
                            // imgFile.getAbsolutePath());
                            int padding = (THUMBNAIL_WIDTH - imageBitmap.getWidth()) / 2;
                            holder.seedImage.setPadding(padding, 0, padding, 0);
                            holder.seedImage.setImageBitmap(imageBitmap);

                            holder.seedImage.setVisibility(View.VISIBLE);
                            holder.weatherView.setVisibility(View.GONE);

                            holder.seedImage.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.parse("file://" + imgFile.getAbsolutePath()), "image/*");
                                    mContext.startActivity(intent);
                                }
                            });
                        } catch (Exception e) {
                            Log.e(getClass().getName(),
                                    "imgFile.getPath()=" + imgFile.getPath() + " - " + e.getMessage());
                        }

                    }
                }
            }
            holder.actionWidget.setState(currentAction.getState());
            holder.actionWidget.setAction(currentAction);

        }
        return ll;
    }

    private Bitmap getThumbnail(File imgFile) throws Exception {
        Log.d("getThumbnail", imgFile.getPath());
        Bitmap imageBitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2;
            imageBitmap = BitmapFactory.decodeFile(imgFile.getPath(), options);
            // imageBitmap = BitmapFactory.decodeByteArray(mImageData, 0,
            // mImageData.length);
            Float width = Float.valueOf(imageBitmap.getWidth());
            Float height = Float.valueOf(imageBitmap.getHeight());
            Float ratio = width / height;
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) (THUMBNAIL_HEIGHT * ratio), THUMBNAIL_HEIGHT,
                    false);
        } catch (Exception e) {
            throw new Exception("Image file cannot be decoded :" + imgFile.getAbsolutePath());
        }
        return imageBitmap;
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {
        Uri selectedImageUri = Uri.fromFile(new File(path));
        // Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        // new String[] { MediaStore.MediaColumns._ID },
        // MediaStore.MediaColumns.DATA + "=?", new String[] { path },
        // null);
        Cursor ca = cr.query(selectedImageUri, null, MediaStore.Images.Media.DATA + " like ? ",
                new String[]{selectedImageUri.getPath()}, null);

        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            Log.d(TAG, ca.getString(ca.getColumnIndex(MediaStore.MediaColumns.DATA)));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }

        ca.close();
        return null;

    }

    class IActionAscendantComparator implements Comparator<BaseAction> {
        @Override
        public int compare(BaseAction obj1, BaseAction obj2) {
            int result = 0;
            if (obj1.getDuration() >= 0 && obj2.getDuration() >= 0) {
                result = obj1.getDuration() < obj2.getDuration() ? -1 : 0;
            }

            return result;
        }
    }

    class IActionDescendantComparator implements Comparator<ActionOnSeed> {
        @Override
        public int compare(ActionOnSeed obj1, ActionOnSeed obj2) {
            int result = 0;
            if (obj1.getDateActionDone() != null && obj2.getDateActionDone() != null) {
                result = obj1.getDateActionDone().getTime() > obj2.getDateActionDone().getTime() ? -1 : 0;
            }

            return result;
        }
    }

    private void showNoticeDialog(final int position, final GrowingSeed seed, final ActionOnSeed currentAction,
                                  final CompoundButton switchButton) {

        final EditText userinput = new EditText(mContext.getApplicationContext());
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(userinput).setTitle("Add a note about your task");
        builder.setPositiveButton(currentAction.getName(), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                new AsyncTask<ActionOnSeed, Integer, ActionOnSeed>() {
                    @Override
                    protected ActionOnSeed doInBackground(ActionOnSeed... params) {

                        ActionOnSeed actionItem = params[0];
                        if (ActionOnSeed.class.isInstance(actionItem)) {
                            actionItem.setData(userinput.getText().toString());
                            actionItem.execute(seed);
                        }
                        return actionItem;
                    }

                    @Override
                    protected void onPostExecute(ActionOnSeed result) {
                        Toast.makeText(mContext, "Action done : " + result.getName(), Toast.LENGTH_SHORT).show();
                        actions.set(position, result);
                        // actions.remove(position);
                        notifyDataSetChanged();
                        mContext.sendBroadcast(new Intent(BroadCastMessages.ACTION_EVENT));
                        super.onPostExecute(result);
                    }
                }.execute(currentAction);
            }
        }).setNegativeButton(mContext.getResources().getString(R.string.button_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        switchButton.setChecked(false);

                    }
                });
        // AlertDialog dialog = builder.create();
        builder.setCancelable(true);
        builder.show();

    }
}
