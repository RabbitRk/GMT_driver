package mark1.gmt.rk_rabbitt.gmt_driver;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mark1.gmt.rk_rabbitt.gmt_driver.DBHelper.recycleAdapter;

/**
 * Created by Rabbitt on 06,February,2019
 */
public class job_alert_adapter extends RecyclerView.Adapter<job_alert_adapter.holder> {

    List<recycleAdapter> dataModelArrayList;

    public job_alert_adapter(List<recycleAdapter> datamodelArray) {
        this.dataModelArrayList = datamodelArray;
    }

    @NonNull
    @Override
    public job_alert_adapter.holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.job_alert_item, null);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull job_alert_adapter.holder holder, int i) {
        recycleAdapter dataModel = dataModelArrayList.get(i);
        holder.book_id.setText(dataModel.getBook_id());
        holder.v_type.setText(dataModel.getV_type());
        holder.travel_type.setText(dataModel.getTravel_type());
        holder.ori.setText(dataModel.getOrigin());
        holder.dest.setText(dataModel.getDestination());
        holder.dateof.setText(dataModel.getTimeat());
        holder.package_id.setText(dataModel.getPackage_id());
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    class holder extends RecyclerView.ViewHolder{

        TextView book_id, travel_type, v_type, dateof, ori, dest, package_id;

        holder(@NonNull View itemView) {
            super(itemView);

            book_id = itemView.findViewById(R.id.book_id);
            travel_type = itemView.findViewById(R.id.travel_type);
            v_type = itemView.findViewById(R.id.v_type);
            dateof = itemView.findViewById(R.id.dateof);
            ori = itemView.findViewById(R.id.ori);
            dest = itemView.findViewById(R.id.dest);
            package_id = itemView.findViewById(R.id.packed);
        }
    }
}
