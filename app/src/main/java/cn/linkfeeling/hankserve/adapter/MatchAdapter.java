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

        MatchResult matchResult = list.get(holder.getLayoutPosition());
        holder.tv_bracelet_id.setText(matchResult.getDeviceName());
        holder.tv_deviceName.setText(matchResult.getWristband());
        holder.tv_heat.setText(String.valueOf(matchResult.getMatchResult()));
        holder.tv_deviceSeq.setText(matchResult.getDeviceSeq());
        holder.tv_watchSeq.setText(matchResult.getWatchSeq());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class BLEViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_bracelet_id;
        private TextView tv_deviceName;
        private TextView tv_heat;
        private TextView tv_deviceSeq;
        private TextView tv_watchSeq;


        BLEViewHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

            tv_bracelet_id = itemView.findViewById(R.id.tv_bracelet_id);
            tv_deviceName = itemView.findViewById(R.id.tv_deviceName);
            tv_heat = itemView.findViewById(R.id.tv_heat);
            tv_deviceSeq = itemView.findViewById(R.id.tv_deviceSeq);
            tv_watchSeq = itemView.findViewById(R.id.tv_watchSeq);

        }
    }
}
