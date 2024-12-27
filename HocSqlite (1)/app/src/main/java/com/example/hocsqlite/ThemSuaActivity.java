package com.example.hocsqlite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ThemSuaActivity extends AppCompatActivity {
    EditText edtMa, edtTen, edtDT;
    Button btnThemSua, btnThoat;
    String trangthai;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_sua);

        addView();
        addEvent();
    }

    private void addView() {
        intent = getIntent();
        trangthai = intent.getStringExtra("TRANGTHAI");

        edtMa = findViewById(R.id.edtMa);
        edtTen = findViewById(R.id.edtTen);
        edtDT = findViewById(R.id.edtDT);
        btnThemSua = findViewById(R.id.btnThemSua);
        btnThoat = findViewById(R.id.btnThoat);

        if ("SUA".equals(trangthai)) {
            Contact ct = (Contact) intent.getSerializableExtra("CONTACT");
            edtMa.setText(String.valueOf(ct.getMa()));
            edtMa.setEnabled(false);
            edtTen.setText(ct.getTen());
            edtDT.setText(ct.getDienthoai());
            btnThemSua.setText("Cập nhật");
        } else {
            btnThemSua.setText("Thêm");
        }
    }

    private void addEvent() {
        btnThemSua.setOnClickListener(view -> {
            Contact ct = new Contact(
                    Integer.parseInt(edtMa.getText().toString()),
                    edtTen.getText().toString(),
                    edtDT.getText().toString()
            );
            intent.putExtra("CONTACT", ct);
            setResult(RESULT_OK, intent);
            finish();
        });

        btnThoat.setOnClickListener(view -> finish());
    }
}
