package com.timfeid.devils;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment implements Listener {
    private static final String ARG_GAME_NUMBER = "gameNumber";
    private static final String TAG = "GameFragment";

    private LiveGameLayout liveGame = null;

    protected GameInterface game = null;
    protected View rootView;

    private int gameNumber;

    private OnFragmentInteractionListener mListener;
    protected LinearLayout gameLayout = null;
    protected LayoutFactory layoutFactory = new LayoutFactory();

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance(int gameNumber) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GAME_NUMBER, gameNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameNumber = getArguments().getInt(ARG_GAME_NUMBER);
        }

        Team team = Team.getInstance();
        team.withSchedule(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game,container,false);
        gameLayout = rootView.findViewById(R.id.game_info);
        populateView();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void populateView() {
        if (gameLayout == null || game == null) {
            return;
        }

        final GameLayout gameLayout = layoutFactory.createLayout();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameLayout.build();
            }
        });
    }

    public void withSchedule(Schedule schedule) {
        game = schedule.getGame(gameNumber);
        populateView();
    }

    @Override
    public void handle(Observable observable) {
        if (observable instanceof Schedule) {
            withSchedule((Schedule) observable);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class LayoutFactory {
        public GameLayout createLayout() {
            try {
                if (game.isFinal()) {
                    return new PreviousGameLayout(game, rootView, getActivity());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new UpcomingGameLayout(game, rootView, getActivity());
        }

        public LiveGameLayout getLiveGame() {
            return liveGame;
        }
    }
}
