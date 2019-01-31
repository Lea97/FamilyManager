package dhbw.familymanager.familymanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ShowMemberFragment extends Fragment {

    public void ShowMemberFragment(){}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        List<String> memberList = new ArrayList<String>();
        memberList.add("testNutzer");

        ArrayAdapter<String> memeberlisteAdapter = new ArrayAdapter<>(getActivity(), // Die aktuelle Umgebung (diese Activity)
                        R.layout.list_item_memberlist, // ID der XML-Layout Datei
                        R.id.list_item_memberlist_textview, // ID des TextViews
                        memberList); // Beispieldaten in einer ArrayList

        // ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.activity_list_item,memberList);
        //  ListView listView = (ListView) findViewById(R.id.member_list);
        // listView.setAdapter(adapter);

        View rootView = inflater.inflate(R.layout.show_members, container, false);

        ListView memberlisteListView = (ListView) rootView.findViewById(R.id.listview_members);
        memberlisteListView.setAdapter(memeberlisteAdapter);

        return rootView;

    }

}
