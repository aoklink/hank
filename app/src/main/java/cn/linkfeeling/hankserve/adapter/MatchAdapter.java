package cn.linkfeeling.hankserve.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.linkfeeling.hankserve.R;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.MatchResult;


public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.BLEViewHolder> {

    private Context context;
    private List<MatchResult> list;


    public MatchAdapter(Context context, List<MatchResult> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public BLEViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_match_display, null);
        return new BLEViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BLEViewHolder holder, int position) {

        try {
            MatchResult matchResult = list.get(holder.getLayoutPosition());
            holder.tv_bracelet_id.setText(matchResult.getDeviceName());
            holder.tv_deviceName.setText(matchResult.getWristband());
            holder.tv_heat.setText(matchResult.getMatch_time());
            holder.tv_watchNum.setText(matchResult.getWatchNum());
            holder.tv_deviceNum.setText(matchResult.getDeviceNum());
            holder.tv_two.setText(matchResult.getMatch_two());
            holder.tv_three.setText(matchResult.getMatch_three());
            holder.tv_device_status.setText(matchResult.isDeviceStatus() ? "设备data异常" : "设备data正常");
            holder.tv_watch_status.setText(matchResult.isWatchStatus() ? "手环data异常" : "手环data正常");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class BLEViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_bracelet_id;
        private TextView tv_deviceName;
        private TextView tv_heat;
        private TextView tv_two;
        private TextView tv_three;
        private TextView tv_device_status;
        private TextView tv_watch_status;
        private TextView tv_watchNum;
        private TextView tv_deviceNum;



        BLEViewHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

            tv_bracelet_id = itemView.findViewById(R.id.tv_bracelet_id);
            tv_deviceName = itemView.findViewById(R.id.tv_deviceName);
            tv_heat = itemView.findViewById(R.id.tv_heat);
            tv_two = itemView.findViewById(R.id.tv_two);
            tv_three = itemView.findViewById(R.id.tv_three);
            tv_watch_status = itemView.findViewById(R.id.tv_watch_status);
            tv_device_status = itemView.findViewById(R.id.tv_device_status);
            tv_watchNum = itemView.findViewById(R.id.tv_watchNum);
            tv_deviceNum = itemView.findViewById(R.id.tv_deviceNum);


        }
    }
}
