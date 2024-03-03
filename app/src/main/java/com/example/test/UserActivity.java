package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.test.databinding.ActivityUserBinding;
import com.example.test.fragments.AccountFragment;
import com.example.test.fragments.CasinoFragment;
import com.example.test.fragments.HomeFragment;
import com.example.test.fragments.SportsFragment;

public class UserActivity extends AppCompatActivity {
    ActivityUserBinding binding;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
         userEmail = intent.getStringExtra("userEmail");

        binding=ActivityUserBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());
       binding.bottomNavigation.setBackground(null);
       replaceFragment(new HomeFragment());
       binding.bottomNavigation.setItemIconTintList(null);
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_selected },
                new int[] { android.R.attr.state_enabled }
        };

        int[] colors = new int[] {
                Utils.clrAccent,
                Color.WHITE
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        binding.bottomNavigation.setItemTextColor(colorStateList);
//       binding.bottomNavigation.setItemIconTintList();
       binding.bottomNavigation.setOnItemSelectedListener(item -> {
           switch (item.getItemId()){
               case R.id.home:
                   replaceFragment(new HomeFragment());
                   break;
               case R.id.casino:
                   replaceFragment(new CasinoFragment());
                   break;
               case R.id.sport:
                   replaceFragment(new SportsFragment());
                   break;
               case R.id.account:
                   replaceFragment(new AccountFragment());
                   break;
           }
           return true;
       });


    }
    void initID(){

    }
  void replaceFragment(Fragment fragment){
      FragmentManager fragmentManager= getSupportFragmentManager();
      FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
      fragmentTransaction.replace(R.id.frag_container,fragment);

      Bundle args = new Bundle();
      args.putString("userEmail", userEmail);
      fragment.setArguments(args);

      fragmentTransaction.commit();
  }
}