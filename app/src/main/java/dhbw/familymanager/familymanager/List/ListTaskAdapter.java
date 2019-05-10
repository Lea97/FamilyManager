package dhbw.familymanager.familymanager.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.Task;

public class ListTaskAdapter extends BaseAdapter {
    private List<Task> tasks;
    private LayoutInflater inflator;
    private Task task;

    public ListTaskAdapter(List<Task> tasks, Context context) {
        this.tasks = tasks;
        inflator = LayoutInflater.from(context);
    }

    public int getCount() {
        return tasks.size();
    }

    public Object getItem(int position) {
        return tasks.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ListTaskAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = inflator.inflate(R.layout.list_layout, parent, false);
            holder = new ListTaskAdapter.ViewHolder();
            holder.task_view = (TextView) convertView.findViewById(R.id.list_task_view);
            holder.done_box = (CheckBox) convertView.findViewById(R.id.list_checkbox);
            convertView.setTag(holder);

        } else {
            holder = (ListTaskAdapter.ViewHolder) convertView.getTag();
        }
        task = (Task) getItem(position);
        holder.done_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tasks.get(position).setIsDone(isChecked);

                if (isChecked) {
                    holder.task_view.setPaintFlags(holder.task_view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }else{
                    holder.task_view.setPaintFlags(0);
                }
            }
        });

        holder.task_view.setText(task.getTaskContent());
        holder.done_box.setChecked(task.isDone());

        return convertView;
    }

    static class ViewHolder {

        TextView task_view;
        CheckBox done_box;
    }

}
