package com.timfeid.devils;

/**
 * Created by Tim on 2/5/2018.
 * Main data processor
 */

public class Team {
    private static Team instance;
    private Schedule schedule;
    private PlayerStats playerStats;
    private News news;
    private Videos videos;
    private Standings standings;

    private Team() {
        schedule = new Schedule();
        playerStats = new PlayerStats();
        news = new News();
        videos = new Videos();
        standings = new Standings();
    }

    public static Team getInstance() {
        if (instance == null) {
            instance = new Team();
        }

        return instance;
    }

    void withSchedule(Listener listener) {
        if (!schedule.done()) {
            schedule.addListener(listener);
            return;
        }

        listener.handle(schedule);
    }

    void withStandings(Listener listener) {
        if (!standings.done()) {
            standings.addListener(listener);
            return;
        }

        listener.handle(standings);
    }

    void withPlayerStats(Listener listener) {
        if (!playerStats.done()) {
            playerStats.addListener(listener);
            return;
        }

        listener.handle(playerStats);
    }

    void withNews(Listener listener) {
        if (!news.done()) {
            news.addListener(listener);
            return;
        }

        listener.handle(news);
    }

    void withVideos(Listener listener) {
        if (!videos.done()) {
            videos.addListener(listener);
            return;
        }

        listener.handle(videos);
    }
}
