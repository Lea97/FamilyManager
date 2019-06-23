package dhbw.familymanager.familymanager.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import dhbw.familymanager.familymanager.Lists;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.Task;

public class TaskAdapter extends BaseAdapter {
    Context context;
    String files[];
    LayoutInflater inflter;
    Activity activity;

    public TaskAdapter(Context applicationContext, String[] files, Activity activity) {
        this.context = context;
        this.files = files;
        this.activity = activity;
        inflter = (LayoutInflater.from(applicationContext));
    }

    public TaskAdapter(List<Task> tasks, Lists lists) {
    }

    @Override
    public int getCount() {
        return files.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        view = inflter.inflate(R.layout.task_layout, null);
        TextView member = (TextView) view.findViewById(R.id.task_name);
        member.setText(files[i]);
        return view;
    }
}
