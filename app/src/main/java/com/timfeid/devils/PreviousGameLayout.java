package com.timfeid.devils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Tim on 2/10/2018.
 */

class PreviousGameLayout extends GameLayout {
    protected TextView homeSog;
    protected TextView awaySog;
    protected int onIceBoxId = R.layout.on_ice_box;
    private ImageView firstStarImage;
    private ImageView secondStarImage;
    private ImageView thirdStarImage;
    protected LinearLayout onIceBox;
    private TextView firstStarName;
    private TextView secondStarName;
    private TextView thirdStarName;
    private TextView firstStarStats;
    private TextView secondStarStats;
    private TextView thirdStarStats;
    protected View threeStarsBox;
    protected List<String> teams = Arrays.asList("home", "away");
    protected List<Integer> boxscoreStats = Arrays.asList(
            R.string.shots,
            R.string.pim,
            R.string.pp_ops,
            R.string.hits,
            R.string.blocks,
            R.string.faceoff_p,
            R.string.giveaways,
            R.string.takeaways
    );

    public PreviousGameLayout(GameInterface game, View rootView, Activity activity) {
        super(game, rootView, activity);
    }

    @Override
    int getLayoutId() {
        return R.layout.game_previous;
    }

    @Override
    void initView() {
        threeStarsBox = findViewById(R.id.three_stars_box);

        firstStarImage = (ImageView) findViewById(R.id.first_star_image);
        secondStarImage = (ImageView) findViewById(R.id.second_star_image);
        thirdStarImage = (ImageView) findViewById(R.id.third_star_image);

        firstStarName = (TextView) findViewById(R.id.first_star_name);
        secondStarName = (TextView) findViewById(R.id.second_star_name);
        thirdStarName = (TextView) findViewById(R.id.third_star_name);

        firstStarStats = (TextView) findViewById(R.id.first_star_stats);
        secondStarStats = (TextView) findViewById(R.id.second_star_stats);
        thirdStarStats = (TextView) findViewById(R.id.third_star_stats);

        homeSog = (TextView) findViewById(R.id.home_team_sog);
        awaySog = (TextView) findViewById(R.id.away_team_sog);

        onIceBox = (LinearLayout) findViewById(R.id.players_on_ice_box);
        findViewById(R.id.boxscore_box).setVisibility(View.GONE);
    }

    @Override
    public void fill() throws JSONException {
        populateLineScore();
        populateThreeStars();
        populateScoringSummary();
        if (game.isFinal()) {
            if (game.hasShootout()) {
                gameTime.setText(R.string.final_shootout);
            } else if (game.hasOt()) {
                gameTime.setText(R.string.final_overtime);
            } else {
                gameTime.setText(R.string.final_regulation);
            }
        }
    }


    void populateLineScore() throws JSONException {
        setTextViewTextByTag("HOME_TEAM_SCORE", game.getGoalsFor("home"));
        setTextViewTextByTag("AWAY_TEAM_SCORE", game.getGoalsFor("away"));

        int period = 0;
        while (game.periodHasBeenPlayed(++period)) {
            TextView headerText = (TextView) findViewById("period_"+period+"_name");
            headerText.setVisibility(View.VISIBLE);

            TextView homeText = (TextView) findViewById("home_team_p"+period+"_score");
            TextView awayText = (TextView) findViewById("away_team_p"+period+"_score");

            homeText.setVisibility(View.VISIBLE);
            awayText.setVisibility(View.VISIBLE);

            homeText.setText(String.valueOf(game.getPeriodScore("home", period)));
            awayText.setText(String.valueOf(game.getPeriodScore("away", period)));
        }

        if (game.hasShootout()) {
            TextView shootoutTitle = (TextView) findViewById(R.id.period_so_name);
            shootoutTitle.setVisibility(View.VISIBLE);

            TextView shootoutHomeText = (TextView) findViewById(R.id.home_team_so_score);
            shootoutHomeText.setText(String.format(Locale.US, "%d", game.getShootoutGoals("home")));
            shootoutHomeText.setVisibility(View.VISIBLE);

            TextView shootoutAwayText = (TextView) findViewById(R.id.away_team_so_score);
            shootoutAwayText.setText(String.format(Locale.US, "%d", game.getShootoutGoals("away")));
            shootoutAwayText.setVisibility(View.VISIBLE);
        }

        homeSog.setText(MessageFormat.format("{0}{1}", game.getShotsOnGoal("home"), getResources().getString(R.string.shots_on_goal)));
        awaySog.setText(MessageFormat.format("{0}{1}", game.getShotsOnGoal("away"), getResources().getString(R.string.shots_on_goal)));
    }

    private void populateThreeStars() throws JSONException {
        int firstStarId = game.getFirstStarId();
        int secondStarId = game.getSecondStarId();
        int thirdStarId = game.getThirdStarId();

        imageCircleUrl(firstStarImage, getImageFor(firstStarId));
        imageCircleUrl(secondStarImage, getImageFor(secondStarId));
        imageCircleUrl(thirdStarImage, getImageFor(thirdStarId));

        firstStarName.setText(game.getFirstStar().getString("fullName"));
        secondStarName.setText(game.getSecondStar().getString("fullName"));
        thirdStarName.setText(game.getThirdStar().getString("fullName"));

        firstStarStats.setText(game.getPlayerStats(firstStarId));
        secondStarStats.setText(game.getPlayerStats(secondStarId));
        thirdStarStats.setText(game.getPlayerStats(thirdStarId));

        onIceBox.setVisibility(View.GONE);
    }

    void populateScoringSummary() throws JSONException {
        LinearLayout scoringSummaryLayout = rootView.findViewById(R.id.scoring_summary_layout);
        List<Play> plays = game.getScoringPlays();
        scoringSummaryLayout.removeAllViews();

        for (Play play : plays) {
            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.scoring_summary_box, scoringSummaryLayout, false);
            scoringSummaryLayout.addView(layout);
            ImageView photo = layout.findViewById(R.id.scoring_summary_photo);
            TextView scorer = layout.findViewById(R.id.scoring_summary_scorer);
            TextView assists = layout.findViewById(R.id.scoring_summary_assists);
            TextView score = layout.findViewById(R.id.scoring_summary_score);
            TextView type = layout.findViewById(R.id.scoring_summary_type);
            TextView time = layout.findViewById(R.id.scoring_summary_time);
            TextView strength = layout.findViewById(R.id.scoring_summary_strength);
            final ImageButton viewVideo = layout.findViewById(R.id.view_video);
            Play.ScoringPlayer scorerObj = play.getScorer();
            Person person = scorerObj.getPerson();
            String scorerText = (person != null ? person.getFullName() : "Unknown") + " (" + scorerObj.getSeasonTotal() + ")";
            String strengthText = play.getStrength();

            final GameContent content = game.getGameContent();
            GameContent.Content highlight = null;
            if (content != null) {
                highlight = content.findHighlightById(play.getId());
            }

            if (!strengthText.equals(Game.STRENGTH_EVEN)) {
                strength.setText(strengthText);
            }

            scorer.setText(scorerText);

            if (scorerObj.getPerson() != null) {
                imageCircleUrl(photo, getImageFor(scorerObj.getPerson().getId()));
            }

            assists.setText(play.getAssists());
            score.setText(play.getScore());
            type.setText(play.getScoreType());
            time.setText(play.getTime());

            if (highlight != null) {
                final GameContent.Content hl = highlight;
                viewVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playVideo(hl);
                    }
                });
            } else {
                viewVideo.setVisibility(View.GONE);
            }
        }
    }

    private void playVideo(final GameContent.Content content) {
        Intent intent = new Intent(Intent.ACTION_VIEW );
        intent.setDataAndType(Uri.parse(content.getMobileUrl()), "video/*");
        getActivity().startActivity(intent);
    }

}
