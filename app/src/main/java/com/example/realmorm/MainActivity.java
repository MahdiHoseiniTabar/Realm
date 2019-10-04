package com.example.realmorm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.realmorm.model.User;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private EditText editText_userName;
    private Button button_addUser;
    private RecyclerView recyclerView_user;
    private UserAdapter userAdapter;
    int nextInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText_userName = findViewById(R.id.editText_user_name);
        button_addUser = findViewById(R.id.button_add_user);
        recyclerView_user = findViewById(R.id.recyclerView_user);
        recyclerView_user.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("realm.db")
                .build();
        final Realm realm = Realm.getInstance(config);

        if (userAdapter == null)
             userAdapter = new UserAdapter(this,realm.where(User.class).findAll());
            recyclerView_user.setAdapter(userAdapter);

        button_addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                User user = new User();
                if (realm.where(User.class).max("id") != null)
                    nextInt = realm.where(User.class).max("id").intValue() + 1;
                else
                    nextInt = 1;

                user.setId(nextInt);
                user.setUserName(editText_userName.getText().toString());
                realm.copyToRealm(user);
                realm.commitTransaction();
                //notifyAdapter(realm);
            }
        });

        RealmResults<User> realmResults = realm.where(User.class).findAllAsync();

        realmResults.addChangeListener(new RealmChangeListener<RealmResults<User>>() {
            @Override
            public void onChange(RealmResults<User> users) {
                Log.i("RealmMainActivity", "onChange: " );
                userAdapter.setUsers(users);
                userAdapter.notifyDataSetChanged();
            }
        });

    }

    private void notifyAdapter(Realm realm) {
        userAdapter.setUsers(realm.where(User.class).findAll());
        userAdapter.notifyDataSetChanged();
    }

    class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private Context context;
        private List<User> users;

        public UserAdapter(Context context, List<User> users) {
            this.context = context;
            this.users = users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
            userViewHolder.bind(users.get(i));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            private TextView textView_userId;
            private TextView textView_userName;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                textView_userId = itemView.findViewById(R.id.text_user_id);
                textView_userName = itemView.findViewById(R.id.text_user_name);
            }

            public void bind(User user) {
                textView_userName.setText(user.getUserName());
                textView_userId.setText(user.getId() + "- ");
            }
        }
    }
}
