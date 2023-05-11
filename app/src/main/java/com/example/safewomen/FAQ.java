package com.example.safewomen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import java.net.Inet4Address;

public class FAQ extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
    }
    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        intent = new Intent(FAQ.this, Home.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_settings:
                        intent = new Intent(FAQ.this, SettingsActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_faq:
                        intent = new Intent(FAQ.this, FAQ.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_logout:
                        intent = new Intent(FAQ.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }
}