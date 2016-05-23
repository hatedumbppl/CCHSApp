package com.example.cl18peelerw.cchsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Parse.initialize(this, "1nbCZcm4WHUpYs0C89oTo231mhcpL2LRa5KfsYtw", "cxaCVQZnK3rzTxMdPuGltUycHxkll1GpCC0qPohK");
    }

    GridLayout infoTable = (GridLayout) findViewById(R.id.infoTable);
    LinearLayout contain = (LinearLayout) findViewById(R.id.contain);
    LinearLayout information = (LinearLayout) findViewById(R.id.information);
    List<ParseObject> daily = new ArrayList<ParseObject>();
    List<ParseObject> page = new ArrayList<ParseObject>();
    List<ParseObject> user = new ArrayList<ParseObject>();
    List<ParseObject> flappy = new ArrayList<ParseObject>();
    String date = "";
    start();

    public void start() {
        Calendar today = Calendar.getInstance();
        int d = today.get(Calendar.DAY_OF_MONTH);
        int m = today.get(Calendar.MONTH) + 1; //January is 0!
        int y = today.get(Calendar.YEAR);
        d -= 1;
        if(d == 0) {
            m -= 1;
            switch(m) {
                case 9:
                case 4:
                case 6:
                case 11:
                    d = 30;
                    break;
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    d = 31;
                case 2:
                    if(y % 4 == 0) {d = 29;}
                    else {d = 28;}
                    break;
                case 0:
                    m = 12;
                    d = 31;
                    y -= 1;
            }
        }
        String dd = ((Integer)d).toString();
        String mm = ((Integer)m).toString();
        String yyyy = ((Integer)y).toString();
        if(Integer.parseInt(dd)<10) {dd='0'+dd;}
        if(Integer.parseInt(mm)<10) {mm='0'+mm;}
        date = mm+'/'+dd+'/'+yyyy;

        ParseQuery<ParseObject> analytics = ParseQuery.getQuery("Analytics");
        final ParseQuery<ParseObject> pageAnalytics = ParseQuery.getQuery("PageAnalytics");
        final ParseQuery<ParseObject> userIDs = ParseQuery.getQuery("UserIDs");
        final ParseQuery<ParseObject> leaderboard = ParseQuery.getQuery("Leaderboard");
        analytics.setLimit(1000);
        pageAnalytics.setLimit(1000);
        userIDs.setLimit(1000);
        leaderboard.setLimit(1000);
        pageAnalytics.whereEqualTo("Final", 1);
        analytics.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                for (int i = 0; i < results.size(); i++) {
                    daily.add(results.get(i));
                }
                pageAnalytics.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> results, ParseException e) {
                        for (int i = 0; i < results.size(); i++) {
                            page.add(results.get(i));
                        }
                        userIDs.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> results, ParseException e) {
                                for (int i = 0; i < results.size(); i++) {
                                    user.add(results.get(i));
                                }
                                leaderboard.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> results, ParseException e) {
                                        for (int i = 0; i < results.size(); i++) {
                                            flappy.add(results.get(i));
                                        }
                                        load();
                                        sortArray(user);
                                        sortScores(flappy);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void load() {
        for(int i = 0; i < daily.size(); i++) {
            if(date == daily.get(i).get("Date")) {
                int a = (int)daily.get(i).get("Active");
                int t = (int)daily.get(i).get("Total");
                int p = (10000 * a / t) / 100;
                ((TextView) findViewById(R.id.activeTextView)).setText("Active/Total (" + date + ") " + a + "/" + t + "/" + p + "%");
                int dayNum = (int)daily.get(i).get("dayNum");
                dayNum -= 30;
                if(dayNum < 1) {dayNum = 1;}
                ParseObject oldDay = daily.get(0);
                for(int j = 0; j < daily.size(); j++) {
                    if(daily.get(j).get("dayNum") == dayNum) {
                        oldDay = daily.get(j);
                    }
                }
                int g = t - Integer.parseInt(oldDay.get("Total").toString());
                int gp = (10000 * g / Integer.parseInt(oldDay.get("Total").toString())) / 100;
                ((TextView) findViewById(R.id.growthTextView)).setText("Growth (" + oldDay.get("Date") + "-" + date + ") " + g + "/" + gp + "%");
            }
        }
    }

    public void toDaily() {
        contain.setVisibility(View.INVISIBLE);
        information.setVisibility(View.VISIBLE);

        TableRow header = new TableRow(this);
        TextView date = new TextView(this.getBaseContext());
        date.setText("Date");
        header.addView(date);
        TextView active = new TextView(this.getBaseContext());
        active.setText("Active");
        header.addView(active);
        TextView total = new TextView(this.getBaseContext());
        total.setText("Total");
        header.addView(total);
        infoTable.addView(header);

        for(int i = 0; i < daily.size(); i++) {
            TableRow newRow = new TableRow(this);
            TextView cell1 = new TextView(this.getBaseContext());
            cell1.setText(daily.get(i).get("Date").toString());
            newRow.addView(cell1);
            TextView cell2 = new TextView(this.getBaseContext());
            cell2.setText(daily.get(i).get("Active").toString());
            newRow.addView(cell2);
            TextView cell3 = new TextView(this.getBaseContext());
            cell3.setText(daily.get(i).get("Total").toString());
            newRow.addView(cell3);
            infoTable.addView(newRow);
        }
    }

    //setup analytics page table and header
    public void toPage() {
        contain.setVisibility(View.INVISIBLE);
        information.setVisibility(View.VISIBLE);

        TableRow header = new TableRow(this);
        TextView pageHeader = new TextView(this.getBaseContext());
        pageHeader.setText("Page");
        header.addView(pageHeader);
        TextView hits = new TextView(this.getBaseContext());
        hits.setText("Hits");
        header.addView(hits);
        infoTable.addView(header);

        String[] pages = {"Home", "Sport", "Lunch", "Store", "Club", "Calendar", "Planner", "Talon", "News", "Map", "Closing", "Flappy", "About"};
        for(int i = 0; i < pages.length; i++) {
            TableRow tr = new TableRow(this);
            TextView cell1 = new TextView(this.getBaseContext());
            cell1.setText(pages[i]);
            tr.addView(cell1);

            TextView cell2 = new TextView(this.getBaseContext());
            cell2.setText((String) page.get(i).get(pages[i]));
            tr.addView(cell2);

            infoTable.addView(tr);
        }
    }

    public void toUser() {

        contain.setVisibility(View.VISIBLE);
        information.setVisibility(View.INVISIBLE);

        var th1 = document.createElement("th");
        th1.innerHTML = "UUID";
        infoTable.appendChild(th1);
        var th2 = document.createElement("th");
        th2.innerHTML = "Active";
        infoTable.appendChild(th2);
        var th3 = document.createElement("th");
        th3.innerHTML = "Version";
        infoTable.appendChild(th3);

        for(int i = 0; i < user.size(); i++) {
            var tr = document.createElement("tr");
            var td1 = document.createElement("td");
            var td2 = document.createElement("td");
            var td3 = document.createElement("td");
            td1.innerHTML = user[i].get("uuid");
            td2.innerHTML = user[i].get("active");
            var val = "";
            try {
                val = user[i].get("version").toString();
                if(val == "2") {val = "2.0";}
                td3.innerHTML = val;
            }
            catch(e) {
                td3.innerHTML = "1.2";
            }
            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            infoTable.appendChild(tr);
        }
    }

    function toFlappy() {
        contain.style.display = "none";
        information.style.display = "block";

        var th1 = document.createElement("th");
        th1.innerHTML = "Name";
        infoTable.appendChild(th1);
        var th2 = document.createElement("th");
        th2.innerHTML = "Score";
        infoTable.appendChild(th2);
        var th3 = document.createElement("th");
        th3.innerHTML = "Visible";
        infoTable.appendChild(th3);

        for(i = 0; i < flappy.length; i++) {
            var tr = document.createElement("tr");
            var td1 = document.createElement("td");
            var td2 = document.createElement("td");
            var td3 = document.createElement("td");
            td1.innerHTML = flappy[i].get("Name");
            td2.innerHTML = flappy[i].get("Score");
            if(flappy[i].get("Display") == 1) {
                td3.innerHTML = "Yes";
            } else {
                td3.innerHTML = "No";
            }
            td3.setAttribute("id", flappy[i].id);
            td3.setAttribute("onclick", "changeDisplay(this.id)");
            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);
            infoTable.appendChild(tr);
        }
    }

    function changeDisplay(id) {
        var board = Parse.Object.extend("Leaderboard");
        var query = new Parse.Query(board);
        query.equalTo("objectId", id);
        query.find().then(function(results) {
            var result = results[0];
            console.log(result);
            if(result.get("Display") == 0) {
                result.set("Display", 1);
            } else {
                result.set("Display", 0);
            }
            result.save().then(function(obj) {
                for(i = 0; i < flappy.length; i++) {
                    if(obj.id == flappy[i].id) {
                        flappy[i].set("Display", obj.get("Display"));
                    }
                }
            });
            while(infoTable.rows.length > 0) {
                infoTable.deleteRow(0);
            }
            for(i = 0; i < flappy.length; i++) {
                var tr = document.createElement("tr");
                var td1 = document.createElement("td");
                var td2 = document.createElement("td");
                var td3 = document.createElement("td");
                td1.innerHTML = flappy[i].get("Name");
                td2.innerHTML = flappy[i].get("Score");
                if(flappy[i].get("Display") == 1) {
                    td3.innerHTML = "Yes";
                } else {
                    td3.innerHTML = "No";
                }
                td3.setAttribute("id", flappy[i].id);
                td3.setAttribute("onclick", "changeDisplay(this.id)");
                tr.appendChild(td1);
                tr.appendChild(td2);
                tr.appendChild(td3);
                infoTable.appendChild(tr);
            }
        });
    }

    public void sortArray(List<ParseObject> array)
    {
        List<ParseObject> result = new ArrayList<ParseObject>();
        for(int i = 0; i < array.size(); i++) {
            if(i == 0) {
                result.add(array.get(i));
                continue;
            }
            int index = 0;
            for(int j = 0; j < result.size(); j++) {
                int year1 = Integer.parseInt(((String)array.get(i).get("active")).substring(6));
                int year2 = Integer.parseInt(((String)result.get(j).get("active")).substring(6));
                if(year1 < year2) {}
                else if(year1 == year2) {
                    int month1 = Integer.parseInt(((String)array.get(i).get("active")).substring(0,2));
                    int month2 = Integer.parseInt(((String)result.get(j).get("active")).substring(0,2));
                    if(month1 < month2) {}
                    else if(month1 == month2) {
                        int day1 = Integer.parseInt(((String)array.get(i).get("active")).substring(3,5));
                        int day2 = Integer.parseInt(((String)result.get(j).get("active")).substring(3,5));
                        if(day1 < day2) {}
                        else if(day1 == day2) {}
                        else {
                            index++;
                        }
                    }
                    else {
                        index++;
                    }
                }
                else {
                    index++;
                }
            }
            result.add(index,array.get(i));
        }

        for(int i = 0; i < result.size(); i++) {
            array.set(i, result.get(i));
        }
    }

    public void sortScores(List<ParseObject> array) {
        List<ParseObject> result = new ArrayList<ParseObject>();
        for(int i = 0; i < array.size(); i++) {
            if(i == 0) {
                result.add(array.get(i));
                continue;
            }
            int index = 0;
            for(int j = 0; j < result.size(); j++) {
                int score1 = (int)array.get(i).get("Score");
                int score2 = (int)result.get(j).get("Score");
                if(score1 < score2) {
                    index++;
                } else {}
            }
            result.add(index,array.get(i));
        }
    }

    public void toHome() {
        contain.setVisibility(View.VISIBLE);
        information.setVisibility(View.INVISIBLE);
        ((ViewGroup)infoTable.getParent()).removeView(infoTable);
        infoTable = new GridLayout(this);
        information.addView(infoTable);
    }
}
