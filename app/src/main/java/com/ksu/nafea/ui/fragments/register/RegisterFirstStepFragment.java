package com.ksu.nafea.ui.fragments.register;

import android.app.ProgressDialog;
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

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.account.UserAccount;
import com.ksu.nafea.utilities.InvalidFieldException;
import com.ksu.nafea.utilities.NafeaUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFirstStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFirstStepFragment extends Fragment
{
    public static final String TAG = "RegisterFirstStep";
    private TextView emailLabel, confirmEmailLabel, passwordLabel, rePassLabel;
    private EditText emailField, confirmEmailField, passwordField, rePassField;
    private Button nextButton, cancelButton;
    private ProgressDialog progressDialog;


    public RegisterFirstStepFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFirstStepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFirstStepFragment newInstance(String param1, String param2)
    {
        RegisterFirstStepFragment fragment = new RegisterFirstStepFragment();
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
        View main = inflater.inflate(R.layout.fragment_register_first_step, container, false);

        viewsInit(main);
        addFieldListener(emailLabel, emailField);
        addFieldListener(confirmEmailLabel, confirmEmailField);
        addFieldListener(passwordLabel, passwordField);
        addFieldListener(rePassLabel, rePassField);

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                executeNext();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                executeCancel();
            }
        });


        return main;
    }

    private void viewsInit(View main)
    {
        emailLabel = (TextView) main.findViewById(R.id.regFirst_txt_email);
        confirmEmailLabel = (TextView) main.findViewById(R.id.regFirst_txt_confirmEmail);
        passwordLabel = (TextView) main.findViewById(R.id.regFirst_txt_pass);
        rePassLabel = (TextView) main.findViewById(R.id.regFirst_txt_rePass);

        emailField = (EditText) main.findViewById(R.id.regFirst_et_email);
        confirmEmailField = (EditText) main.findViewById(R.id.regFirst_et_confirmEmail);
        passwordField = (EditText) main.findViewById(R.id.regFirst_et_pass);
        rePassField = (EditText) main.findViewById(R.id.regFirst_et_rePass);

        nextButton = (Button) main.findViewById(R.id.regFirst_b_next);
        cancelButton = (Button) main.findViewById(R.id.regFirst_b_cancel);

        progressDialog = new ProgressDialog(main.getContext());
    }


    private void executeNext()
    {
        if(!validateFieldsSyntax())
            return;

        validateEmailExistence();
    }

    private void executeCancel()
    {
        openLoginPage();
    }



    private void addFieldListener(final TextView label, final EditText field)
    {
        field.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                try
                {
                    isFieldSyntaxValid(label, field);
                    NafeaUtil.updateField(field, "");
                }
                catch(InvalidFieldException e)
                {
                    NafeaUtil.updateField(field, e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }



    //-----------------------------------------[open pages]-----------------------------------------

    private void openLoginPage()
    {
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_registerFirstStep_to_login);
    }

    private void openSecondStepPage()
    {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        Student newStudent = new Student(email, password);

        User.userAccount = newStudent;
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_registerFirstStep_to_registerSecondStep);
    }

    //-----------------------------------------[Validating]-----------------------------------------

    private boolean validateFieldsSyntax()
    {
        EditText currentField = emailField;

        try
        {
            isFieldSyntaxValid(emailLabel, emailField);
            NafeaUtil.updateField(currentField, "");

            currentField = confirmEmailField;
            isFieldSyntaxValid(confirmEmailLabel, confirmEmailField);
            NafeaUtil.updateField(currentField, "");

            currentField = passwordField;
            isFieldSyntaxValid(passwordLabel, passwordField);
            NafeaUtil.updateField(currentField, "");

            currentField = rePassField;
            isFieldSyntaxValid(rePassLabel, rePassField);
            NafeaUtil.updateField(currentField, "");
        }
        catch(InvalidFieldException e)
        {
            NafeaUtil.updateField(currentField, e.getMessage());
            return false;
        }

        return true;
    }

    private void isFieldSyntaxValid(TextView label, EditText field) throws InvalidFieldException
    {
        String fieldLabel = label.getText().toString();
        String fieldText = field.getText().toString();

        if(field.equals(emailField))
        {
            UserAccount.isValidEmail(fieldLabel, fieldText);
        }
        else if(field.equals(confirmEmailField))
        {
            String originalLabel = emailLabel.getText().toString();
            String originalField = emailField.getText().toString();

            UserAccount.isValidConfirmField(originalField, fieldText, originalLabel, fieldLabel);
        }
        else if(field.equals(passwordField))
        {
            UserAccount.isValidPassword(fieldLabel, fieldText);
        }
        else if(field.equals(rePassField))
        {
            String originalLabel = passwordLabel.getText().toString();
            String originalField = passwordField.getText().toString();

            UserAccount.isValidConfirmField(originalField, fieldText, originalLabel, fieldLabel);
        }
    }


    private void validateEmailExistence()
    {
        String email = emailField.getText().toString();
        Student student = new Student(email, null);

        progressDialog.show();
        UserAccount.isEmailExist(student, new QueryRequestFlag<Boolean>()
        {
            @Override
            public void onQuerySuccess(Boolean resultObject)
            {
                progressDialog.dismiss();

                if(resultObject != null)
                {
                    if(!resultObject)
                    {
                        NafeaUtil.updateField(emailField, "");
                        openSecondStepPage();
                        return;
                    }
                }

                String msg = "الإيميل مستعمل";
                NafeaUtil.updateField(emailField, msg);
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                progressDialog.dismiss();

                String msg = "هناك خطأ في الإيميل!";
                NafeaUtil.updateField(emailField, msg);
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
            }
        });

    }


}