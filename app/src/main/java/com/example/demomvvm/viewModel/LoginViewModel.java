package com.example.demomvvm.viewModel;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;


import com.example.demomvvm.view.HelloWorld;

import com.example.demomvvm.model.User;

public class LoginViewModel extends BaseObservable {
    private String email, password;
    public ObservableField<String> messageLogin = new ObservableField<>();

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }
    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public void onClickLogin(Context context){
        User user = new User(getEmail(),getPassword());
        if(user.isValidEmail() && user.isValidPassword()){
            Intent intent = new Intent(context, HelloWorld.class);
            context.startActivity(intent);
            Toast.makeText(context, "Hello Page", Toast.LENGTH_SHORT).show();
        }else{
            messageLogin.set("Email or password INVALID");
        }
    }
}
