package com.brapeba.shutdown;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joanmi on 18-Oct-15.
 */
public class Main extends AppCompatActivity
{
    private String[] values = new String[] { "Power off", "Reboot", "Soft Reboot", "Recovery", "Download mode" };
    private View prevView;
    private int selection;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /*
        LinearLayout acLayout = new LinearLayout(this);
        LinearLayout.LayoutParams acLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setContentView(acLayout, acLp);
        */
        final ListView lvShutdown = new ListView(this);
        LinearLayout.LayoutParams lvLp = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        lvShutdown.setLayoutParams(lvLp);
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) { list.add(values[i]); }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,R.layout.shlistview, list);
        lvShutdown.setAdapter(adapter);
        LinearLayout adLayout = new LinearLayout(this);
        LinearLayout.LayoutParams adLp = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        adLayout.setLayoutParams(adLp);
        adLayout.addView(lvShutdown);
        AlertDialog.Builder abBuilder = new AlertDialog.Builder(this, R.style.AppTheme_PopupOverlay);
        abBuilder.setView(adLayout);
        lvShutdown.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (prevView != null)
                    ((TextView) prevView).setTextColor(Color.parseColor("#3F51B5"));
                prevView = view;
                ((TextView) view).setTextColor(Color.parseColor("#FF4081"));
                selection = position;
                //Snackbar.make(view,"Selected= "+selection,Snackbar.LENGTH_SHORT).show();
            }
        });
        abBuilder.setNegativeButton(R.string.string2, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                finish();
            }
        });
        abBuilder.setPositiveButton(R.string.string3, new DialogInterface.OnClickListener()
        {
            String[] wtd = new String[]{""};
            String cmd;
            byte[] btd;

            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                switch (selection)
                {
                    case 0: // power off
                        //wtd = new String[]{"su", "-c", "reboot", "-p"};
                        cmd="reboot -p";
                        break;
                    case 1: // reboot
                        //wtd = new String[]{"su", "-c", "reboot"};
                        cmd="reboot";
                        break;
                    case 2: // soft reboot
                        //wtd = new String[]{"su", "-c", "killall", "zygote"};
                        cmd="killall zygote";
                        break;
                    case 3: // recovery
                        //wtd = new String[]{"su", "-c", "reboot","recovery"};
                        cmd="reboot recovery";
                        break;
                    case 4: // download mode
                        //wtd = new String[]{"su", "-c", "reboot", "download"};
                        cmd="reboot download";
                        break;
                }
                try
                {
                    btd = cmd.getBytes();
                    Process p = Runtime.getRuntime().exec("su");
                    OutputStream stream = p.getOutputStream();
                    stream.write(btd);
                    stream.flush();
                    stream.close();
                    p.waitFor();
                    //Process proc = Runtime.getRuntime().exec(wtd);
                    //proc.waitFor();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                finish();
            }
        });
        abBuilder.create().show();
    }

    private class StableArrayAdapter extends ArrayAdapter<String>
    {
        private Context mContext;
        private int id;
        private List <String>items ;
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects)
        {
            super(context, textViewResourceId, objects);
            mContext=context;
            id = textViewResourceId;
            items = objects ;
            for (int i = 0; i < objects.size(); ++i)
            {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public View getView(int position, View v, ViewGroup parent)
        {
            View mView = v ;
            if(mView == null)
            {
                LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(id, null);
            }

            TextView text = (TextView) mView;

            if(items.get(position) != null )
            {
                text.setTextColor(Color.parseColor("#3F51B5"));
                text.setText(items.get(position));
            }

            return mView;
        }

        @Override
        public long getItemId(int position)
        {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds()
        {
            return true;
        }
    }
}
