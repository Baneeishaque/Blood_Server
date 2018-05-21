package caventa.ansheer.ndk.caventa.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import caventa.ansheer.ndk.caventa.R;
import caventa.ansheer.ndk.caventa.adapters.Work_Advances_Adapter;
import caventa.ansheer.ndk.caventa.adapters.Work_Expense_Adapter;
import caventa.ansheer.ndk.caventa.constants.General_Data;
import caventa.ansheer.ndk.caventa.models.Work_Advance;
import caventa.ansheer.ndk.caventa.models.Work_Expense;
import ndk.prism.common_utils.Date_Picker_Utils;
import ndk.prism.common_utils.Date_Utils;
import ndk.prism.common_utils.Toast_Utils;

public class Add_loan extends AppCompatActivity {

    private Work_Advances_Adapter work_advances_adapter;

    private Work_Expense_Adapter work_expenses_adapter;

    private List<Work_Advance> work_advances;

    private List<Work_Expense> work_expenses;

    private RecyclerView work_expenses_recycler_view;
    private RecyclerView work_advances_recycler_view;

    TextView txt_total_advance, txt_total_expense, txt_total_profit;

    double total_advance = 0, total_expense = 0, total_profit = 0;
    private Calendar calendar;
    private SimpleDateFormat sdf;
    private EditText txt_name;
    private EditText txt_address;

    private View mProgressView;
    private View mLoginFormView;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_work);

        application_context = getApplicationContext();
        settings = getApplicationContext().getSharedPreferences(General_Data.SHARED_PREFERENCE, Context.MODE_PRIVATE);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        ImageView pick_date = findViewById(R.id.show_calendar);
        final TextView txt_date = findViewById(R.id.work_date);

        calendar = Calendar.getInstance();
        txt_date.setText(Date_Utils.normal_Date_Format_words.format(calendar.getTime()));
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                txt_date.setText(Date_Utils.normal_Date_Format_words.format(calendar.getTime()));
            }
        };

        pick_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date_Picker_Utils.show_date_picker(Add_Other_Expense.this, date, calendar);
            }
        });

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date_Picker_Utils.show_date_picker(Add_Other_Expense.this, date, calendar);
            }
        });

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_work, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item_cancel) {
//            Intent i = new Intent(application_context, Sales_Person_Dashboard_Page.class);
//            startActivity(i);
            finish();
            return true;
        }

        if (id == R.id.menu_item_save) {
            attempt_work_save();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    /* Keep track of the login task to ensure we can cancel it if requested. */
    private Work_Save_Task mAuthTask = null;

    private void initView() {
        txt_name = findViewById(R.id.name);
        txt_address = findViewById(R.id.address);
		txt_installment_amount = findViewById(R.id.installment_amount);
    }

    /* Represents an asynchronous login task used to authenticate the user. */
    public class Work_Save_Task extends AsyncTask<Void, Void, String[]> {

        String task_description;
        Double task_amount;
		Double task_installment_amount;
        
        Work_Save_Task(String description, Double amount , Double installment_amount) {
            task_description = description;
            task_amount = amount;
			task installment_amount = installment_amount;
        }

        DefaultHttpClient http_client;
        HttpPost http_post;
        ArrayList<NameValuePair> name_pair_value;
        String network_action_response;

        @Override
        protected String[] doInBackground(Void... params) {
            try {
                http_client = new DefaultHttpClient();
                http_post = new HttpPost(General_Data.SERVER_IP_ADDRESS + "/android/add_loan.php");
                name_pair_value = new ArrayList<NameValuePair>(3);
                name_pair_value.add(new BasicNameValuePair("description", task_description));
                name_pair_value.add(new BasicNameValuePair("amount", task_amount));
				name_pair_value.add(new BasicNameValuePair("installment_amount", task_installment_amount));
                
                http_post.setEntity(new UrlEncodedFormEntity(name_pair_value));
                ResponseHandler<String> response_handler = new BasicResponseHandler();
                network_action_response = http_client.execute(http_post, response_handler);
                return new String[]{"0", network_action_response};

            } catch (UnsupportedEncodingException e) {
                return new String[]{"1", "UnsupportedEncodingException : " + e.getLocalizedMessage()};
            } catch (ClientProtocolException e) {
                return new String[]{"1", "ClientProtocolException : " + e.getLocalizedMessage()};
            } catch (IOException e) {
                return new String[]{"1", "IOException : " + e.getLocalizedMessage()};
            }
        }


        @Override
        protected void onPostExecute(final String[] network_action_response_array) {
            mAuthTask = null;

            showProgress(false);

            Log.d(General_Data.TAG, network_action_response_array[0]);
            Log.d(General_Data.TAG, network_action_response_array[1]);

            if (network_action_response_array[0].equals("1")) {
                Toast.makeText(Add_Other_Expense.this, "Error : " + network_action_response_array[1], Toast.LENGTH_LONG).show();
                Log.d(General_Data.TAG, network_action_response_array[1]);
            } else {

                try {
                    JSONObject json = new JSONObject(network_action_response_array[1]);
                    String count = json.getString("status");
                    switch (count) {
                        case "0":
                            Toast.makeText(application_context, "OK", Toast.LENGTH_LONG).show();
                            finish();
                            break;
                        case "1":
                            Toast.makeText(application_context, "Error : " + json.getString("error"), Toast.LENGTH_LONG).show();
                            txt_name.requestFocus();
                            break;
                        default:
                            Toast.makeText(application_context, "Error : Check json", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(application_context, "Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    Log.d(General_Data.TAG, e.getLocalizedMessage());
                }


            }


        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    private void attempt_work_save() {
        if (mAuthTask != null) {
            return;
        }

        reset_errors(new EditText[]{txt_name, txt_address});

        Pair<Boolean, EditText> empty_check_result = empty_check(new Pair[]{new Pair(txt_name, "Please Enter Expense Description..."),
                new Pair(txt_address, "Please Enter Expense Amount...")});

        if (empty_check_result.first) {
            // There was an error; don't attempt login and focus the first form field with an error.
            empty_check_result.second.requestFocus();
        } else {

            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            if (isOnline()) {
                showProgress(true);
                mAuthTask = new Work_Save_Task(txt_name.getText().toString(), txt_address.getText().toString());
                mAuthTask.execute((Void) null);
            } else {
                Toast_Utils.longToast(getApplicationContext(), "Internet is unavailable");
            }
        }
    }

    Context application_context;

    //TODO : Introduce common class

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    //TODO : Introduce common class
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //TODO : Introduce common class
    private Pair<Boolean, EditText> empty_check(Pair[] editText_Error_Pairs) {
        for (Pair<EditText, String> editText_Error_Pair : editText_Error_Pairs) {
            if (TextUtils.isEmpty(editText_Error_Pair.first.getText().toString())) {
                editText_Error_Pair.first.setError(editText_Error_Pair.second);
                return new Pair<>(true, editText_Error_Pair.first);
            }
        }
        return new Pair<>(false, null);
    }

    //TODO : Introduce common class
    private Pair<Boolean, EditText> number_check_double(Pair[] editText_Error_Pairs) {
        for (Pair<EditText, String> editText_Error_Pair : editText_Error_Pairs) {
            try {
                Double.parseDouble(editText_Error_Pair.first.getText().toString());
            } catch (NumberFormatException ex) {
                editText_Error_Pair.first.setError(editText_Error_Pair.second);
                return new Pair<>(true, editText_Error_Pair.first);
            }
        }
        return new Pair<>(false, null);
    }

    //TODO : Introduce common class
    private Pair<Boolean, EditText> number_check_integer(Pair[] editText_Error_Pairs) {
        for (Pair<EditText, String> editText_Error_Pair : editText_Error_Pairs) {
            try {
                Integer.parseInt(editText_Error_Pair.first.getText().toString());
            } catch (NumberFormatException ex) {
                editText_Error_Pair.first.setError(editText_Error_Pair.second);
                return new Pair<>(true, editText_Error_Pair.first);
            }
        }
        return new Pair<>(false, null);
    }

    //TODO : Introduce common class
    private void reset_errors(EditText[] edit_texts) {
        for (EditText edit_text : edit_texts) {
            edit_text.setError(null);
        }
    }

}
