package com.example.useri.chatbot;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private final int REQ_CODE_SPEECH_INPUT = 100;
    ImageView btnSpeak,btnvol,langbtn;
    TextToSpeech tts;
    EditText editText;
    Boolean flagFab = true;
    Boolean flagFab1 = false;
    Boolean flag=true;
    Boolean lang=false;

    String userQuery;
    TextView textView ,txt;
    Context context=this;

    String query,resultnew;
    int MY_DATA_CHECK_CODE = 1000;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference notebookRef = db.collection("user");

    private NoteAdapter adapter;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         recyclerView = findViewById(R.id.recyclerView);

        tts = new TextToSpeech(this, this);
        btnSpeak = findViewById(R.id.fab_img);
        btnvol=findViewById(R.id.fab_img1);
        langbtn=findViewById(R.id.fab_img2);
        textView=findViewById(R.id.textView4);
        editText = (EditText)findViewById(R.id.editText);
        relativeLayout=findViewById(R.id.addBtn);

        btnvol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volumeupdown();
            }

            private void volumeupdown() {
                ImageView fab_images = (ImageView)findViewById(R.id.fab_img1);

                Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.speaker);
                Bitmap image1 = BitmapFactory.decodeResource(getResources(),R.drawable.speakeroff);
                if(flagFab1){
                    ImageViewAnimatedChange(MainActivity.this,fab_images,image);
                    flagFab1=false;
                    flag=true;
                }
                else
                {
                    ImageViewAnimatedChange(MainActivity.this,fab_images,image1);
                    flagFab1=true;
                    flag=false;
                }
            }
        });

        langbtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                ImageView fab_images3 = (ImageView)findViewById(R.id.fab_img2);

                Bitmap image13 = BitmapFactory.decodeResource(getResources(),R.drawable.tamil);
                Bitmap image3 = BitmapFactory.decodeResource(getResources(),R.drawable.english);
                if(lang){
                    ImageViewAnimatedChange(MainActivity.this,fab_images3,image3);
                    lang=false;
                    flag=true;
                    textView.setTextSize(21);

                    textView.setText("  Farming Assistance");

                    relativeLayout.setVisibility(View.VISIBLE);

                }
                else
                {
                    ImageViewAnimatedChange(MainActivity.this,fab_images3,image13);
                   lang=true;
                    relativeLayout.setVisibility(View.INVISIBLE);
                    textView.setText("    விவசாயி உதவி");
                    textView.setTextSize(22);




                }
            }
        });




        setUpRecyclerView();
//        InputMethodManager imm = (InputMethodManager) this
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        if (imm.isAcceptingText()) {
//            textView.setVisibility(View.GONE);
//            btnvol.setVisibility(View.GONE);
//        } else {
//        }


        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();




            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView fab_img = (ImageView)findViewById(R.id.fab_img);
                Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.ic_send_white_24dp);
                Bitmap img1 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_mic_white_24dp);


                if (s.toString().trim().length()!=0 && flagFab){
                    ImageViewAnimatedChange(MainActivity.this,fab_img,img);
                    flagFab=false;


                    btnSpeak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                           userQuery= editText.getText().toString();
                           editText.getText().clear();

                            RetrieveFeedTask task=new RetrieveFeedTask();
                            query=userQuery;
                                task.execute(userQuery);





                        }
                    });

                }
                else if (s.toString().trim().length()==0){
                    ImageViewAnimatedChange(MainActivity.this,fab_img,img1);
                    flagFab=true;

                    btnSpeak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            promptSpeechInput();




                        }
                    });


                }


            }


            @Override
            public void afterTextChanged(Editable s) {


            }
        });
//        String textToBeTranslate= "வரவேற்பு";



    }
    void Translate(String query,String languagePair) throws ExecutionException, InterruptedException {
        TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(context);
        String translationResult = translatorBackgroundTask.execute(query,languagePair).get();
        translationResult = translationResult.substring(translationResult.indexOf('[')+1);
        translationResult = translationResult.substring(0,translationResult.indexOf("]"));
        translationResult = translationResult.substring(translationResult.indexOf('"')+1);
        translationResult = translationResult.substring(0,translationResult.indexOf('"'));
//        Toast.makeText(context,translationResult, Toast.LENGTH_LONG).show();
        userQuery=translationResult;
        resultnew=translationResult;
        Log.d("Translation Result", String.valueOf(translationResult)); // Logs the result in Android Monitor
    }



    public void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    private void setUpRecyclerView() {
        Query query = notebookRef.orderBy("Timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        adapter = new NoteAdapter(options);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(adapter);




    }






    public void onStart() {
        super.onStart();
        adapter.startListening();


    }




    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.UK);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        if(lang)
         intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Ask your Query");

        try {

            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "sorry! Your device doesn't support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    userQuery=result.get(0);

                    query=userQuery;
                    if(!lang) {
                        String languagePair = "ta-en"; //English to French ("<source_language>-<target_language>")
                        //Executing the translation function
                        try {
                            Translate(query, languagePair);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    RetrieveFeedTask task=new RetrieveFeedTask();
                    task.execute(userQuery);


                }
                break;
            }

        }
    }









    // Create GetText Metod
    public String GetText(String query) throws UnsupportedEncodingException {

        String text = "";
        BufferedReader reader = null;

        // Send data
        try {

            // Defined URL  where to send data
            URL url = new URL("https://api.api.ai/v1/query?v=20150910");

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Authorization", "Bearer ee7be0442ca34713976a9cd5ff9e7684");
            conn.setRequestProperty("Content-Type", "application/json");

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            JSONArray queryArray = new JSONArray();
            queryArray.put(query);
            jsonParam.put("query", queryArray);
//            jsonParam.put("name", "order a medium pizza");
            jsonParam.put("lang", "en");
            jsonParam.put("sessionId", "1234567890");


            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            Log.d("karma", "after conversion is " + jsonParam.toString());
            wr.write(jsonParam.toString());
            wr.flush();
            Log.d("karma", "json is " + jsonParam);

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;


            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();



            JSONObject object1 = new JSONObject(text);
            JSONObject object = object1.getJSONObject("result");
            JSONObject fulfillment = null;
            String speech = null;
//            if (object.has("fulfillment")) {
            fulfillment = object.getJSONObject("fulfillment");
//                if (fulfillment.has("speech")) {
            speech = fulfillment.optString("speech");
//                }
//            }


            Log.d("karma ", "response is " + text);

            return speech;

        } catch (Exception ex) {
            Log.d("karma", "exception at last " + ex);
        } finally {
            try {

                reader.close();
            } catch (Exception ex) {
            }
        }

        return null;
    }

    public NoteAdapter getAdapter() {
        return adapter;
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            String s = null;
            try {

                s = GetText(voids[0]);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.d("karma", "Exception occurred " + e);
            }

            return s;

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    // Call smooth scroll
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() );
                }
            });


            resultnew=s;
            if (lang){
                flag=false;
                flagFab1=false;

            String languagePair = "en-ta"; //English to French ("<source_language>-<target_language>")
            //Executing the translation function
            try {
                Translate(resultnew,languagePair);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            }
//            Toast.makeText(MainActivity.this, query+resultnew, Toast.LENGTH_SHORT).show();
            if(flag) {
                tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
            }
            Map<String, Object> user = new HashMap<>();
            user.put("Timestamp", FieldValue.serverTimestamp());

            user.put("Query", query);
            user.put("Result", resultnew);





// Add a new document with a generated ID
            db.collection("user")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });




        }

    }



    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();

            tts.shutdown();
        }
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }



}