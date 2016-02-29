package com.app.fish.catchreport;


import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFishFragment extends Fragment {
    public static final String FISH_LAKES_DB = "FishAndLakes.db";


    private Spinner speciesSpin;
    private EditText weightEditText;
    private EditText lengthEditText;
    private CheckBox releasedCheckBox;
    private CheckBox taggedCheckBox;
    private EditText quantity;
    private static String currentLake;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "mFish";

    // TODO: Rename and change types of parameters
    private Fish mFish;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param info Parameter 1. Contains fish and lake data.
     * @return A new instance of fragment AddFishFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFishFragment newInstance(TripInfoStorage info, int cur) {
        AddFishFragment fragment = new AddFishFragment();
        Bundle args = new Bundle();
        Fish fish = info.getFish(cur);
        args.putSerializable(ARG_PARAM1, fish);
        args.putString("lakeID",info.getLake().getId()+"");
        fragment.setArguments(args);
        return fragment;
    }

    public AddFishFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFish = (Fish) getArguments().getSerializable(ARG_PARAM1);
        }

        if(mFish == null){
            mFish = new Fish();
        }


        currentLake = getArguments().getString("lakeID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_fish, container, false);

        final ArrayList<String> species = fillFishList();

        speciesSpin = (Spinner) v.findViewById(R.id.speciesSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_layout_ws, species);
        adapter.setDropDownViewResource(R.layout.spinner_layout_ws);

        speciesSpin.setAdapter(adapter);

        int pos = species.indexOf(mFish.getSpecies());
        if(pos<0) {
            pos = 0;
            mFish.setSpecies((String) speciesSpin.getSelectedItem());
        }
        speciesSpin.setSelection(pos, true);
        speciesSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFish.setSpecies(species.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                int pos = species.indexOf(mFish.getSpecies());
                if (pos < 0)
                    pos = 0;
                speciesSpin.setSelection(pos, true);
            }
        });

        weightEditText = (EditText) v.findViewById(R.id.weightEditText);

        if(mFish.getWeight() > 0){
            weightEditText.setText(mFish.getWeight() + "");
            mFish.setWeight(Double.parseDouble(lengthEditText.getText().toString()));
        }

        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String word = weightEditText.getText().toString();
                if (word.equals("")) {
                    mFish.setWeight(0);
                    weightEditText.setHint("Weight");
                }
                else {
                    double w = Double.parseDouble("0"+word);
                    mFish.setWeight(w);
                }
            }
        });


        lengthEditText = (EditText) v.findViewById(R.id.lengthEditText);

        if(mFish.getLength() > 0) {
            lengthEditText.setText(mFish.getLength() + "");
            mFish.setLength(Double.parseDouble(lengthEditText.getText().toString()));
        }
        lengthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String word = lengthEditText.getText().toString();
                if (word.equals("")) {
                    mFish.setLength(0);
                    lengthEditText.setHint("Length");
                } else {
                    double l = Double.parseDouble("0" + word);
                    mFish.setLength(l);
                }
            }
        });


        releasedCheckBox = (CheckBox) v.findViewById(R.id.releasedCheckBox);
        releasedCheckBox.setChecked(mFish.isReleased());
        releasedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFish.setReleased(isChecked);
            }
        });

        taggedCheckBox = (CheckBox) v.findViewById(R.id.tagCheckBox);
        taggedCheckBox.setChecked(mFish.isTagged());
        taggedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFish.setTagged(isChecked);
            }
        });

        quantity = (EditText) v.findViewById(R.id.quantityEditText);

        if(mFish.getQuantity() > 1) {
            quantity.setText(mFish.getQuantity() + "");
        }

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try
                {
                    int qty = Integer.parseInt(quantity.getText().toString());
                    mFish.setQuantity(qty);
                }catch(Exception e)
                {
                    mFish.setQuantity(1);
                    quantity.setHint("1");
                }
            }
        });

        return v;
    }

    public ArrayList<String> fillFishList()
    {
        ArrayList<String> lakeNames = new ArrayList<String>();
        DatabaseHandler db = new DatabaseHandler(this.getActivity(), FISH_LAKES_DB);
        db.openDatabase();
        SQLiteCursor cur;

        cur = db.runQuery("SELECT Species FROM FoundIn JOIN Fish ON FoundIn.FishID=Fish._id WHERE WaterBodyID=?",
                new String[]{currentLake});

        while(cur.moveToNext())
        {
            lakeNames.add(cur.getString(0));
        }
        cur.close();
        db.close();
        return lakeNames;
    }
}
