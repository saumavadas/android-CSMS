package com.iitnetsolutions.saumavaranjandas.coldstoragemanagementsystem;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.text.Html;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.math.BigDecimal;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
//import java.sql.SQLException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class bondActivity extends AppCompatActivity
{
    private Button submit_form;
    private EditText viewthe_barcode, editCustomerName, editAddress, editCity, editPostOfficeCode, editDistrict, editState,
            editPIN, editTestWeight1, editTestWeight2, editTestWeight3,
            editQuality, editShadePosition, editVehicleNo, editRemarks, editPacket,
            editConfirmPacket;
    private TextView txtShadePosition, txtPacket, txtCPacket;
    public String IP;
    String ConnectionResult = "";
    int user_id;
    int Last_insert_id_val;
    public int BondId;
    public String user_name;
    public String barcodeVal, DataMode;
    private String qry;

    Connection connect;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bond);

        Bundle bundle = getIntent().getExtras();
        user_id         = bundle.getInt("ID");
        user_name       = bundle.getString("UserName");
        barcodeVal      = bundle.getString("barcodeVal");
        DataMode        = bundle.getString("DataMode2ins");


        submit_form         = (Button)findViewById(R.id.submit_form);
        viewthe_barcode     = (EditText)findViewById(R.id.viewthe_barcode);
        editCustomerName    = (EditText)findViewById(R.id.editCustomerName);
        editAddress         = (EditText)findViewById(R.id.editAddress);
        editCity            = (EditText)findViewById(R.id.editCity);
        editDistrict        = (EditText)findViewById(R.id.editDistrict);
        editPostOfficeCode  = (EditText)findViewById(R.id.editPostOfficeCode);
        editState           = (EditText)findViewById(R.id.editState);
        editPIN             = (EditText)findViewById(R.id.editPIN);
        editTestWeight1     = (EditText)findViewById(R.id.editTestWeight1);
        editTestWeight2     = (EditText)findViewById(R.id.editTestWeight2);
        editTestWeight3     = (EditText)findViewById(R.id.editTestWeight3);
        editQuality         = (EditText)findViewById(R.id.editQuality);
        editShadePosition   = (EditText)findViewById(R.id.editShadePosition); //Required
        editVehicleNo       = (EditText)findViewById(R.id.editVehicleNo);
        editRemarks         = (EditText)findViewById(R.id.editRemarks);
        editPacket          = (EditText)findViewById(R.id.editPacket);
        editConfirmPacket   = (EditText)findViewById(R.id.editConfirmPacket);//Required

        txtShadePosition = (TextView)findViewById(R.id.txtShadePosition);
        txtPacket = (TextView)findViewById(R.id.txtPacket);
        txtCPacket = (TextView)findViewById(R.id.txtCPacket);

        txtShadePosition.setText("Shade Position:*", TextView.BufferType.SPANNABLE);
        Spannable span = (Spannable) txtShadePosition.getText();
        span.setSpan(new ForegroundColorSpan(0xFFFF0000), 15, "Shade Position:*".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);


        txtPacket.setText("Packet:*", TextView.BufferType.SPANNABLE);
        Spannable span1 = (Spannable) txtPacket.getText();
        span1.setSpan(new ForegroundColorSpan(0xFFFF0000), 7, "Packet:*".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        txtCPacket.setText("Confirm Packet:*", TextView.BufferType.SPANNABLE);
        Spannable span2 = (Spannable) txtCPacket.getText();
        span2.setSpan(new ForegroundColorSpan(0xFFFF0000), 15, "Confirm Packet:*".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        viewthe_barcode.setText(barcodeVal);
        viewthe_barcode.setKeyListener(null);
        viewthe_barcode.setEnabled(false);
        editCustomerName.requestFocus();

        editTestWeight1.setText("50");
        editTestWeight2.setText("50");
        editTestWeight3.setText("50");

        //Toast.makeText(bondActivity.this, "barcodeVal = "+barcodeVal, Toast.LENGTH_SHORT).show();
        //Toast.makeText(bondActivity.this, "DataMode = "+DataMode, Toast.LENGTH_SHORT).show();
        if(DataMode.equals("Draft"))
        {
            ConnectionHelper conStr = new ConnectionHelper();
            connect = conStr.connectionclasss();
            try
            {
                if (connect == null)
                {
                    ConnectionResult = "Check Your Internet Access!";
                    AlertDialog alertDialog = new AlertDialog.Builder(bondActivity.this).create();
                    alertDialog.setTitle("Login Message");
                    alertDialog.setMessage(ConnectionResult);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                else
                {
                    String query = "SELECT * FROM StoreReceive WHERE BondNo = "+ new BigInteger(viewthe_barcode.getText().toString()) +" " ;
                    Statement stmt = connect.createStatement();
                    //Toast.makeText(MainActivity.this, query.toString(), Toast.LENGTH_LONG).show();
                    ResultSet rs = stmt.executeQuery(query);

                    ConnectionResult = " successful";

                    while (rs.next())
                    {
                        editCustomerName.setText(rs.getString("BondHolderName"));
                        editAddress.setText(rs.getString("Address1"));
                        editCity.setText(rs.getString("CityVillage"));
                        editPostOfficeCode.setText(rs.getString("Post"));
                        editDistrict.setText(rs.getString("District"));
                        editState.setText(rs.getString("State"));
                        editPIN.setText(rs.getString("PIN"));
                        editTestWeight1.setText(rs.getString("TestWeight1"));
                        editTestWeight2.setText(rs.getString("TestWeight2"));
                        editTestWeight3.setText(rs.getString("TestWeight3"));
                        editQuality.setText(rs.getString("Quality"));
                        editShadePosition.setText(rs.getString("ShadePosition"));
                        editVehicleNo.setText(rs.getString("VhicleNo"));
                        editRemarks.setText(rs.getString("Remarks"));
                        editPacket.setText(rs.getString("Packet"));
                    }
                    connect.close();
                }
            }
            catch (Exception ex)
            {
                ConnectionResult = ex.getMessage();
            }
        }

        submit_form.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(viewthe_barcode.getText().toString().equals(""))
                {
                    viewthe_barcode.setError("Bar code is empty");
                }
                else if( editShadePosition.getText().toString().equals(""))
                {
                    editShadePosition.setError("Shade Position is required");
                    /*txtShadePosition.setText("Shade Position:*", TextView.BufferType.SPANNABLE);
                    Spannable span = (Spannable) txtShadePosition.getText();
                    span.setSpan(new ForegroundColorSpan(0xFFFF0000), 0, "Shade Position:*".length(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);*/
                }
                else if( editPacket.getText().toString().equals(""))
                {
                    editPacket.setError("Packet is required");
                    /*txtPacket.setText("Packet:*", TextView.BufferType.SPANNABLE);
                    Spannable span = (Spannable) txtPacket.getText();
                    span.setSpan(new ForegroundColorSpan(0xFFFF0000), 0, "Packet:*".length(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);*/
                }
                else if(editConfirmPacket.getText().toString().equals(""))
                {
                    editConfirmPacket.setError("Confirm the packet value again");
                    /*txtCPacket.setText("Confirm Packet:*", TextView.BufferType.SPANNABLE);
                    Spannable span = (Spannable) txtCPacket.getText();
                    span.setSpan(new ForegroundColorSpan(0xFFFF0000), 0, "Confirm Packet:*".length(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);*/
                }
                else if(!editPacket.getText().toString().equals(editConfirmPacket.getText().toString()))
                {
                    editPacket.setError("Please check the packet no.");
                }
                else
                {
                    //insert into the table
                    ConnectionHelper conStr = new ConnectionHelper();
                    connect = conStr.connectionclasss();
                    if (connect == null)
                    {
                        ConnectionResult = "Check Your Internet Access!";
                        AlertDialog alertDialog = new AlertDialog.Builder(bondActivity.this).create();
                        alertDialog.setTitle("Login Message");
                        alertDialog.setMessage(ConnectionResult);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                    else
                    {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = new Date();
                        String ReceiptDate = dateFormat.format(date);

                        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date1 = new Date();
                        String EntryDateTime = dateFormat1.format(date1);
                        String SIMNo = "";
                        String edit_datamode = "Draft";

                        //Toast.makeText(bondActivity.this, "DataMode2 = "+DataMode, Toast.LENGTH_SHORT).show();

                        if(DataMode.equals("Draft"))
                        {
                            qry = "UPDATE StoreReceive SET "+
                                    "ReceiptDate = '"+ReceiptDate+"', "+
                                    "BondHolderName = '"+editCustomerName.getText().toString()+"', "+
                                    "Address1 = '"+editAddress.getText().toString()+"', "+
                                    "CityVillage = '"+editCity.getText().toString()+"', "+
                                    "Post = '"+editPostOfficeCode.getText().toString()+"', "+
                                    "District = '"+editDistrict.getText().toString()+"', "+
                                    "[State] = '"+editState.getText().toString()+"', "+
                                    "PIN = '"+editPIN.getText().toString()+"', "+
                                    "TestWeight1 = "+editTestWeight1.getText().toString()+", "+
                                    "TestWeight2 = "+editTestWeight2.getText().toString()+", "+
                                    "TestWeight3 = "+editTestWeight3.getText().toString()+", "+
                                    "Quality = '"+editQuality.getText().toString()+"', "+
                                    "ShadePosition = '"+editShadePosition.getText().toString()+"', "+
                                    "VhicleNo = '"+editVehicleNo.getText().toString()+"', "+
                                    "Remarks = '"+editRemarks.getText().toString()+"', "+
                                    "Packet = "+editPacket.getText().toString()+", "+
                                    "UserID = "+user_id+", "+
                                    "EntryDateTime = '"+EntryDateTime+"', "+
                                    "DeviceID = '"+Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)+"', "+
                                    "SIMNo = '"+SIMNo+"' "+

                                    "WHERE BondNo  = "+ new BigInteger(viewthe_barcode.getText().toString()) +"  ";
                        }
                        else
                        {
                             qry = "INSERT INTO StoreReceive (\n" +
                                     "BondNo," +
                                     "ReceiptDate," +
                                     "BondHolderName," +
                                     "Address1," +
                                     "Post," +
                                     "CityVillage," +
                                     "District," +
                                     "[State]," +
                                     "PIN," +
                                     "TestWeight1," +
                                     "TestWeight2," +
                                     "TestWeight3," +
                                     "Quality," +
                                     "ShadePosition," +
                                     "VhicleNo," +
                                     "Remarks," +
                                     "Packet," +
                                     "UserID," +
                                     "EntryDateTime," +
                                     "DeviceID," +
                                     "SIMNo,"+
                                     "DataMode) VALUES("+
                                     viewthe_barcode.getText().toString() +",'"+
                                     ReceiptDate+"', '"+
                                     editCustomerName.getText().toString() +"', '"+
                                     editAddress.getText().toString()+"', '"+
                                     editPostOfficeCode.getText().toString()+"', '"+
                                     editCity.getText().toString()+"', '"+
                                     editDistrict.getText().toString()+"', '"+
                                     editState.getText().toString()+"', '"+
                                     editPIN.getText().toString()+"', "+
                                     editTestWeight1.getText().toString()+", "+
                                     editTestWeight2.getText().toString()+", "+
                                     editTestWeight3.getText().toString()+", '"+
                                     editQuality.getText().toString()+"', '"+
                                     editShadePosition.getText().toString()+"', '"+
                                     editVehicleNo.getText().toString()+"', '"+
                                     editRemarks.getText().toString()+"', '"+
                                     editPacket.getText().toString()+"', "+
                                     user_id+", '"+
                                     EntryDateTime+"', '"+
                                     Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)+"', '"+
                                     SIMNo+"', '" +
                                     edit_datamode+ "' )";

                        }
                        //Log.e("QRY = ", qry);
                        //Toast.makeText(bondActivity.this, "Qry="+ qry,Toast.LENGTH_LONG).show();
                        try
                        {
                            Statement stmt = connect.createStatement();
                            stmt.executeQuery(qry);
                            connect.close();
                        }
                        catch (SQLException e)
                        {
                            //e.printStackTrace();
                            Log.e("ERROR2", e.getMessage());
                        }

                        String Last_insert_id = "SELECT IDENT_CURRENT('StoreReceive')";
                        try
                        {
                            Statement stmt = connect.createStatement();
                            ResultSet rs = stmt.executeQuery(Last_insert_id);
                            while (rs.next())
                            {
                                Last_insert_id_val = ((BigDecimal) rs.getObject(1)).intValue();
                                //Toast.makeText(bondActivity.this, "Last_insert_id="+ Last_insert_id_val,Toast.LENGTH_LONG).show();
                            }
                            connect.close();
                        }
                        catch (SQLException e)
                        {
                            Log.e("ERROR3", e.getMessage());
                        }
                    }
                    Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);

                    intent.putExtra("ID", user_id);
                    intent.putExtra("UserName", user_name);
                    intent.putExtra("barcodeVal", barcodeVal);

                    intent.putExtra("BondNo", Last_insert_id_val);
                    intent.putExtra("CustomerName", editCustomerName.getText().toString());
                    intent.putExtra("Address", editAddress.getText().toString());
                    intent.putExtra("CityVillage", editCity.getText().toString());
                    intent.putExtra("PostOfficeCode", editPostOfficeCode.getText().toString());
                    intent.putExtra("District", editDistrict.getText().toString());
                    intent.putExtra("PIN", editPIN.getText().toString());
                    intent.putExtra("TestWeight1", editTestWeight1.getText().toString());
                    intent.putExtra("TestWeight2", editTestWeight2.getText().toString());
                    intent.putExtra("TestWeight3", editTestWeight3.getText().toString());
                    intent.putExtra("Quality", editQuality.getText().toString());
                    intent.putExtra("ShadePosition", editShadePosition.getText().toString());
                    intent.putExtra("VehicleNo", editVehicleNo.getText().toString());
                    intent.putExtra("Remarks", editRemarks.getText().toString());
                    intent.putExtra("Packet", editPacket.getText().toString());

                    viewthe_barcode.setText("");
                    editCustomerName.setText("");
                    editAddress.setText("");
                    editCity.setText("");
                    editPostOfficeCode.setText("");
                    editDistrict.setText("");
                    editPIN.setText("");
                    editQuality.setText("");
                    editShadePosition.setText("");
                    editVehicleNo.setText("");
                    editRemarks.setText("");
                    editPacket.setText("");
                    startActivity(intent);
                }
            }
        });
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

    private String getColoredSpanned(String text, String color)
    {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
}