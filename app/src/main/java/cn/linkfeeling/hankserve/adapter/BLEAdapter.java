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


public class BLEAdapter extends RecyclerView.Adapter<BLEAdapter.BLEViewHolder> {

    private Context context;
    private List<BleDeviceInfo> list;


    public BLEAdapter(Context context, List<BleDeviceInfo> list) {
        this.context = context;
        this.list = list;
    }



    @Override
    public BLEViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ble_display, null);
        return new BLEViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BLEViewHolder holder, int position) {

        BleDeviceInfo bleDeviceInfo = list.get(holder.getLayoutPosition());
        holder.tv_bracelet_id.setText(bleDeviceInfo.getBracelet_id());
        holder.tv_deviceName.setText("".equals(bleDeviceInfo.getDevice_name()) ? "空闲区":bleDeviceInfo.getDevice_name());
        holder.tv_heat.setText(bleDeviceInfo.getHeart_rate());
        holder.tv_report.setText(bleDeviceInfo.isReport()?"上报成功":"上报失败");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class BLEViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_bracelet_id;
        private TextView tv_deviceName;
        private TextView tv_heat;
        private TextView tv_report;




        BLEViewHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

            tv_bracelet_id=itemView.findViewById(R.id.tv_bracelet_id);
            tv_deviceName=itemView.findViewById(R.id.tv_deviceName);
            tv_heat=itemView.findViewById(R.id.tv_heat);
            tv_report=itemView.findViewById(R.id.tv_report);

        }
    }
}
