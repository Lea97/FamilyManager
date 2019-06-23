package dhbw.familymanager.familymanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import dhbw.familymanager.familymanager.R;

public class GalleryViewAdapter extends BaseAdapter {

    Context context;
    String photos[];
    LayoutInflater inflter;
    Activity activity;

    public GalleryViewAdapter(Context applicationContext, String[] photos, Activity activity) {
        this.context = context;
        this.photos = photos;
        this.activity = activity;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return photos.length;
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
        view = inflter.inflate(R.layout.photo_gallery_items, null);
        ImageView icon = (ImageView) view.findViewById(R.id.pictureIcon);
        StorageReference storageReference = storage.getReference(photos[i]);
        Glide.with(activity).load(storageReference).into(icon);
        return icon;
    }
}
