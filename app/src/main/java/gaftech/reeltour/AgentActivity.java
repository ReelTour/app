package gaftech.reeltour;

/**
 * Created by r.suleymanov on 23.06.2015.
 * email: ruslancer@gmail.com
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.helpshift.Helpshift;

import gaftech.reeltour.authentification.SessionManagerActivity;
import gaftech.reeltour.models.Tour;
import gaftech.reeltour.models.ToursDatabase;


public class AgentActivity extends SessionManagerActivity implements View.OnClickListener{
    SessionManagerActivity session;
    ToursDatabase tour_database;
    Tour current_tour;
    Button newTourBtn;
    Button existingTourBtn;
    Button loginBtn;
    Button logoutBtn;
    Button editExistTourBtn;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_agent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_faq:
                Helpshift.showFAQs(AgentActivity.this);
                return true;
            case R.id.contactus:
                Helpshift.showConversation(AgentActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);
        Helpshift.install(this.getApplication(),
                Configuration.HELPSHIFT_APIKEY,
                Configuration.HELPSHIFT_DOMAIN,
                Configuration.HELPSHIFT_APPID);
        newTourBtn = (Button)findViewById(R.id.start_new_tour_btn);
        existingTourBtn = (Button) findViewById(R.id.start_existing_tour_btn);
        loginBtn = (Button) findViewById(R.id.login_button);
        logoutBtn = (Button) findViewById(R.id.logout_button);
        editExistTourBtn = (Button) findViewById(R.id.edit_existing_tour_btn);
        //setup listeners
        newTourBtn.setOnClickListener(this);
        existingTourBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        editExistTourBtn.setOnClickListener(this);

        newTourBtn.setVisibility(View.GONE);
        editExistTourBtn.setVisibility(View.GONE);
        logoutBtn.setVisibility(View.GONE);
        tour_database = new ToursDatabase(this);
        current_tour = tour_database.getCurrentTour();
        if (current_tour == null) {
            existingTourBtn.setVisibility(View.GONE);
            editExistTourBtn.setVisibility(View.GONE);
        }
        //first no access
        onAccessNotAllowed();


    }

    @Override
    public void onAccessAllowed() {
        super.onAccessAllowed();
        newTourBtn.setVisibility(View.VISIBLE);
        if (current_tour != null) {
            editExistTourBtn.setVisibility(View.VISIBLE);
        }
        logoutBtn.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.GONE);
    }

    @Override
    public void onAccessNotAllowed() {
        super.onAccessNotAllowed();
        newTourBtn.setVisibility(View.GONE);
        editExistTourBtn.setVisibility(View.GONE);
        logoutBtn.setVisibility(View.GONE);
        loginBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.edit_existing_tour_btn:
                intent = new Intent(getBaseContext(), CreateTourActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(SessionManagerActivity.ACCOUNT_EXTRA_FIELD, currentSession);
                intent.putExtra("edit", true);
                startActivity(intent);
                finish();
                break;
            case R.id.start_existing_tour_btn:
                intent = new Intent(this, WaitingScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.start_new_tour_btn:
                intent = new Intent(getBaseContext(), CreateTourActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(SessionManagerActivity.ACCOUNT_EXTRA_FIELD, currentSession);
                startActivity(intent);
                finish();
                break;
            case R.id.login_button:
                this.createSession();
                break;
            case R.id.logout_button:
                this.destroySession();
                break;
        }
    }
}
