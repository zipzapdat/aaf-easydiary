package me.blog.korn123.easydiary.timeline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import me.blog.korn123.commons.utils.CommonUtils;
import me.blog.korn123.commons.utils.DateUtils;
import me.blog.korn123.commons.utils.EasyDiaryUtils;
import me.blog.korn123.commons.utils.FontUtils;
import me.blog.korn123.easydiary.R;
import me.blog.korn123.easydiary.diary.DiaryDao;
import me.blog.korn123.easydiary.diary.DiaryDto;

/**
 * Created by CHO HANJOONG on 2017-07-20.
 */

public class TimelineRecyclerViewAdapter extends RecyclerView.Adapter<TimelineRecyclerViewAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter,
        FastScrollRecyclerView.MeasurableAdapter {

    private static final int REGULAR_ITEM = 0;
    private static final int TALL_ITEM = 1;

    private final Context mContext;
    private final List<DiaryDto> mDiaryList;
    private final int mLayoutResourceId;

    public TimelineRecyclerViewAdapter(@NonNull Context context, @LayoutRes int layoutResourceId, @NonNull List<DiaryDto> list) {
        this.mContext = context;
        this.mDiaryList = list;
        this.mLayoutResourceId = layoutResourceId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLayoutResourceId, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        initFontStyle(holder);
        float fontSize = CommonUtils.loadFloatPreference(mContext, "font_size", 0);
        if (fontSize > 0) {
            holder.textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
            holder.textView2.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        }

        DiaryDto diaryDto = mDiaryList.get(position);
        if (position > 0 && StringUtils.equals(diaryDto.getDateString(), mDiaryList.get(position - 1).getDateString())) {
            holder.titleContainer.setVisibility(View.GONE);
            holder.weather.setImageResource(0);
        } else {
//            holder.title.setText(diaryDto.getDateString() + " " + DateUtils.timeMillisToDateTime(diaryDto.getCurrentTimeMillis(), "EEEE"));
            holder.title.setText(DateUtils.getFullPatternDate(diaryDto.getCurrentTimeMillis()));
            holder.titleContainer.setVisibility(View.VISIBLE);
            // 현재 날짜의 목록을 조회
            List<DiaryDto> mDiaryList = DiaryDao.readDiaryByDateString(diaryDto.getDateString());
            boolean initWeather = false;
            if (mDiaryList.size() > 0) {
                for (DiaryDto temp : mDiaryList) {
                    if (temp.getWeather() > 0) {
                        initWeather = true;
                        EasyDiaryUtils.initWeatherView(holder.weather, temp.getWeather());
                        break;
                    }
                }
                if (!initWeather) {
                    holder.weather.setVisibility(View.GONE);
                    holder.weather.setImageResource(0);
                }
            } else {
                holder.weather.setVisibility(View.GONE);
                holder.weather.setImageResource(0);
            }
        }

        if (position % 2 == 0) {
            holder.textView1.setVisibility(View.VISIBLE);
            holder.textView2.setVisibility(View.INVISIBLE);
            holder.horizontalLine1.setVisibility(View.VISIBLE);
            holder.horizontalLine2.setVisibility(View.INVISIBLE);
            holder.textView1.setText(DateUtils.timeMillisToDateTime(diaryDto.getCurrentTimeMillis(), DateUtils.TIME_HMS_PATTERN_COLONE) + "\n" + diaryDto.getTitle());
        } else {
            holder.textView1.setVisibility(View.INVISIBLE);
            holder.textView2.setVisibility(View.VISIBLE);
            holder.horizontalLine1.setVisibility(View.INVISIBLE);
            holder.horizontalLine2.setVisibility(View.VISIBLE);
            holder.textView2.setText(DateUtils.timeMillisToDateTime(diaryDto.getCurrentTimeMillis(), DateUtils.TIME_HMS_PATTERN_COLONE) + "\n" + diaryDto.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return mDiaryList.size();
    }

    @Override
    public int getViewTypeHeight(RecyclerView recyclerView, int viewType) {
//        if (viewType == REGULAR_ITEM) {
//            return recyclerView.getResources().getDimensionPixelSize(R.dimen.list_item_height);
//        } else if (viewType == TALL_ITEM) {
//            return recyclerView.getResources().getDimensionPixelSize(R.dimen.list_item_tall_height);
//        }
        return recyclerView.getResources().getDimensionPixelSize(R.dimen.list_item_height);
//        return 0;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return DateUtils.getShortPatternDate(mDiaryList.get(position).getCurrentTimeMillis());
    }

    private void initFontStyle(TimelineRecyclerViewAdapter.ViewHolder holder) {
        FontUtils.setTypeface(mContext, mContext.getAssets(), holder.textView1);
        FontUtils.setTypeface(mContext, mContext.getAssets(), holder.textView2);
        FontUtils.setTypeface(mContext, mContext.getAssets(), holder.title);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView title;
        View horizontalLine1;
        View horizontalLine2;
        ViewGroup titleContainer;
        ImageView weather;

        public ViewHolder(View itemView) {
            super(itemView);
            textView1 = (TextView) itemView.findViewById(R.id.text1);
            textView2 = (TextView) itemView.findViewById(R.id.text2);
            title = (TextView) itemView.findViewById(R.id.title);
            horizontalLine1 = itemView.findViewById(R.id.horizontalLine1);
            horizontalLine2 = itemView.findViewById(R.id.horizontalLine2);
            titleContainer = (ViewGroup) itemView.findViewById(R.id.titleContainer);
            weather = (ImageView) itemView.findViewById(R.id.weather);
        }
    }

}
