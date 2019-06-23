package dhbw.familymanager.familymanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;

import dhbw.familymanager.familymanager.R;

public class FolderAdapter extends BaseAdapter {

    Context context;
    String files[];
    LayoutInflater inflter;
    Activity activity;

    public FolderAdapter(Context applicationContext, String[] files, Activity activity) {
        this.context = context;
        this.files = files;
        this.activity = activity;
        inflter = (LayoutInflater.from(applicationContext));
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
        view = inflter.inflate(R.layout.folder_listview_items, null);
        TextView member = (TextView) view.findViewById(R.id.textViewFolderName);
        member.setText(files[i]);
        return view;
    }
}
