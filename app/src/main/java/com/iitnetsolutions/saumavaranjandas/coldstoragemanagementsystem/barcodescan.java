package com.iitnetsolutions.saumavaranjandas.coldstoragemanagementsystem;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class barcodescan extends AppCompatActivity
{
    private Button scan_barcode, submit_form2mainform, check_barcode;
    private EditText edit_barcode;

    int user_id;
    public String user_name, dataMode;
    Connection connect;
    String ConnectionResult = "", DataMode;
    int RecCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcodescan);

        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getInt("ID");
        user_name = bundle.getString("UserName");

        scan_barcode            = (Button)findViewById(R.id.scan_barcode);
        edit_barcode            = (EditText)findViewById(R.id.edit_barcode);
        submit_form2mainform    = (Button)findViewById(R.id.submit_form2mainform);
        check_barcode           = (Button)findViewById(R.id.check_barcode);

        final Activity activity = this;
        scan_barcode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES); //QRCode and bar code both
                integrator.setPrompt("Scanning...").setCameraId(0).setBeepEnabled(false).setBarcodeImageEnabled(true).initiateScan();
            }
        });
        submit_form2mainform.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(barcodescan.this, DraftListActivity.class);
                intent.putExtra("ID", user_id);
                intent.putExtra("UserName", user_name);
                intent.putExtra("barcodeVal", edit_barcode.getText().toString());
                intent.putExtra("DataMode2ins", "Draft");
                startActivity(intent);
            }
        });
        check_barcode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(edit_barcode.getText().toString().equals("") )
                {
                    edit_barcode.setError("Barcode is required");
                }
                else
                {
                    try
                    {
                        ConnectionHelper conStr = new ConnectionHelper();
                        connect = conStr.connectionclasss();        // Connect to database
                        if (connect == null)
                        {
                            ConnectionResult = "Check Your Internet Access!";
                            AlertDialog alertDialog = new AlertDialog.Builder(barcodescan.this).create();
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
                            String query = "SELECT * FROM StoreReceive WHERE BondNo = "+ new BigInteger(edit_barcode.getText().toString()) +" " ;
                            Statement stmt = connect.createStatement();
                            //Toast.makeText(MainActivity.this, query.toString(), Toast.LENGTH_LONG).show();
                            ResultSet rs = stmt.executeQuery(query);

                            ConnectionResult = " successful";

                            while (rs.next())
                            {
                                DataMode  = rs.getString("DataMode");
                                RecCount++;
                            }
                            //Toast.makeText(barcodescan.this, "datamode = "+DataMode, Toast.LENGTH_LONG).show();
                            connect.close();
                        }
                        //Toast.makeText(barcodescan.this, "RecCount = "+RecCount, Toast.LENGTH_LONG).show();
                        //ToastMsg("on check "+edit_barcode.getText().toString());
                        if(RecCount > 0)
                        {
                            if(DataMode.equals("Draft"))
                            {
                                //Toast.makeText(barcodescan.this, "on DRAFT = ", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(barcodescan.this, bondActivity.class);
                                intent.putExtra("ID", user_id);
                                intent.putExtra("UserName", user_name);
                                intent.putExtra("barcodeVal", edit_barcode.getText().toString());
                                intent.putExtra("DataMode2ins", "Draft");
                                startActivity(intent);
                            }
                            else if(DataMode == "Blocked")
                            {
                                ToastMsg("This Bond No has already used, please try new one");
                            }
                        }
                        else
                        {
                            //Toast.makeText(barcodescan.this, "on NEW = ", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(barcodescan.this, bondActivity.class);
                            intent.putExtra("ID", user_id);
                            intent.putExtra("UserName", user_name);
                            intent.putExtra("barcodeVal", edit_barcode.getText().toString());
                            intent.putExtra("DataMode2ins", "NEW");
                            startActivity(intent);
                        }
                    }
                    catch (Exception ex)
                    {
                        ConnectionResult = ex.getMessage();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result !=  null)
        {
            if(result.getContents() == null)
            {
                Toast.makeText(this, "You canceled scanning", Toast.LENGTH_LONG).show();
            }
            else
            {
                edit_barcode.setText(result.getContents().toString());
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void ToastMsg(String msg)
    {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
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

