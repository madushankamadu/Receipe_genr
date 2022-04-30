package com.example.recipegenerator;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class RecipeFragment extends Fragment {

    private static final String TAG = "data_passing";
    List<String> itemArrayList;
    List<Recipe> recipeList;
    ListView list;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    TextView itemTitle,ingrText,insText;

    public RecipeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static RecipeFragment newInstance(String param1, String param2) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle extras =this.getArguments();
        if (extras != null) {
            ArrayList<Recipe> arraylist  = extras.getParcelableArrayList("arraylist");
            for (int i = 0; i<=arraylist.size(); i++){
                Recipe single = arraylist.get(i);
                String title = single.getTitle();
                Log.d(TAG, "onCreate: "+single);
            }

        }



        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe, container, false);
        list = v.findViewById(R.id.list_recipes);
        itemArrayList  = new ArrayList<>();
        recipeList = new ArrayList<>();

        // make the recipe list

        recipeList = MainActivity.recipes;
        for (int i = 0 ; i < recipeList.size() ; i++){
             itemArrayList.add(recipeList.get(i).getTitle());
            Log.d(TAG, "onCreateView: "+recipeList.get(i).getTitle());
        }

        ListAdapter listAdapter = new ListAdapter(getActivity(),R.layout.list_item,itemArrayList);
        list.setAdapter(listAdapter);



        // set the dialog builder to view content


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



                dialogBuilder = new AlertDialog.Builder(getActivity());
                View vv = getLayoutInflater().inflate(R.layout.description_dialog, null);

                itemTitle = vv.findViewById(R.id.title_item);
                ingrText = vv.findViewById(R.id.ingr_text);
                insText = vv.findViewById(R.id.ins_text);


                //set ingerdients

                itemTitle.setText(recipeList.get(i).getTitle()+"-");

                String text = "";
                for (int j = 0; j < recipeList.get(i).getIngredients().length(); j++){
                    try {
                        text  += "* "+recipeList.get(i).getIngredients().getString(j)+"\n";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ingrText.setText(text);


                //set instructions
                String insTxt = "";
                for (int j = 0; j<recipeList.get(i).getInstructions().length(); j++){
                    try {
                        insTxt += "- "+recipeList.get(i).getInstructions().getString(j)+"\n\n";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                insText.setText(insTxt);


                dialogBuilder.setView(vv);
                dialog = dialogBuilder.create();
                dialog.show();
                dialog.getWindow().setLayout(400, 600);

            }
        });

        return v;
    }
}