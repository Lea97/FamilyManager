package dhbw.familymanager.familymanager.chat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import dhbw.familymanager.familymanager.R;

public class MessageAdapter extends ArrayAdapter<ChatBubble> {

    private Activity activity;
    private List<ChatBubble> messages;

    public MessageAdapter(Activity context, int resource, List<ChatBubble> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        dhbw.familymanager.familymanager.chat.MessageAdapter.ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        int layoutResource = 0;
        ChatBubble ChatBubble = getItem(position);
        int viewType = getItemViewType(position);

        if (ChatBubble.myMessage()) {
            layoutResource = R.layout.left_chat_bubble;
        } else {
            layoutResource = R.layout.right_chat_bubble;
        }

        if (convertView != null) {
            holder = (dhbw.familymanager.familymanager.chat.MessageAdapter.ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new dhbw.familymanager.familymanager.chat.MessageAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }

        //set message content
        holder.msg.setText(ChatBubble.getContent());

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {

        return position % 2;
    }

    private class ViewHolder {
        private TextView msg;

        public ViewHolder(View v) {
            msg = (TextView) v.findViewById(R.id.txt_msg);
        }
    }
}