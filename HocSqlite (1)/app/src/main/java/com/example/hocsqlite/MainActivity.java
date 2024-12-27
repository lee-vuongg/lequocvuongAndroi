package com.example.hocsqlite;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    String dbName = "ContactDB.db";
    String dbPath = "/databases/";
    SQLiteDatabase db = null;
    ArrayAdapter<Contact> adapter;
    ListView lvContact;
    Button btnThem;
    Contact ct;
    int posUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xuLyCopy();
        addView();
        hienThiContact();
        addEvent();
    }

    private void addView() {
        lvContact = findViewById(R.id.lvContact);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvContact.setAdapter(adapter);
        btnThem = findViewById(R.id.btnThem);
        registerForContextMenu(lvContact);
    }

    private void addEvent() {
        btnThem.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ThemSuaActivity.class);
            intent.putExtra("TRANGTHAI", "THEM");
            startActivityForResult(intent, 113);
        });

        lvContact.setOnItemClickListener((parent, view, position, id) -> {
            ct = adapter.getItem(position);
            posUpdate = position;
        });
    }

    private void hienThiContact() {
        db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Contact", null);
        adapter.clear();
        while (cursor.moveToNext()) {
            int ma = cursor.getInt(0);
            String ten = cursor.getString(1);
            String dienthoai = cursor.getString(2);
            adapter.add(new Contact(ma, ten, dienthoai));
        }
        cursor.close();
    }

    private void xuLyCopy() {
        try {
            File dbFile = getDatabasePath(dbName);
            if (!dbFile.exists()) {
                copyDataFromAsset();
                Toast.makeText(this, "Database copied successfully", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    private void copyDataFromAsset() {
        try {
            InputStream myInput = getAssets().open(dbName);
            String outFileName = getApplicationInfo().dataDir + dbPath + dbName;
            File f = new File(getApplicationInfo().dataDir + dbPath);
            if (!f.exists()) f.mkdir();
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception ex) {
            Log.e("Error", ex.toString());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context, menu);


        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ct = adapter.getItem(info.position);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (ct == null) {
            Toast.makeText(this, "Không có liên hệ nào được chọn", Toast.LENGTH_SHORT).show();
            return super.onContextItemSelected(item);
        }

        if (item.getItemId() == R.id.mnuSua) {
            Intent intent = new Intent(MainActivity.this, ThemSuaActivity.class);
            intent.putExtra("TRANGTHAI", "SUA");
            intent.putExtra("CONTACT", ct);
            startActivityForResult(intent, 113);
        } else if (item.getItemId() == R.id.mnuXoa) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa liên hệ này không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        try {
                            db.delete("Contact", "Ma=?", new String[]{String.valueOf(ct.getMa())});
                            adapter.remove(ct);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, "Đã xóa liên hệ", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("Database Error", e.toString());
                            Toast.makeText(this, "Không xóa được liên hệ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Không", null)
                    .show();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 113) { // 113 là mã yêu cầu thêm liên lạc
            Contact ctNew = (Contact) data.getSerializableExtra("CONTACT");

            try {
                ContentValues values = new ContentValues();
                values.put("Ma", ctNew.getMa());
                values.put("Ten", ctNew.getTen());
                values.put("Dienthoai", ctNew.getDienthoai());

                if ("THEM".equals(data.getStringExtra("TRANGTHAI"))) {
                    if (db.insert("Contact", null, values) > 0) {
                        adapter.add(ctNew);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Đã thêm thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Thêm không thành công", Toast.LENGTH_SHORT).show();
                    }
                } else if ("SUA".equals(data.getStringExtra("TRANGTHAI"))) {
                    db.update("Contact", values, "Ma=?", new String[]{String.valueOf(ctNew.getMa())});
                    adapter.remove(adapter.getItem(posUpdate));
                    adapter.insert(ctNew, posUpdate);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Đã cập nhật thành công", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("Lỗi", e.toString());
            }
        }
    }
}
