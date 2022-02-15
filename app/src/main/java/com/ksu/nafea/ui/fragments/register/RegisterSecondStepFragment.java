package com.ksu.nafea.ui.fragments.register;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ksu.nafea.R;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.account.UserAccount;
import com.ksu.nafea.utilities.InvalidFieldException;
import com.ksu.nafea.utilities.NafeaUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterSecondStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterSecondStepFragment extends Fragment
{
    public static final String TAG = "RegisterSecondStep";
    private TextView firstNameLabel, lastNameLabel;
    private EditText firstNameField, lastNameField;
    private Button nextButton, cancelButton;

    public RegisterSecondStepFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterSecondStepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterSecondStepFragment newInstance(String param1, String param2)
    {
        RegisterSecondStepFragment fragment = new RegisterSecondStepFragment();
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
        View main = inflater.inflate(R.layout.fragment_register_second_step, container, false);

        viewsInit(main);
        addFieldListener(firstNameLabel, firstNameField);
        addFieldListener(lastNameLabel, lastNameField);

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

        return  main;
    }


    private void viewsInit(View main)
    {
        firstNameLabel = (TextView) main.findViewById(R.id.regSecond_txt_firstName);
        lastNameLabel = (TextView) main.findViewById(R.id.regSecond_txt_lastName);

        firstNameField = (EditText) main.findViewById(R.id.regSecond_et_firstName);
        lastNameField = (EditText) main.findViewById(R.id.regSecond_et_lastName);

        nextButton = (Button) main.findViewById(R.id.regSecond_b_next);
        cancelButton = (Button) main.findViewById(R.id.regSecond_b_cancel);
    }


    private void executeNext()
    {
        if(!validateFieldsSyntax())
            return;

        openSelectUniversityPage();
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
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_registerSecondStep_to_login);
    }

    private void openSelectUniversityPage()
    {
        String email = User.userAccount.getEmail();
        String password = User.userAccount.getPassword();
        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        Student newStudent = new Student(email, password, firstName, lastName, null);

        User.userAccount = newStudent;
        User.isBrowsing = false;
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_registerSecondStep_to_selectUniversity);
    }

    //-----------------------------------------[Validating]-----------------------------------------

    private boolean validateFieldsSyntax()
    {
        EditText currentField = firstNameField;

        try
        {
            isFieldSyntaxValid(firstNameLabel, firstNameField);
            NafeaUtil.updateField(currentField, "");

            currentField = lastNameField;
            isFieldSyntaxValid(lastNameLabel, lastNameField);
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

        UserAccount.isValidInput(fieldLabel, fieldText, true, true);
    }


}