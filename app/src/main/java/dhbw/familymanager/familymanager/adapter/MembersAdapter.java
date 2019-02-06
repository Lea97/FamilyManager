package dhbw.familymanager.familymanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.User;

public class MembersAdapter extends BaseAdapter {

        Context context;
        User users[];
        LayoutInflater inflter;
        Activity activity;

        public MembersAdapter(Context applicationContext, User[] useres, Activity activity) {
            this.context = context;
            this.users = useres;
            this.activity = activity;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return users.length;
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
            view = inflter.inflate(R.layout.list_item_memberlist, null);
            TextView member = (TextView) view.findViewById(R.id.textViewMember);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            member.setText(users[i].getEmail());
            StorageReference storageReference = storage.getReference(users[i].getPicturePath());
            Glide.with(activity).load(storageReference).into(icon);
            return view;
        }


}
