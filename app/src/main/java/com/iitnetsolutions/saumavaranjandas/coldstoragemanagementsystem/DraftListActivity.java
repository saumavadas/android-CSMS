package com.iitnetsolutions.saumavaranjandas.coldstoragemanagementsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DraftListActivity extends AppCompatActivity
{
    int user_id;
    String user_name, ConnectionResult;
    Connection connect;
    SimpleAdapter ADAhere;
    ListView LV_Bonds;
    TextView Draft_List_Header;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_list);

        LV_Bonds = (ListView)findViewById(R.id.LV_Bonds);
        Draft_List_Header = (TextView) findViewById(R.id.Draft_List_Header);

        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getInt("ID");
        user_name = bundle.getString("UserName");

        List<Map<String,String>> MyData = null;
        MyData= this.doInBackground(user_id);
        String[] fromwhere = { "ID","BondNO" };
        int[] viewswhere = {R.id.lblID , R.id.BondNO};
        ADAhere = new SimpleAdapter(DraftListActivity.this, MyData,R.layout.listtemplate, fromwhere, viewswhere);
        LV_Bonds.setAdapter(ADAhere);


        LV_Bonds.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                HashMap<String,Object> obj=(HashMap<String,Object>)ADAhere.getItem(position);
                String BondNO = (String)obj.get("BondNO");
                //Toast.makeText(DraftListActivity.this, ID, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(DraftListActivity.this, bondActivity.class);
                intent.putExtra("ID", user_id);
                intent.putExtra("UserName", user_name);
                intent.putExtra("barcodeVal", BondNO);
                intent.putExtra("DataMode2ins", "Draft");
                startActivity(intent);
            }
        });
    }


    public List<Map<String,String>> doInBackground(int user_id)
    {
        List<Map<String, String>> data = null;
        data = new ArrayList<Map<String, String>>();
        try
        {
            ConnectionHelper conStr = new ConnectionHelper();
            connect = conStr.connectionclasss();        // Connect to database
            if (connect == null)
            {
                ConnectionResult = "Check Your Internet Access!";
            }
            else
            {
                // Change below query according to your own database.
                String query = "SELECT * FROM StoreReceive WHERE UserID = "+ user_id +" AND DataMode = 'Draft' " ;
                Statement stmt = connect.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next())
                {
                    Map<String,String> datanum = new HashMap<String,String>();
                    datanum.put("ID",rs.getString("ReceiveID"));
                    datanum.put("BondNO",rs.getString("BondNo"));
                    data.add(datanum);
                }
                ConnectionResult = " successful";
                connect.close();
            }
        }
        catch (Exception ex)
        {
            ConnectionResult = ex.getMessage();
        }
        return data;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.show_user_profile).setTitle("Login As: "+user_name);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.show_user_profile:
                Toast.makeText(this, "Login as "+user_name, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), printProfile.class);
                intent.putExtra("UserName", user_name);
                intent.putExtra("ID", user_id);
                startActivity(intent);
                break;
            case R.id.action_logout:
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
        return true;
    }
}