package com.ksu.nafea.ui.fragments.browse;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.College;
import com.ksu.nafea.ui.nafea_views.NSpinner;
import com.ksu.nafea.ui.nafea_views.recycler_view.GeneralRecyclerAdapter;
import com.ksu.nafea.ui.nafea_views.recycler_view.ListAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectFragment<T> extends Fragment
{
    public static final String TAG = "SelectFragment";
    protected View main;
    private LinearLayout topLayout;
    protected NSpinner dropdown;
    protected RecyclerView recyclerView;
    protected ProgressDialog progressDialog;

    private ArrayList<T> data;

    public SelectFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectFragment newInstance(String param1, String param2)
    {

        SelectFragment fragment = new SelectFragment();
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
        main = inflater.inflate(R.layout.fragment_select, container, false);

        data = new ArrayList<T>();

        viewsInit();
        onSelectFragmentCreated(main);
        fillDropdown();

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                fillRecyclerView(dropdown.getSelectedOption());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        return main;
    }


    protected void openPage(int pageID)
    {
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(pageID);
    }

    //-------------------------------------------------[UI methods]-------------------------------------------------

    private void viewsInit()
    {
        topLayout = (LinearLayout) main.findViewById(R.id.select_layout_top);
        dropdown = (NSpinner) main.findViewById(R.id.select_dropdown);
        recyclerView = (RecyclerView) main.findViewById(R.id.select_recyclerView);
        progressDialog = new ProgressDialog(getContext());
    }

    private void updateRecyclerView()
    {
        final int itemViewLayout = getItemViewLayout();

        ListAdapter listAdapter = new ListAdapter()
        {
            @Override
            public int getResourceLayout()
            {
                return itemViewLayout;
            }

            @Override
            public int getItemCount()
            {
                return data.size();
            }

            @Override
            public void onBind(View itemView, final int position)
            {
                onItemViewBind(itemView, position);
            }
        };


        GeneralRecyclerAdapter adapter = new GeneralRecyclerAdapter(getContext(), listAdapter);
        recyclerView.setItemViewCacheSize(data.size());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    protected void showToastMsg(String msg)
    {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    protected void setBarTitle(String title)
    {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    protected TextView addTextView(String text)
    {
        TextView textView = new TextView(getContext(), null, 0, R.style.Nafea_SelectFragment_TextView);
        topLayout.addView(textView);
        textView.setText(text);

        return textView;
    }
    protected TextView addTextView(String text, int style)
    {
        TextView textView = new TextView(getContext(), null, 0, style);
        topLayout.addView(textView);
        textView.setText(text);

        return textView;
    }

    //-------------------------------------------------[Data methods]-------------------------------------------------

    protected ArrayList<T> getData()
    {
        return data;
    }

    protected QueryRequestFlag<ArrayList<String>> retrieveDropdownData()
    {
        dropdown.addOption(getString(R.string.allOption));

        progressDialog.show();
        QueryRequestFlag<ArrayList<String>> requestFlag = new QueryRequestFlag<ArrayList<String>>()
        {
            @Override
            public void onQuerySuccess(ArrayList<String> resultObject)
            {
                progressDialog.dismiss();

                if(resultObject != null)
                {
                    dropdown.addOptionsList(resultObject);
                    fillRecyclerView(dropdown.getSelectedOption());
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                progressDialog.dismiss();

                showToastMsg("فشل عرض البيانات");
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
            }
        };

        return requestFlag;
    }
    protected QueryRequestFlag<ArrayList<T>> retrieveRecyclerViewData()
    {
        progressDialog.show();

        QueryRequestFlag<ArrayList<T>> requestFlag = new QueryRequestFlag<ArrayList<T>>()
        {
            @Override
            public void onQuerySuccess(ArrayList<T> resultObject)
            {
                progressDialog.dismiss();

                if(resultObject != null)
                {
                    data = resultObject;
                    updateRecyclerView();
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                progressDialog.dismiss();

                showToastMsg("فشل عرض البيانات");
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
            }
        };

        return requestFlag;
    }

    //-------------------------------------------------[Overridable methods]-------------------------------------------------

    protected void onSelectFragmentCreated(View main)
    {

    }



    protected void fillDropdown()
    {

    }
    protected void fillRecyclerView(final String selectedOption)
    {

    }


    protected int getItemViewLayout()
    {
        return R.layout.item_view_select;
    }
    protected void onItemViewBind(View itemView, final int position)
    {
        ConstraintLayout mainLayout = (ConstraintLayout) itemView.findViewById(R.id.select_mainLayout);
        TextView itemText = (TextView) itemView.findViewById(R.id.select_txt_text);

        final String itemString = getDefaultItemString(position);
        if(itemString != null)
            itemText.setText(itemString);


        mainLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onDefaultItemClicked(position);
            }
        });
    }



    protected String getDefaultItemString(int position)
    {
        return null;
    }
    protected void onDefaultItemClicked(int position)
    {

    }


}