package com.vamsi.xyzreader;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleDetailFragment extends Fragment {

    TextView tvBody;

    public ArticleDetailFragment() {
        // Required empty public constructor
    }


    public static ArticleDetailFragment newInstance(String body) {

        ArticleDetailFragment f = new ArticleDetailFragment();
        Bundle b = new Bundle();
        b.putString("body", body);

        f.setArguments(b);

        return f;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);


        tvBody = (TextView) view.findViewById(R.id.tvBody);

        Spanned body;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            body = Html.fromHtml(getArguments().getString("body"), Html.FROM_HTML_MODE_LEGACY);
        } else {
            body = Html.fromHtml(getArguments().getString("body"));
        }

        tvBody.setText(body);

        //Toast.makeText(getActivity(), "Hii"+getArguments().getInt("pos"), Toast.LENGTH_SHORT).show();


        return view;

    }

}
