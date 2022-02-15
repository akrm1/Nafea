package com.ksu.nafea.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Admin;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.account.UserAccount;
import com.ksu.nafea.ui.activities.MainActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment
{

    public static final String TAG = "LoginActivity";

    private ArrayList<TextView> labels;
    private ArrayList<EditText> fields;
    private ArrayList<Button> buttons;
    private TextView createAccount;
    private ProgressDialog progressDialog;


    private void showToastMsg(Context context, String msg)
    {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public LoginFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2)
    {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View main = inflater.inflate(R.layout.fragment_login, container, false);

        viewsInit(main);
        addFieldsListener();

        // when create an account is pressed.
        createAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openRegisterPage();
            }
        });

        // when login Button is pressed.
        buttons.get(0).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                executeLogin(main.getContext());
            }
        });

        return main;
    }

    private void viewsInit(View mainView)
    {
        labels = new ArrayList<TextView>();
        fields = new ArrayList<EditText>();
        buttons = new ArrayList<Button>();
        progressDialog = new ProgressDialog(mainView.getContext());

        labels.add((TextView) mainView.findViewById(R.id.login_txt_email));
        labels.add((TextView) mainView.findViewById(R.id.login_txt_pass));

        fields.add((EditText) mainView.findViewById(R.id.login_et_email));
        fields.add((EditText) mainView.findViewById(R.id.login_et_pass));

        buttons.add((Button) mainView.findViewById(R.id.login_b_login));

        createAccount = (TextView) mainView.findViewById(R.id.login_txtb_register);
    }

    private void addFieldsListener()
    {
        for(int i = 0; i < fields.size(); i++)
        {
            final EditText field = fields.get(i);
            field.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    for(int j = 0; j < fields.size(); j++)
                        fields.get(j).setTextColor(Color.BLACK);
                }

                @Override
                public void afterTextChanged(Editable s)
                {

                }
            });
        }
    }

    private void openRegisterPage()
    {
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_login_to_registerFirstStep);
    }

    private void executeLogin(final Context context)
    {
        String email = fields.get(0).getText().toString();
        String password = fields.get(1).getText().toString();

        if(email.charAt(0) == '#')
        {
            email = email.substring(1);
            Admin admin = new Admin(email, password);
            Admin.loginAdmin(admin, onLoginRequestFlag(context));
        }
        else
        {
            Student student = new Student(email, password);
            UserAccount.login(student, onLoginRequestFlag(context));
        }

    }


    private  QueryRequestFlag<Student> onLoginRequestFlag(final Context context)
    {
        progressDialog.show();

        return new QueryRequestFlag<Student>()
        {
            @Override
            public void onQuerySuccess(Student resultObject)
            {
                progressDialog.dismiss();

                if(resultObject != null)
                {
                    User.userAccount = resultObject;
                    openHomePage();
                }
                else
                {
                    showToastMsg(context, "خطأ في المعلومات!");
                    for(int i = 0; i < fields.size(); i++)
                        fields.get(i).setTextColor(Color.RED);
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                progressDialog.dismiss();

                showToastMsg(context, "خطأ في المعلومات!");
                for(int i = 0; i < fields.size(); i++)
                    fields.get(i).setTextColor(Color.RED);

                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
            }
        };
    }


    private void openHomePage()
    {
        ((MainActivity) getActivity()).getNavMenu().findItem(R.id.navSection_logout).setVisible(true);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_login_to_home);
    }

}